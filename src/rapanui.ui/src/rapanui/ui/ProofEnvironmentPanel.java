package rapanui.ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import java.util.HashMap;
import java.util.Map;

import rapanui.core.ConclusionProcess;
import rapanui.core.ProofEnvironment;
import rapanui.core.ProofEnvironmentObserver;
import rapanui.dsl.DefinitionReference;
import rapanui.dsl.Formula;

class ProofEnvironmentPanel extends JPanel implements ProofEnvironmentObserver {
	private static final long serialVersionUID = 1L;

	// ugly hack: use this instead of Integer.MAX_VALUE to avoid integer overflow
	private static final int MAX_WIDTH = 5000;

	private static final Font mathFont = new Font("Courier New", Font.PLAIN, 14);

	private final Application app;
	private final ProofEnvironment model;

	private final JPanel premisePanel = new JPanel(new GridLayout(0, 4));
	private ConclusionProcessView activeConclusion;

	private final Map<Formula, JPanel> premiseViewMap = new HashMap<Formula, JPanel>();
	private final Map<ConclusionProcess, JPanel> conclusionViewMap
		= new HashMap<ConclusionProcess, JPanel>();

	public ProofEnvironmentPanel(Application app, ProofEnvironment model) {
		assert app != null;
		assert model != null;

		this.app = app;
		this.model = model;
		initializeContent();

		for (Formula premise : model.getPremises())
			displayPremise(premise);
		for (ConclusionProcess conclusion : model.getConclusions())
			displayConclusion(conclusion);

		model.addObserver(this);
	}

