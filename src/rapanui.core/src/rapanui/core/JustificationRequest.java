package rapanui.core;

import rapanui.dsl.Term;

public class JustificationRequest {
	private final Term left;
	private final FormulaType type;
	private final Term right;

	public JustificationRequest(Term left, FormulaType type, Term right) {
		this.left = left;
		this.type = type;
		this.right = right;
	}

	public Term getLeft() {
		return left;
	}

	public FormulaType getType() {
		return type;
	}

	public Term getRight() {
		return right;
	}
}
