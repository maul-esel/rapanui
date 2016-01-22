package rapanui.dsl;

import org.eclipse.emf.ecore.util.EcoreUtil;

public abstract class Builder {
	public static Equation reverse(Equation input) {
		return createEquation(input.getRight(), input.getLeft());
	}

	/**
	* NOTE: this method copies the input terms!
	*/
	public static Equation createEquation(Term left, Term right) {
		assert left != null;
		assert right != null;

		Equation output = DslFactory.eINSTANCE.createEquation();
		output.setLeft(EcoreUtil.copy(left));
		output.setRight(EcoreUtil.copy(right));
		return output;
	}

	/**
	* NOTE: this method copies the input terms!
	*/
	public static Inclusion createInclusion(Term left, Term right) {
		assert left != null;
		assert right != null;

		Inclusion output = DslFactory.eINSTANCE.createInclusion();
		output.setLeft(EcoreUtil.copy(left));
		output.setRight(EcoreUtil.copy(right));
		return output;
	}

	/**
	* NOTE: this method copies the input term!
	*/
	public static DefinitionReference createDefinitionReference(Term target, Definition definition) {
		assert target != null;
		assert definition != null;

		DefinitionReference reference = DslFactory.eINSTANCE.createDefinitionReference();
		reference.setTarget(EcoreUtil.copy(target));
		reference.setDefinition(definition);
		return reference;
	}

	/**
	* NOTE: this method copies the input terms!
	*/
	public static BinaryOperation createBinaryOperation(Term left, BINARY_OPERATOR operator, Term right) {
		assert left != null;
		assert operator != null;
		assert right != null;

		BinaryOperation output = DslFactory.eINSTANCE.createBinaryOperation();
		output.setLeft(EcoreUtil.copy(left));
		output.setOperator(operator);
		output.setRight(EcoreUtil.copy(right));
		return output;
	}

	/**
	* NOTE: this method copies the input term!
	*/
	public static UnaryOperation createUnaryOperation(Term operand, POSTFIX_UNARY_OPERATOR operator) {
		assert operand != null;
		assert operator != null;

		UnaryOperation output = DslFactory.eINSTANCE.createUnaryOperation();
		output.setOperand(EcoreUtil.copy(operand));
		output.setOperator(operator);
		return output;
	}
}
