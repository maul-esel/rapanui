package rapanui.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.DefinitionReference;
import rapanui.dsl.DslFactory;
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

	public static UICommand createConclusionProcess(ProofEnvironment environment, JTextComponent input) {
		return new RunnableUICommand(() -> {
			environment.addConclusion(Parser.getInstance().parseTerm(input.getText()));
			input.setText(null);
		});
	}

	public static UICommand createFormulaPremise(ProofEnvironment environment, JTextComponent input) {
		return new RunnableUICommand(() -> {
			environment.addPremise(Parser.getInstance().parseFormula(input.getText()));
			input.setText(null);
		});
	}

	public static UICommand createDefinitionReferencePremise(ProofEnvironment environment, JTextComponent input,
			JComboBox<String> definitionSelection) {
		return new RunnableUICommand(() -> {
			DefinitionReference reference = DslFactory.eINSTANCE.createDefinitionReference();
			reference.setTarget(Parser.getInstance().parseTerm(input.getText()));
			reference.setDefinition(environment.getRuleSystems()
					.resolveDefinition(definitionSelection.getSelectedItem().toString())
			);
			environment.addPremise(reference);
			input.setText(null);
		});
	}

	public static UICommand createProofEnvironment(Application app) {
		return new RunnableUICommand(app::createEnvironment);
	}
}
