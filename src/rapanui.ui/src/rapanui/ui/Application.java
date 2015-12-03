package rapanui.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Parser;
import rapanui.dsl.moai.RuleSystem;

public class Application {
	private final List<ApplicationObserver> observers = new ArrayList<ApplicationObserver>();
	private final List<ProofEnvironment> environments = new ArrayList<ProofEnvironment>();
	private final List<RuleSystem> ruleSystems = new ArrayList<RuleSystem>();

	private final Parser parser = new Parser();

	public static void main(String[] args) {
		Application instance = new Application();
		new MainWindow(instance);
	}

	public Application() {
		createEnvironment(); // always create initial environment

		// TODO: remove dummy data
		createEnvironment();
		createEnvironment();
	}

	public void loadRuleSystem(String fileName) throws IOException {
		String source = new String(Files.readAllBytes(Paths.get(fileName)));
		RuleSystem system = parser.parseRuleSystem(source);
		ruleSystems.add(system);
		notifyObservers(o -> o.ruleSystemLoaded(system));
	}

	public void createEnvironment() {
		ProofEnvironment environment = new ProofEnvironment(ruleSystems.toArray(new RuleSystem[ruleSystems.size()]));
		environments.add(environment);
		notifyObservers(o -> o.environmentAdded(environment));
	}

	public void removeEnvironment(ProofEnvironment environment) {
		if (environments.remove(environment))
			notifyObservers(o -> o.environmentRemoved(environment));
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