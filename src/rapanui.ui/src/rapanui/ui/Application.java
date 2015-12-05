package rapanui.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Parser;
import rapanui.dsl.RuleSystem;

public class Application {
	private final List<ApplicationObserver> observers = new ArrayList<ApplicationObserver>();
	private final List<ProofEnvironment> environments = new ArrayList<ProofEnvironment>();
	private final List<RuleSystem> ruleSystems = new ArrayList<RuleSystem>();

	public static void main(String[] args) {
		Application instance = new Application();
		new MainWindow(instance);
	}

	public Application() {
		try {
			loadRuleSystem("../rapanui.library/library.raps");
		} catch (IOException e) {
			e.printStackTrace();
		}

		createEnvironment(); // always create initial environment

		// TODO: remove dummy data
		createEnvironment();
		createEnvironment();
	}

	public String[] getKnownDefinitionNames() {
		return ruleSystems.stream()
			.flatMap(system -> Arrays.stream(system.getDefinitionNames()))
			.toArray(String[]::new);
	}

	public void loadRuleSystem(String fileName) throws IOException {
		String source = new String(Files.readAllBytes(Paths.get(fileName)));
		RuleSystem system = Parser.getInstance().parseRuleSystem(source);
		ruleSystems.add(system);
		notifyObservers(o -> o.ruleSystemLoaded(system));
	}

	public ProofEnvironment[] getEnvironments() {
		return environments.toArray(new ProofEnvironment[environments.size()]);
	}

	public void createEnvironment() {
		ProofEnvironment environment = new ProofEnvironment(ruleSystems.toArray(new RuleSystem[ruleSystems.size()]));
		environments.add(environment);

		// TODO: remove mock data
		MockData.mockPremises(this, environment);
		MockData.createAndMockConclusionProcess(environment, "R");
		MockData.createAndMockConclusionProcess(environment, "R");

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