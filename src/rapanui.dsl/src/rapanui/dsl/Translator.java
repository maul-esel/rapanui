package rapanui.dsl;

import java.util.Map;
import java.util.Stack;

import org.eclipse.emf.ecore.util.EcoreUtil;

public class Translator implements Visitor {
	private final Map<String, Term> dictionary;
	private final Stack<Term> termResult = new Stack<Term>();

	public Translator(Map<String, Term> dictionary) {
		this.dictionary = dictionary;
	}

	public Term translate(Term input) {
		termResult.clear();
		input.accept(this);

		assert termResult.size() == 1;
		return termResult.pop();
	}

	@Override public void visit(BinaryOperation input) {
		Term right = termResult.pop(), left = termResult.pop();
		termResult.push(Builder.createBinaryOperation(left, input.getOperator(), right));
	}

	@Override public void visit(UnaryOperation input) {
		termResult.push(Builder.createUnaryOperation(termResult.pop(), input.getOperator()));
	}

	@Override public void visit(VariableReference input) {
		String variable = input.getVariable();
		if (!dictionary.containsKey(variable))
			throw new IncompleteDictionaryException(variable);
		termResult.push(EcoreUtil.copy(dictionary.get(variable)));
	}

	@Override public void visit(ConstantReference input) {
		termResult.push(EcoreUtil.copy(input));
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

	public static class IncompleteDictionaryException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public IncompleteDictionaryException(String variable) {
			super("Unknown variable '" + variable + "'");
		}
	}
}
