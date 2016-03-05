package rapanui.dsl;

import java.util.Stack;

public class Serializer implements Visitor {
	private static final ThreadLocal<Serializer> instances = new ThreadLocal<Serializer>() {
		@Override protected Serializer initialValue() {
			return new Serializer();
		}
	};

	private final Stack<String> result = new Stack<String>();

	private Serializer() {}

	public static Serializer getInstance() {
		return instances.get();
	}

	public String serialize(Predicate predicate) {
		result.clear();
		predicate.accept(this);

		assert result.size() == 1;
		return result.pop();
	}

	public String serialize(Term term) {
		assert term.isComplete();
		result.clear();
		term.accept(this);

		assert result.size() == 1;
		return result.pop();
	}

	@Override public void visit(Formula formula) {
		String right = result.pop(), left = result.pop();
		result.push(left + " " + formula.getFormulaType().getLiteral() + " " + right);
	}

	@Override public void visit(DefinitionReference reference) {
		result.push(result.pop() + " is \"" + reference.getDefinition().getName() + "\"");
	}

	@Override public void visit(BinaryOperation operation) {
		String right = result.pop(), left = result.pop();

		if (operation.getLeft() instanceof BinaryOperation || operation.getLeft() instanceof UnaryOperation)
			left = "(" + left + ")";
		if (operation.getRight() instanceof BinaryOperation || operation.getRight() instanceof UnaryOperation)
			right = "(" + right + ")";

		result.push(left + " " + operation.getOperator().getLiteral() + " " + right);
	}

	@Override public void visit(UnaryOperation operation) {
		String operand = result.pop();

		if (operation.getOperand() instanceof BinaryOperation || operation.getOperand() instanceof UnaryOperation)
			operand = "(" + operand + ")";

		result.push(operand + operation.getOperator().getLiteral());	}

	@Override public void visit(VariableReference variable) {
		result.push(variable.getVariable());
	}

	@Override public void visit(ConstantReference constant) {
		result.push(constant.getConstant());
	}
}
