package rapanui.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import rapanui.core.ConclusionProcess;
import rapanui.core.ConclusionProcessObserver;
import rapanui.core.FormulaType;
import rapanui.core.Transformation;

import rapanui.dsl.DslHelper;

class ConclusionProcessView extends JPanel implements ConclusionProcessObserver {
	private static final long serialVersionUID = 1L;

	private final ConclusionProcess model;

	private final JPanel longForm = new JPanel(new GridBagLayout());
	private JLabel titleLabel;

	private Color borderColor = Color.BLACK;
	private int borderWidth = 1;

	public ConclusionProcessView(ConclusionProcess model) {
		assert model != null;
		this.model = model;

		initializeContent();

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
		header.add(titleLabel = ProofEnvironmentPanel.createMathematicalLabel(shortForm));
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
		longForm.add(ProofEnvironmentPanel.createMathematicalLabel("R "), c);

		// TODO: remove dummy data
		String[] steps = new String[] { "= R;I", "= R;(S;T)", "= (R;S);T", "= S;T"};
		String[] reasons = new String[] { "Neutralität von I", "nach Voraussetzung", "Assoziativität", "nach Voraussetzung" };

		for (int i = 0; i < steps.length; ++i)
			addTransformation(steps[i], reasons[i]);

		add(header);
		add(separator);
		add(longForm);
	}

	private int line = 0; // TODO: remove and use model data
	private void addTransformation(String transformation, String reason) { // TODO: Transformation as parameter
		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.gridx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridy = line;
		c.weightx = 0.35;
		longForm.add(ProofEnvironmentPanel.createMathematicalLabel(transformation), c);

		c.gridx = 2;
		c.weightx = 0;
		longForm.add(new JLabel("(" + reason + ")"), c);
		line++;
	}

	private String getTitle() {
		return DslHelper.serialize(model.getStartTerm())
		+ " "
		+ (model.getType() == FormulaType.Equality ? "=" : "⊆")
		+ " "
		+ DslHelper.serialize(model.getLastTerm());
	}

	@Override
	public void transformationAdded(Transformation transformation) {
		titleLabel.setText(getTitle());
	}

	@Override
	public void transformationRemoved(Transformation transformation) {
		titleLabel.setText(getTitle());
	}
}
