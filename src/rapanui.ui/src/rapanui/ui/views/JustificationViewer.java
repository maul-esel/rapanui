package rapanui.ui.views;

import java.awt.Insets;
import java.util.Map;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
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

	public JustificationViewer() {
		setEditable(false);
		this.setMargin(new Insets(10,10,10,10));
		initializeStyles();
	}

	private void initializeStyles() {
		defaultStyle = getStyledDocument().addStyle(null, null);
		setFontSize(defaultStyle, 14);
		setItalic(defaultStyle, true);

		mathStyle = getStyledDocument().addStyle(null, defaultStyle);
		setFontFamily(mathStyle, "DejaVu Sans Mono");
		setItalic(mathStyle, false);

		boldMathStyle = getStyledDocument().addStyle(null, mathStyle);
		setBold(boldMathStyle, true);

		boldStyle = getStyledDocument().addStyle(null, null);
		setFontSize(boldStyle, 15);
		setBold(boldStyle, true);
	}

	public void loadJustification(Justification justification) {
		clear();

		appendText("Es gilt ", boldStyle);
		appendMathText(justification.getJustifiedFormula().serialize());
		appendText(", " + DisplayStringHelper.shortDescription(justification) + ".\n\n");

		if (justification instanceof EnvironmentPremiseJustification || justification instanceof ProofJustification)
			return; // no further details

		appendText("Begründung:\n\n", boldStyle);
		displayJustification(justification, 0);
	}

	public void clear() {
		setText(null);
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
}
