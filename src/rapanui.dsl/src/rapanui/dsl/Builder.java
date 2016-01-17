package rapanui.dsl;

import org.eclipse.emf.ecore.util.EcoreUtil;

public abstract class Builder {
	public static Equation reverse(Equation input) {
		return createEquation(EcoreUtil.copy(input.getRight()), EcoreUtil.copy(input.getLeft()));
	}

	public static Equation createEquation(Term left, Term right) {
		Equation output = DslFactory.eINSTANCE.createEquation();
		output.setLeft(left);
		output.setRight(right);
		return output;
	}

	public static Inclusion createInclusion(Term left, Term right) {
		Inclusion output = DslFactory.eINSTANCE.createInclusion();
		output.setLeft(left);
		output.setRight(right);
		return output;
	}

	public static DefinitionReference createDefinitionReference(Term target, Definition definition) {
		DefinitionReference reference = DslFactory.eINSTANCE.createDefinitionReference();
		reference.setTarget(target);
		reference.setDefinition(definition);
		return reference;
	}

	public static BinaryOperation createBinaryOperation(Term left, BINARY_OPERATOR operator, Term right) {
		BinaryOperation output = DslFactory.eINSTANCE.createBinaryOperation();
		output.setLeft(left);
		output.setOperator(operator);
		output.setRight(right);
		return output;
	}

	public static UnaryOperation createUnaryOperation(Term operand, POSTFIX_UNARY_OPERATOR operator) {
		UnaryOperation output = DslFactory.eINSTANCE.createUnaryOperation();
		output.setOperand(operand);
		output.setOperator(operator);
		return output;
	}
}
