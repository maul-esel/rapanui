package rapanui.ui;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.html.HTMLDocument;

import static javax.swing.text.StyleConstants.*;

import rapanui.core.ConclusionProcess;
import rapanui.core.EnvironmentPremiseJustification;
import rapanui.core.Justification;
import rapanui.core.ProofEnvironment;
import rapanui.core.ProofJustification;
import rapanui.core.RuleApplication;
import rapanui.core.SubtermEqualityJustification;
import rapanui.core.Transformation;
import rapanui.dsl.DefinitionReference;
import rapanui.dsl.Formula;
import rapanui.dsl.Predicate;
import rapanui.dsl.Rule;
import rapanui.dsl.Term;
import rapanui.ui.views.FontManager;

public class ProofFormatter {
	public static String shortDescription(Justification justification) {
		if (justification instanceof EnvironmentPremiseJustification)
			return "nach Voraussetzung";
		else if (justification instanceof RuleApplication)
			return "nach " + ((RuleApplication)justification).getAppliedRule().getName();
		else if (justification instanceof ProofJustification) {
			ConclusionProcess conclusion = ((ProofJustification)justification).getConclusion();
			int conclusionIndex = Arrays.asList(conclusion.getEnvironment().getConclusions()).indexOf(conclusion);
			return "nach Folgerung #" + (conclusionIndex + 1);
		} else if (justification instanceof SubtermEqualityJustification)
			return "weil " + ((SubtermEqualityJustification) justification).getOriginalSubTerm().serialize()
					+ " = "
					+ ((SubtermEqualityJustification) justification).getNewSubTerm().serialize();
		return Objects.toString(justification);
	}

	private final HTMLDocument document = new HTMLDocument();
	private final Style textStyle;
	private final Style boldStyle;
	private final Style mathStyle;
	private final Style boldMathStyle;

	private ProofFormatter() {
		textStyle = document.addStyle("text", null);
		setFontSize(textStyle, 14);

		boldStyle = document.addStyle("bold", textStyle);
		setBold(boldStyle, true);

		mathStyle = document.addStyle("math", textStyle);
		setFontFamily(mathStyle, FontManager.getMathFontFamily());
		setItalic(mathStyle, false);

		boldMathStyle = document.addStyle("math-bold", mathStyle);
		setBold(boldMathStyle, true);
	}

	public ProofFormatter(ProofEnvironment environment) {
		this();
		format(environment);
	}

	public ProofFormatter(Justification justification) {
		this();

		formatting("Es gilt %m, %j.", justification.getJustifiedFormula(), justification);
		if (justification instanceof EnvironmentPremiseJustification
			|| justification instanceof ProofJustification)
			return;

		appendText(boldStyle, "\n\nBegründung:\n");
		format(justification, 0);
	}

	public HTMLDocument getDocument() {
		return document;
	}

	private void format(ProofEnvironment environment) {
		appendText(boldStyle, "Voraussetzungen\n\n");

		Predicate[] premises = environment.getPremises();
		if (premises.length > 0)
			text("Es gelte: ");
		else
			text("keine");

		for (int i = 0; i < premises.length; ++i) {
			if (i != 0)
				text(", ");

			if (premises[i] instanceof DefinitionReference) {
				DefinitionReference reference = (DefinitionReference)premises[i];
				formatting("%m ist %s (%m)",
					reference.getTarget(),
					reference.getDefinition().getName(),
					reference.resolve().stream().map(Formula::serialize).collect(Collectors.joining(", "))
				);
			} else
				math(premises[i].serialize());
		}
		text("\n\n");

		int referenceCounter = 0, i = 0;
		for (ConclusionProcess conclusion : environment.getConclusions())
			referenceCounter = format(conclusion, ++i, referenceCounter);
	}

