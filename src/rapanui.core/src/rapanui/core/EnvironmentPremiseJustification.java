package rapanui.core;

import rapanui.dsl.Formula;

/**
 * Placeholder {@link Justification} for formulas contained in the environment's premises.
 */
public class EnvironmentPremiseJustification extends Justification {
	public EnvironmentPremiseJustification(Formula justifiedFormula) {
		super(justifiedFormula);
	}
}
