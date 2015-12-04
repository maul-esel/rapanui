package rapanui.core;

public interface ConclusionProcessObserver {
	void transformationAdded(Transformation transformation);
	void transformationRemoved(Transformation transformation); // unused for now
}
