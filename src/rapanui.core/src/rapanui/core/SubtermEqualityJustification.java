package rapanui.core;

import rapanui.dsl.moai.Formula;
import rapanui.dsl.moai.Term;

public class SubtermEqualityJustification extends Justification {
	private final Term subTerm;
	private final Justification justification;

	public SubtermEqualityJustification(Formula justifiedFormula, Term subTerm, Justification justification) {
		super(justifiedFormula);
		this.subTerm = subTerm;
		this.justification = justification;
	}

	public Term getSubTerm() {
		return subTerm;
	}

	public Justification getJustification() {
		return justification;
	}
}
