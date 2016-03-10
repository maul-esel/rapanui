package rapanui.ui.commands;

import rapanui.ui.models.ConclusionProcessModel;

public class DeleteConclusionCommand extends AbstractCommand {
	private static final long serialVersionUID = 1L;

	private final ConclusionProcessModel conclusion;

	public DeleteConclusionCommand(ConclusionProcessModel conclusion) {
		super("\u2718", "Folgerung entfernen");
		this.conclusion = conclusion;
		updateEnabled();
	}

	@Override
	public void execute() {
		conclusion.remove();
	}

	@Override
	protected boolean canExecute() {
		return true;
	}
}
