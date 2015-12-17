package rapanui.ui.controls;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

public class SimpleLink extends JButton {
	private static final long serialVersionUID = 1L;

	private static final Color defaultColor = new Color(0x000099);
	private static final Color hoverColor = Color.ORANGE;

	public SimpleLink(String text, String tooltip, ActionListener action) {
		this(text, tooltip);
		addActionListener(action);
	}

	public SimpleLink(String text, String tooltip) {
		setText(text);
		setToolTipText(tooltip);

		// styling
		setMargin(new Insets(0,0,0,0));
		setBorderPainted(false);
		setOpaque(false);
		setContentAreaFilled(false);
		setFont(getFont().deriveFont(20f));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setForeground(defaultColor);
	}

	@Override
	public Dimension getMaximumSize() {
		return getMinimumSize();
	}

	@Override
	public void processMouseEvent(MouseEvent e) {
		switch (e.getID()) {
		case MouseEvent.MOUSE_ENTERED:
			setForeground(hoverColor);
			break;
		case MouseEvent.MOUSE_EXITED:
			setForeground(defaultColor);
			break;
		}
		super.processMouseEvent(e);
	}
}
