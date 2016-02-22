package rapanui.ui.commands;

import rapanui.ui.models.ApplicationModel;
import rapanui.ui.models.ProofEnvironmentModel;

public class DeleteEnvironmentCommand extends AbstractCommand implements ApplicationModel.Observer {
	private static final long serialVersionUID = 1L;

	private final ApplicationModel appModel;

	public DeleteEnvironmentCommand(ApplicationModel appModel) {
		super("\u2718", "Aktuellen Beweis löschen");
		this.appModel = appModel;

		appModel.addObserver(this);
		updateEnabled();
	}

	@Override
	public void execute() {
		appModel.removeEnvironment(appModel.getActiveEnvironment());
	}

	@Override
	protected boolean canExecute() {
		return appModel.getActiveEnvironment() != null;
	}

	/* ****************************************** *
	 * ApplicationModel.Observer                  *
	 * ****************************************** */

	@Override
	public void environmentDeleted(ProofEnvironmentModel environmentModel) {
		updateEnabled();
	}

	@Override
	public void environmentCreated(ProofEnvironmentModel environmentModel) {
		updateEnabled();
	}

	@Override
	public void environmentActivated(ProofEnvironmentModel environmentModel) {
		updateEnabled();
	}
}
