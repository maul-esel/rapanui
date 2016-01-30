package rapanui.ui.commands;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

abstract class AbstractCommand extends AbstractAction {
	private static final long serialVersionUID = 1L;

	protected AbstractCommand(String text, String description) {
		putValue(NAME, text);
		putValue(SHORT_DESCRIPTION, description);
	}

	public abstract void execute();
	protected abstract boolean canExecute();

	protected void updateEnabled() {
		setEnabled(canExecute());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		execute();
	}
}
