package rapanui.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import rapanui.core.ConclusionProcess;
import rapanui.core.Emitter;
import rapanui.core.FormulaType;
import rapanui.core.ProofEnvironment;
import rapanui.core.SuggestionFinder;
import rapanui.core.Transformation;
import rapanui.dsl.RuleSystemCollection;
import rapanui.ui.models.ApplicationModel;

public class Application {
	private final List<ApplicationObserver> observers = new ArrayList<ApplicationObserver>();
	private final List<ProofEnvironment> environments = new ArrayList<ProofEnvironment>();

	private final RuleSystemCollection ruleSystems = new RuleSystemCollection();

	private static final int MAX_SUGGESTION_CACHE_SIZE = 15;
	private final Cache<ConclusionProcess, Emitter<Transformation>> suggestionCache
		= new Cache<ConclusionProcess, Emitter<Transformation>>(MAX_SUGGESTION_CACHE_SIZE);

	public static void main(String[] args) {
		Application instance = new Application();
		ApplicationModel model = new ApplicationModel(instance);
		new MainWindow(model);
	}

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

	public Emitter<Transformation> loadSuggestions(ConclusionProcess target, FormulaType suggestionType) {
		return suggestionCache.get(target, conclusion ->
			SuggestionFinder.getDefaultInstance().makeSuggestionsAsync(conclusion, suggestionType)
		);
	}

	public void applySuggestion(ConclusionProcess target, Transformation suggestion) {
		assert target != null;

		if (suggestionCache.hasKey(target)) {
			suggestionCache.get(target).stop();
			suggestionCache.delete(target);
		}
		target.appendTransformation(suggestion);
	}

	public void addObserver(ApplicationObserver observer) {
		observers.add(observer);
	}

	public void deleteObserver(ApplicationObserver observer) {
		observers.remove(observer);
	}

	protected void notifyObservers(Consumer<ApplicationObserver> notification) {
		for (ApplicationObserver observer : observers) {
			if (observer != null)
				notification.accept(observer);
		}
	}
}