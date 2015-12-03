package rapanui.ui;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Parser;
import rapanui.dsl.moai.DefinitionReference;
import rapanui.dsl.moai.MoaiFactory;

final class MockData {
	private static final Parser parser = new Parser();

	static void mockPremises(ProofEnvironment env) {
		env.addPremise(parser.parseFormula("R;S = S;R"));
		env.addPremise(createDefinitionReference("R;R", "reflexiv"));
		env.addPremise(parser.parseFormula("S = R;R"));
		env.addPremise(createDefinitionReference("S;S", "transitiv"));
	}

	static DefinitionReference createDefinitionReference(String term, String defName) {
		DefinitionReference ref = MoaiFactory.eINSTANCE.createDefinitionReference();
		ref.setTarget(parser.parseTerm(term));
		ref.setDefinitionName("\"" + defName + "\"");
		return ref;
	}
}
