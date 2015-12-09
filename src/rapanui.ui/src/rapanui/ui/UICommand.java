package rapanui.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Supplier;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Parser;

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

	public static UICommand createConclusionProcess(ProofEnvironment environment, Supplier<String> startTermSupplier) {
		return new RunnableUICommand(
			() -> environment.addConclusion(Parser.getInstance().parseTerm(startTermSupplier.get()))
		);
	}

	public static UICommand createFormulaPremise(ProofEnvironment environment, Supplier<String> premiseSupplier) {
		return new RunnableUICommand(
			() -> environment.addPremise(Parser.getInstance().parseFormula(premiseSupplier.get()))
		);
	}

	public static UICommand createProofEnvironment(Application app) {
		return new RunnableUICommand(app::createEnvironment);
	}
}
