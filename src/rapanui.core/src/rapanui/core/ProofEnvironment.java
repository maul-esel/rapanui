package rapanui.core;

import java.util.ArrayList;
import java.util.List;

import rapanui.dsl.Formula;
import rapanui.dsl.RuleSystemCollection;
import rapanui.dsl.Term;

import static rapanui.core.Patterns.*;

/**
 * The basic environment in which proofs are made. Contains a list of premises
 * and the resulting conclusions.
 */
public class ProofEnvironment {
	private final RuleSystemCollection ruleSystems;
	private final List<Formula> premises;
	private final List<ConclusionProcess> conclusions;
	private final List<Observer> observers;

	/**
	 * Create a new environment.
	 *
	 * @param ruleSystems The rule systems upon which any conclusions in the environment are based. Mmust not be null.
	 */
	public ProofEnvironment(RuleSystemCollection ruleSystems) {
		assert ruleSystems != null;

		this.ruleSystems = ruleSystems;
		this.premises = new ArrayList<Formula>();
		this.conclusions = new ArrayList<ConclusionProcess>();
		this.observers = new ArrayList<Observer>();
	}

	public RuleSystemCollection getRuleSystems() {
		return ruleSystems;
	}

	/**
	 * Add a new premise to the environment so it can be used in conclusions.
	 *
	 * @param premise The formula to add as premise. Must not be null.
	 *
	 * If the premise already exists in the environment, it will not be added again.
	 */
	public void addPremise(Formula premise) {
		assert premise != null;
		if (addToSet(premises, premise))
			notifyObservers(observers, Observer::premiseAdded, premise);
	}

	/**
	 * @return An array of premises. Guaranteed to be non-null.
	 */
	public Formula[] getPremises() {
		return listToArray(premises, Formula[]::new);
	}

	/**
	 * Starts a new conclusion process in the environment.
	 *
	 * @param startTerm The initial term of the conclusion. Must not be null.
	 */
	public void addConclusion(Term startTerm) {
		assert startTerm != null;
		ConclusionProcess conclusion = new ConclusionProcess(this, startTerm);
		conclusions.add(conclusion);
		notifyObservers(observers, Observer::conclusionStarted, conclusion);
	}

	public ConclusionProcess[] getConclusions() {
		return listToArray(conclusions, ConclusionProcess[]::new);
	}

	public interface Observer {
		void premiseAdded(Formula premise);
		void premiseRemoved(Formula premise); // unused for now

		void conclusionStarted(ConclusionProcess conclusion);
		void conclusionRemoved(ConclusionProcess conclusion); // unused for now
		void conclusionMoved(ConclusionProcess conclusion); // unused for now
	}

	/**
	 * Adds an observer to the environment. Observers are notified upon changes.
	 *
	 * @param observer The new observer
	 */
	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	/**
	 * Unregisters an observer from the environment, so it is no longer notified of changes.
	 *
	 * @param observer The observer to remove
	 */
	public void deleteObserver(Observer observer) {
		observers.remove(observer);
	}
}
