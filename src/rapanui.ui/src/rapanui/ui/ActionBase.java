package rapanui.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public abstract class ActionBase<TTarget> extends AbstractAction {
	private static final long serialVersionUID = 1L;

	protected final TTarget target;

	protected ActionBase(TTarget target) {
		this.target = target;
	}

	public abstract void execute();

	@Override
	public void actionPerformed(ActionEvent event) {
		execute();
	}
}
