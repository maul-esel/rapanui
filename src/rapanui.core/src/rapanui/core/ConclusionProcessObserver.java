package rapanui.core;

/**
 * Implemented by classes that wish to be notified about changes to a @see ConclusionProcess
 */
public interface ConclusionProcessObserver {
	/**
	 * Called when a new @see Transformation is appended to the @see ConclusionProcess
	 *
	 * @param transformation The newly appended @see Transformation. Guaranteed to be non-null.
	 */
	void transformationAdded(Transformation transformation);

	/**
	 * Called when a @see Transformation is removed from the conclusion. Currently unused.
	 *
	 * @param transformation The removed @see Transformation. Guaranteed to be non-null.
	 */
	void transformationRemoved(Transformation transformation);
}
