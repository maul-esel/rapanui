package rapanui.core;

import rapanui.dsl.Formula;

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
		// allow inverted ranges (endTermIndex < startTermIndex)
		assert 0 <= startTermIndex && startTermIndex < conclusion.getTransformations().length;
		assert 0 <= endTermIndex && endTermIndex < conclusion.getTransformations().length;

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
