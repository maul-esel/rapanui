package rapanui.core;

import java.util.Arrays;

import rapanui.dsl.*;

public class SuggestionFinder {
	private final JustificationFinder justificationFinder;

	/* Limit recursion depth to 1 to achieve acceptable performance.
	 *
	 * TODO: Alternatively, avoid recursive rule application searches
	 * by modifying getDefaultInstance() to pass another AggregateJustificationFinder
	 * (which only includes the other algorithms) to the new RuleApplicationFinder.
	 */
	private static final int MAX_RECURSION = 1;

	private static SuggestionFinder defaultInstance;

	public SuggestionFinder(JustificationFinder justificationFinder) {
		assert justificationFinder != null;
		this.justificationFinder = justificationFinder;
	}

	public static SuggestionFinder getDefaultInstance() {
		if (defaultInstance == null) {
			AggregateJustificationFinder finder = new AggregateJustificationFinder(Arrays.asList(
				new PremiseJustificationFinder(),
				new ProofJustificationFinder()
			));
			finder.addJustificationFinder(new SubtermEqualityJustificationFinder(finder));
			finder.addJustificationFinder(new RuleApplicationFinder(finder));
			defaultInstance = new SuggestionFinder(finder);
		}
		return defaultInstance;
	}

	public Emitter<Transformation> makeSuggestionsAsync(ConclusionProcess target, BINARY_RELATION suggestionType,
			boolean prependSuggestion) {
		Formula formulaTemplate;
		if (prependSuggestion)
			formulaTemplate = Builder.createFormula(null, suggestionType, target.getFirstTerm());
		else
			formulaTemplate = Builder.createFormula(target.getLastTerm(), suggestionType, null);
		return justificationFinder.justifyAsync(target.getEnvironment(), formulaTemplate, MAX_RECURSION)
			.map(justification -> createTransformation(target, justification));
	}

	protected Transformation createTransformation(ConclusionProcess target, Justification justification) {
		Formula formula = justification.getJustifiedFormula();
		return new Transformation(target,
				formula.getLeft(),
				formula.getRight(),
				formula.getFormulaType(),
				justification);
	}
}
