package rapanui.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.JButton;

class SymbolKeyboard extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final char[] keyboardSymbols = { '˘', '*', '⁺', 'Π', '∅', '⊆', '∩', '∪', '=', ';', '*', '\\', 'ᶜ',  'I' };

	SymbolKeyboard() {
		initializeContent();
	}

	private void initializeContent() {
		setLayout(new GridLayout(2, 7));
		for (char symbol : keyboardSymbols) {
			JButton key = new JButton(Character.toString(symbol));
			add(key);

			key.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					Component focusTarget = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
					if (focusTarget instanceof JTextField) {
						JTextField textField = (JTextField)focusTarget;
						String text = textField.getText();
						int caret = textField.getCaretPosition();
						textField.setText(text.substring(0, caret) + symbol + text.substring(caret));
					}
				}
			});
		}
	}
}
