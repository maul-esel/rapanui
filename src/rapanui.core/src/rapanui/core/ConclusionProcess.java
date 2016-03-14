package rapanui.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import rapanui.dsl.Term;
import rapanui.dsl.BINARY_RELATION;

import static rapanui.core.Patterns.*;

/**
 * Represents the core proof process, in which a term is transformed using rules and premises.
 */
public class ConclusionProcess {
	private final ProofEnvironment environment;
	private final Term startTerm;
	private final List<Transformation> transformations = new LinkedList<Transformation>();
	private final List<Observer> observers = new LinkedList<Observer>();

	/**
	 * Creates a new conclusion process
	 *
	 * @param environment The environment containing the process. Must not be null.
	 * @param startTerm The initial term of the process. Must not be null.
	 */
	ConclusionProcess(ProofEnvironment environment, Term startTerm) {
		assert environment != null;
		assert startTerm != null;

		this.environment = environment;
		this.startTerm = startTerm;
	}

	/**
	 * @return The environment containing the conclusion. Guaranteed to be non-null.
	 */
	public ProofEnvironment getEnvironment() {
		return environment;
	}

	/**
	 * @return The conclusion's start term. Guaranteed to be non-null.
	 */
	public Term getStartTerm() {
		return startTerm;
	}

	/**
	 * Retrieves the (current) last term in the transformation chain.
	 * @return The output term of the last transformation, or if there are none, the initial term. Guaranteed to be non-null.
	 */
	public Term getLastTerm() {
		if (transformations.size() == 0)
			return startTerm;
		return transformations.get(transformations.size() - 1).getOutput();
	}

	/**
	 * Computes the list of all terms occurring in the transformation chain.
	 * @return The terms in the conclusion, in order. Guaranteed to be non-null.
	 */
	public Term[] getTerms() {
		Transformation[] transformations = getTransformations();
		Term[] terms = new Term[1+transformations.length];

		terms[0] = getStartTerm();
		for (int i = 0; i < transformations.length; ++i)
			terms[i+1] = transformations[i].getOutput();

		return terms;
	}

	/**
	 * Computes the type of the conclusion, i.e. equality or inclusion.
	 *
	 * @return {@link rapanui.dsl.BINARY_RELATION.INCLUSION} if any transformation is an inclusion,
	 * 	{@link rapanui.dsl.BINARY_RELATION.EQUATION} otherwise. Guaranteed to be non-null.
	 */
	public BINARY_RELATION getFormulaType() {
		return getFormulaType(0, transformations.size());
	}

	/**
	 * Computes the type of a range of transformations in the conclusion.
	 *
	 * @param startRange The index of the first term (not transformation!) to include in the range
	 * @param endRange The index of the last term (not transformation!) to include in the range
	 *
	 * @return {@link rapanui.dsl.BINARY_RELATION.INCLUSION} if any transformation in the range is
	 * an inclusion, {@link rapanui.dsl.BINARY_RELATION.EQUATION} otherwise. If {@code startIndex ==
	 * endIndex}, there is no transformation in the range and {@link rapanui.dsl.BINARY_RELATION.EQUATION}
	 * is returned. Guaranteed to be non-null.
	 *
	 * @throws IllegalArgumentException if the arguments do not specify a valid range
	 */
	public BINARY_RELATION getFormulaType(int startRange, int endRange) {
		if (startRange < 0 || startRange > transformations.size())
			throw new IllegalArgumentException("startRange");
		else if (endRange < startRange || endRange > transformations.size())
			throw new IllegalArgumentException("endRange");

		if (transformations.subList(startRange, endRange).stream()
				.map(Transformation::getFormulaType)
				.anyMatch(BINARY_RELATION.INCLUSION::equals))
			return BINARY_RELATION.INCLUSION;

		return BINARY_RELATION.EQUATION;
	}

	public Transformation[] getTransformations() {
		return listToArray(transformations, Transformation[]::new);
	}

	/**
	 * Removes the last transformation from the conclusion.
	 * Note that this also removes all derivatives of this transformation from the environment.
	 */
	public void undoTransformation() {
		Transformation lastTransformation = transformations.get(transformations.size() - 1);

		Set<Transformation> derivatives = environment.getAnalyst().findDirectDerivatives(lastTransformation);
		for (Transformation derivative : derivatives)
				derivative.getContainer().resetBefore(derivative);

		transformations.remove(transformations.size() - 1);
		notifyObservers(observers, Observer::transformationRemoved, lastTransformation);
	}

	/**
	 * Removes the given transformation and all subsequent ones from the conclusion.
	 * Note that this also removes all their derivatives from the environment.
	 */
	public void resetBefore(Transformation transformation) {
		if (!transformations.contains(transformation))
			return; // do not error

		for (int i = transformations.size() - 1; transformations.get(i) != transformation; --i)
			undoTransformation();
		undoTransformation();
	}

	/**
	 * Appends a new transformation to the chain.
	 *
	 * @param transformation The transformation to append. Must not be null.
	 *
	 * The new transformation's input term must equal the current last term on the conclusion process.
	 */
	public void appendTransformation(Transformation transformation) {
		assert transformation != null;
		assert transformation.getContainer() == this;

		if (!getLastTerm().structurallyEquals(transformation.getInput()))
			throw new IllegalArgumentException("The transformation's input term " + transformation.getInput().serialize()
					+ " must be equal to the conclusion's last term " + getLastTerm().serialize());
		transformations.add(transformation);
		notifyObservers(observers, Observer::transformationAdded, transformation);
	}

	public Set<String> getFreeVariables() {
		return Arrays.stream(getTerms())
			.flatMap(term -> term.getFreeVariables().stream())
			.collect(Collectors.toSet());
	}

	public interface Observer {
		/**
		 * Called when a new {@link Transformation} is appended to the {@link ConclusionProcess}
		 *
		 * @param transformation The newly appended {@link Transformation}. Guaranteed to be non-null.
		 */
		default void transformationAdded(Transformation transformation) {}

		/**
		 * Called when a {@link Transformation} is removed from the conclusion.
		 *
		 * @param transformation The removed {@link Transformation}. Guaranteed to be non-null.
		 */
		default void transformationRemoved(Transformation transformation) {}
	}

	/**
	 * Adds an observer to the process. Observers are notified upon changes.
	 *
	 * @param observer The new observer. Must not be null.
	 */
	public void addObserver(Observer observer) {
		assert observer != null;
		observers.add(observer);
	}

	/**
	 * Unregisters an observer from the process, so it is no longer notified of changes.
	 *
	 * @param observer The observer to remove
	 */
	public void deleteObserver(Observer observer) {
		observers.remove(observer);
	}
}
