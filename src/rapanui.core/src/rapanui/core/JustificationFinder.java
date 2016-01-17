package rapanui.core;

/**
 * Finds justifications for given justification requests.
 */
public interface JustificationFinder {
	/**
	 * Asynchronously finds justifications that match the request and emits them via the returned emitter.
	 *
	 * @param environment The environment which can be used for justifications
	 * @param request Describes the kind of formulas that should be justified
	 * @param recursionDepth The maximum number of recursion levels
	 *
	 * @return An emitter that emits a new justification whenever it finds one.
	 */
	Emitter<Justification> justifyAsync(ProofEnvironment environment, JustificationRequest request, int recursionDepth);
}
