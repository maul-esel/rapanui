package rapanui.ui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

public class CollapseButton extends SimpleLink implements ActionListener {
	private static final long serialVersionUID = 1L;

	boolean isCollapsed;
	JComponent[] targets;

	public CollapseButton(JComponent... targets) {
		super("", null);
		this.targets = targets;
		isCollapsed = !targets[0].isVisible();
		addActionListener(this);
	}

	@Override
	public String getText() {
		return isCollapsed ? "\u25b7" : "\u25bc";
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		isCollapsed = !isCollapsed;
		for (JComponent target : targets)
			target.setVisible(!isCollapsed);
		revalidate();
	}
}
