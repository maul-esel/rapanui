package rapanui.ui;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Parser;
import rapanui.dsl.moai.DefinitionReference;
import rapanui.dsl.moai.MoaiFactory;

final class MockData {
	static void mockPremises(ProofEnvironment env) {
		env.addPremise(Parser.getInstance().parseFormula("R;S = S;R"));
		env.addPremise(createDefinitionReference("R;R", "reflexiv"));
		env.addPremise(Parser.getInstance().parseFormula("S = R;R"));
		env.addPremise(createDefinitionReference("S;S", "transitiv"));
	}

	static DefinitionReference createDefinitionReference(String term, String defName) {
		DefinitionReference ref = MoaiFactory.eINSTANCE.createDefinitionReference();
		ref.setTarget(Parser.getInstance().parseTerm(term));
		ref.setDefinitionName("\"" + defName + "\"");
		return ref;
	}

	static void createAndMockConclusionProcess(ProofEnvironment env, String term) {
		env.addConclusion(Parser.getInstance().parseTerm(term));
	}
}
