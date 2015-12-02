package rapanui.core;

import java.util.Arrays;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import rapanui.dsl.DslHelper;
import rapanui.dsl.moai.Formula;

/**
 * Analyzes dependencies between conclusions, transformations, justifications and premises
 * within a ProofEnvironment.
 *
 * Definition of what a "dependency" is (in this context) below. If A is a dependency of B,
 * B is called a "derivative" of A.
 */
public class DependencyAnalyst {
	private final ProofEnvironment environment;

	/**
	 * @param environment The environment to analyze.
	 */
	DependencyAnalyst(ProofEnvironment environment) {
		this.environment = environment;
		// TODO: cache
	}

	/**
	 * Given an environment premise, find all transformations that directly or
	 * indirectly depend on the premise.
	 *
	 * @param premise
	 * @return All transformations that depend on the premise, from all of the environment's conclusion processes
	 *
	 * A transformation depends on a premise directly, if the premise is used
	 * anywhere in the transformation's justification graph. Dependency in general
	 * is defined as the transitive closure of direct dependency.
	 */
	public Transformation[] findDerivatives(Formula premise) {
		return findDerivativesRecursive(premise, this::isDirectDependency);
	}

	/**
	 * Given one of the environment's conclusion process, find all transformations
	 * that directly or indirectly depend on the conclusion process.
	 *
	 * @param conclusion
	 * @return All transformations that depend on the conclusion, from all of the environment's conclusion processes
	 *
	 * A transformation t1 depends on a conclusion process if it depends on any of the
	 * conclusion process' transformations. Therefore the set of all derivatives of the
	 * conclusion is the union of all derivatives of the conclusion's transformations.
	 */
	public Transformation[] findDerivatives(ConclusionProcess conclusion) {
		return Arrays.stream(conclusion.getTransformations())
			.flatMap(dependency -> Arrays.stream(findDerivatives(dependency)))
			.distinct()
			.toArray(Transformation[]::new);
	}

	/**
	 * Given a transformation inside a conclusion process of the environment, find
	 * all transformations that depend directly or indirectly on the transformation.
	 *
	 * @param dependency
	 * @return All transformations that depend on the given transformation, from all
	 *	 of the environment's conclusion processes
	 *
	 * A transformation t1 depends on another transformation t2 directly, if t2 occurs
	 * anywhere in t1's justification graph. Dependency in general is defined as the
	 * transitive closure of direct dependency.
	 */
	public Transformation[] findDerivatives(Transformation dependency) {
		return findDerivativesRecursive(dependency, this::isDirectDependency);
	}

	/**
	 * Helper method for @see findDerivatives(Transformation) and @see
	 * Transformation(Formula). Given a way to test for direct dependency,
	 * retrieves all derivatives.
	 *
	 * @param dependency The object whose derivatives should be retrieved. Currently
	 * 	either a @see Formula or a @see Transformation.
	 * @param isDirectDependency A predicate that, given an instance of T and a
	 * 	transformation, checks if the transformation depends directly on the first argument.
	 * @return All transformations that depend on the given object, from all
	 *	 of the environment's conclusion processes
	 *
	 * This methods first finds direct dependencies using the test methods below.
	 * It then proceeds to do a fixed-point iteration to retrieve indirect dependencies.
	 */
	protected <T> Transformation[] findDerivativesRecursive(T dependency, BiPredicate<T, Transformation> isDirectDependency) {
		// direct derivatives
		Set<Transformation> derivatives = findTransformations(
				transformation -> isDirectDependency.test(dependency, transformation)
		);

		// transitive dependencies via fixed-point iteration for relation has_direct_derivative⁺
		Set<Transformation> newDerivatives = derivatives;
		int oldSize = 0, newSize = derivatives.size();
		do {
			oldSize = newSize;
			newDerivatives = newDerivatives.stream()
				.flatMap(derivative -> Arrays.stream(findDerivatives(derivative)))
				.collect(Collectors.toSet());
			derivatives.addAll(newDerivatives);
			newSize = derivatives.size();
		} while (oldSize < newSize);

		return derivatives.toArray(new Transformation[derivatives.size()]);
	}

