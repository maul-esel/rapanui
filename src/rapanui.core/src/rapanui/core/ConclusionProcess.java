package rapanui.core;

import java.util.ArrayList;
import java.util.List;

import rapanui.dsl.moai.Term;

public class ConclusionProcess {
	private final Term startTerm;
	private final List<Transformation> transformations;
	private final List<ConclusionProcessObserver> observers;

	ConclusionProcess(ProofEnvironment environment, Term startTerm) {
		assert environment != null;
		assert startTerm != null;

		this.startTerm = startTerm;
		this.transformations = new ArrayList<Transformation>();
		this.observers = new ArrayList<ConclusionProcessObserver>();
	}

	public Term getStartTerm() {
		return startTerm;
	}

	public Term getLastTerm() {
		if (transformations.size() == 0)
			return startTerm;
		return transformations.get(transformations.size() - 1).getOutput();
	}

	// TODO: get conclusion 'type' (equals or subset)

	public Transformation[] getTransformations() {
		return Patterns.listToArray(transformations, Transformation[]::new);
	}

	public void appendTransformation(Transformation transformation) {
		assert transformation != null;

		if (!this.getLastTerm().equals(transformation.getInput()))
			throw new IllegalArgumentException();
		transformations.add(transformation);
		Patterns.notifyObservers(observers, ConclusionProcessObserver::transformationAdded, transformation);
	}

	public void removeLastTransformation() {
		assert transformations.size() > 0;
		Transformation removed = transformations.remove(transformations.size() - 1);
		Patterns.notifyObservers(observers, ConclusionProcessObserver::transformationRemoved, removed);
	}

	public void addObserver(ConclusionProcessObserver observer) {
		observers.add(observer);
	}

	public void deleteObserver(ConclusionProcessObserver observer) {
		observers.remove(observer);
	}
}
