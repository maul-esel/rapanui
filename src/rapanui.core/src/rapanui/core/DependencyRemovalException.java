package rapanui.core;

public class DependencyRemovalException extends Exception {
	private static final long serialVersionUID = 1L;

	DependencyRemovalException() {
		super("Cannot remove an object that is a dependency of a transformation.");
	}
}
