package rapanui.dsl;

public class Serializer {
	private static final Serializer instance = new Serializer();

	private Serializer() {}

	public static Serializer getInstance() {
		return instance;
	}

	public String serialize(Formula formula) {
		if (formula instanceof Equation)
			return serialize((Equation)formula);
		else if (formula instanceof Inclusion)
			return serialize((Inclusion)formula);
		else if (formula instanceof DefinitionReference)
			return serialize((DefinitionReference)formula);
		throw new IllegalStateException("Unknown formula type: " + formula.getClass());
	}

	public String serialize(Term term) {
		return serialize(term, false);
	}

	protected String serialize(Term term, boolean isNested) {
		if (term instanceof BinaryOperation)
			return serialize((BinaryOperation)term, isNested);
		else if (term instanceof UnaryOperation)
			return serialize((UnaryOperation)term, isNested);
		else if (term instanceof VariableReference)
			return serialize((VariableReference)term);
		else if (term instanceof ConstantReference)
			return serialize((ConstantReference)term);
		throw new IllegalStateException("Unknown term type: " + term.getClass());
	}

	protected String serialize(Equation equation) {
		return serialize(equation.getLeft()) + " = " + serialize(equation.getRight());
	}

	protected String serialize(Inclusion inclusion) {
		return serialize(inclusion.getLeft()) + " ⊆ " + serialize(inclusion.getRight());
	}

	protected String serialize(DefinitionReference reference) {
		return serialize(reference.getTarget()) + " is \"" + reference.getDefinitionName() + "\"";
	}

	protected String serialize(BinaryOperation operation, boolean isNested) {
		String serialized = serialize(operation.getLeft(), true)
				+ " " + operation.getOperator().getLiteral() + " "
				+ serialize(operation.getRight(), true);

		if (isNested)
			return "(" + serialized + ")";
		return serialized;
	}

	protected String serialize(UnaryOperation operation, boolean isNested) {
		String serialized = serialize(operation.getOperand(), true)
				+ operation.getOperator().getLiteral();

		if (isNested)
			return "(" + serialized + ")";
		return serialized;
	}

	protected String serialize(VariableReference variable) {
		return variable.getVariable();
	}

	protected String serialize(ConstantReference constant) {
		return constant.getConstant();
	}
}
