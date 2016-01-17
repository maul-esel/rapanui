package rapanui.dsl;

import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;

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
		return Builder.createBinaryOperation(
			translate(input.getLeft()),
			input.getOperator(),
			translate(input.getRight())
		);
	}

	public UnaryOperation translate(UnaryOperation input) {
		return Builder.createUnaryOperation(translate(input.getOperand()), input.getOperator());
	}

	public Term translate(VariableReference input) {
		return EcoreUtil.copy(dictionary.get(input.getVariable()));
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
		return Builder.createEquation(translate(input.getLeft()), translate(input.getRight()));
	}

	public Inclusion translate(Inclusion input) {
		return Builder.createInclusion(translate(input.getLeft()), translate(input.getRight()));
	}

	public DefinitionReference translate(DefinitionReference input) {
		return Builder.createDefinitionReference(translate(input.getTarget()), input.getDefinition());
	}
}
