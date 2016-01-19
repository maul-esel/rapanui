package rapanui.core;

import rapanui.dsl.Term;

/**
 * Represents a search request for @see Justification instances matching certain criteria.
 * This is passed to @see JustificationFinder instances.
 */
public class JustificationRequest {
	private final Term left;
	private final FormulaType type;
	private final Term right;

	/**
	 * Creates a new instance. All parameters may be null to specify no criterion.
	 *
	 * @param left The left-hand side of the formulas to be found and justified.
	 * @param type The type of the formulas to be found and justified.
	 * @param right The right-hand side of the formulas to be found and justified.
	 */
	public JustificationRequest(Term left, FormulaType type, Term right) {
		this.left = left;
		this.type = type;
		this.right = right;
	}

	/**
	 * @return The requested left-hand side. May be null.
	 */
	public Term getLeft() {
		return left;
	}

	/**
	 * @return The requested formula type. May be null.
	 */
	public FormulaType getType() {
		return type;
	}

	/**
	 * @return The requested right-hand side. May be null.
	 */
	public Term getRight() {
		return right;
	}
}
