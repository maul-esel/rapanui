package rapanui.ui.commands;

import rapanui.ui.models.ApplicationModel;
import rapanui.ui.models.ProofEnvironmentModel;

public class ExportEnvironmentCommand extends AbstractCommand implements ApplicationModel.Observer {
	private static final long serialVersionUID = 1L;

	private final ApplicationModel model;

	public ExportEnvironmentCommand(ApplicationModel model) {
		super("\u2b8b", "Beweis exportieren");
		this.model = model;
		model.addObserver(this);
		updateEnabled();
	}

	@Override
	public void execute() {
		model.getActiveEnvironment().export();
	}

	@Override
	protected boolean canExecute() {
		return model.getActiveEnvironment() != null;
	}

	@Override
	public void environmentActivated(ProofEnvironmentModel environment) {
		updateEnabled();
	}
}
