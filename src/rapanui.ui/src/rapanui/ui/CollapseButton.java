package rapanui.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

class CollapseButton extends JButton implements ActionListener {
	boolean isCollapsed;
	JComponent[] targets;

	public CollapseButton(JComponent... targets) {
		this.targets = targets;
		isCollapsed = !targets[0].isVisible();
		addActionListener(this);
	}

	@Override
	public String getText() {
		return isCollapsed ? "\uD83D\uDF82" : "\uD83D\uDF83";
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		isCollapsed = !isCollapsed;
		for (JComponent target : targets)
			target.setVisible(!isCollapsed);
		revalidate();
	}
}
