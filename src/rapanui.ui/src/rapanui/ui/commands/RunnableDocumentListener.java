package rapanui.ui.commands;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class RunnableDocumentListener implements DocumentListener {
	private final Runnable runnable;

	public RunnableDocumentListener(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		runnable.run();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		runnable.run();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		runnable.run();
	}
}
