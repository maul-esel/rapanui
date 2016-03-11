package rapanui.ui.views;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;

import static javax.swing.text.StyleConstants.*;

import rapanui.core.Justification;
import rapanui.ui.ProofFormatter;

class JustificationViewer extends JTextPane {
	private static final long serialVersionUID = 1L;

	private final StyledDocument helpTextDocument = new HTMLDocument();

	public JustificationViewer() {
		setEditable(false);
		setMargin(new Insets(10,10,10,10));
		setContentType("text/html");
		initializeHelpText();
	}

	public void loadJustification(Justification justification) {
		setDocument(new ProofFormatter(justification).getDocument());
	}

	public void clear() {
		setStyledDocument(helpTextDocument);
	}

	private void initializeHelpText() {
		setStyledDocument(helpTextDocument);

		Style mathStyle = helpTextDocument.addStyle(null, null);
		setFontSize(mathStyle, 14);
		setAlignment(mathStyle, ALIGN_CENTER);
		setFontFamily(mathStyle, FontManager.getMathFontFamily());

		Style helpStyle = helpTextDocument.addStyle(null, null);
		System.out.println(getFontFamily(helpStyle));
		setFontSize(helpStyle, 14);
		setLeftIndent(helpStyle, 30);
		setRightIndent(helpStyle, 30);
		setAlignment(helpStyle, ALIGN_JUSTIFIED);
		StyleConstants.setForeground(helpStyle, Color.gray);

		Style helpHeadingStyle = helpTextDocument.addStyle(null, helpStyle);
		setBold(helpHeadingStyle, true);
		setAlignment(helpHeadingStyle, ALIGN_CENTER);

		Style helpSubheadingStyle = helpTextDocument.addStyle(null, helpStyle);
		setAlignment(helpSubheadingStyle, ALIGN_CENTER);

		appendText("\n\nWillkommen zu rapanui\n", helpHeadingStyle);
		appendText("Unterstützte Beweisführung in der relationalen Algebra\n\n", helpSubheadingStyle);
		appendText("Vorgehen:\n\n", helpHeadingStyle);

		appendText("Gib oben links Voraussetzungen an, z.B.\n", helpStyle);
		appendText("R = R ; R\n", mathStyle);

		appendText("Bei der Eingabe hilft die unten links erscheinende Bildschirmtastatur.\n\n"
				+ "Gib links mittig einen Startterm für deinen Beweis ein, z.B.\n", helpStyle);
		appendText("R ; S*\n", mathStyle);
		appendText("\nRechts oben werden mögliche Umformungen des Terms angezeigt. Wenn du"
				+ " eine auswählst, wird sie dir hier erklärt.\n\n"
				+ "Wende eine der Umformungen durch Doppelklick an. Sie wird links angezeigt "
				+ "und es werden neue Umformungen geladen.\n\n"
				+ "Wiederhole diesen Vorgang, bis du die zu beweisende Aussage erreicht hast.",
				helpStyle);
	}

	private void appendText(String text, Style style) {
		try {
			StyledDocument document = getStyledDocument();
			int pos = document.getLength();
			document.insertString(pos, text, style);
		} catch (BadLocationException e) {}
	}
}