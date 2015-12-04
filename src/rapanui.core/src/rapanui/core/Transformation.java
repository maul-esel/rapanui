package rapanui.core;

import rapanui.dsl.moai.Term;

/**
 * Describes a transformation step from one term to another.
 *
 * This class is immutable.
 *
 */
public class Transformation {
	private final ConclusionProcess container;
	private final Term input;
	private final Term output;
	private final Justification justification;
	private final FormulaType type;

	/**
	 * Creates a new transformation from the given parameters.
	 *
	 * @param input The original term which is transformed
	 * @param output The result of the transformation
	 * @param type The type of transformation
	 * @param justification A justification why this transformation is allowed
	 */
	Transformation(ConclusionProcess container, Term input, Term output, FormulaType type, Justification justification) {
		// TODO: validate parameters? Or rely on valid input?

		assert container != null;
		assert input != null;
		assert output != null;
		assert justification != null;

		assert input.structurallyEquals(container.getLastTerm());

		this.container = container;
		this.input = input;
		this.output = output;
		this.type = type;
		this.justification = justification;
	}

	public ConclusionProcess getContainer() {
		return container;
	}

	public Term getInput() {
		return input;
	}

	public Term getOutput() {
		return output;
	}

	public FormulaType getType() {
		return type;
	}

	public Justification getJustification() {
		return justification;
	}
}