	/**
	 * Given two transformation, decides if the first is a direct dependency of the second.
	 *
	 * @param dependency
	 * @param derivative
	 * @return true if it is a dependency, false otherwise
	 *
	 * A transformation t2 directly depends on another transformation t1 if t2's justification
	 * depends on t1.
	 */
	public boolean isDirectDependency(Transformation dependency, Transformation derivative) {
		return isDirectDependency(dependency, derivative.getJustification());
	}

	/**
	 * Given a premise and a transformation, decides if the transformation directly depends on the premise.
	 *
	 * @param premise An environment premise
	 * @param derivative
	 * @return true if it is a dependency, false otherwise
	 *
	 * A transformation t directly depends on a premise p if t's justification depends on p.
	 */
	public boolean isDirectDependency(Formula premise, Transformation derivative) {
		return isDirectDependency(premise, derivative.getJustification());
	}

	/**
	 * Given a premise and a justification, decides if the latter directly depends on the former
	 *
	 * @param premise
	 * @param derivative
	 * @return
	 *
	 * The rules for direct dependency are visible in this method's source code.
	 * Basically, an EnvironmentPremiseJustification depends on the premise it
	 * refers to. Any other type of justification directly depends on a premise
	 * if the justifications or transitions contained within it depend on the premise.
	 */
	public boolean isDirectDependency(Formula premise, Justification derivative) {
		if (derivative instanceof EnvironmentPremiseJustification)
			return DslHelper.equal(derivative.getJustifiedFormula(), premise);

		else if (derivative instanceof ProofJustification) {
			ProofJustification proof = (ProofJustification)derivative;
			Transformation[] usedTransformations = Arrays.copyOfRange(
					proof.getConclusion().getTransformations(),
					proof.getStartTermIndex(),
					proof.getEndTermIndex());
			return Arrays.stream(usedTransformations).anyMatch(transformation -> isDirectDependency(premise, transformation));

		} else if (derivative instanceof RuleApplication)
			return Arrays.stream(((RuleApplication)derivative).getPremiseJustifications())
				.anyMatch(justification -> isDirectDependency(premise, justification));

		else if (derivative instanceof SubtermEqualityJustification)
			return isDirectDependency(premise, ((SubtermEqualityJustification) derivative).getJustification());

		throw new IllegalStateException("Unsupported justification type: " + derivative.getClass());
	}

	/**
	 * Given a transformation and a justification, decides if the justification directly depends
	 * on the transformation.
	 *
	 * @param dependency
	 * @param derivative
	 * @return
	 *
	 * Similarly to the overloaded method, direct dependency is defined recursively
	 * over the justifications or transformations contained in the justification. 
	 */
	public boolean isDirectDependency(Transformation dependency, Justification derivative) {
		if (derivative instanceof EnvironmentPremiseJustification)
			return false;

		else if (derivative instanceof ProofJustification) {
			ProofJustification proof = (ProofJustification)derivative;
			Transformation[] usedTransformations = Arrays.copyOfRange(
					proof.getConclusion().getTransformations(),
					proof.getStartTermIndex(),
					proof.getEndTermIndex());
			return Arrays.stream(usedTransformations).anyMatch(transformation ->
				dependency == transformation || isDirectDependency(dependency, transformation)
			);

		} else if (derivative instanceof RuleApplication)
			return Arrays.stream(((RuleApplication)derivative).getPremiseJustifications())
				.anyMatch(justification -> isDirectDependency(dependency, justification));

		else if (derivative instanceof SubtermEqualityJustification)
			return isDirectDependency(dependency, ((SubtermEqualityJustification) derivative).getJustification());

		throw new IllegalStateException("Unsupported justification type: " + derivative.getClass());
	}

	/**
	 * Iterates through all transformations in the environment and returns those
	 * matching a condition.
	 *
	 * @param searchCondition
	 * @return
	 */
	protected Set<Transformation> findTransformations(Predicate<Transformation> searchCondition) {
		return Arrays.stream(environment.getConclusions())
			.flatMap(conclusion ->
				Arrays.stream(conclusion.getTransformations())
				.filter(searchCondition)
			).collect(Collectors.toSet());
	}
}
