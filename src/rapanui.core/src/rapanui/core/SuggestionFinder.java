package rapanui.core;

import rapanui.dsl.*;

public class SuggestionFinder {
	private final JustificationFinder justificationFinder;

	public SuggestionFinder(JustificationFinder justificationFinder) {
		assert justificationFinder != null;
		this.justificationFinder = justificationFinder;
	}

	public Emitter<Transformation> makeSuggestionsAsync(ConclusionProcess target, FormulaType suggestionType) {
		JustificationRequest request = new JustificationRequest(target.getLastTerm(), suggestionType, null);
		return justificationFinder.justifyAsync(request)
			.map(justification -> createTransformation(target, justification));
	}

	protected Transformation createTransformation(ConclusionProcess target, Justification justification) {
		Term left, right;
		FormulaType type;

		Formula formula = justification.getJustifiedFormula();
		if (formula instanceof Equation) {
			left = ((Equation)formula).getLeft();
			right = ((Equation)formula).getRight();
			type = FormulaType.Equality;
		} else if (formula instanceof Inclusion) {
			left = ((Inclusion)formula).getLeft();
			right = ((Inclusion)formula).getRight();
			type = FormulaType.Inclusion;
		} else {
			throw new IllegalStateException("Unsupported formula type in justification");
		}

		return new Transformation(target, left, right, type, justification);
	}
}
