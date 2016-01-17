package rapanui.ui;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Parser;
import rapanui.dsl.Builder;
import rapanui.dsl.DefinitionReference;
import rapanui.dsl.RuleSystem;

final class MockData {
	static void mockPremises(Application app, ProofEnvironment env) {
		env.addPremise(Parser.getInstance().parseFormula("R;S = S;R"));
		env.addPremise(createDefinitionReference(app, "R;R", "reflexiv"));
		env.addPremise(Parser.getInstance().parseFormula("S = R;R"));
		env.addPremise(createDefinitionReference(app, "S;S", "transitiv"));
	}

	static DefinitionReference createDefinitionReference(Application app, String term, String defName) {
		return Builder.createDefinitionReference(
			Parser.getInstance().parseTerm(term),
			getRuleSystem(app).getDefinitions().stream().filter(p -> p.getName().equals(defName)).findFirst().orElse(null)
		);
	}

	static void createAndMockConclusionProcess(ProofEnvironment env, String term) {
		env.addConclusion(Parser.getInstance().parseTerm(term));
	}

	static RuleSystem getRuleSystem(Application app) {
		return app.getRuleSystems().get(0);
	}
}
