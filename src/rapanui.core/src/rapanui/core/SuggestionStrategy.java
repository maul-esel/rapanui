package rapanui.core;

/**
 * Suggests possible transformations applicable to a term.
 */
public interface SuggestionStrategy {
	/**
	 * Asynchronously generates suggestions and emits them via the returned emitter.
	 *
	 * @param target The conclusion to which the suggestions should be applicable, i.e. they should
	 * 	transform the conclusion's last term, based on the conclusion's environment information.
	 * @param suggestionType The type of suggestion to make, i.e. either only transformations
	 * 	into equal terms, or both equal and greater terms.
	 *
	 * @return An emitter that emits new suggestions whenever it finds one.
	 */
	Emitter<Transformation> makeSuggestionsAsync(ConclusionProcess target, FormulaType suggestionType);
}
