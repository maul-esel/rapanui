package rapanui.core;

import java.util.Arrays;

import rapanui.dsl.*;

public class SuggestionFinder {
	private final JustificationFinder justificationFinder;
	private static final int MAX_RECURSION = 2;

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
			defaultInstance = new SuggestionFinder(finder);
		}
		return defaultInstance;
	}

	public Emitter<Transformation> makeSuggestionsAsync(ConclusionProcess target, FormulaType suggestionType) {
		FormulaTemplate template = new FormulaTemplate(target.getLastTerm(), suggestionType, null);
		return justificationFinder.justifyAsync(target.getEnvironment(), template, MAX_RECURSION)
			.map(justification -> createTransformation(target, justification));
	}

	protected Transformation createTransformation(ConclusionProcess target, Justification justification) {
		Formula formula = justification.getJustifiedFormula();
		Term left = formula.getLeft(), right = formula.getRight();
		FormulaType type;
		if (formula.getFormulaType() == BINARY_RELATION.EQUATION) {
			type = FormulaType.EQUATION;
		} else if (formula.getFormulaType() == BINARY_RELATION.INCLUSION) {
			type = FormulaType.INCLUSION;
		} else {
			throw new IllegalStateException("Unsupported formula type in justification");
		}

		return new Transformation(target, left, right, type, justification);
	}
}