	private void initializeContent() {
		setOpaque(false);
		setLayout(new MultilineLayout());

		Font titleFont = new Font(getFont().getFamily(), Font.BOLD, 20);

		/* premises */
		premisePanel.setOpaque(false);

		/* creating new premises */
		JPanel newPremisePanel = new JPanel(new MultilineLayout());
		newPremisePanel.setOpaque(false);
		newPremisePanel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5,5,5,5)));

		JTextField formulaInput = new SyntaxTextField(SyntaxTextField.ParsingMode.Formula);

		JTextField termInput = new SyntaxTextField(SyntaxTextField.ParsingMode.Term);
		termInput.setMaximumSize(new Dimension(MAX_WIDTH, termInput.getMaximumSize().height));

		JComboBox<String> definitionSelection = new JComboBox<String>(app.getRuleSystems().getDefinitionNames());
		definitionSelection.setMaximumSize(new Dimension(MAX_WIDTH, definitionSelection.getMaximumSize().height));

		JButton createDefRefPremise = new SimpleLink("\u2714", "Neue Voraussetzung erstellen");

		JLabel newPremiseLabel = new JLabel("Neue Voraussetzung:");
		newPremiseLabel.setAlignmentX(RIGHT_ALIGNMENT);

		newPremisePanel.add(newPremiseLabel, (Integer)0);
		newPremisePanel.add(new JSeparator(), (Integer)1);

		newPremisePanel.add(new JLabel("Sei "), (Integer)2);
		newPremisePanel.add(formulaInput);
		newPremisePanel.add(new SimpleLink("\u2714", "Neue Voraussetzung erstellen",
				new CreateFormulaPremiseCommand(model, formulaInput::getText)));

		newPremisePanel.add(new JLabel("Sei "), (Integer)3);
		newPremisePanel.add(termInput);
		newPremisePanel.add(definitionSelection);
		newPremisePanel.add(createDefRefPremise);

		/* premise header */
		JPanel premiseHeader = new JPanel();
		premiseHeader.setOpaque(false);
		premiseHeader.setLayout(new BoxLayout(premiseHeader, BoxLayout.X_AXIS));

		JLabel premiseTitle = new JLabel("Voraussetzungen");
		premiseTitle.setFont(titleFont);

		premiseHeader.add(new CollapseButton(premisePanel, newPremisePanel));
		premiseHeader.add(Box.createHorizontalStrut(5));
		premiseHeader.add(premiseTitle);
		premiseHeader.add(Box.createHorizontalGlue());

		/* conclusion header */
		JPanel conclusionHeader = new JPanel();
		conclusionHeader.setOpaque(false);
		conclusionHeader.setLayout(new BoxLayout(conclusionHeader, BoxLayout.X_AXIS));

		JLabel conclusionTitle = new JLabel("Folgerungen");
		conclusionTitle.setFont(titleFont);

		conclusionHeader.add(conclusionTitle);
		conclusionHeader.add(Box.createHorizontalGlue());

		/* creating new conclusions */
		JPanel newConclusionPanel = new JPanel(new MultilineLayout());
		newConclusionPanel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5,5,5,5)));
		newConclusionPanel.setOpaque(false);

		JTextField startTermInput = new  SyntaxTextField(SyntaxTextField.ParsingMode.Term);

		newConclusionPanel.add(new JLabel("Neue Folgerung:"), (Integer)0);
		newConclusionPanel.add(new JSeparator(), (Integer)1);
		newConclusionPanel.add(new JLabel("Startterm: "), (Integer)2);
		newConclusionPanel.add(startTermInput);
		newConclusionPanel.add(new SimpleLink("\u2714", "Neue Folgerung erstellen",
				new CreateConclusionProcessCommand(model, startTermInput::getText)));

		/* complete panel layout */
		add(premiseHeader, (Integer)0);
		add(premisePanel, (Integer)1);
		add(newPremisePanel, (Integer)2);
		add(Box.createVerticalStrut(30), (Integer)3);
		add(conclusionHeader, (Integer)4);
		add(Box.createVerticalStrut(10), (Integer)5);
		add(newConclusionPanel, (Integer)6);
	}

	private void displayPremise(Formula premise) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setOpaque(false);

		if (premise instanceof DefinitionReference) {
			DefinitionReference ref = (DefinitionReference)premise;
			panel.add(createMathematicalLabel(ref.getTarget().serialize()));
			panel.add(Box.createHorizontalStrut(3));
			panel.add(new JLabel(ref.getDefinition().getName()));
		} else
			panel.add(createMathematicalLabel(premise.serialize()));

		/*
		 * Implementation of modification features has been postponed.
		 *
		 * panel.add(new SimpleLink("\u2718", "Voraussetzung entfernen"));
		 */

		panel.setBorder(new EmptyBorder(5,5,5,5));
		premisePanel.add(panel);
		premiseViewMap.put(premise, panel);
	}

	private void displayConclusion(ConclusionProcess conclusion) {
		((MultilineLayout)getLayout()).newLine();
		ConclusionProcessView view = new ConclusionProcessView(conclusion);
		add(view);
		conclusionViewMap.put(conclusion, view);
	}

	private void activate(ConclusionProcessView conclusion) {
		if (activeConclusion != conclusion) {
			if (activeConclusion != null)
				activeConclusion.deactivate();
			activeConclusion = conclusion;
			conclusion.activate();
		}
	}

	static JLabel createMathematicalLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(mathFont);
		label.setAlignmentX(LEFT_ALIGNMENT);
		return label;
	}

	@Override
	public void premiseAdded(Formula premise) {
		displayPremise(premise);
	}

	@Override
	public void premiseRemoved(Formula premise) {
		if (premiseViewMap.containsKey(premise)) {
			premisePanel.remove(premiseViewMap.get(premise));
			premiseViewMap.remove(premise);
		}
	}

	@Override
	public void conclusionStarted(ConclusionProcess conclusion) {
		displayConclusion(conclusion);
	}

	@Override
	public void conclusionRemoved(ConclusionProcess conclusion) {
		if (conclusionViewMap.containsKey(conclusion)) {
			remove(conclusionViewMap.get(conclusion));
			conclusionViewMap.remove(conclusion);
		}
	}

	@Override
	public void conclusionMoved(ConclusionProcess conclusion) {
		// currently not used
	}
}