package rapanui.core;

import rapanui.dsl.moai.Formula;

public interface ProofEnvironmentObserver {
	void premiseAdded(Formula premise);
	void premiseRemoved(Formula premise);

	void conclusionStarted(ConclusionProcess conclusion);
	void conclusionRemoved(ConclusionProcess conclusion);
	void conclusionMoved(ConclusionProcess conclusion);
}
