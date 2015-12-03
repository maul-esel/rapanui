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
	}

	public RuleSystem[] getRuleSystems() {
		return ruleSystems;
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
