package rapanui.core;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import rapanui.dsl.Builder;
import rapanui.dsl.Formula;
import rapanui.dsl.Term;
import rapanui.dsl.BINARY_RELATION;

/**
 * A {@link JustificationFinder} that searches previous proofs conducted by the user in the same environment.
 */
public class ProofJustificationFinder implements JustificationFinder {
	@Override
	public Emitter<Justification> justifyAsync(ProofEnvironment environment, Formula formulaTemplate, int recursionDepth) {
		return Emitter.fromResultComputation(acceptor -> searchProofs(acceptor, environment, formulaTemplate));
	}

	@FunctionalInterface private interface EmitHelper { void accept(ConclusionProcess conclusion, Term[] terms, int left, int right); }

	/**
	 * Depending on which terms are specified in the template, this method selects one of four helper methods
	 * to actually extract justifications from a given conclusion. Code common to all these helper methods,
	 * namely the creation of {@link ProofJustification} instances, is encapsulated in the "emit" helper function.
	 *
	 * The method then iterates through all conclusions, and passes their data to the selected helper method
	 * along with a reference to the emit helper function.
	 */
	private void searchProofs(Consumer<Justification> acceptor, ProofEnvironment environment, Formula formulaTemplate) {
		final EmitHelper emit = (conclusion, terms, leftIndex, rightIndex) -> {
			int start = Math.min(leftIndex, rightIndex), end = Math.max(leftIndex, rightIndex);
			BINARY_RELATION type = conclusion.getFormulaType(start, end);

			if (formulaTemplate.getFormulaType() != null && type != formulaTemplate.getFormulaType()) {
				if (formulaTemplate.getFormulaType() == BINARY_RELATION.EQUATION)
					return; // equation needed, but only has inclusion ~> invalid result
				type = BINARY_RELATION.INCLUSION; // inclusion needed, even has equation ~> works
			}

			Formula formula = Builder.createFormula(terms[leftIndex], type, terms[rightIndex]);
			acceptor.accept(new ProofJustification(formula, conclusion, start, end));
		};

		final BiConsumer<ConclusionProcess, Term[]> worker;
		if (formulaTemplate.getLeft() == null && formulaTemplate.getRight() == null)
			worker = (conclusion, terms) -> searchProofsWithoutTerms(conclusion, terms, emit);
		else if (formulaTemplate.getLeft() != null && formulaTemplate.getRight() != null)
			worker = (conclusion, terms) -> searchProofsWithTwoTerms(conclusion, terms, formulaTemplate.getLeft(), formulaTemplate.getRight(), emit);
		else if (formulaTemplate.getLeft() != null)
			worker = (conclusion, terms) -> searchProofsWithLeftTerm(conclusion, terms, formulaTemplate.getLeft(), emit);
		else
			worker = (conclusion, terms) -> searchProofsWithRightTerm(conclusion, terms, formulaTemplate.getRight(), emit);

		for (final ConclusionProcess conclusion : environment.getConclusions()) {
			final Term[] terms = conclusion.getTerms();
			worker.accept(conclusion, terms);
		}
	}

	private void searchProofsWithoutTerms(ConclusionProcess conclusion, Term[] terms, EmitHelper emit) {
		// simply emit all pairs of terms (ordered)
		for (int i = 0; i < terms.length; ++i)
			for (int j = i+1; j < terms.length; ++j)
				emit.accept(conclusion, terms, i, j);
	}

	private void searchProofsWithTwoTerms(ConclusionProcess conclusion, Term[] terms, Term leftTermTemplate, Term rightTermTemplate, EmitHelper emit) {
		// locate both specified terms
		int indexLeft = -1, indexRight = -1;

		for (int i = 0; i < terms.length; ++i) {
			if (leftTermTemplate.isTemplateFor(terms[i]))
				indexLeft = i;
			else if (rightTermTemplate.isTemplateFor(terms[i]))
				indexRight = i;
		}
		if (indexRight < 0 || indexLeft < 0)
			return;

		// if the terms are in order (inclusion or equation) or equal, emit them
		if (indexLeft < indexRight)
			emit.accept(conclusion, terms, indexLeft, indexRight);
		else if (indexLeft > indexRight && conclusion.getFormulaType(indexRight, indexLeft) == BINARY_RELATION.EQUATION)
			emit.accept(conclusion, terms, indexLeft, indexRight);
	}

	private void searchProofsWithLeftTerm(ConclusionProcess conclusion, Term[] terms, Term leftTermTemplate, EmitHelper emit) {
		// locate term in conclusion
		int indexLeft = -1;
		for (int i = 0; i < terms.length && indexLeft < 0; ++i) {
			if (leftTermTemplate.isTemplateFor(terms[i]))
				indexLeft = i;
		}
		if (indexLeft < 0)
			return;

		// emit all terms to the right of the given term
		for (int j = indexLeft + 1; j < terms.length; ++j)
			emit.accept(conclusion, terms, indexLeft,  j);
		// emit also those that are left of it, but are equal (equality is symmetric)
		for (int j = indexLeft - 1; j >= 0 && conclusion.getFormulaType(j, indexLeft) == BINARY_RELATION.EQUATION; --j)
			emit.accept(conclusion, terms, indexLeft, j);
	}

	private void searchProofsWithRightTerm(ConclusionProcess conclusion, Term[] terms, Term rightTermTemplate, EmitHelper emit) {
		// locate term in conclusion
		int indexRight = -1;
		for (int i = 0; i < terms.length && indexRight < 0; ++i) {
			if (rightTermTemplate.isTemplateFor(terms[i]))
				indexRight = i;
		}
		if (indexRight < 0)
			return;

		// emit all terms to the left of the given term
		for (int j = 0; j < indexRight; ++j)
			emit.accept(conclusion, terms, j, indexRight);
		// emit also those that are right of it, but are equal
		for (int j = indexRight + 1; j < terms.length && conclusion.getFormulaType(indexRight, j) == BINARY_RELATION.EQUATION; ++j)
			emit.accept(conclusion, terms, j, indexRight);
	}
}
