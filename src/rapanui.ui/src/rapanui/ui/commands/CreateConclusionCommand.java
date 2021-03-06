package rapanui.ui.commands;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Parser;
import rapanui.dsl.Term;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class CreateConclusionCommand extends AbstractCommand {
	private static final long serialVersionUID = 1L;

	private final ProofEnvironment env;
	private final Document inputModel;

	private final Parser parser = new Parser();

	public CreateConclusionCommand(ProofEnvironment env, Document inputModel) {
		super("\u2714", "Neue Folgerung erstellen");

		this.env = env;
		this.inputModel = inputModel;

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
			Term startTerm = parser.parseTerm(input);
			env.addConclusion(startTerm);

			inputModel.remove(0, inputModel.getLength());
		} catch (BadLocationException | IllegalArgumentException e) {
			// TODO: display error?
		}
	}
}
