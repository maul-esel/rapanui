package rapanui.ui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;

class SymbolKeyboard extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int keyboardLines = 3;
	private static final char[] keyboardSymbols = { 'I', '˘', '*', '⁺', 'ᶜ', 'Π', '∩', '∪', ';', '\\', '∅', '⊆', '=', '(', ')' };

	SymbolKeyboard() {
		initializeContent();
	}

	private void initializeContent() {
		setLayout(new GridLayout(keyboardLines, keyboardSymbols.length / keyboardLines));
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
						textField.setCaretPosition(caret + 1);
					}
				}
			});
		}
	}
}
