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
			if (formulaTemplate.isTemplateFor(premise))
				acceptor.accept(new EnvironmentPremiseJustification(premise));
			else if (premise instanceof Equation && formulaTemplate.isTemplateFor(Builder.reverse((Equation)premise)))
				acceptor.accept(new EnvironmentPremiseJustification(premise));
		}
	}
}
