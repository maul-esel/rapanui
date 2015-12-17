package rapanui.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import rapanui.core.ConclusionProcess;
import rapanui.core.ConclusionProcessObserver;
import rapanui.core.EnvironmentPremiseJustification;
import rapanui.core.FormulaType;
import rapanui.core.Justification;
import rapanui.core.ProofJustification;
import rapanui.core.RuleApplication;
import rapanui.core.SubtermEqualityJustification;
import rapanui.core.Transformation;
import rapanui.ui.controls.CollapseButton;
import rapanui.ui.views.ProofEnvironmentView;

public class ConclusionProcessView extends JPanel implements ConclusionProcessObserver {
	private static final long serialVersionUID = 1L;

	private final ConclusionProcess model;

	private final JPanel longForm = new JPanel(new GridBagLayout());
	private JLabel titleLabel;

	private Color borderColor = Color.BLACK;
	private int borderWidth = 1;

	private int displayedTransformations = 0;

	public ConclusionProcessView(ConclusionProcess model) {
		assert model != null;
		this.model = model;

		initializeContent();

		for (Transformation transformation : model.getTransformations())
			displayTransformation(transformation);

		model.addObserver(this);
	}

	public void activate() {
		borderColor = Color.ORANGE;
		borderWidth = 2;
		updateBorder();
	}

	public void deactivate() {
		borderColor = Color.BLACK;
		borderWidth = 1;
		updateBorder();
	}

	private void updateBorder() {
		setBorder(new CompoundBorder(new EmptyBorder(5,0,5,0),
				new CompoundBorder(new LineBorder(borderColor, borderWidth), new EmptyBorder(5,5,5,5))));
	}

	private void initializeContent() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);
		updateBorder();

		String shortForm = getTitle();

		JPanel header = new JPanel();
		header.setBackground(Color.WHITE);
		header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));

		longForm.setOpaque(false);

		JSeparator separator = new JSeparator();

		header.add(new CollapseButton(longForm, separator));
		header.add(titleLabel = ProofEnvironmentView.createMathematicalLabel(shortForm));
		header.add(Box.createHorizontalGlue());

		/*
		 * Implementation of modification features has been postponed.
		 *
		 * header.add(new SimpleLink("\u2B06", "Nach oben"));
		 * header.add(new SimpleLink("\u2B07", "Nach unten"));
		 * header.add(new SimpleLink("\u27F2", "Letzter Schritt rückgängig"));
		 * header.add(new SimpleLink("\u2718", "Folgerung entfernen"));
		 */

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = 5;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0.3;
		longForm.add(ProofEnvironmentView.createMathematicalLabel("R "), c);

		add(header);
		add(separator);
		add(longForm);
	}

	private void displayTransformation(Transformation transformation) {
		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.gridx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridy = displayedTransformations;
		c.weightx = 0.35;
		longForm.add(ProofEnvironmentView.createMathematicalLabel(
				(transformation.getType() == FormulaType.Equality ? "= " : "⊆ ")
				+ transformation.getOutput().serialize()),
				c);

		c.gridx = 2;
		c.weightx = 0;
		longForm.add(new JLabel(justificationText(transformation.getJustification())), c);
		displayedTransformations++;
	}

	private String getTitle() {
		return model.getStartTerm().serialize()
		+ " "
		+ (model.getType() == FormulaType.Equality ? "=" : "⊆")
		+ " "
		+ model.getLastTerm().serialize();
	}

	private String justificationText(Justification justification) {
		if (justification instanceof EnvironmentPremiseJustification)
			return "(nach Voraussetzung)";
		else if (justification instanceof RuleApplication)
			return "(" + ((RuleApplication)justification).getAppliedRule().getName() + ")";
		else if (justification instanceof ProofJustification)
			return "(siehe oben)";
		else if (justification instanceof SubtermEqualityJustification)
			return "(" + ((SubtermEqualityJustification) justification).getOriginalSubTerm().serialize()
					+ " = "
					+ ((SubtermEqualityJustification) justification).getNewSubTerm().serialize() + ")";
		return "";
	}

	@Override
	public void transformationAdded(Transformation transformation) {
		titleLabel.setText(getTitle());
		displayTransformation(transformation);
	}

	@Override
	public void transformationRemoved(Transformation transformation) {
		titleLabel.setText(getTitle());
	}
}
