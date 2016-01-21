package rapanui.core;

import rapanui.dsl.Equation;
import rapanui.dsl.Formula;
import rapanui.dsl.Inclusion;
import rapanui.dsl.Term;

/**
 * Represents a formula with some variable parts, namely the left-hand term,
 * the right-hand term or the formula type (equation or inclusion).
 */
public class FormulaTemplate {
	private final Term leftTerm;
	private final FormulaType formulaType;
	private final Term rightTerm;

	/**
	 * Creates a new instance. All parameters may be null.
	 *
	 * @param leftTerm The left-hand side of the formula.
	 * @param type The type of the formula.
	 * @param rightTerm The right-hand side of the formula.
	 */
	public FormulaTemplate(Term leftTerm, FormulaType formulaType, Term rightTerm) {
		this.leftTerm = leftTerm;
		this.formulaType = formulaType;
		this.rightTerm = rightTerm;
	}

	/**
	 * @return The formula's left-hand side. May be null.
	 */
	public Term getLeftTerm() {
		return leftTerm;
	}

	/**
	 * @return If the left-hand term is specified
	 */
	public boolean hasLeftTerm() {
		return leftTerm != null;
	}

	/**
	 * @return The formula type. May be null.
	 */
	public FormulaType getFormulaType() {
		return formulaType;
	}

	/**
	 * @return If the formual's type is specified
	 */
	public boolean hasFormulaType() {
		return formulaType != null;
	}

	/**
	 * @return The formula's right-hand side. May be null.
	 */
	public Term getRightTerm() {
		return rightTerm;
	}

	/**
	 * @return If the right-hand term is specified
	 */
	public boolean hasRightTerm() {
		return rightTerm != null;
	}

	/**
	 * @return True if the given formula matches the template, false otherwise.
	 */
	public boolean isTemplateFor(Formula instance) {
		Term left, right;
		FormulaType type;

		if (instance instanceof Inclusion) {
			left = ((Inclusion)instance).getLeft();
			right = ((Inclusion)instance).getRight();
			type = FormulaType.Inclusion;
		} else if (instance instanceof Equation) {
			left = ((Equation)instance).getLeft();
			right = ((Equation)instance).getRight();
			type = FormulaType.Equality;
		} else
			return false; // TODO: maybe log a warning?

		return (!hasFormulaType() || getFormulaType() == type) && (!hasLeftTerm() || getLeftTerm().structurallyEquals(left))
				&& (!hasRightTerm() || getRightTerm().structurallyEquals(right));
	}
}
