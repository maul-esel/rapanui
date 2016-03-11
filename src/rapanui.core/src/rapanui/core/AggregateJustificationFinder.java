package rapanui.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import rapanui.dsl.Formula;

/**
 * Helper class that aggregates the results of multiple {@link JustificationFinder} implementations
 */
public class AggregateJustificationFinder implements JustificationFinder {
	private final List<JustificationFinder> finders = new LinkedList<JustificationFinder>();

	/**
	 * Creates a new instance with an empty collection of {@link JustificationFinder} implementations
	 */
	public AggregateJustificationFinder() {}

	/**
	 * Creates a new instance and initializes it with the given {@link JustificationFinder} implementations
	 *
	 * @param finders A {@link Collection} of {@link JustificationFinder} instances that this instance delegates requests to
	 */
	public AggregateJustificationFinder(Collection<? extends JustificationFinder> finders) {
		this();
		this.finders.addAll(finders);
	}

	/**
	 * Adds a new {@link JustificationFinder} to this instance's list
	 *
	 * @param finder The new {@link JustificationFinder}. It will be used in all future calls to
	 * {@link #justifyAsync(ProofEnvironment, Formula, int)}. Must not be null.
	 */
	public void addJustificationFinder(JustificationFinder finder) {
		assert finder != null;
		finders.add(finder);
	}

	/**
	 * Implements the {@link JustificationFinder} method by delegating to all {@link JustificationFinder} implementations
	 * stored in this instance.
	 *
	 * @return An emitter that combines the emitters returned by all the employed instances.
	 */
	@Override
	public Emitter<Justification> justifyAsync(ProofEnvironment environment, Formula formulaTemplate, int recursionDepth) {
		if (recursionDepth < 0)
			return Emitter.empty();
		return Emitter.combine(
			finders.stream()
			.map(finder -> finder.justifyAsync(environment, formulaTemplate, recursionDepth))
			.collect(Collectors.toList())
		);
	}
}
