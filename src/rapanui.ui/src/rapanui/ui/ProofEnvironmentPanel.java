package rapanui.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

class ProofEnvironmentPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// ugly hack: use this instead of Integer.MAX_VALUE to avoid integer overflow
	private static final int MAX_WIDTH = 5000;

	private static final Font mathFont = new Font("Courier", Font.PLAIN, 14);

	private JPanel premisePanel;

	public ProofEnvironmentPanel(/* ProofEnvironment env */) {
		initializeContent();
	}

	private void initializeContent() {
		setOpaque(false);
		setLayout(new MultilineLayout());

		Font titleFont = new Font(getFont().getFamily(), Font.BOLD, 20);

		/* premises */
		premisePanel = new JPanel(new GridLayout(0, 4));
		premisePanel.setOpaque(false);

		/* creating new premises */
		JPanel newPremisePanel = new JPanel(new MultilineLayout());
		newPremisePanel.setOpaque(false);
		newPremisePanel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5,5,5,5)));

		// TODO: remove dummy content
		JTextField formulaInput = new JTextField("R = S;R");
		formulaInput.setFont(mathFont);
		JTextField termInput = new JTextField("R;S*");
		termInput.setFont(mathFont);
		termInput.setMaximumSize(new Dimension(MAX_WIDTH, termInput.getMaximumSize().height));
		JComboBox<String> definitionSelection = new JComboBox<String>(new String[] { "reflexiv", "transitiv" });
		definitionSelection.setMaximumSize(new Dimension(MAX_WIDTH, definitionSelection.getMaximumSize().height));

		JButton createFormulaPremise = new SimpleLink("\u2714", "Neue Voraussetzung erstellen");
		JButton createDefRefPremise = new SimpleLink("\u2714", "Neue Voraussetzung erstellen");

		JLabel newPremiseLabel = new JLabel("Neue Voraussetzung:");
		newPremiseLabel.setAlignmentX(RIGHT_ALIGNMENT);

		newPremisePanel.add(newPremiseLabel, (Integer)0);
		newPremisePanel.add(new JSeparator(), (Integer)1);

		newPremisePanel.add(new JLabel("Sei "), (Integer)2);
		newPremisePanel.add(formulaInput);
		newPremisePanel.add(createFormulaPremise);

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

		JTextField startTermInput = new JTextField("S;S;R"); // TODO: remove dummy content
		startTermInput.setFont(mathFont);

		newConclusionPanel.add(new JLabel("Neue Folgerung:"), (Integer)0);
		newConclusionPanel.add(new JSeparator(), (Integer)1);
		newConclusionPanel.add(new JLabel("Startterm: "), (Integer)2);
		newConclusionPanel.add(startTermInput);
		newConclusionPanel.add(new SimpleLink("\u2714", "Neue Folgerung erstellen"));

		/* complete panel layout */
		add(premiseHeader, (Integer)0);
		add(premisePanel, (Integer)1);
		add(newPremisePanel, (Integer)2);
		add(Box.createVerticalStrut(30), (Integer)3);
		add(conclusionHeader, (Integer)4);
		add(Box.createVerticalStrut(10), (Integer)5);
		add(newConclusionPanel, (Integer)6);

		// TODO: remove dummy content
		createConclusion();
		createConclusion();
		createFormulaPremise("R;S = S;R");
		createDefinitionReferencePremise("R;R", "reflexiv");
		createFormulaPremise("S = R;R");
		createDefinitionReferencePremise("S;S", "transitiv");
	}

	private void createFormulaPremise(String formula) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setOpaque(false);

		panel.add(createMathematicalLabel(formula));
		panel.add(new SimpleLink("\u2718", "Voraussetzung entfernen"));
		
		panel.setBorder(new EmptyBorder(5,5,5,5));
		
		premisePanel.add(panel);

		// TODO: setup validation
	}

	private void createDefinitionReferencePremise(String term, String definitionName /* TODO: make Definition instance */) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setOpaque(false);
		
		panel.add(createMathematicalLabel(term));
		panel.add(Box.createHorizontalStrut(3));
		panel.add(new JLabel(definitionName));
		panel.add(new SimpleLink("\u2718", "Voraussetzung entfernen"));

		panel.setBorder(new EmptyBorder(5,5,5,5));
		
		premisePanel.add(panel);
	}
	
	private void createConclusion() {
		((MultilineLayout)getLayout()).newLine();
		add(new ConclusionProcessView());
	}

	static JLabel createMathematicalLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(mathFont);
		label.setAlignmentX(LEFT_ALIGNMENT);
		return label;
	}
}