	private int format(ConclusionProcess conclusion, int conclusionNumber, int referenceCounter) {
		appendText(boldStyle, "Folgerung #" + conclusionNumber + "\n\n");

		Transformation[] transformations = conclusion.getTransformations();
		int[] referenceNumbers = new int[transformations.length];
		for (int i = 0; i < transformations.length; ++i) {
			referenceCounter = format(transformations[i].getJustification(), referenceCounter);
			referenceNumbers[i] = referenceCounter;
		}

		text("Somit gilt:\n");
		math(conclusion.getStartTerm().serialize());

		int padLength = conclusion.getStartTerm().serialize().length();
		String pad = CharBuffer.allocate( padLength ).toString().replace('\0', ' ');

		Term[] terms = conclusion.getTerms();
		for (int i = 1; i < terms.length; ++i)
			formatting(" %m %m [%d]\n%m", "=", terms[i], referenceNumbers[i-1], i < terms.length - 1 ? pad : "");

		return referenceCounter;
	}

	private int format(Justification justification, int referenceCounter) {
		if (justification instanceof RuleApplication)
			return format((RuleApplication)justification, referenceCounter);

		if (justification instanceof EnvironmentPremiseJustification
				|| justification instanceof ProofJustification) {
			formatting("Es gilt %m %j [%d].\n\n", justification.getJustifiedFormula(), justification, ++referenceCounter);

		} else if (justification instanceof SubtermEqualityJustification) {
			referenceCounter = format(((SubtermEqualityJustification)justification).getJustification(), referenceCounter);
			formatting("Daraus folgt %m [%d].\n\n", justification.getJustifiedFormula(), ++referenceCounter);

		} else
			throw new IllegalStateException("Unknown justification type");

		return referenceCounter;
	}


	private int format(RuleApplication justification, int referenceCounter) {
		String referenceList = "";
		for (Justification premiseJustification : justification.getPremiseJustifications()) {
			referenceCounter = format(premiseJustification, referenceCounter);
			referenceList += "[" + referenceCounter + "], ";
		}

		formatting("Nach der Regel %s:\n\n%m\nmit %t",
				justification.getAppliedRule().getName(),
				justification.getAppliedRule(),
				justification.getVariableTranslation());

		if (!referenceList.isEmpty())
			text(" und ", referenceList.substring(0, referenceList.length() - 2));

		formatting(" folgt dann %m [%d].\n\n", justification.getJustifiedFormula(), ++referenceCounter);
		return referenceCounter;
	}

	private void formatting(String format, Object... objects) {
		Matcher matcher = Pattern.compile("([^%]*)%(\\w)([^%]*)").matcher(format);
		for (int i = 0; matcher.find(); ++i) {
			text(matcher.group(1));
			char typeSpecifier = matcher.group(2).charAt(0);
			switch (typeSpecifier) {
			case 'd': text(Integer.toString((int)objects[i]));
				break;
			case 's': text(Objects.toString(objects[i]));
				break;
			case 'm':
				if (objects[i] instanceof Rule)
					math(((Rule)objects[i]).serialize());
				else if (objects[i] instanceof Formula)
					math(((Formula)objects[i]).serialize());
				else if (objects[i] instanceof Term)
					math(((Term)objects[i]).serialize());
				else if (objects[i] instanceof String)
					math(objects[i].toString());
				else
					throw new IllegalArgumentException("Object " + objects[i] + " cannot be formatted as math");
				break;
			case 'j': text(shortDescription((Justification)objects[i]));
				break;
			case 't': formatVariableTranslation((Map<String, Term>)objects[i]);
				break;
			}
			text(matcher.group(3));
		}
	}

	private void formatVariableTranslation(Map<String, Term> translation) {
		boolean isFirst = true;
		for (String variable : translation.keySet()) {
			if (!isFirst)
				text(", ");
			else
				isFirst = false;
			math(variable, " = ");
			appendText(boldMathStyle, translation.get(variable).serialize());
		}
	}

	private void text(String... textFragments) {
		appendText(textStyle, textFragments);
	}

	private void math(String... mathTextFragments) {
		appendText(mathStyle, mathTextFragments);
	}

	private void appendText(Style style, String... textFragments) {
		try {
			document.insertString(document.getLength(), String.join("", textFragments), style);
		} catch (BadLocationException e) {}
	}
}
