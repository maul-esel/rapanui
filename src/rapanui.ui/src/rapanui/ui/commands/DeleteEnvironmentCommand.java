package rapanui.ui.commands;

import rapanui.core.Transformation;
import rapanui.ui.Application;
import rapanui.ui.models.ApplicationModel;
import rapanui.ui.models.ProofEnvironmentModel;

public class DeleteEnvironmentCommand extends AbstractCommand implements ApplicationModel.Observer {
	private static final long serialVersionUID = 1L;

	private final Application app;
	private final ApplicationModel appModel;

	public DeleteEnvironmentCommand(ApplicationModel appModel, Application app) {
		super("\u2718", "Aktuellen Beweis löschen");
		this.app = app;
		this.appModel = appModel;

		appModel.addObserver(this);		
	}

	@Override
	public void execute() {
		app.removeEnvironment(appModel.getActiveEnvironment().getUnderlyingModel());
	}

	@Override
	public boolean isEnabled() {
		return appModel.getActiveEnvironment() != null;
	}

	@Override
	public void environmentDeleted(ProofEnvironmentModel environmentModel) {
		updateEnabled();
	}

	@Override
	public void environmentCreated(ProofEnvironmentModel environmentModel) {}

	@Override
	public void environmentActivated(ProofEnvironmentModel environmentModel) {}

	@Override
	public void suggestionsLoaded(Transformation[] suggestions) {}

	
}
