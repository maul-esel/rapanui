package rapanui.core;

import java.util.LinkedList;
import java.util.Stack;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.util.EcoreUtil;

import rapanui.dsl.*;

/**
 * A @see JustificationFinder that searches for subterms which are known to equal something else.
 * If it finds one, it replaces the subterm with the equal term and creates a justification
 * for the equality of the original and the modified term.
 */
public class SubtermEqualityJustificationFinder implements JustificationFinder {
	private final JustificationFinder delegateFinder;

	/**
	 * @param delegateFinder The @see JustificationFinder to use when looking for terms equal to a subterm
	 */
	public SubtermEqualityJustificationFinder(JustificationFinder delegateFinder) {
		assert delegateFinder != null;
		this.delegateFinder = delegateFinder;
	}

	@Override
	public Emitter<Justification> justifyAsync(ProofEnvironment environment, FormulaTemplate formulaTemplate,
			int recursionDepth) {
		if (recursionDepth <= 0 || (formulaTemplate.getLeftTerm() == null && formulaTemplate.getRightTerm() == null)) // needs at least one recursion and one term
			return Emitter.empty();

		return Emitter.fromResultComputation(acceptor -> searchSubtermEqualities(acceptor, environment, formulaTemplate, recursionDepth));
	}

	/**
	 * Internal function that performs the actual search.
	 */
	private void searchSubtermEqualities(Consumer<Justification> acceptor, ProofEnvironment environment, FormulaTemplate formulaTemplate, int recursionDepth) {
		if (formulaTemplate.getLeftTerm() == null || formulaTemplate.getRightTerm() == null) { // exactly one term is specified (see justifyAsync)
			Term originalTerm = (formulaTemplate.getLeftTerm() == null ? formulaTemplate.getRightTerm() : formulaTemplate.getLeftTerm());
			Term[] subterms = collectSubterms(originalTerm);

			for (Term originalSubterm : subterms) { // iterate through all subterms, bottom- to topmost
				FormulaTemplate subTemplate = new FormulaTemplate(originalSubterm, FormulaType.EQUATION, null);
				 delegateFinder.justifyAsync(environment, subTemplate, recursionDepth - 1).onEmit(subJustification -> {

					 // avoid duplicates: if this is justified by replacing a subterm of originalSubterm in originalSubterm, then
					 // the same can and will be achieved by replacing the subterm of originalSubterm directly in originalTerm.
					 // Therefore, to avoid duplicates, subJustification must not itself be a SubtermEqualityJustification.
					 if (subJustification instanceof SubtermEqualityJustification)
						 return;

					 Equation subEquation = (Equation)subJustification.getJustifiedFormula();
					 Term newSubterm = subEquation.getRight();
					 Term newTerm = replaceSubterm(originalTerm, originalSubterm, newSubterm);

					 Formula equation;
					 if (formulaTemplate.getRightTerm() == null)
						 equation = Builder.createEquation(originalTerm, newTerm);
					 else
						 equation = Builder.createEquation(newTerm, originalTerm);

					 acceptor.accept(new SubtermEqualityJustification(equation, originalSubterm, newSubterm, subJustification));
				 });
			}
		} else { // both terms are given
			// tracks the current node in the AST of the left and right term respectively
			Term currentLeft = formulaTemplate.getLeftTerm(), currentRight = formulaTemplate.getRightTerm();
			if (currentLeft.structurallyEquals(currentRight)) // if the terms are equal, the search is pointless
				return;

			// go down the AST while equality can still be justified by subterm replacement
			while (isCompatible(currentLeft, currentRight) && !isLeaf(currentLeft)) {
				// if the roots are the same, one or more of the child nodes in each tree must be different
				// if it is one, select those as currentLeft, currentRight. If more, the stop the search.

				if (currentLeft instanceof UnaryOperation) { // only one child node exists, so select it
					currentLeft = ((UnaryOperation)currentLeft).getOperand();
					currentRight = ((UnaryOperation)currentRight).getOperand();

				} else if (currentLeft instanceof BinaryOperation) {
					BinaryOperation left = (BinaryOperation)currentLeft, right = (BinaryOperation)currentRight;

					boolean leftChildrenEqual = left.getLeft().structurallyEquals(right.getLeft()),
							rightChildrenEqual = left.getRight().structurallyEquals(right.getRight());
					assert !leftChildrenEqual || !rightChildrenEqual; // because then currentLeft would equal currentRight

					if (!leftChildrenEqual && !rightChildrenEqual)
						break; // stop the search
					else if (leftChildrenEqual) { // right child nodes differ, so select them
						currentLeft = left.getRight();
						currentRight = right.getRight();
					} else { // left child nodes differ, so select them
						currentLeft = left.getLeft();
						currentRight = right.getLeft();
					}
				} else // should not happen unless new Term classes are introduced
					throw new IllegalStateException("Unsupported term class: " + currentLeft.getClass()); // TODO: or just break? or log warning?

				// for the current pair of subterms: try to justify their equality.
				// If successful, use this as justification.

				FormulaTemplate subTemplate = new FormulaTemplate(currentLeft, FormulaType.EQUATION, currentRight);
				Emitter<Justification> subEmitter = delegateFinder.justifyAsync(environment, subTemplate, recursionDepth - 1);
				subEmitter.onEmit(subJustification -> {
					if (subJustification instanceof SubtermEqualityJustification)
						return;
					subEmitter.stop(); // one justification suffices – but do not use first() because of previous condition

					Equation subEquation = (Equation)subJustification.getJustifiedFormula();
					Term subLeft = subEquation.getLeft(), subRight = subEquation.getRight();
					// should be equal to currentLeft, currentRight (but can't use those in lambda)

					Formula equation = Builder.createEquation(formulaTemplate.getLeftTerm(), formulaTemplate.getRightTerm());
					Justification justification = new SubtermEqualityJustification(equation, subLeft, subRight, subJustification);
					acceptor.accept(justification);
				});
			}
		}
	}

