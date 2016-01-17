package rapanui.core;

/**
 * Finds justifications for given justification requests.
 */
public interface JustificationFinder {
	/**
	 * Asynchronously finds justifications that match the request and emits them via the returned emitter.
	 *
	 * @param request Describes the kind of formulas that should be justified
	 *
	 * @return An emitter that emits a new justification whenever it finds one.
	 */
	Emitter<Justification> justifyAsync(JustificationRequest request);
}
