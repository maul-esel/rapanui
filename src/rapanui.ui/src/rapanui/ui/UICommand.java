package rapanui.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class UICommand<TTarget> implements ActionListener {
	protected final TTarget target;

	protected UICommand(TTarget target) {
		this.target = target;
	}

	public abstract void execute();

	@Override
	public void actionPerformed(ActionEvent event) {
		execute();
	}
}
