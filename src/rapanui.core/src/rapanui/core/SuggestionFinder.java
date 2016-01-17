package rapanui.core;

import rapanui.dsl.*;

public class SuggestionFinder implements JustificationFinder {
	public Emitter<Transformation> makeSuggestionsAsync(ConclusionProcess target, FormulaType suggestionType) {
		JustificationRequest request = new JustificationRequest(target.getLastTerm(), suggestionType, null);
		return justifyAsync(request).map(justification -> {
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
		});
	}

	@Override
	public Emitter<Justification> justifyAsync(JustificationRequest request) {
		return Emitter.combine(/* TODO: sub-strategies */);
	}
}
