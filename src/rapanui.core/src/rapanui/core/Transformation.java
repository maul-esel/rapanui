package rapanui.core;

import rapanui.dsl.moai.Formula;
import rapanui.dsl.moai.Rule;
import rapanui.dsl.moai.Term;

/**
 * Describes a transformation step from one term to another.
 *
 * This class is immutable.
 *
 */
public class Transformation {
	private final Term input;
	private final Term output;
	private final Rule rule;
	private final int ruleConclusionIndex;
	private final Formula[] premiseMatching;

	/**
	 * Creates a new transformation from the given parameters.
	 *
	 * @param input The original term which is transformed
	 * @param output The result of the transformation
	 * @param rule The underlying rule which allows the transformation
	 * @param int The index of the used conclusion among the rule's conclusions
	 * @param premiseMatching Matches the rule's premises to known facts or premises of the environment
	 */
	Transformation(Term input, Term output, Rule rule, int ruleConclusionIndex, Formula[] premiseMatching) {
		// TODO: validate parameters? Or rely on valid input?

		assert input != null;
		assert output != null;
		assert rule != null;
		assert 0 <= ruleConclusionIndex && ruleConclusionIndex < rule.getConclusions().size();
		assert premiseMatching != null;

		this.input = input;
		this.output = output;
		this.rule = rule;
		this.ruleConclusionIndex = ruleConclusionIndex;
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

	public int getRuleConclusionIndex() {
		return ruleConclusionIndex;
	}

	public Formula[] getPremiseMatching() {
		return premiseMatching;
	}
}
