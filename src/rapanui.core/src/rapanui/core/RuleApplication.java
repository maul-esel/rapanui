package rapanui.core;

import java.util.Map;

import rapanui.dsl.moai.Formula;
import rapanui.dsl.moai.Rule;
import rapanui.dsl.moai.Term;

public class RuleApplication extends Justification {
	private final Rule appliedRule;
	private final Map<String, Term> variableTranslation;
	private final Justification[] premiseJustifications;

	public RuleApplication(
			Formula justifiedFormula,
			Rule appliedRule,
			Map<String, Term> variableTranslation,
			Justification[] premiseJustifications) {
		super(justifiedFormula);

		assert appliedRule !=  null;
		assert variableTranslation != null;
		assert premiseJustifications != null;

		this.appliedRule = appliedRule;
		this.variableTranslation = variableTranslation;
		this.premiseJustifications = premiseJustifications;
	}

	public Rule getAppliedRule() {
		return appliedRule;
	}

	public Justification[] getPremiseJustifications() {
		return premiseJustifications;
	}
}
