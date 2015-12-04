package rapanui.core;

import rapanui.dsl.moai.Formula;
import rapanui.dsl.moai.Term;

public class SubtermEqualityJustification extends Justification {
	private final Term originalSubTerm;
	private final Term newSubTerm;
	private final Justification justification;

	public SubtermEqualityJustification(Formula justifiedFormula, Term originalSubTerm, Term newSubTerm, Justification justification) {
		super(justifiedFormula);
		this.originalSubTerm = originalSubTerm;
		this.newSubTerm = newSubTerm;
		this.justification = justification;
	}

	public Term getOriginalSubTerm() {
		return originalSubTerm;
	}

	public Term getNewSubTerm() {
		return newSubTerm;
	}

	public Justification getJustification() {
		return justification;
	}
}
