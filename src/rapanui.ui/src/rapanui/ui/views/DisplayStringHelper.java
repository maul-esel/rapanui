package rapanui.ui.views;

import java.util.Arrays;
import java.util.Objects;

import rapanui.core.*;

class DisplayStringHelper {
	public static String toSymbol(FormulaType formulaType) {
		switch (formulaType) {
		case EQUATION:
			return "=";
		case INCLUSION:
			return "⊆";
		}
		throw new IllegalArgumentException();
	}

	public static String shortDescription(Justification justification) {
		if (justification instanceof EnvironmentPremiseJustification)
			return "nach Voraussetzung";
		else if (justification instanceof RuleApplication)
			return "nach " + ((RuleApplication)justification).getAppliedRule().getName();
		else if (justification instanceof ProofJustification) {
			ConclusionProcess conclusion = ((ProofJustification)justification).getConclusion();
			int conclusionIndex = Arrays.asList(conclusion.getEnvironment().getConclusions()).indexOf(conclusion);
			return "nach Folgerung #" + (conclusionIndex + 1);
		} else if (justification instanceof SubtermEqualityJustification)
			return "weil " + ((SubtermEqualityJustification) justification).getOriginalSubTerm().serialize()
					+ " = "
					+ ((SubtermEqualityJustification) justification).getNewSubTerm().serialize();
		return Objects.toString(justification);
	}
}
