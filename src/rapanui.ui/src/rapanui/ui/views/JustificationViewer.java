package rapanui.ui.views;

import java.awt.Insets;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import static javax.swing.text.StyleConstants.*;

import rapanui.core.*;

class JustificationViewer extends JTextPane {
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_STYLE = "default";
	private static final String MATH_STYLE = "math";
	private static final String BOLD_STYLE = "bold";

	public JustificationViewer() {
		setEditable(false);
		this.setMargin(new Insets(10,10,10,10));
		initializeStyles();
	}

	private void initializeStyles() {
		Style defaultStyle = getStyledDocument().addStyle(DEFAULT_STYLE, null);
		setFontSize(defaultStyle, 14);
		setItalic(defaultStyle, true);

		Style mathStyle = getStyledDocument().addStyle(MATH_STYLE, defaultStyle);
		setFontFamily(mathStyle, "DejaVu Sans Mono");
		setItalic(mathStyle, false);

		Style boldStyle = getStyledDocument().addStyle(BOLD_STYLE, null);
		setFontSize(boldStyle, 15);
		setBold(boldStyle, true);
	}

	public void loadJustification(Justification justification) {
		clear();

		try {
			appendBoldText("Es gilt ");
			appendMathText(justification.getJustifiedFormula().serialize());
			appendText(", " + DisplayStringHelper.shortDescription(justification) + ".\n\n");

			if (justification instanceof EnvironmentPremiseJustification || justification instanceof ProofJustification)
				return; // no further details

			appendBoldText("Begründung:\n\n");
			displayJustification(justification, 0);
		} catch (BadLocationException e) {
			assert false;
		}
	}

	public void clear() {
		setText(null);
	}

	private int displayJustification(Justification justification, int referenceCounter) throws BadLocationException {
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

	private int displayJustification(EnvironmentPremiseJustification justification, int referenceCounter)
			throws BadLocationException {
		appendText("Es gilt ");
		appendMathText(justification.getJustifiedFormula().serialize());
		appendText(" " + DisplayStringHelper.shortDescription(justification) + " [" + (++referenceCounter) + "].\n\n");
		return referenceCounter;
	}

	private int displayJustification(ProofJustification justification, int referenceCounter)
			throws BadLocationException {
		appendText("Es gilt ");
		appendMathText(justification.getJustifiedFormula().serialize());
		appendText(" " + DisplayStringHelper.shortDescription(justification) + " [" + (++referenceCounter) + "]\n\n.");
		return referenceCounter;
	}

	private int displayJustification(SubtermEqualityJustification justification, int referenceCounter)
		throws BadLocationException {
		referenceCounter = displayJustification(justification.getJustification(), referenceCounter);
		appendText("Daraus folgt ");
		appendMathText(justification.getJustifiedFormula().serialize());
		appendText(" [" + (++referenceCounter) + "].\n\n");
		return referenceCounter;
	}

	private int displayJustification(RuleApplication justification, int referenceCounter)
		throws BadLocationException {
		Justification[] premiseJustifications = justification.getPremiseJustifications();
		String referenceList = "";
		for (Justification premiseJustification : premiseJustifications) {
			referenceCounter = displayJustification(premiseJustification, referenceCounter);
			referenceList += "[" + referenceCounter + "], ";
		}
		if (!referenceList.isEmpty())
			referenceList = referenceList.substring(0, referenceList.length() - 2);

		appendText("Nach der Regel " + justification.getAppliedRule().getName() + ":");
		// TODO: display rule, but with distinct variable names
		appendText(", mit "); // TODO: display variable translation
		if (!referenceList.isEmpty())
			appendText(" und " + referenceList);
		appendText(", folgt dann ");
		appendMathText(justification.getJustifiedFormula().serialize());
		appendText(" [" + (++referenceCounter) + "].\n\n");
		return referenceCounter;
	}

	private void appendBoldText(String text) throws BadLocationException {
		StyledDocument document = getStyledDocument();
		document.insertString(document.getLength(), text, document.getStyle(BOLD_STYLE));
	}

	private void appendText(String text) throws BadLocationException {
		StyledDocument document = getStyledDocument();
		document.insertString(document.getLength(), text, document.getStyle(DEFAULT_STYLE));
	}

	private void appendMathText(String mathText) throws BadLocationException {
		StyledDocument document = getStyledDocument();
		document.insertString(document.getLength(), mathText, document.getStyle(MATH_STYLE));
	}
}
