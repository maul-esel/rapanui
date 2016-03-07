package rapanui.ui.models;

import rapanui.core.ConclusionProcess;
import rapanui.core.Justification;
import rapanui.core.Transformation;
import rapanui.dsl.Term;

import java.util.List;
import java.util.LinkedList;

public class ConclusionProcessModel implements ConclusionProcess.Observer {
	private final ProofEnvironmentModel container;
	private final ConclusionProcess conclusion;

	private final List<Observer> observers = new LinkedList<Observer>();

	ConclusionProcessModel(ProofEnvironmentModel container, ConclusionProcess conclusion) {
		this.container = container;
		this.conclusion = conclusion;

		conclusion.addObserver(this);
	}

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
		void activated();
		void deactivated();
		void titleChanged(String newTitle);
		void transformationAdded(Transformation transformation);
	}

	@Override
	public void transformationAdded(Transformation transformation) {
		for (Observer observer : observers)
			observer.transformationAdded(transformation);
		for (Observer observer : observers)
			observer.titleChanged(getTitle());
	}

	@Override
	public void transformationRemoved(Transformation transformation) { /* currently unused */ }
}
