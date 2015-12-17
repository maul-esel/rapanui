package rapanui.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Supplier;

import rapanui.ui.models.ProofEnvironmentModel;

public abstract class UICommand implements ActionListener {
	public abstract void execute();

	@Override
	public void actionPerformed(ActionEvent event) {
		execute();
	}

	protected static class RunnableUICommand extends UICommand {
		private final Runnable executor;

		public RunnableUICommand(Runnable executor) {
			this.executor = executor;
		}

		@Override
		public void execute() {
			executor.run();
		}
	}

	public static UICommand createProofEnvironment(Application app) {
		return new RunnableUICommand(app::createEnvironment);
	}

	public static UICommand removeProofEnvironment(Application app, Supplier<ProofEnvironmentModel> environment) {
		return new RunnableUICommand(() -> {
			app.removeEnvironment(environment.get().getUnderlyingModel());
		});
	}
}
