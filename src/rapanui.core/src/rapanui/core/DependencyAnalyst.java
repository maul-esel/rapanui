package rapanui.core;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import rapanui.dsl.Formula;
import rapanui.dsl.Predicate;

/**
 * Analyzes dependencies between conclusions, transformations, justifications and premises
 * within a ProofEnvironment.
 *
 * <p>Definition of what a "dependency" is (in this context) below. If A is a dependency of B,
 * B is called a "derivative" of A.</p>
 */
public class DependencyAnalyst {
	private final ProofEnvironment environment;

	/**
	 * @param environment The environment to analyze.
	 */
	DependencyAnalyst(ProofEnvironment environment) {
		this.environment = environment;
	}

	/**
	 * Retrieves direct derivatives of the given premise.
	 * For the definition of direct dependence, see {@link #isDirectDependency(Predicate, Transformation)}.
	 */
	public Set<Transformation> findDirectDerivatives(Predicate dependency) {
		return filterTransformations(transformation -> isDirectDependency(dependency, transformation));
	}

	/**
	 * Retrieves the direct derivatives of the given transformation.
	 * For the definition of direct dependence, see {@link #isDirectDependency(Transformation, Transformation)}.
	 */
	public Set<Transformation> findDirectDerivatives(Transformation dependency) {
		return filterTransformations(transformation -> isDirectDependency(dependency, transformation));
	}

	/**
	 * Retrieves the direct derivatives of the given conclusion process.
	 *
	 * <p<A transformation (directly) depends on a conclusion process, if it (directly) depends on
	 * one of the conclusion's transformations, but is not itself part of the conclusion.</p>
	 */
	public Set<Transformation> findDirectDerivatives(ConclusionProcess dependency) {
		return Arrays.stream(environment.getConclusions())
			.filter(conclusion -> conclusion != dependency)
			.flatMap(conclusion -> Arrays.stream(conclusion.getTransformations()))
			.filter(derivative ->
				Arrays.stream(dependency.getTransformations())
				.anyMatch(dependencyTransformation -> isDirectDependency(dependencyTransformation, derivative))
			).collect(Collectors.toSet());
	}

	/**
	 * Given a premise, tests if it has any derivatives in the environment
	 *
	 * @param premise
	 * @return true if at least one transformation depends on the premise
	 */
	public boolean hasDerivatives(Predicate premise) {
		return allTransformations()
			.anyMatch(transformation -> isDirectDependency(premise, transformation));
	}

	/**
	 * Given a transformation, tests if it has any derivatives in the environment
	 *
	 * @param dependency
	 * @return true if at least one transformation depends on the given transformation
	 */
	public boolean hasDerivatives(Transformation dependency) {
		return allTransformations()
			.anyMatch(transformation -> isDirectDependency(dependency, transformation));
	}

	/**
	 * Given a conclusion process, tests if it has any derivatives in the environment
	 *
	 * @param dependency
	 * @return true if at least one transformation that does not belong to the given conclusion itself,
	 * 	depends on any transformation in the given conclusion
	 */
	public boolean hasDerivatives(ConclusionProcess dependency) {
		return Arrays.stream(environment.getConclusions())
			.filter(conclusion -> conclusion != dependency)
			.flatMap(conclusion -> Arrays.stream(conclusion.getTransformations()))
			.anyMatch(transformation ->
				Arrays.stream(dependency.getTransformations())
				.anyMatch(d -> isDirectDependency(d, transformation))
			);
	}

	/**
	 * Given an environment premise, find all transformations that directly or
	 * indirectly depend on the premise.
	 *
	 * <p>For the definition of direct dependence, see {@link #isDirectDependency(Predicate, Transformation)}.
	 * Dependency in general is defined as:</p>
	 * 		<pre>{@code depends_on_premise = direct_dependency ∪ direct_dependency ; depends_on_transformation}</pre>
	 *
	 * @param premise
	 * @return All transformations that depend on the premise, from all of the environment's conclusion processes
	 */
	public Set<Transformation> findDerivatives(Predicate premise) {
		return findTransitiveDerivatives(findDirectDerivatives(premise));
	}

	/**
	 * Given a transformation inside a conclusion process of the environment, find
	 * all transformations that depend directly or indirectly on the transformation.
	 *
	 * <p>For the definition of direct dependence, see {@link #isDirectDependency(Transformation, Transformation)}.
	 * Dependency in general is defined as the transitive closure of direct dependency.</p>
	 *
	 * @param dependency
	 * @return All transformations that depend on the given transformation, from all
	 *	 of the environment's conclusion processes
	 */
	public Set<Transformation> findDerivatives(Transformation dependency) {
		return findTransitiveDerivatives(findDirectDerivatives(dependency));
	}

	/**
	 * Given one of the environment's conclusion process, find all transformations
	 * that directly or indirectly depend on the conclusion process.
	 *
	 * <p>A transformation {@code t1} depends on a conclusion process if it depends on any of the
	 * conclusion process' transformations, but is not part of the conclusion itself.
	 * Therefore the set of all derivatives of the conclusion is the union of all
	 * derivatives of the conclusion's transformations, minus the conclusion's transformations.</p>
	 *
	 * @param conclusion
	 * @return All transformations that depend on the conclusion, from all of the environment's conclusion processes
	 */
	public Set<Transformation> findDerivatives(ConclusionProcess conclusion) {
		return Arrays.stream(conclusion.getTransformations())
			.flatMap(dependency -> findDerivatives(dependency).stream())
			.filter(derivative -> derivative.getContainer() != conclusion)
			.collect(Collectors.toSet());
	}