	private boolean isLeaf(Term term) {
		return term instanceof VariableReference || term instanceof ConstantReference;
	}

	/**
	 * Two terms are considered compatible if they represent the same operation (binary or unary)
	 */
	private boolean isCompatible(Term left, Term right) {
		if (!left.eClass().equals(right.eClass()))
			return false;

		if (left instanceof UnaryOperation)
			return ((UnaryOperation)left).getOperator()
					.equals(((UnaryOperation)right).getOperator());
		else if (left instanceof BinaryOperation)
			return ((BinaryOperation)left).getOperator()
					.equals(((BinaryOperation)right).getOperator());
		return true;
	}

	/**
	 * Replaces a subterm with another subterm and returns the result.
	 *
	 * @param originalTerm The term which contains the subterm to be replaced. The instance itself is not modified but cloned.
	 * @param originalSubterm The subterm which should be replaced.
	 * @param newSubterm The new subterm.
	 *
	 * @return A modified copy of the input term
	 */
	private Term replaceSubterm(Term originalTerm, Term originalSubterm, Term newSubterm) {
		Term newTerm = EcoreUtil.copy(originalTerm);

		// find path from originalSubterm to container
		Stack<Integer> indexPath = new Stack<Integer>();
		Term current = originalSubterm;
		while (current != originalTerm) {
			Term parent = (Term)current.eContainer();
			indexPath.push(parent.eContents().indexOf(current));
			current = parent;
		}

		// walk path from cloned container to cloned originalSubterm
		current = newTerm;
		while (!indexPath.isEmpty())
			current = (Term)current.eContents().get(indexPath.pop());
		assert current.structurallyEquals(originalSubterm);

		// replace cloned originalSubterm
		EcoreUtil.replace(current, EcoreUtil.copy(newSubterm));

		return newTerm;
	}

	/**
	 * Collects all subterms of a term (except the term itself)
	 */
	private Term[] collectSubterms(Term input) {
		LinkedList<Term> subterms = new LinkedList<Term>();

		// collect all subterms, with input term last
		input.accept(new Visitor() {
			@Override public void visit(Term term) { subterms.add(term); }
		});

		subterms.removeLast(); // do not include input term itself
		return subterms.toArray(new Term[subterms.size()]);
	}
}
