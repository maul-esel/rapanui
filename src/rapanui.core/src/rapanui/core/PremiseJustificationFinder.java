package rapanui.core;

import java.util.function.Consumer;

import rapanui.dsl.*;

/**
 * A @see JustificationFinder implementation that searches the environment's premises.
 */
public class PremiseJustificationFinder implements JustificationFinder {
	@Override
	public Emitter<Justification> justifyAsync(ProofEnvironment environment, FormulaTemplate formulaTemplate,
			int recursionDepth) {
		return Emitter.fromResultComputation(acceptor -> searchPremises(environment, formulaTemplate, acceptor));
	}

	/**
	 * Internal method that does the actual search (called asynchronously).
	 */
	protected void searchPremises(ProofEnvironment environment, FormulaTemplate formulaTemplate, Consumer<Justification> acceptor) {
		for (Formula premise : environment.getPremises()) { // TODO: resolved premises
			if (matchesTemplate(formulaTemplate, premise))
				acceptor.accept(new EnvironmentPremiseJustification(premise));
			else if (premise instanceof Equation && matchesTemplate(formulaTemplate, Builder.reverse((Equation)premise)))
				acceptor.accept(new EnvironmentPremiseJustification(premise));
		}
	}

	/**
	 * Helper method to determine if a formula matches a given @see FormulaTemplate.
	 */
	private boolean matchesTemplate(FormulaTemplate template, Formula formula) {
		if (formula instanceof Inclusion) {
			Inclusion inclusion = (Inclusion) formula;
			return (!template.hasFormulaType() || template.getFormulaType() == FormulaType.Inclusion)
					&& (!template.hasLeftTerm() || template.getLeftTerm().structurallyEquals(inclusion.getLeft()))
					&& (!template.hasRightTerm() || template.getRightTerm().structurallyEquals(inclusion.getRight()));
		} else if (formula instanceof Equation) {
			Equation equation = (Equation) formula;
			return (!template.hasFormulaType() || template.getFormulaType() == FormulaType.Equality)
					&& (!template.hasLeftTerm() || template.getLeftTerm().structurallyEquals(equation.getLeft()))
					&& (!template.hasRightTerm() || template.getRightTerm().structurallyEquals(equation.getRight()));
		}
		return false;
	}
}
