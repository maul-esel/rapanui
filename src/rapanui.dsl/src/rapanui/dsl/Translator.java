package rapanui.dsl;

import java.util.Map;

public class Translator {
	private final Map<String, Term> dictionary;

	public Translator(Map<String, Term> dictionary) {
		this.dictionary = dictionary;
	}

	public Term translate(Term input) {
		if (input instanceof BinaryOperation)
			return translate((BinaryOperation)input);
		else if (input instanceof UnaryOperation)
			return translate((UnaryOperation)input);
		else if (input instanceof VariableReference)
			return translate((VariableReference)input);
		else if (input instanceof ConstantReference)
			return input;
		throw new IllegalStateException("Unknown term type: " + input.getClass());
	}

	public BinaryOperation translate(BinaryOperation input) {
		BinaryOperation output = DslFactory.eINSTANCE.createBinaryOperation();
		output.setLeft(translate(input.getLeft()));
		output.setOperator(input.getOperator());
		output.setRight(translate(input.getRight()));
		return output;
	}

	public UnaryOperation translate(UnaryOperation input) {
		UnaryOperation output = DslFactory.eINSTANCE.createUnaryOperation();
		output.setOperand(translate(input.getOperand()));
		output.setOperator(input.getOperator());
		return output;
	}

	public Term translate(VariableReference input) {
		return dictionary.get(input.getVariable());
	}

	public Formula translate(Formula input) {
		if (input instanceof Equation)
			return translate((Equation)input);
		else if (input instanceof Inclusion)
			return translate((Inclusion)input);
		else if (input instanceof DefinitionReference)
			return translate((DefinitionReference)input);
		throw new IllegalStateException("Unknown formula type: " + input.getClass());
	}

	public Equation translate(Equation input){
		Equation output = DslFactory.eINSTANCE.createEquation();
		output.setLeft(translate(input.getLeft()));
		output.setRight(translate(input.getRight()));
		return output;
	}

	public Inclusion translate(Inclusion input) {
		Inclusion output = DslFactory.eINSTANCE.createInclusion();
		output.setLeft(translate(input.getLeft()));
		output.setRight(translate(input.getRight()));
		return output;
	}

	public DefinitionReference translate(DefinitionReference input) {
		DefinitionReference output = DslFactory.eINSTANCE.createDefinitionReference();
		output.setTarget(translate(input.getTarget()));
		output.setDefinitionName(input.getDefinitionName());
		return output;
	}
}
