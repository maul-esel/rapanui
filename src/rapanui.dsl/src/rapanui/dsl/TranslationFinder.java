package rapanui.dsl;

import java.util.HashMap;
import java.util.Map;

/**
 * Learns a translation dictionary from given translations.
 */
public class TranslationFinder {
	private final Map<String, Term> dictionary;
	private final Translator translator;

	/**
	 * Creates a new instance with an empty dictionary.
	 */
	public TranslationFinder() {
		this(new HashMap<String, Term>());
	}

	/**
	 * Creates a new instance with a given dictionary.
	 *
	 * @param dictionary The dictionary to use for translations.
	 *
	 * Note: The dictionary is copied by-reference. Therefore, all changes to the dictionary affect the instance.
	 */
	public TranslationFinder(Map<String, Term> dictionary) {
		assert dictionary != null;
		this.dictionary = dictionary;
		this.translator = new Translator(dictionary);
	}

	public Map<String, Term> getDictionary() {
		return dictionary;
	}

	/**
	 * Creates a new instance with the current instance's dictionary. Subsequent changes to the original
	 * or the clone will no affect the other.
	 */
	public TranslationFinder clone() {
		return new TranslationFinder(new HashMap<String, Term>(dictionary));
	}

	/**
	 * Translates the given term using the dictionary the instance has learned so far.
	 *
	 * @throws Translator.IncompleteDictionaryException
	 */
	public Term translate(Term original) {
		return translator.translate(original);
	}

	/**
	 * Informs the instance of a valid translation. From this, the instance derives dictionary entries
	 * and checks if the translation is compatible with what it has previously learned.
	 *
	 * @param original The original term
	 * @param translation The term the original term was translated to. May be null or incomplete.
	 * @return False if this translation conflicts with previously learned dictionary entries, true otherwise.
	 *
	 * Note that this method may affect the instance's dictionary even if false is returned. Currently,
	 * the only way to reset is to perform this operation on a clone.
	 */
	public boolean train(Term original, Term translation) {
		assert original != null && original.isComplete();

		if (translation == null)
			return true;

		if (original instanceof VariableReference)
			return train((VariableReference)original, translation);
		else if (original instanceof ConstantReference)
			return train((ConstantReference)original, translation);
		else if (original instanceof UnaryOperation)
			return train((UnaryOperation)original, translation);
		else if (original instanceof BinaryOperation)
			return train((BinaryOperation)original, translation);
		throw new IllegalStateException("Unknown term type: " + original.eClass());
	}

	private boolean train(VariableReference from, Term to) {
		String variable = from.getVariable();
		if (dictionary.containsKey(variable)) {
			try {
				dictionary.put(variable, to.mergeTemplate(dictionary.get(variable)));
			} catch (IncompatibleTemplateException e) {
				return false;
			}
		} else
			dictionary.put(variable, to);
		return true;
	}

	private boolean train(ConstantReference from, Term to) {
		return to instanceof ConstantReference && from.getConstant().equals(((ConstantReference)to).getConstant());
	}

	private boolean train(UnaryOperation from, Term to) {
		if (!(to instanceof UnaryOperation) || ((UnaryOperation)to).getOperator() != from.getOperator())
			return false;
		return train(from.getOperand(), ((UnaryOperation)to).getOperand());
	}

	private boolean train(BinaryOperation from, Term to) {
		if (!(to instanceof BinaryOperation) || ((BinaryOperation)to).getOperator() != from.getOperator())
			return false;
		return train(from.getLeft(), ((BinaryOperation)to).getLeft())
				&& train(from.getRight(), ((BinaryOperation)to).getRight());
	}
}
