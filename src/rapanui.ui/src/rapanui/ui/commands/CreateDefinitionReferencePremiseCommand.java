package rapanui.ui.commands;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Builder;
import rapanui.dsl.Parser;
import rapanui.dsl.Term;
import rapanui.ui.models.ApplicationModel;

import javax.swing.ComboBoxModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class CreateDefinitionReferencePremiseCommand extends AbstractCommand {
	private static final long serialVersionUID = 1L;

	private final ProofEnvironment env;
	private final ApplicationModel app;
	private final Document inputModel;
	private final ComboBoxModel<String> definitionModel;

	private final Parser parser = new Parser();

	public CreateDefinitionReferencePremiseCommand(ProofEnvironment env, ApplicationModel app, Document inputModel, ComboBoxModel<String> definitionModel) {
		super("\u2714", "Neue Voraussetzung erstellen");

		this.env = env;
		this.app = app;
		this.inputModel = inputModel;
		this.definitionModel = definitionModel;

		inputModel.addDocumentListener(new RunnableDocumentListener(this::updateEnabled));
		updateEnabled();
	}

	@Override
	protected boolean canExecute() {
		try {
			return parser.canParseTerm(inputModel.getText(0, inputModel.getLength()));
		} catch (BadLocationException e) {
			return false;
		}
	}

	@Override
	public void execute() {
		try {
			String input = inputModel.getText(0, inputModel.getLength());
			Term target = parser.parseTerm(input);
			String definitionName = definitionModel.getSelectedItem().toString();

			env.addPremise(Builder.createDefinitionReference(target, app.getRuleSystems().resolveDefinition(definitionName)));

			inputModel.remove(0, inputModel.getLength());
		} catch (BadLocationException | IllegalArgumentException e) {
			// TODO: display error?
		}
	}
}
