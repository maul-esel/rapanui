package rapanui.core;

public interface ConclusionProcessObserver {
	void transformationAdded(Transformation t);
	void transformationRemoved(Transformation transformation);
}
