package rapanui.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AggregateJustificationFinder implements JustificationFinder {
	private final List<JustificationFinder> finders = new LinkedList<JustificationFinder>();

	public AggregateJustificationFinder() {}

	public AggregateJustificationFinder(Collection<? extends JustificationFinder> finders) {
		this();
		this.finders.addAll(finders);
	}

	@Override
	public Emitter<Justification> justifyAsync(JustificationRequest request) {
		return Emitter.combine(
			finders.stream()
			.map(finder -> finder.justifyAsync(request))
			.collect(Collectors.toList())
		);
	}
}
