package rapanui.core;

/**
 * Finds justifications for given justification requests.
 */
public interface JustificationFinder {
	/**
	 * Asynchronously finds justifications that match the request and emits them via the returned emitter.
	 *
	 * @param environment The environment which can be used for justifications. Guaranteed to be non-null.
	 * @param request Describes the kind of formulas that should be justified. Guaranteed to be non-null.
	 * @param recursionDepth The maximum number of recursion levels. Guaranteed to be non-null.
	 *
	 * @return An emitter that emits a new justification whenever it finds one. Must not be null.
	 */
	Emitter<Justification> justifyAsync(ProofEnvironment environment, JustificationRequest request, int recursionDepth);
}
