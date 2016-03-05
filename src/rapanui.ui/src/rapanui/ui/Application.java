package rapanui.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import rapanui.core.ConclusionProcess;
import rapanui.core.Emitter;
import rapanui.core.ProofEnvironment;
import rapanui.core.SuggestionFinder;
import rapanui.core.Transformation;
import rapanui.dsl.BINARY_RELATION;
import rapanui.dsl.RuleSystemCollection;

public class Application {
	private final List<Observer> observers = new ArrayList<Observer>();
	private final List<ProofEnvironment> environments = new ArrayList<ProofEnvironment>();

	private final RuleSystemCollection ruleSystems = new RuleSystemCollection();

	public Application() {
		ruleSystems.load("../rapanui.library/library.raps");
		createEnvironment(); // always create initial environment
	}

	public ProofEnvironment[] getEnvironments() {
		return environments.toArray(new ProofEnvironment[environments.size()]);
	}

	public RuleSystemCollection getRuleSystems() {
		return ruleSystems;
	}

	public void createEnvironment() {
		ProofEnvironment environment = new ProofEnvironment(ruleSystems);
		environments.add(environment);
		notifyObservers(o -> o.environmentAdded(environment));
	}

	public void removeEnvironment(ProofEnvironment environment) {
		if (environments.remove(environment))
			notifyObservers(o -> o.environmentRemoved(environment));
	}

	public Emitter<Transformation> loadSuggestions(ConclusionProcess target, BINARY_RELATION suggestionType) {
		return SuggestionFinder.getDefaultInstance().makeSuggestionsAsync(target, suggestionType);
	}

	public void applySuggestion(ConclusionProcess target, Transformation suggestion) {
		assert target != null;
		target.appendTransformation(suggestion);
	}

	public interface Observer {
		void environmentAdded(ProofEnvironment environment);
		void environmentRemoved(ProofEnvironment environment);
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	public void deleteObserver(Observer observer) {
		observers.remove(observer);
	}

	protected void notifyObservers(Consumer<Observer> notification) {
		for (Observer observer : observers) {
			if (observer != null)
				notification.accept(observer);
		}
	}
}