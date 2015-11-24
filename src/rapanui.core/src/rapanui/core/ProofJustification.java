package rapanui.core;

import rapanui.dsl.moai.Formula;

public class ProofJustification extends Justification {
	private final ConclusionProcess conclusion;
	private final int startTermIndex;
	private final int endTermIndex;

	public ProofJustification(
			Formula justifiedFormula,
			ConclusionProcess conclusion,
			int startTermIndex,
			int endTermIndex) {
		super(justifiedFormula);

		assert conclusion != null;
		assert 0 <= startTermIndex && startTermIndex <= endTermIndex;
		assert endTermIndex < conclusion.getTransformations().length;

		this.conclusion = conclusion;
		this.startTermIndex = startTermIndex;
		this.endTermIndex = endTermIndex;
	}

	public ConclusionProcess getConclusion() {
		return conclusion;
	}

	public int getStartTermIndex() {
		return startTermIndex;
	}

	public int getEndTermIndex() {
		return endTermIndex;
	}
}
