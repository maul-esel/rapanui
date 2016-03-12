package rapanui.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import rapanui.core.*;
import rapanui.ui.ProofFormatter;

public class SuggestionView extends JPanel {
	private static final long serialVersionUID = 1L;

	private final Transformation model;

	SuggestionView(Transformation model) {
		assert model != null;
		this.model = model;
		initializeContent();
	}

	private void initializeContent() {
		setOpaque(false);
		setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY), new EmptyBorder(3,0,3,0)));
		setLayout(new BorderLayout());

		String shortForm = String.format("<html><pre>\t%s %s\t\t(%s)</pre></html>",
			model.getFormulaType().getLiteral(),
			model.getOutput().serialize(),
			ProofFormatter.shortDescription(model.getJustification())
		);

		JLabel shortFormLabel = new JLabel(shortForm);
		shortFormLabel.setFont(FontManager.getDefaultFont()); // TODO: use math font for math part
		add(shortFormLabel, BorderLayout.NORTH);
	}

	public void setSelected(boolean isSelected) {
		if (isSelected)
			setOpaque(true);
		else
			setOpaque(false);
	}
}
