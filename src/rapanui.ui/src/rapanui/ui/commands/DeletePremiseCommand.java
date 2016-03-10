package rapanui.ui.commands;

import rapanui.dsl.Predicate;
import rapanui.ui.models.ProofEnvironmentModel;

public class DeletePremiseCommand extends AbstractCommand {
	private static final long serialVersionUID = 1L;

	private final ProofEnvironmentModel environment;
	private final Predicate premise;

	public DeletePremiseCommand(ProofEnvironmentModel environment, Predicate premise) {
		super("\u2718", "Voraussetzung entfernen");
		this.environment = environment;
		this.premise = premise;
		updateEnabled();
	}

	@Override
	public void execute() {
		environment.removePremise(premise);
	}

	@Override
	protected boolean canExecute() {
		return true;
	}

}
