package rapanui.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;
import javax.swing.border.LineBorder;

import rapanui.core.*;

public class SuggestionView extends JPanel {
	private static final long serialVersionUID = 1L;

	private final Transformation model;

	SuggestionView(Transformation model) {
		this.model = model;
		initializeContent();
	}

	private void initializeContent() {
		setOpaque(false);
		setBorder(new LineBorder(Color.DARK_GRAY));
		setLayout(new BorderLayout());

		String shortForm = String.format("<html><pre>\t%s %s\t\t(%s)</pre></html>", model.getType() == FormulaType.EQUATION ? "=" : "⊆", model.getOutput().serialize(), shortJustification(model.getJustification()));
		// TODO: use math font
		add(new JLabel(shortForm), BorderLayout.NORTH);
	}

	public void setSelected(boolean isSelected) {
		if (isSelected)
			setOpaque(true); //setBackground(Color.PINK);
		else
			setOpaque(false);
	}

	private static String shortJustification(Justification justification) {
		if (justification instanceof EnvironmentPremiseJustification)
			return "nach Voraussetzung";
		return ""; // TODO
	}
}
