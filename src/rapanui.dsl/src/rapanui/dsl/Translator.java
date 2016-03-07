package rapanui.dsl;

import java.util.Map;
import java.util.Stack;

import org.eclipse.emf.ecore.util.EcoreUtil;

public class Translator implements Visitor {
	private final Map<String, Term> dictionary;
	private final Stack<Term> termResult = new Stack<Term>();
	private boolean allowIncomplete = false;

	public Translator(Map<String, Term> dictionary) {
		this.dictionary = dictionary;
	}

	public Term translate(Term input) {
		return translate(input, false);
	}

	public Term translate(Term input, boolean allowIncomplete) {
		termResult.clear();
		this.allowIncomplete = allowIncomplete;
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
		if ((!dictionary.containsKey(variable) || !dictionary.get(variable).isComplete()) && !allowIncomplete)
			throw new IncompleteDictionaryException(variable);
		termResult.push(EcoreUtil.copy(dictionary.get(variable)));
	}

	@Override public void visit(ConstantReference input) {
		termResult.push(EcoreUtil.copy(input));
	}

	public Predicate translate(Predicate input) {
		if (input instanceof Formula)
			return translate((Formula)input);
		else if (input instanceof DefinitionReference)
			return translate((DefinitionReference)input);
		throw new IllegalStateException("Unknown predicate type: " + input.getClass());
	}

	public Formula translate(Formula input){
		return Builder.createFormula(translate(input.getLeft()), input.getFormulaType(), translate(input.getRight()));
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
