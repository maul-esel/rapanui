package rapanui.core;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import rapanui.dsl.Builder;
import rapanui.dsl.Formula;
import rapanui.dsl.Term;

/**
 * A @see JustificationFinder that searches previous proofs conducted by the user in the same environment.
 */
public class ProofJustificationFinder implements JustificationFinder {
	@Override
	public Emitter<Justification> justifyAsync(ProofEnvironment environment, FormulaTemplate formulaTemplate,
			int recursionDepth) {
		return Emitter.fromResultComputation(acceptor -> searchProofs(acceptor, environment, formulaTemplate));
	}

	@FunctionalInterface private interface EmitHelper { void accept(ConclusionProcess conclusion, Term[] terms, int left, int right); }

	/*
	 * Depending on which terms are specified in the template, this method selects one of four helper methods
	 * to actually extract justifications from a given conclusion. Code common to all these helper methods,
	 * namely the creation of @see ProofJustification instances, is encapsulated in the "emit" helper function.
	 *
	 * The method then iterates through all conclusions, and passes their data to the selected helper method
	 * along with a reference to the emit helper function.
	 */
	private void searchProofs(Consumer<Justification> acceptor, ProofEnvironment environment, FormulaTemplate formulaTemplate) {
		final EmitHelper emit = (conclusion, terms, leftIndex, rightIndex) -> {
			int start = Math.min(leftIndex, rightIndex), end = Math.max(leftIndex, rightIndex);
			FormulaType type = conclusion.getFormulaType(start, end);

			if (formulaTemplate.hasFormulaType() && type != formulaTemplate.getFormulaType()) {
				if (formulaTemplate.getFormulaType() == FormulaType.EQUATION)
					return; // equation needed, but only has inclusion ~> invalid result
				type = FormulaType.INCLUSION; // inclusion needed, even has equation ~> works
			}

			Formula formula = null;
			if (type == FormulaType.EQUATION)
				formula = Builder.createEquation(terms[leftIndex], terms[rightIndex]);
			else if (type == FormulaType.INCLUSION)
				formula = Builder.createInclusion(terms[leftIndex], terms[rightIndex]);

			acceptor.accept(new ProofJustification(formula, conclusion, start, end));
		};

		final BiConsumer<ConclusionProcess, Term[]> worker;
		if (!formulaTemplate.hasLeftTerm() && !formulaTemplate.hasRightTerm())
			worker = (conclusion, terms) -> searchProofsWithoutTerms(conclusion, terms, emit);
		else if (formulaTemplate.hasLeftTerm() && formulaTemplate.hasRightTerm())
			worker = (conclusion, terms) -> searchProofsWithTwoTerms(conclusion, terms, formulaTemplate.getLeftTerm(), formulaTemplate.getRightTerm(), emit);
		else if (formulaTemplate.hasLeftTerm())
			worker = (conclusion, terms) -> searchProofsWithLeftTerm(conclusion, terms, formulaTemplate.getLeftTerm(), emit);
		else
			worker = (conclusion, terms) -> searchProofsWithRightTerm(conclusion, terms, formulaTemplate.getRightTerm(), emit);

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

	private void searchProofsWithTwoTerms(ConclusionProcess conclusion, Term[] terms, Term leftTerm, Term rightTerm, EmitHelper emit) {
		// locate both specified terms
		int indexLeft = -1, indexRight = -1;

		for (int i = 0; i < terms.length; ++i) {
			if (terms[i].structurallyEquals(leftTerm))
				indexLeft = i;
			else if (terms[i].structurallyEquals(rightTerm))
				indexRight = i;
		}
		if (indexRight < 0 || indexLeft < 0)
			return;

		// if the terms are in order (inclusion or equation) or equal, emit them
		if (indexLeft < indexRight)
			emit.accept(conclusion, terms, indexLeft, indexRight);
		else if (indexLeft > indexRight && conclusion.getFormulaType(indexRight, indexLeft) == FormulaType.EQUATION)
			emit.accept(conclusion, terms, indexLeft, indexRight);
	}

	private void searchProofsWithLeftTerm(ConclusionProcess conclusion, Term[] terms, Term leftTerm, EmitHelper emit) {
		// locate term in conclusion
		int indexLeft = -1;
		for (int i = 0; i < terms.length && indexLeft < 0; ++i) {
			if (terms[i].structurallyEquals(leftTerm))
				indexLeft = i;
		}
		if (indexLeft < 0)
			return;

		// emit all terms to the right of the given term
		for (int j = indexLeft + 1; j < terms.length; ++j)
			emit.accept(conclusion, terms, indexLeft,  j);
		// emit also those that are left of it, but are equal (equality is symmetric)
		for (int j = indexLeft - 1; j > 0 && conclusion.getFormulaType(j, indexLeft) == FormulaType.EQUATION; --j)
			emit.accept(conclusion, terms, indexLeft, j);
	}

	private void searchProofsWithRightTerm(ConclusionProcess conclusion, Term[] terms, Term rightTerm, EmitHelper emit) {
		// locate term in conclusion
		int indexRight = -1;
		for (int i = 0; i < terms.length && indexRight < 0; ++i) {
			if (terms[i].structurallyEquals(rightTerm))
				indexRight = i;
		}
		if (indexRight < 0)
			return;

		// emit all terms to the left of the given term
		for (int j = 0; j < indexRight; ++j)
			emit.accept(conclusion, terms, j, indexRight);
		// emit also those that are right of it, but are equal
		for (int j = indexRight + 1; j < terms.length && conclusion.getFormulaType(indexRight, j) == FormulaType.EQUATION; ++j)
			emit.accept(conclusion, terms, j, indexRight);
	}
}
