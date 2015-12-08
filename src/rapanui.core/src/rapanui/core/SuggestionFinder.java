package rapanui.core;

public class SuggestionFinder implements SuggestionStrategy {
	@Override
	public Emitter<Transformation> makeSuggestionsAsync(ConclusionProcess target, FormulaType suggestionType) {
		return Emitter.combine(/* TODO: sub-strategies */);
	}
}
