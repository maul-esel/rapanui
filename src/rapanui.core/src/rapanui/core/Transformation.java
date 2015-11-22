package rapanui.core;

import rapanui.dsl.moai.Formula;
import rapanui.dsl.moai.Rule;
import rapanui.dsl.moai.Term;

public class Transformation {
	private final Term input;
	private final Term output;
	private final Rule rule;
	private final Formula[] premiseMatching;

	Transformation(Term input, Term output, Rule rule, Formula[] premiseMatching) {
		// TODO: validate parameters? Or rely on valid input?

		assert input != null;
		assert output != null;
		assert rule != null;
		assert premiseMatching != null;

		this.input = input;
		this.output = output;
		this.rule = rule;
		this.premiseMatching = premiseMatching;
	}

	public Term getInput() {
		return input;
	}

	public Term getOutput() {
		return output;
	}

	public Rule getRule() {
		return rule;
	}

	public Formula[] getPremiseMatching() {
		return premiseMatching;
	}
}
