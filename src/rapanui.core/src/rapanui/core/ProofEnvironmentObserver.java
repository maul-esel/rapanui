package rapanui.core;

import rapanui.dsl.Formula;

public interface ProofEnvironmentObserver {
	void premiseAdded(Formula premise);
	void premiseRemoved(Formula premise); // unused for now

	void conclusionStarted(ConclusionProcess conclusion);
	void conclusionRemoved(ConclusionProcess conclusion); // unused for now
	void conclusionMoved(ConclusionProcess conclusion); // unused for now
}
