package rapanui.core;

import java.util.ArrayList;
import java.util.List;

import rapanui.dsl.DslHelper;
import rapanui.dsl.moai.Term;

import static rapanui.core.Patterns.*;

/**
 * Represents the core proof process, in which a term is transformed using rules and premises.
 */
public class ConclusionProcess {
	private final Term startTerm;
	private final List<Transformation> transformations;
	private final List<ConclusionProcessObserver> observers;

	/**
	 * Creates a new conclusion process
	 *
	 * @param environment The environment containing the process (must not be null)
	 * @param startTerm The initial term of the process (must not be null)
	 */
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

	/**
	 * Retrieves the (current) last term in the transformation chain.
	 * @return The output term of the last transformation, or if there are none, the initial term
	 */
	public Term getLastTerm() {
		if (transformations.size() == 0)
			return startTerm;
		return transformations.get(transformations.size() - 1).getOutput();
	}

	// TODO: get conclusion 'type' (equals or subset)

	public Transformation[] getTransformations() {
		return listToArray(transformations, Transformation[]::new);
	}

	/**
	 * Appends a new transformation to the chain.
	 *
	 * @param transformation The transformation to append
	 *
	 * The new transformation's input term must equal the current last term on the conclusion process.
	 */
	public void appendTransformation(Transformation transformation) {
		assert transformation != null;

		if (!DslHelper.equal(getLastTerm(), transformation.getInput()))
			throw new IllegalArgumentException();
		transformations.add(transformation);
		notifyObservers(observers, ConclusionProcessObserver::transformationAdded, transformation);
	}

	/**
	 * Reverts one step by removing the last transformation in the chain.
	 *
	 * There must be at least one transformation.
	 */
	public void removeLastTransformation() {
		assert transformations.size() > 0;
		Transformation removed = transformations.remove(transformations.size() - 1);
		notifyObservers(observers, ConclusionProcessObserver::transformationRemoved, removed);
	}

	/**
	 * Adds an observer to the process. Observers are notified upon changes.
	 *
	 * @param observer The new observer
	 */
	public void addObserver(ConclusionProcessObserver observer) {
		observers.add(observer);
	}

	/**
	 * Unregisters an observer from the process, so it is no longer notified of changes.
	 *
	 * @param observer The observer to remove
	 */
	public void deleteObserver(ConclusionProcessObserver observer) {
		observers.remove(observer);
	}
}
