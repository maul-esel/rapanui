package rapanui.ui;

import rapanui.core.ProofEnvironment;

public interface ApplicationObserver {
	void environmentAdded(ProofEnvironment environment);
	void environmentRemoved(ProofEnvironment environment);
}
