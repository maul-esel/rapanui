package rapanui.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class that aggregates the results of multiple @see JustificationFinder implementations
 */
public class AggregateJustificationFinder implements JustificationFinder {
	private final List<JustificationFinder> finders = new LinkedList<JustificationFinder>();

	/**
	 * Creates a new instance with an empty collection of @see JustificationFinder implementations
	 */
	public AggregateJustificationFinder() {}

	/**
	 * Creates a new instance and initializes it with the given @see JustificationFinder implementations
	 *
	 * @param finders A @see Collection of @see JustificationFinder instances that this instance delegates requests to
	 */
	public AggregateJustificationFinder(Collection<? extends JustificationFinder> finders) {
		this();
		this.finders.addAll(finders);
	}

	/**
	 * Adds a new @see JustificationFinder to this instance's list
	 *
	 * @param finder The new @see JustificationFinder. It will be used in all future calls to @see justifyAsync.
	 * 	Must not be null.
	 */
	public void addJustificationFinder(JustificationFinder finder) {
		assert finder != null;
		finders.add(finder);
	}

	/**
	 * Implements the @see JustificationFinder method by delegating to all @see JustificationFinder implementations
	 * stored in this instance.
	 *
	 * @return An emitter that combines the emitters returned by all the employed instances.
	 */
	@Override
	public Emitter<Justification> justifyAsync(ProofEnvironment environment, FormulaTemplate formulaTemplate, int recursionDepth) {
		return Emitter.combine(
			finders.stream()
			.map(finder -> finder.justifyAsync(environment, formulaTemplate, recursionDepth))
			.collect(Collectors.toList())
		);
	}
}
