package rapanui.ui.commands;

import rapanui.core.ProofEnvironment;
import rapanui.core.Transformation;
import rapanui.ui.Application;
import rapanui.ui.models.ApplicationModel;
import rapanui.ui.models.ProofEnvironmentModel;

import java.util.function.Supplier;

public class DeleteEnvironmentCommand extends AbstractCommand implements ApplicationModel.Observer {
	private static final long serialVersionUID = 1L;

	private final Application app;
	private final Supplier<ProofEnvironment> targetEnvironment;

	public DeleteEnvironmentCommand(ApplicationModel appModel, Application app) {
		super("\u2718", "Aktuellen Beweis löschen");
		this.app = app;
		targetEnvironment = () -> appModel.getActiveEnvironment().getUnderlyingModel();

		appModel.addObserver(this);		
	}

	@Override
	public void execute() {
		app.removeEnvironment(targetEnvironment.get());
	}

	@Override
	public boolean isEnabled() {
		return targetEnvironment.get() != null;
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
