package rapanui.core;

import rapanui.dsl.Formula;

/**
 * Base class for all justification classes. A justification states why a certain conclusion (see {@link ConclusionProcess})
 * may be drawn or why a rule may be applied (see {@link RuleApplication}).
 */
public abstract class Justification {
	private final Formula justifiedFormula;

	/**
	 * Creates a new instance for the given formula.
	 *
	 * @param justifiedFormula The formula that is justified by the new instance. Must not be null.
	 */
	protected Justification(Formula justifiedFormula) {
		assert justifiedFormula != null;
		this.justifiedFormula = justifiedFormula;
	}

	/**
	 * @return The justified formula. Guaranteed to be non-null.
	 */
	public Formula getJustifiedFormula() {
		return justifiedFormula;
	}
}
