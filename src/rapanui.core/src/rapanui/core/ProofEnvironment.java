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

	public ProofEnvironment(RuleSystem[] ruleSystems) {
		assert ruleSystems != null;

		this.ruleSystems = ruleSystems;
		this.premises = new ArrayList<Formula>();
		this.conclusions = new ArrayList<ConclusionProcess>();
	}

	public RuleSystem[] getRuleSystems() {
		return ruleSystems;
	}

	public void addPremise(Formula premise) {
		assert premise != null;
		addToSet(premises, premise);
	}

	public boolean removePremise(Formula premise) {
		// TODO: check dependencies
		assert premise != null;
		return removeWithCheck(premises, premise);
	}

	public boolean removePremise(int index) {
		// TODO: check dependencies
		return removeWithCheck(premises, index);
	}

	public Formula[] getPremises() {
		return listToArray(premises, Formula[]::new);
	}

	public void addConclusion(Term startTerm) {
		assert startTerm != null;
		conclusions.add(new ConclusionProcess(this, startTerm));
	}

	public boolean removeConclusion(ConclusionProcess conclusion) {
		assert conclusion != null;
		return removeWithCheck(conclusions, conclusion);
	}

	public boolean removeConclusion(int index) {
		return removeWithCheck(conclusions, index);
	}

	public ConclusionProcess[] getConclusions() {
		return listToArray(conclusions, ConclusionProcess[]::new);
	}
}
