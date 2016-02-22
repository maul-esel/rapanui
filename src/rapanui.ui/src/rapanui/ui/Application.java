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
import rapanui.ui.models.ApplicationModel;
import rapanui.ui.views.MainWindow;

public class Application {
	private final List<ApplicationObserver> observers = new ArrayList<ApplicationObserver>();
	private final List<ProofEnvironment> environments = new ArrayList<ProofEnvironment>();

	private final RuleSystemCollection ruleSystems = new RuleSystemCollection();

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

	public Emitter<Transformation> loadSuggestions(ConclusionProcess target, BINARY_RELATION suggestionType) {
		return SuggestionFinder.getDefaultInstance().makeSuggestionsAsync(target, suggestionType);
	}

	public void applySuggestion(ConclusionProcess target, Transformation suggestion) {
		assert target != null;
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