package rapanui.ui;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.RuleSystem;

public interface ApplicationObserver {
	void ruleSystemLoaded(RuleSystem loaded);
	void environmentAdded(ProofEnvironment environment);
	void environmentRemoved(ProofEnvironment environment);
}
