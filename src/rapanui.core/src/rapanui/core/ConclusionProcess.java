package rapanui.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import rapanui.dsl.DslHelper;
import rapanui.dsl.moai.Term;

import static rapanui.core.Patterns.*;

/**
 * Represents the core proof process, in which a term is transformed using rules and premises.
 */
public class ConclusionProcess {
	private final ProofEnvironment environment;
	private final Term startTerm;
	private final List<Transformation> transformations;
	private final List<ConclusionProcessObserver> observers;

	/**
	 * Creates a new conclusion process
	 *
	 * @param environment The environment containing the process (must not be null)
	 * @param startTerm The initial term of the process (must not be null)
	 */
	ConclusionProcess(ProofEnvironment environment, Term startTerm) {
		assert environment != null;
		assert startTerm != null;

		this.environment = environment;
		this.startTerm = startTerm;
		this.transformations = new ArrayList<Transformation>();
		this.observers = new ArrayList<ConclusionProcessObserver>();
	}

	public ProofEnvironment getEnvironment() {
		return environment;
	}

	public Term getStartTerm() {
		return startTerm;
	}

	/**
	 * Retrieves the (current) last term in the transformation chain.
	 * @return The output term of the last transformation, or if there are none, the initial term
	 */
	public Term getLastTerm() {
		if (transformations.size() == 0)
			return startTerm;
		return transformations.get(transformations.size() - 1).getOutput();
	}

	/**
	 * Computes the type of the conclusion, i.e. equality or inclusion.
	 *
	 * @return @see ConclusionType.Inclusion if any transformation is an inclusion, @see ConclusionType.Equality otherwise
	 */
	public FormulaType getType() {
		return getType(0, transformations.size());
	}

	/**
	 * Computes the type of a range of transformations in the conclusion.
	 *
	 * @param startRange The index of the first term (not transformation!) to include in the range
	 * @param endRange The index of the last term (not transformation!) to include in the range
	 *
	 * @return @see ConclusionType.Inclusion if any transformation in the range is an inclusion, @see ConclusionType.Equality otherwise.
	 * 	If startIndex == endIndex, there is no transformation in the range and @see ConclusionType.Equality is returned.
	 *
	 * @throws IllegalArgumentException if the arguments do not specify a valid range
	 */
	public FormulaType getType(int startRange, int endRange) {
		if (startRange < 0 || startRange > transformations.size())
			throw new IllegalArgumentException("startRange");
		else if (endRange < startRange || endRange > transformations.size())
			throw new IllegalArgumentException("endRange");

		if (startRange == endRange)
			return FormulaType.Equality;

		List<FormulaType> transformationTypes = transformations
				.subList(Math.max(startRange - 1, 0), endRange)
				.stream()
				.map(Transformation::getType)
				.collect(Collectors.toList());

		if (transformationTypes.contains(FormulaType.Inclusion))
			return FormulaType.Inclusion;
		return FormulaType.Equality;
	}

	public Transformation[] getTransformations() {
		return listToArray(transformations, Transformation[]::new);
	}

	/**
	 * Appends a new transformation to the chain.
	 *
	 * @param transformation The transformation to append
	 *
	 * The new transformation's input term must equal the current last term on the conclusion process.
	 */
	public void appendTransformation(Transformation transformation) {
		assert transformation != null;
		assert transformation.getContainer() == this;

		if (!DslHelper.equal(getLastTerm(), transformation.getInput()))
			throw new IllegalArgumentException();
		transformations.add(transformation);
		notifyObservers(observers, ConclusionProcessObserver::transformationAdded, transformation);
	}

	/**
	 * Adds an observer to the process. Observers are notified upon changes.
	 *
	 * @param observer The new observer
	 */
	public void addObserver(ConclusionProcessObserver observer) {
		observers.add(observer);
	}

	/**
	 * Unregisters an observer from the process, so it is no longer notified of changes.
	 *
	 * @param observer The observer to remove
	 */
	public void deleteObserver(ConclusionProcessObserver observer) {
		observers.remove(observer);
	}
}
