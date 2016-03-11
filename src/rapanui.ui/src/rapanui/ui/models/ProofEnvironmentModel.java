package rapanui.ui.models;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.html.HTMLDocument;

import rapanui.core.ConclusionProcess;
import rapanui.core.Justification;
import rapanui.core.ProofEnvironment;
import rapanui.core.Transformation;
import rapanui.dsl.Predicate;
import rapanui.ui.ProofFormatter;
import rapanui.ui.commands.*;

public class ProofEnvironmentModel implements ProofEnvironment.Observer {
	private final ApplicationModel container;
	private final ProofEnvironment env;
	private final String name;

	private final List<Observer> observers = new LinkedList<Observer>();
	private final Map<ConclusionProcess, ConclusionProcessModel> conclusionModelMap = new HashMap<ConclusionProcess, ConclusionProcessModel>();
	private ConclusionProcessModel activeConclusion = null;

	public ProofEnvironmentModel(ApplicationModel container, ProofEnvironment env, String name) {
		assert env != null;
		assert name!= null;
		assert container != null;

		this.env = env;
		this.name = name;
		this.container = container;

		for (ConclusionProcess conclusion : env.getConclusions())
			conclusionStarted(conclusion);
		env.addObserver(this);

		definitionSelectionModel = new DefaultComboBoxModel<String>(container.getRuleSystems().getDefinitionNames());

		createFormulaPremiseCommand = new CreateFormulaPremiseCommand(env, formulaPremiseInputModel);
		createDefinitionReferencePremiseCommand = new CreateDefinitionReferencePremiseCommand(
				env, container, definitionPremiseInputModel, definitionSelectionModel);
		createConclusionCommand = new CreateConclusionCommand(env, conclusionTermInputModel);
	}

	public String getName() {
		return name;
	}

	public void onActivate() {
		if (activeConclusion != null)
			activeConclusion.onActivate();
	}

	public void export() {
		container.requestFilePath(true, new String[]{ "txt" }, path -> {
			HTMLDocument document = new ProofFormatter(env).getDocument();
			try {
				// TODO: HTML export
				PrintWriter writer = new PrintWriter(path);
				writer.write(document.getText(0, document.getLength()));
				writer.close();
			} catch (FileNotFoundException|BadLocationException e) {
				throw new IllegalStateException("Export failed", e);
			}
		});
	}

	/* ****************************************** *
	 * Sub-data models (premises, conclusions)    *
	 * ****************************************** */

	public Predicate[] getPremises() {
		return env.getPremises();
	}

	public ConclusionProcessModel[] getConclusions() {
		return conclusionModelMap.values().toArray(new ConclusionProcessModel[conclusionModelMap.size()]);
	}

	public ConclusionProcessModel getActiveConclusion() {
		return activeConclusion;
	}

	void loadSuggestions(ConclusionProcess conclusion) {
		container.loadSuggestions(env, conclusion);
	}

	void clearSuggestions() {
		container.clearSuggestions();
	}

	void displayJustification(Justification justification) {
		container.displayJustification(justification);
	}

	ProofEnvironment getUnderlyingModel() {
		return env;
	}

	void activateConclusion(ConclusionProcessModel conclusionModel) {
		ConclusionProcessModel previous = activeConclusion;
		activeConclusion = conclusionModel;

		if (previous != null)
			previous.onDeactivate();
		if (activeConclusion != null)
			activeConclusion.onActivate();
	}

	void requestConfirmation(String message, Consumer<Boolean> handler) {
		container.requestConfirmation(message, handler);
	}

	void highlight(Collection<Transformation> transformations) {
		for (ConclusionProcessModel conclusion : conclusionModelMap.values())
			conclusion.highlight(transformations);
	}

	void unhighlight() {
		for (ConclusionProcessModel conclusion : conclusionModelMap.values())
			conclusion.unhighlight();
	}

	void removeConclusion(ConclusionProcess conclusion) {
		if (env.getAnalyst().hasDerivatives(conclusion)) {
			highlight(env.getAnalyst().findDerivatives(conclusion));
			requestConfirmation(
				"Diese Aktion würde auch die markierten Daten entfernen. Fortfahren?",
				result -> {
					if (result)
						env.removeConclusion(conclusion);
					else
						unhighlight();
				}
			);
		} else
			env.removeConclusion(conclusion);
	}

	public void removePremise(Predicate premise) {
		if (env.getAnalyst().hasDerivatives(premise)) {
			highlight(env.getAnalyst().findDerivatives(premise));
			requestConfirmation(
				"Diese Aktion würde auch die markierten Daten entfernen. Fortfahren?",
				result -> {
					if (result)
						env.removePremise(premise);
					else
						unhighlight();
				}
			);
		} else
			env.removePremise(premise);
	}

	/* ****************************************** *
	 * Sub-UI models                              *
	 * ****************************************** */

	public final Document formulaPremiseInputModel = new PlainDocument();
	public final Document definitionPremiseInputModel = new PlainDocument();
	public final Document conclusionTermInputModel = new PlainDocument();

	public final ComboBoxModel<String> definitionSelectionModel;

	public final Action createFormulaPremiseCommand;
	public final Action createDefinitionReferencePremiseCommand;
	public final Action createConclusionCommand;

	public Action getDeletePremiseCommand(Predicate premise) {
		return new DeletePremiseCommand(this, premise);
	}

	/* ****************************************** *
	 * Observer proxy                             *
	 * ****************************************** */

	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	public static interface Observer {
		default void premiseAdded(Predicate premise) {}
		default void premiseRemoved(Predicate premise) {}
		default void conclusionStarted(ConclusionProcessModel conclusionModel) {}
		default void conclusionRemoved(ConclusionProcessModel conclusionModel) {}
	}

	@Override
	public void premiseAdded(Predicate premise) {
		for (Observer observer : observers)
			observer.premiseAdded(premise);
	}

	@Override
	public void premiseRemoved(Predicate premise) {
		for (Observer observer : observers)
			observer.premiseRemoved(premise);
	}

	@Override
	public void conclusionStarted(ConclusionProcess conclusion) {
		ConclusionProcessModel model = new ConclusionProcessModel(this, conclusion);
		conclusionModelMap.put(conclusion, model);

		for (Observer observer : observers)
			observer.conclusionStarted(model);

		activateConclusion(model);
	}

	@Override
	public void conclusionRemoved(ConclusionProcess conclusion) {
		if (!conclusionModelMap.containsKey(conclusion))
			return;

		ConclusionProcessModel model = conclusionModelMap.get(conclusion);
		if (model == activeConclusion)
			activateConclusion(null);

		conclusionModelMap.remove(conclusion);
		for (Observer observer : observers)
			observer.conclusionRemoved(model);
	}
}
