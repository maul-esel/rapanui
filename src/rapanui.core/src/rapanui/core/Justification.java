package rapanui.core;

import rapanui.dsl.Formula;

public abstract class Justification {
	private final Formula justifiedFormula;

	protected Justification(Formula justifiedFormula) {
		assert justifiedFormula != null;
		this.justifiedFormula = justifiedFormula;
	}

	public Formula getJustifiedFormula() {
		return justifiedFormula;
	}
}
