package rapanui.core;

import rapanui.dsl.Term;
import rapanui.dsl.BINARY_RELATION;

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
	private final BINARY_RELATION formulaType;

	/**
	 * Creates a new transformation from the given parameters.
	 *
	 * @param input The original term which is transformed
	 * @param output The result of the transformation
	 * @param formulaType The type of transformation
	 * @param justification A justification why this transformation is allowed
	 */
	Transformation(ConclusionProcess container, Term input, Term output, BINARY_RELATION formulaType, Justification justification) {
		// TODO: validate parameters? Or rely on valid input?

		assert container != null;
		assert input != null;
		assert output != null;
		assert justification != null;

		assert input.structurallyEquals(container.getLastTerm());

		this.container = container;
		this.input = input;
		this.output = output;
		this.formulaType = formulaType;
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

	public BINARY_RELATION getFormulaType() {
		return formulaType;
	}

	public Justification getJustification() {
		return justification;
	}
}
