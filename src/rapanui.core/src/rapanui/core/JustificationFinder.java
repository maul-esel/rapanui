package rapanui.core;

import rapanui.dsl.Formula;

/**
 * Finds justifications for formulas matching given @see FormulaTemplate instances.
 */
public interface JustificationFinder {
	/**
	 * Asynchronously finds justifications that match the template and emits them via the returned emitter.
	 *
	 * @param environment The environment which can be used for justifications. Guaranteed to be non-null.
	 * @param formulaTemplate Describes the kind of formulas that should be justified. Guaranteed to be non-null.
	 * @param recursionDepth The maximum number of recursion levels. Guaranteed to be non-null.
	 *
	 * @return An emitter that emits a new justification whenever it finds one. Must not be null.
	 */
	Emitter<Justification> justifyAsync(ProofEnvironment environment, Formula formulaTemplate, int recursionDepth);
}
