package rapanui.ui.models;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import rapanui.core.ConclusionProcess;
import rapanui.core.ProofEnvironment;
import rapanui.core.ProofEnvironmentObserver;
import rapanui.dsl.Formula;
import rapanui.ui.commands.*;

public class ProofEnvironmentModel implements ProofEnvironmentObserver {
	private final ApplicationModel container;
	private final ProofEnvironment env;
	private final String name;

	private final List<Observer> observers = new LinkedList<Observer>();
	private final List<ConclusionProcessModel> conclusions = new LinkedList<ConclusionProcessModel>();

	public ProofEnvironmentModel(ApplicationModel container, ProofEnvironment env, String name) {
		assert env != null;
		assert name!= null;
		assert container != null;

		this.env = env;
		this.name = name;
		this.container = container;

		conclusions.addAll(
				Arrays.stream(env.getConclusions())
				.map(conclusion -> new ConclusionProcessModel(this, conclusion))
				.collect(Collectors.toList())
		);
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

	/* ****************************************** *
	 * Sub-data models (premises, conclusions)    *
	 * ****************************************** */

	public Formula[] getPremises() {
		return env.getPremises();
	}

	public ConclusionProcessModel[] getConclusions() {
		return conclusions.toArray(new ConclusionProcessModel[conclusions.size()]);
	}

	void loadSuggestions(ConclusionProcess conclusion) {
		container.loadSuggestions(env, conclusion);
	}

	ProofEnvironment getUnderlyingModel() {
		return env;
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
		void premiseAdded(Formula premise);
		void conclusionStarted(ConclusionProcessModel conclusionModel);
	}

	@Override
	public void premiseAdded(Formula premise) {
		for (Observer observer : observers)
			observer.premiseAdded(premise);
	}

	@Override
	public void premiseRemoved(Formula premise) { /* currently unused */ }

	@Override
	public void conclusionStarted(ConclusionProcess conclusion) {
		ConclusionProcessModel model = new ConclusionProcessModel(this, conclusion);
		conclusions.add(model);

		for (Observer observer : observers)
			observer.conclusionStarted(model);
	}

	@Override
	public void conclusionRemoved(ConclusionProcess conclusion) { /* currently unused */ }

	@Override
	public void conclusionMoved(ConclusionProcess conclusion) { /* currently unused */ }
}
