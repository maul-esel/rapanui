package rapanui.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

import rapanui.dsl.Parser;
import rapanui.dsl.moai.RuleSystem;

public class Application {
	private final List<RuleSystem> ruleSystems = new ArrayList<RuleSystem>();

	private final Parser parser = new Parser();

	public static void main(String[] args) {
		new Application().run();
	}

	public void run() {
		new MainWindow(this);
	}

	public void loadRuleSystem(String fileName) throws IOException {
		String source = new String(Files.readAllBytes(Paths.get(fileName)));
		RuleSystem system = parser.parseRuleSystem(source);
		ruleSystems.add(system);
	}
}