	/**
	 * Helper method for {@link #findDerivatives(Transformation)} and
	 * {@link #findDerivatives(Predicate)}. Given the set of direct derivatives,
	 * retrieves all derivatives.
	 *
	 * <p>This methods does a fixed-point iteration to retrieve indirect dependencies.</p>
	 *
	 * @param directDerivatives The set of direct derivatives
	 *
	 * @return All transformations that depend on the given derivatives, from all
	 *	 of the environment's conclusion processes
	 */
	protected Set<Transformation> findTransitiveDerivatives(Set<Transformation> directDerivatives) {
		Set<Transformation> derivatives = directDerivatives;

		Stream<Transformation> newDerivatives = directDerivatives.stream();
		int oldSize = 0, newSize = derivatives.size();

		// transitive dependencies via fixed-point iteration for relation has_direct_derivative⁺
		while (oldSize < newSize) {
			oldSize = newSize;
			newDerivatives = newDerivatives
				.flatMap(derivative -> findDirectDerivatives(derivative).stream());
			derivatives.addAll(newDerivatives.collect(Collectors.toSet()));
			newSize = derivatives.size();
		}

		return derivatives;
	}

	/**
	 * Given two transformations, decides if the first is a direct dependency of the second.
	 *
	 * <p>A transformation {@code t2} directly depends on another transformation {@code t1}
	 * if {@code t2}'s justification depends on {@code t1}, or {@code t2} follows {@code t1}
	 * in the same conclusion.</p>
	 *
	 * @param dependency
	 * @param derivative
	 * @return true if it is a dependency, false otherwise
	 */
	public boolean isDirectDependency(Transformation dependency, Transformation derivative) {
		return dependsOn(dependency, derivative.getJustification())
			|| isSuccessor(dependency, derivative);
	}

	private boolean isSuccessor(Transformation first, Transformation later) {
		if (first.getContainer() != later.getContainer())
			return false;
		List<Transformation> list = Arrays.asList(first.getContainer().getTransformations());
		return list.indexOf(first) < list.indexOf(later);
	}

	/**
	 * Given a premise and a transformation, decides if the transformation directly
	 * depends on the premise.
	 *
	 * <p>A transformation {@code t} directly depends on a premise {@code p} if {@code t}'s
	 * justification depends on {@code p}.</p>
	 *
	 * @param premise An environment premise
	 * @param derivative
	 * @return true if it is a dependency, false otherwise
	 */
	public boolean isDirectDependency(Predicate premise, Transformation derivative) {
		return dependsOn(premise, derivative.getJustification());
	}

	/**
	 * Given a premise and a justification, decides if the latter depends on the former
	 *
	 * <p>The rules for direct dependency are visible in this method's source code.
	 * Basically, an {@link EnvironmentPremiseJustification} depends on a premise, if that premise
	 * is the only premise to resolve to the formula the justification refers to.
	 * Any other type of justification directly depends on a premise
	 * if the justifications or transformations contained within it depend on the premise.</p>
	 */
	public boolean dependsOn(Predicate premise, Justification derivative) {
		if (derivative instanceof EnvironmentPremiseJustification)
			return !Arrays.stream(environment.getPremises())
				.filter(otherPremise -> otherPremise != premise)
				.anyMatch(otherPremise -> resolvesToFormula(otherPremise, derivative.getJustifiedFormula()));

		else if (derivative instanceof ProofJustification) {
			ProofJustification proof = (ProofJustification)derivative;
			Transformation[] usedTransformations = Arrays.copyOfRange(
					proof.getConclusion().getTransformations(),
					proof.getStartTermIndex(),
					proof.getEndTermIndex());
			return Arrays.stream(usedTransformations).anyMatch(transformation -> isDirectDependency(premise, transformation));

		} else if (derivative instanceof RuleApplication)
			return Arrays.stream(((RuleApplication)derivative).getPremiseJustifications())
				.anyMatch(justification -> dependsOn(premise, justification));

		else if (derivative instanceof SubtermEqualityJustification)
			return dependsOn(premise, ((SubtermEqualityJustification) derivative).getJustification());

		throw new IllegalStateException("Unsupported justification type: " + derivative.getClass());
	}

	private boolean resolvesToFormula(Predicate predicate, Formula formula) {
		return predicate.resolve()
			.stream()
			.anyMatch(formula::structurallyEquals);
	}

	/**
	 * Given a transformation and a justification, decides if the justification directly depends
	 * on the transformation.
	 *
	 * <p>Similarly to the overloaded method, direct dependency is defined recursively
	 * over the justifications or transformations contained in the justification.</p>
	 */
	public boolean dependsOn(Transformation dependency, Justification derivative) {
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
				.anyMatch(justification -> dependsOn(dependency, justification));

		else if (derivative instanceof SubtermEqualityJustification)
			return dependsOn(dependency, ((SubtermEqualityJustification) derivative).getJustification());

		throw new IllegalStateException("Unsupported justification type: " + derivative.getClass());
	}

	protected Set<Transformation> filterTransformations(java.util.function.Predicate<Transformation> searchCondition) {
		return Arrays.stream(environment.getConclusions())
			.flatMap(conclusion ->
				Arrays.stream(conclusion.getTransformations())
				.filter(searchCondition)
			).collect(Collectors.toSet());
	}

	private Stream<Transformation> allTransformations() {
		return Arrays.stream(environment.getConclusions())
			.flatMap(conclusion -> Arrays.stream(conclusion.getTransformations()));
	}
}
