package rapanui.ui.models;

import rapanui.core.ConclusionProcess;
import rapanui.core.Justification;
import rapanui.core.Transformation;
import rapanui.dsl.Term;
import rapanui.ui.commands.DeleteConclusionCommand;
import rapanui.ui.commands.UndoTransformationCommand;

import java.util.List;

import javax.swing.Action;

import java.util.Collection;
import java.util.LinkedList;

public class ConclusionProcessModel implements ConclusionProcess.Observer {
	private final ProofEnvironmentModel container;
	private final ConclusionProcess conclusion;

	private final List<Observer> observers = new LinkedList<Observer>();

	ConclusionProcessModel(ProofEnvironmentModel container, ConclusionProcess conclusion) {
		this.container = container;
		this.conclusion = conclusion;

		conclusion.addObserver(this);
		undoCommand = new UndoTransformationCommand(this, conclusion);
		deleteCommand = new DeleteConclusionCommand(this);
	}

	public final Action undoCommand;
	public final Action deleteCommand;

	public String getTitle() {
		return String.format("%s %s %s",
				conclusion.getStartTerm().serialize(),
				conclusion.getFormulaType().getLiteral(),
				conclusion.getLastTerm().serialize());
	}

	public Transformation[] getTransformations() {
		return conclusion.getTransformations();
	}

	public Term getStartTerm() {
		return conclusion.getStartTerm();
	}

	public void activate() {
		container.activateConclusion(this);
	}

	public boolean isActive() {
		return container.getActiveConclusion() == this;
	}

	void onActivate() {
		for (Observer observer : observers)
			observer.activated();
		loadSuggestions();
	}

	void onDeactivate() {
		for (Observer observer : observers)
			observer.deactivated();
		clearSuggestions();
	}

	void loadSuggestions() {
		container.loadSuggestions(conclusion);
	}

	void clearSuggestions() {
		container.clearSuggestions();
	}

	public void displayJustification(Justification justification) {
		container.displayJustification(justification);
	}

	public void undoTransformation() {
		Transformation[] transformations = conclusion.getTransformations();
		Transformation last = transformations[transformations.length - 1];
		if (container.getUnderlyingModel().getAnalyst().hasDerivatives(last)) {
			container.highlight(container.getUnderlyingModel().getAnalyst().findDerivatives(last));
			container.requestConfirmation(
				"Diese Aktion würde auch die markierten Daten entfernen. Fortfahren?",
				result -> {
					if (result) {
						conclusion.undoTransformation();
						container.clearSuggestions();
					} else
						container.unhighlight();
			});
		} else {
			conclusion.undoTransformation();
			container.clearSuggestions();
		}
	}

	public void remove() {
		container.removeConclusion(conclusion);
	}

	void highlight(Collection<Transformation> transformations) {
		for (Observer observer : observers)
			observer.highlightRequested(transformations);
	}

	void unhighlight() {
		for (Observer observer : observers)
			observer.unhighlightRequested();
	}

	/* ****************************************** *
	 * Observer proxy                             *
	 * ****************************************** */

	public void addObserver(Observer observer) {
		assert observer != null;
		observers.add(observer);
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	public static interface Observer {
		default void activated() {};
		default void deactivated() {};
		default void titleChanged(String newTitle) {};
		default void transformationAdded(Transformation transformation) {};
		default void transformationRemoved(Transformation transformation) {};

		default void highlightRequested(Collection<Transformation> transformations) {};
		default void unhighlightRequested() {};
	}

	@Override
	public void transformationAdded(Transformation transformation) {
		for (Observer observer : observers)
			observer.transformationAdded(transformation);
		for (Observer observer : observers)
			observer.titleChanged(getTitle());
	}

	@Override
	public void transformationRemoved(Transformation transformation) {
		for (Observer observer : observers)
			observer.transformationRemoved(transformation);
		for (Observer observer : observers)
			observer.titleChanged(getTitle());
	}
}
