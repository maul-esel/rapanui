package rapanui.core;

import java.util.function.Predicate;

import rapanui.dsl.Formula;

public class KnowledgeBase {
	public Emitter<Justification> findAllAsync(Predicate<Formula> selector) {
		return Emitter.fromResultComputation(consume -> { /* TODO */ });
	}

	public Emitter<Justification> findFirstAsync(Predicate<Formula> selector) {
		return Emitter.first(findAllAsync(selector), 1);
	}
}
