package rapanui.ui.views;

import java.awt.Color;
import java.awt.Insets;
import java.util.Map;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import static javax.swing.text.StyleConstants.*;

import rapanui.core.*;
import rapanui.dsl.Term;

class JustificationViewer extends JTextPane {
	private static final long serialVersionUID = 1L;

	private Style defaultStyle;
	private Style mathStyle;
	private Style boldMathStyle;
	private Style boldStyle;
	private Style helpHeadingStyle;
	private Style helpSubheadingStyle;
	private Style helpStyle;

	private final StyledDocument helpTextDocument = new DefaultStyledDocument();
	private final StyledDocument defaultDocument;

	public JustificationViewer() {
		setEditable(false);
		this.setMargin(new Insets(10,10,10,10));
		defaultDocument = getStyledDocument();
		initializeStyles();
		initializeHelpText();
	}

	private void initializeStyles() {
		defaultStyle = defaultDocument.addStyle(null, null);
		setFontSize(defaultStyle, 14);

		mathStyle = defaultDocument.addStyle(null, defaultStyle);
		setFontFamily(mathStyle, FontManager.getMathFontFamily());
		setItalic(mathStyle, false);

		boldMathStyle = defaultDocument.addStyle(null, mathStyle);
		setBold(boldMathStyle, true);

		boldStyle = defaultDocument.addStyle(null, null);
		setFontSize(boldStyle, 15);
		setBold(boldStyle, true);

		helpStyle = helpTextDocument.addStyle(null, null);
		setFontSize(helpStyle, 14);
		setLeftIndent(helpStyle, 30);
		setRightIndent(helpStyle, 30);
		setAlignment(helpStyle, ALIGN_JUSTIFIED);
		StyleConstants.setForeground(helpStyle, Color.gray);

		helpHeadingStyle = helpTextDocument.addStyle(null, helpStyle);
		setBold(helpHeadingStyle, true);
		setAlignment(helpHeadingStyle, ALIGN_CENTER);

		helpSubheadingStyle = helpTextDocument.addStyle(null, helpStyle);
		setAlignment(helpSubheadingStyle, ALIGN_CENTER);
	}

	public void loadJustification(Justification justification) {
		setDocument(defaultDocument);
		setText(null);

		appendText("Es gilt ", boldStyle);
		appendMathText(justification.getJustifiedFormula().serialize());
		appendText(", " + DisplayStringHelper.shortDescription(justification) + ".\n\n");

		if (justification instanceof EnvironmentPremiseJustification || justification instanceof ProofJustification)
			return; // no further details

		appendText("Begründung:\n\n", boldStyle);
		displayJustification(justification, 0);
	}

	public void clear() {
		setStyledDocument(helpTextDocument);
	}

	private int displayJustification(Justification justification, int referenceCounter) {
		if (justification instanceof EnvironmentPremiseJustification)
			return displayJustification((EnvironmentPremiseJustification)justification, referenceCounter);
		else if (justification instanceof ProofJustification)
			return displayJustification((ProofJustification)justification, referenceCounter);
		else if (justification instanceof SubtermEqualityJustification)
			return displayJustification((SubtermEqualityJustification)justification, referenceCounter);
		else if (justification instanceof RuleApplication)
			return displayJustification((RuleApplication)justification, referenceCounter);
		else
			throw new IllegalStateException("Unknown justification type");
	}

	private int displayJustification(EnvironmentPremiseJustification justification, int referenceCounter) {
		appendText("Es gilt ");
		appendMathText(justification.getJustifiedFormula().serialize());
		appendText(" " + DisplayStringHelper.shortDescription(justification) + " [" + (++referenceCounter) + "].\n\n");
		return referenceCounter;
	}

	private int displayJustification(ProofJustification justification, int referenceCounter) {
		appendText("Es gilt ");
		appendMathText(justification.getJustifiedFormula().serialize());
		appendText(" " + DisplayStringHelper.shortDescription(justification) + " [" + (++referenceCounter) + "]\n\n.");
		return referenceCounter;
	}

	private int displayJustification(SubtermEqualityJustification justification, int referenceCounter) {
		referenceCounter = displayJustification(justification.getJustification(), referenceCounter);
		appendText("Daraus folgt ");
		appendMathText(justification.getJustifiedFormula().serialize());
		appendText(" [" + (++referenceCounter) + "].\n\n");
		return referenceCounter;
	}

	private int displayJustification(RuleApplication justification, int referenceCounter) {
		Justification[] premiseJustifications = justification.getPremiseJustifications();
		String referenceList = "";
		for (Justification premiseJustification : premiseJustifications) {
			referenceCounter = displayJustification(premiseJustification, referenceCounter);
			referenceList += "[" + referenceCounter + "], ";
		}
		if (!referenceList.isEmpty())
			referenceList = referenceList.substring(0, referenceList.length() - 2);

		appendText("Nach der Regel " + justification.getAppliedRule().getName() + ":\n\n");
		appendMathText(justification.getAppliedRule().serialize());
		appendText("\nmit ");
		displayVariableTranslation(justification.getVariableTranslation());
		if (!referenceList.isEmpty())
			appendText(" und " + referenceList);
		appendText(" folgt dann ");
		appendMathText(justification.getJustifiedFormula().serialize());
		appendText(" [" + (++referenceCounter) + "].\n\n");
		return referenceCounter;
	}

	private void displayVariableTranslation(Map<String, Term> translation) {
		boolean isFirst = true;
		for (String variable : translation.keySet()) {
			if (!isFirst)
				appendText(", ");
			else
				isFirst = false;
			appendMathText(variable + " = ");
			appendText(translation.get(variable).serialize(), boldMathStyle);
		}
	}

	private void appendText(String text) {
		appendText(text, defaultStyle);
	}

	private void appendMathText(String mathText) {
		appendText(mathText, mathStyle);
	}

	private void appendText(String text, Style style) {
		try {
			StyledDocument document = getStyledDocument();
			int pos = document.getLength();
			document.insertString(pos, text, style);
		} catch (BadLocationException e) {}
	}

	private void appendParagraph(String text, Style style) {
		StyledDocument document = getStyledDocument();
		int pos = document.getLength();
		appendText(text, style);
		document.setParagraphAttributes(pos, text.length(), style, false);
	}

	private void initializeHelpText() {
		setStyledDocument(helpTextDocument);

		appendParagraph("\n\nWillkommen zu rapanui\n", helpHeadingStyle);
		appendParagraph("Unterstützte Beweisführung in der relationalen Algebra\n\n", helpSubheadingStyle);
		appendParagraph("Vorgehen:\n\n", helpHeadingStyle);

		appendParagraph("Gib oben links Voraussetzungen an, z.B. ", helpStyle);
		appendMathText("R = R ; R");

		appendParagraph(". Bei der Eingabe hilft die unten links erscheinende Bildschirmtastatur.\n\n"
				+ "Gib links mittig einen Startterm für deinen Beweis ein, z.B. ", helpStyle);
		appendMathText("R ; S*");
		appendParagraph(".\n\nRechts oben werden mögliche Umformungen des Terms angezeigt. Wenn du"
				+ " eine auswählst, wird sie dir hier erklärt.\n\n"
				+ "Wende eine der Umformungen durch Doppelklick an. Sie wird links angezeigt.\n\n"
				+ "Wiederhole diesen Vorgang, bis du die zu beweisende Aussage erreicht hast.",
				helpStyle);
	}
}
