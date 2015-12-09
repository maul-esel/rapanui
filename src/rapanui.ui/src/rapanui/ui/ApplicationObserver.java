package rapanui.ui;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.RuleSystem;

public interface ApplicationObserver {
	void environmentAdded(ProofEnvironment environment);
	void environmentRemoved(ProofEnvironment environment);
}
