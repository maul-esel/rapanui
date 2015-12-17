package rapanui.ui.controls;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import rapanui.dsl.Parser;

public class SyntaxTextField extends JTextField implements DocumentListener {
	private static final long serialVersionUID = 1L;

	private static final Font mathFont = new Font("Courier New", Font.PLAIN, 14);
	private static final Border invalidBorder = new LineBorder(Color.red);

	private boolean isValid = true;
	private final ParsingMode parsingMode;

	private final Border defaultBorder;

	public SyntaxTextField(ParsingMode mode) {
		super();
		setFont(mathFont);
		this.parsingMode = mode;
		getDocument().addDocumentListener(this);
		defaultBorder = getBorder();
	}

	public static enum ParsingMode {
		Term,
		Formula
	}

	protected void verify() {
		try {
			if (parsingMode == ParsingMode.Term)
				Parser.getInstance().parseTerm(getText());
			else if (parsingMode == ParsingMode.Formula)
				Parser.getInstance().parseFormula(getText());
			isValid = true;
			setBorder(defaultBorder);
		} catch (IllegalArgumentException e) {
			isValid = false;
			setBorder(getText().isEmpty() ? defaultBorder : invalidBorder);
		}
	}

	public boolean isValid() {
		return isValid;
	}

	@Override
	protected void processFocusEvent(FocusEvent e) {
		if (!e.isTemporary() && e.getID() == FocusEvent.FOCUS_LOST)
			verify();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (!isValid)
			verify();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (!isValid)
			verify();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (!isValid)
			verify();
	}
}
