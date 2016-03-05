package rapanui.dsl;

import org.eclipse.emf.ecore.util.EcoreUtil;

public abstract class Builder {
	public static Formula reverse(Formula input) {
		return createFormula(input.getRight(), input.getFormulaType(), input.getLeft());
	}

	/**
	* NOTE: this method copies the input terms!
	*/
	public static Formula createEquation(Term left, Term right) {
		return createFormula(left, BINARY_RELATION.EQUATION, right);
	}

	/**
	* NOTE: this method copies the input terms!
	*/
	public static Formula createInclusion(Term left, Term right) {
		return createFormula(left, BINARY_RELATION.INCLUSION, right);
	}

	/**
	* NOTE: this method copies the input terms!
	*/
	public static Formula createFormula(Term left, BINARY_RELATION type, Term right) {
		Formula output = DslFactory.eINSTANCE.createFormula();
		output.setLeft(EcoreUtil.copy(left));
		output.setRight(EcoreUtil.copy(right));
		output.setFormulaType(type);
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
		UnaryOperation output = DslFactory.eINSTANCE.createUnaryOperation();
		output.setOperand(EcoreUtil.copy(operand));
		output.setOperator(operator);
		return output;
	}
}
