package rapanui.ui.commands;

import rapanui.core.Application;

public class CreateEnvironmentCommand extends AbstractCommand {
	private static final long serialVersionUID = 1L;

	private final Application app;

	public CreateEnvironmentCommand(Application app) {
		super("\u2A01", "Neuen Beweis starten");
		this.app = app;
	}

	@Override
	public void execute() {
		app.createEnvironment();
	}

	@Override
	protected boolean canExecute() { return true; }
}
