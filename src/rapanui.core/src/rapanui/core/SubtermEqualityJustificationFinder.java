package rapanui.core;

import java.util.LinkedList;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

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
	public Emitter<Justification> justifyAsync(ProofEnvironment environment, Formula formulaTemplate, int recursionDepth) {
		if (recursionDepth <= 0 || (formulaTemplate.getLeft() == null && formulaTemplate.getRight() == null)) // needs at least one recursion and one term
			return Emitter.empty();

		Function<Formula,Emitter<Justification>> delegate = (template) ->
			delegateFinder.justifyAsync(environment, template, recursionDepth - 1);
		return Emitter.fromResultComputation(acceptor -> searchSubtermEqualities(acceptor, delegate, formulaTemplate));
	}

	/**
	 * Internal function that performs the actual search.
	 */
	private void searchSubtermEqualities(Consumer<Justification> acceptor, Function<Formula,Emitter<Justification>> delegate, Formula formulaTemplate) {
		boolean leftTermGiven = formulaTemplate.getLeft() != null;

		if (!leftTermGiven || formulaTemplate.getRight() == null) { // exactly one term is specified (see justifyAsync)
			Term originalTerm = leftTermGiven ? formulaTemplate.getLeft() : formulaTemplate.getRight();

			if (originalTerm.isComplete())
				searchWithCompleteTerm(originalTerm, leftTermGiven, acceptor, delegate);
			else
				searchWithIncompleteTerm(originalTerm, leftTermGiven, acceptor, delegate);		

		} else // both terms are given
			if (formulaTemplate.getLeft().isComplete() && formulaTemplate.getRight().isComplete())
				searchWithTwoTerms(formulaTemplate, acceptor, delegate);
	}

	private void searchWithCompleteTerm(Term originalTerm, boolean isLeftTerm,
			Consumer<Justification> acceptor, Function<Formula,Emitter<Justification>> delegate) {
		for (Term originalSubterm : collectSubterms(originalTerm)) { // iterate through all subterms, bottom- to topmost
			Formula subTemplate = Builder.createEquation(originalSubterm, null);
			delegate.apply(subTemplate)
			.reject(SubtermEqualityJustification.class)
			.onEmit(subJustification -> {
				 Term newSubterm = subJustification.getJustifiedFormula().getRight();
				 Term newTerm = replaceSubterm(originalTerm, originalSubterm, newSubterm);

				 Formula equation;
				 if (isLeftTerm)
					 equation = Builder.createEquation(originalTerm, newTerm);
				 else
					 equation = Builder.createEquation(newTerm, originalTerm);

				 acceptor.accept(new SubtermEqualityJustification(equation, originalSubterm, newSubterm, subJustification));
			 });
		}
	}

	/*
	 * 1. Walk down the AST to the minimal incomplete subterm so that the rest term is incomplete
	 * 2. In each step, find 2 equal terms s = r, where s matches the current subterm t
	 * 3. Replace t in the input by s, r respectively and emit the results as equal
	 */
	private void searchWithIncompleteTerm(Term termTemplate, boolean isLeftTerm,
			Consumer<Justification> acceptor, Function<Formula,Emitter<Justification>> delegate) {

		Term current = termTemplate, parent = null;
		while (current != null && !isLeaf(current)) { // walk down the tree to the minimal incomplete term so that the remaining term is complete
			if (current instanceof UnaryOperation) {
				parent = current;
				current = ((UnaryOperation)current).getOperand();
			} else if (current instanceof BinaryOperation) {
				BinaryOperation binary = (BinaryOperation)current;
				boolean leftIncomplete = binary.getLeft() == null || !binary.getLeft().isComplete(),
					rightIncomplete = binary.getRight() == null || !binary.getRight().isComplete();
				if (leftIncomplete && rightIncomplete)
					break; // current is minimal incomplete term
				else { // exactly one child node is incomplete
					parent = current;
					current = leftIncomplete ? binary.getLeft() : binary.getRight();
				}
			} else
				throw new IllegalStateException("Unknown term class: " + current.eClass());

			Formula subTemplate = Builder.createEquation(current, null);
			final Term parentReference = parent; // for lambda
			delegate.apply(subTemplate)
			.reject(SubtermEqualityJustification.class)
			.onEmit(subJustification -> {
				Term incomplete = subTemplate.getLeft(); // == current, which can't be used in lambda

				Term originalSubterm = subJustification.getJustifiedFormula().getLeft();
				Term newSubterm = subJustification.getJustifiedFormula().getRight();

				Term originalTerm = replaceNullableSubterm(termTemplate, incomplete, originalSubterm, parentReference);
				Term newTerm = replaceNullableSubterm(termTemplate, incomplete, newSubterm, parentReference);
				Formula equation;
				if (isLeftTerm)
					equation = Builder.createEquation(originalTerm, newTerm);
				else
					equation = Builder.createEquation(newTerm, originalTerm);

				acceptor.accept(new SubtermEqualityJustification(equation, originalSubterm, newSubterm, subJustification));
			});
		}
	}

	private void searchWithTwoTerms(Formula formulaTemplate, Consumer<Justification> acceptor,
			Function<Formula,Emitter<Justification>> delegate) {
		// tracks the current node in the AST of the left and right term respectively
		Term currentLeft = formulaTemplate.getLeft(), currentRight = formulaTemplate.getRight();
		if (currentLeft.structurallyEquals(currentRight)) // if the terms are equal, the search is pointless
			return;

		// go down the AST while equality can still be justified by subterm replacement
		// invariant: !currentLeft.structurallyEquals(currentRight))
		while (isCompatible(currentLeft, currentRight) && !isLeaf(currentLeft)) {
			// If the roots are the same (i.e. compatible), one or more of the child nodes in each
			// tree must be different. If it is one, select those as currentLeft, currentRight.
			// If more, the stop the search.

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

			Formula subTemplate = Builder.createFormula(currentLeft, BINARY_RELATION.EQUATION, currentRight);
			delegate.apply(subTemplate)
			.reject(SubtermEqualityJustification.class)
			.first()
			.onEmit(subJustification -> {
				Formula subEquation = subJustification.getJustifiedFormula();
				Term subLeft = subEquation.getLeft(), subRight = subEquation.getRight();
				// should be equal to currentLeft, currentRight (but can't use those in lambda)

				Formula equation = Builder.createEquation(formulaTemplate.getLeft(), formulaTemplate.getRight());
				Justification justification = new SubtermEqualityJustification(equation, subLeft, subRight, subJustification);
				acceptor.accept(justification);
			});
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

	private Term replaceNullableSubterm(Term originalTerm, Term originalSubterm, Term newSubterm, Term parent) {
		if (originalSubterm != null)
			return replaceSubterm(originalTerm, originalSubterm, newSubterm);
		else {
			newSubterm = EcoreUtil.copy(newSubterm);
			Term newParent = EcoreUtil.copy(parent);

			if (newParent instanceof UnaryOperation)
				((UnaryOperation)newParent).setOperand(newSubterm);
			else if (newParent instanceof BinaryOperation) // exactly one child should be null
				if (((BinaryOperation)newParent).getLeft() == null)
					((BinaryOperation)newParent).setLeft(newSubterm);
				else
					((BinaryOperation)newParent).setRight(newSubterm);

			return replaceSubterm(originalTerm, parent, newParent);
		}
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
