package rapanui.core;

import java.util.function.Consumer;

import rapanui.dsl.*;

/**
 * A @see JustificationFinder implementation that searches the environment's premises.
 */
public class PremiseJustificationFinder implements JustificationFinder {
	@Override
	public Emitter<Justification> justifyAsync(ProofEnvironment environment, JustificationRequest request,
			int recursionDepth) {
		return Emitter.fromResultComputation(acceptor -> searchPremises(environment, request, acceptor));
	}

	/**
	 * Internal method that does the actual search (called asynchronously).
	 */
	protected void searchPremises(ProofEnvironment environment, JustificationRequest request, Consumer<Justification> acceptor) {
		for (Formula premise : environment.getPremises()) { // TODO: resolved premises
			if (matchesRequest(request, premise))
				acceptor.accept(new EnvironmentPremiseJustification(premise));
			else if (premise instanceof Equation && matchesRequest(request, Builder.reverse((Equation)premise)))
				acceptor.accept(new EnvironmentPremiseJustification(premise));
		}
	}

	/**
	 * Helper method to determine if a formula matches a given @see JustificationRequest.
	 */
	private boolean matchesRequest(JustificationRequest request, Formula premise) {
		if (premise instanceof Inclusion) {
			Inclusion inclusion = (Inclusion) premise;
			return (request.getType() == FormulaType.Inclusion || request.getType() == null)
					&& (request.getLeft() == null || request.getLeft().structurallyEquals(inclusion.getLeft()))
					&& (request.getRight() == null || request.getRight().structurallyEquals(inclusion.getRight()));
		} else if (premise instanceof Equation) {
			Equation equation = (Equation) premise;
			return (request.getType() == null || request.getType() == FormulaType.Equality)
					&& (request.getLeft() == null || request.getLeft().structurallyEquals(equation.getLeft()))
					&& (request.getRight() == null || request.getRight().structurallyEquals(equation.getRight()));
		}
		return false;
	}
}