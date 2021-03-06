package rapanui.ui.controls;

import java.awt.Color;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import rapanui.dsl.Parser;
import rapanui.ui.views.FontManager;

public class SyntaxTextField extends JTextField implements DocumentListener {
	private static final long serialVersionUID = 1L;

	private static final Border invalidBorder = new LineBorder(Color.red);

	private boolean isValid = false;
	private final ParsingMode parsingMode;
	private final Parser parser = new Parser();

	private final Border defaultBorder;

	public SyntaxTextField(ParsingMode mode) {
		this(mode, null);
	}

	public SyntaxTextField(ParsingMode mode, Document model /* may be null */) {
		super(model, null, 0);
		setFont(FontManager.getMathFont());
		this.parsingMode = mode;
		getDocument().addDocumentListener(this);
		defaultBorder = getBorder();
	}

	public static enum ParsingMode {
		Term,
		Formula
	}

	protected void verify() {
		switch (parsingMode) {
			case Term:
				isValid = parser.canParseTerm(getText());
				break;
			case Formula:
				isValid = parser.canParseFormula(getText());
				break;
			default:
				isValid = false;
		}
		setBorder(getText().isEmpty() || isValid ? defaultBorder : invalidBorder);
	}

	public boolean isValid() {
		return isValid;
	}

	@Override
	protected void processFocusEvent(FocusEvent e) {
		if (!e.isTemporary() && e.getID() == FocusEvent.FOCUS_LOST)
			verify();
		super.processFocusEvent(e);
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
