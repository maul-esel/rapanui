package rapanui.ui;

import java.lang.reflect.Field;
import java.util.List;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Parser;
import rapanui.dsl.DefinitionReference;
import rapanui.dsl.DslFactory;
import rapanui.dsl.RuleSystem;

final class MockData {
	static void mockPremises(Application app, ProofEnvironment env) {
		env.addPremise(Parser.getInstance().parseFormula("R;S = S;R"));
		env.addPremise(createDefinitionReference(app, "R;R", "reflexiv"));
		env.addPremise(Parser.getInstance().parseFormula("S = R;R"));
		env.addPremise(createDefinitionReference(app, "S;S", "transitiv"));
	}

	static DefinitionReference createDefinitionReference(Application app, String term, String defName) {
		DefinitionReference ref = DslFactory.eINSTANCE.createDefinitionReference();
		ref.setTarget(Parser.getInstance().parseTerm(term));

		ref.setDefinition(
			getRuleSystem(app).getDefinitions().stream().filter(p -> p.getName().equals(defName)).findFirst().orElse(null)
		);
		return ref;
	}

	static void createAndMockConclusionProcess(ProofEnvironment env, String term) {
		env.addConclusion(Parser.getInstance().parseTerm(term));
	}

	static RuleSystem getRuleSystem(Application app) {
		try {
			Field f = Application.class.getDeclaredField("ruleSystems");
			f.setAccessible(true);
			return ((List<RuleSystem>)f.get(app)).get(0);
		} catch (Exception e) {
			return null;
		}
	}
}
