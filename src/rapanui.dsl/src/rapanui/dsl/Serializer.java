package rapanui.dsl;

import java.util.Stack;

public class Serializer implements Visitor {
	private Serializer() {} // static class

	public static String serialize(Rule rule) {
		SerializationVisitor visitor = new SerializationVisitor();
		rule.accept(visitor);
		return visitor.getResult();
	}

	public static String serialize(Predicate predicate) {
		SerializationVisitor visitor = new SerializationVisitor();
		predicate.accept(visitor);
		return visitor.getResult();
	}

	public static String serialize(Term term) {
		assert term.isComplete();

		SerializationVisitor visitor = new SerializationVisitor();
		term.accept(visitor);
		return visitor.getResult();
	}

	private static class SerializationVisitor implements Visitor {
		private final Stack<String> result = new Stack<String>();

		String getResult() {
			assert result.size() == 1;
			return result.pop();
		}

		@Override public void visit(Rule rule) {
			String conclusions = result.pop();
			for (int i = 1; i < rule.getConclusions().size(); ++i)
				conclusions = result.pop() + "\n\tand " + conclusions;

			if (rule.getPremises().size() == 0)
				result.push("axiom \"" + rule.getName() + "\"\n\talways " + conclusions + "\n");
			else {
				String premises = result.pop();
				for (int i = 1; i < rule.getPremises().size(); ++i)
					premises = result.pop() + "\n\tand " + premises;
				result.push("theorem \"" + rule.getName() + "\"\n\tif " + premises + "\n\tthen " + conclusions + "\n");
			}
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

			if (operation.getLeft() instanceof BinaryOperation)
				left = "(" + left + ")";
			if (operation.getRight() instanceof BinaryOperation)
				right = "(" + right + ")";

			result.push(left + " " + operation.getOperator().getLiteral() + " " + right);
		}

		@Override public void visit(UnaryOperation operation) {
			String operand = result.pop();

			if (operation.getOperand() instanceof BinaryOperation || operation.getOperand() instanceof UnaryOperation)
				operand = "(" + operand + ")";

			result.push(operand + operation.getOperator().getLiteral());
		}

		@Override public void visit(VariableReference variable) {
			result.push(variable.getVariable());
		}

		@Override public void visit(ConstantReference constant) {
			result.push(constant.getConstant());
		}
	}
}
