package rapanui.ui.commands;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.DefinitionReference;
import rapanui.dsl.DslFactory;
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

	public CreateDefinitionReferencePremiseCommand(ProofEnvironment env, ApplicationModel app, Document inputModel, ComboBoxModel<String> definitionModel) {
		super("\u2714", "Neue Voraussetzung erstellen");

		this.env = env;
		this.app = app;
		this.inputModel = inputModel;
		this.definitionModel = definitionModel;

		inputModel.addDocumentListener(new RunnableDocumentListener(this::updateEnabled));
	}

	@Override
	public boolean isEnabled() {
		try {
			return Parser.getInstance().canParseTerm(inputModel.getText(0, inputModel.getLength()));
		} catch (BadLocationException e) {
			return false;
		}
	}

	@Override
	public void execute() {
		try {
			String input = inputModel.getText(0, inputModel.getLength());
			Term target = Parser.getInstance().parseTerm(input);
			String definitionName = definitionModel.getSelectedItem().toString();

			DefinitionReference reference = DslFactory.eINSTANCE.createDefinitionReference();
			reference.setTarget(target);
			reference.setDefinition(app.getRuleSystems().resolveDefinition(definitionName));
			env.addPremise(reference);

			inputModel.remove(0, inputModel.getLength());
		} catch (BadLocationException | IllegalArgumentException e) {
			// TODO: display error?
		}
	}
}
