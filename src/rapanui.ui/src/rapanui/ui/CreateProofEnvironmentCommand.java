package rapanui.ui;

public class CreateProofEnvironmentCommand extends UICommand<Application> {
	protected CreateProofEnvironmentCommand(Application target) {
		super(target);
	}

	@Override
	public void execute() {
		target.createEnvironment();
	}
}
