package rapanui.core;

import java.util.ArrayList;
import java.util.List;

import rapanui.dsl.moai.Formula;
import rapanui.dsl.moai.RuleSystem;
import rapanui.dsl.moai.Term;

import static rapanui.core.Patterns.*;

public class ProofEnvironment {
	private final RuleSystem[] ruleSystems;
	private final List<Formula> premises;
	private final List<ConclusionProcess> conclusions;
	private final List<ProofEnvironmentObserver> observers;

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

	public void addPremise(Formula premise) {
		assert premise != null;
		if (addToSet(premises, premise))
			notifyObservers(observers, ProofEnvironmentObserver::premiseAdded, premise);
	}

	public boolean removePremise(Formula premise) {
		// TODO: check dependencies
		assert premise != null;
		return removeWithCheckAndNotify(premises, premise, observers, ProofEnvironmentObserver::premiseRemoved);
	}

	public boolean removePremise(int index) {
		// TODO: check dependencies
		return removeWithCheckAndNotify(premises, index, observers, ProofEnvironmentObserver::premiseRemoved);
	}

	public Formula[] getPremises() {
		return listToArray(premises, Formula[]::new);
	}

	public void addConclusion(Term startTerm) {
		assert startTerm != null;
		ConclusionProcess conclusion = new ConclusionProcess(this, startTerm);
		conclusions.add(conclusion);
		notifyObservers(observers, ProofEnvironmentObserver::conclusionStarted, conclusion);
	}

	public boolean removeConclusion(ConclusionProcess conclusion) {
		assert conclusion != null;
		return removeWithCheckAndNotify(conclusions, conclusion, observers, ProofEnvironmentObserver::conclusionRemoved);
	}

	public boolean removeConclusion(int index) {
		return removeWithCheckAndNotify(conclusions, index, observers, ProofEnvironmentObserver::conclusionRemoved);
	}

	public ConclusionProcess[] getConclusions() {
		return listToArray(conclusions, ConclusionProcess[]::new);
	}

	public void addObserver(ProofEnvironmentObserver observer) {
		observers.add(observer);
	}

	public void deleteObserver(ProofEnvironmentObserver observer) {
		observers.remove(observer);
	}
}
