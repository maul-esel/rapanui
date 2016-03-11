package rapanui.core;

import java.util.function.Consumer;

import rapanui.dsl.*;

/**
 * A {@link JustificationFinder} implementation that searches the environment's premises.
 */
public class PremiseJustificationFinder implements JustificationFinder {
	@Override
	public Emitter<Justification> justifyAsync(ProofEnvironment environment, Formula formulaTemplate, int recursionDepth) {
		return Emitter.fromResultComputation(acceptor -> searchPremises(environment, formulaTemplate, acceptor));
	}

	/**
	 * Internal method that does the actual search (called asynchronously).
	 */
	protected void searchPremises(ProofEnvironment environment, Formula formulaTemplate, Consumer<Justification> acceptor) {
		for (Formula premise : environment.getResolvedPremises()) {
			if (formulaTemplate.isTemplateFor(premise))
				acceptor.accept(new EnvironmentPremiseJustification(premise));
			if (premise.getFormulaType() == BINARY_RELATION.EQUATION) {
				Formula reversedPremise = Builder.reverse(premise);
				if (formulaTemplate.isTemplateFor(reversedPremise))
					acceptor.accept(new EnvironmentPremiseJustification(reversedPremise));
			}
		}
	}
}
