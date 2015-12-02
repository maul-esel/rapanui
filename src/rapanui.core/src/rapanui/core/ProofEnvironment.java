package rapanui.core;

import java.util.ArrayList;
import java.util.List;

import rapanui.dsl.moai.Formula;
import rapanui.dsl.moai.RuleSystem;
import rapanui.dsl.moai.Term;

import static rapanui.core.Patterns.*;

/**
 * The basic environment in which proofs are made. Contains a list of premises
 * and the resulting conclusions.
 */
public class ProofEnvironment {
	private final RuleSystem[] ruleSystems;
	private final List<Formula> premises;
	private final List<ConclusionProcess> conclusions;
	private final List<ProofEnvironmentObserver> observers;

	private final DependencyAnalyst analyst;

	/**
	 * Create a new environment.
	 *
	 * @param ruleSystems The rule systems upon which any conclusions in the environment are based (must not be null)
	 */
	public ProofEnvironment(RuleSystem[] ruleSystems) {
		assert ruleSystems != null;

		this.ruleSystems = ruleSystems;
		this.premises = new ArrayList<Formula>();
		this.conclusions = new ArrayList<ConclusionProcess>();
		this.observers = new ArrayList<ProofEnvironmentObserver>();

		this.analyst = new DependencyAnalyst(this);
	}

	public RuleSystem[] getRuleSystems() {
		return ruleSystems;
	}

	public DependencyAnalyst getAnalyst() {
		return analyst;
	}

	/**
	 * Add a new premise to the environment so it can be used in conclusions.
	 *
	 * @param premise The formula to add as premise (must not be null)
	 *
	 * If the premise already exists in the environment, it will not be added again.
	 */
	public void addPremise(Formula premise) {
		assert premise != null;
		if (addToSet(premises, premise))
			notifyObservers(observers, ProofEnvironmentObserver::premiseAdded, premise);
	}

	/**
	 * Removes a premise from the environment.
	 *
	 * @param premise The premise to remove
	 * @return True if it was actually removed, false otherwise
	 *
	 * Any conclusions based upon the removed premise become invalid.
	 * @throws DependencyRemovalException If there are any derivatives of the premise in the environment.
	 */
	public boolean removePremise(Formula premise) throws DependencyRemovalException {
		assert premise != null;
		if (analyst.hasDerivatives(premise))
			throw new DependencyRemovalException();
		return removeWithCheckAndNotify(premises, premise, observers, ProofEnvironmentObserver::premiseRemoved);
	}

	/**
	 * Removes a premise from the environment.
	 *
	 * @param index The index of the premise to remove
	 * @return True if it was actually removed, false otherwise
	 *
	 * Any conclusions based upon the removed premise become invalid.
	 * @throws DependencyRemovalException If there are any derivatives of the premise in the environment.
	 */
	public boolean removePremise(int index) throws DependencyRemovalException {
		if (analyst.hasDerivatives(premises.get(index)))
			throw new DependencyRemovalException();
		return removeWithCheckAndNotify(premises, index, observers, ProofEnvironmentObserver::premiseRemoved);
	}

	public Formula[] getPremises() {
		return listToArray(premises, Formula[]::new);
	}

	/**
	 * Starts a new conclusion process in the environment.
	 *
	 * @param startTerm The initial term of the conclusion (must not be null)
	 */
	public void addConclusion(Term startTerm) {
		assert startTerm != null;
		ConclusionProcess conclusion = new ConclusionProcess(this, startTerm);
		conclusions.add(conclusion);
		notifyObservers(observers, ProofEnvironmentObserver::conclusionStarted, conclusion);
	}

	/**
	 * Removes a conclusion process from the environment.
	 *
	 * @param conclusion The conclusion to remove (must not be null)
	 * @return True if it was actually removed, false otherwise
	 *
	 * Any conclusions which are based upon the removed conclusion become invalid.
	 * @throws DependencyRemovalException If any transformation in the environment depends on the conclusion
	 */
	public boolean removeConclusion(ConclusionProcess conclusion) throws DependencyRemovalException {
		assert conclusion != null;
		if (analyst.hasDerivatives(conclusion))
			throw new DependencyRemovalException();
		return removeWithCheckAndNotify(conclusions, conclusion, observers, ProofEnvironmentObserver::conclusionRemoved);
	}

	/**
	 * Removes a conclusion process from the environment.
	 *
	 * @param index The index of the conclusion to remove
	 * @return True if it was actually removed, false otherwise
	 *
	 * Any conclusions which are based upon the removed conclusion become invalid.
	 * @throws DependencyRemovalException If any transformation in the environment depends on the conclusion
	 */
	public boolean removeConclusion(int index) throws DependencyRemovalException {
		if (analyst.hasDerivatives(conclusions.get(index)))
			throw new DependencyRemovalException();
		return removeWithCheckAndNotify(conclusions, index, observers, ProofEnvironmentObserver::conclusionRemoved);
	}

	/**
	 * Changes a conclusion process' position within the environment.
	 *
	 * @param conclusion The conclusion to move
	 * @param newIndex The new index of the conclusion
	 *
	 * @return true if successful, false otherwise
	 */
	public boolean moveConclusion(ConclusionProcess conclusion, int newIndex) {
		if (0 > newIndex || newIndex > conclusions.size())
			return false;
		if (!conclusions.remove(conclusion))
			return false;
		conclusions.add(newIndex, conclusion);
		notifyObservers(observers, ProofEnvironmentObserver::conclusionMoved, conclusion);
		return true;
	}

	/**
	 * Changes a conclusion process' position within the environment.
	 *
	 * @param oldIndex The current index of the conclusion
	 * @param newIndex The new index of the conclusion
	 *
	 * @return true if successful, false otherwise
	 */
	public boolean moveConclusion(int oldIndex, int newIndex) {
		if (0 > oldIndex || oldIndex >= conclusions.size())
			return false;
		return moveConclusion(conclusions.get(oldIndex), newIndex);
	}

	public ConclusionProcess[] getConclusions() {
		return listToArray(conclusions, ConclusionProcess[]::new);
	}

	/**
	 * Adds an observer to the environment. Observers are notified upon changes.
	 *
	 * @param observer The new observer
	 */
	public void addObserver(ProofEnvironmentObserver observer) {
		observers.add(observer);
	}

	/**
	 * Unregisters an observer from the environment, so it is no longer notified of changes.
	 *
	 * @param observer The observer to remove
	 */
	public void deleteObserver(ProofEnvironmentObserver observer) {
		observers.remove(observer);
	}
}
