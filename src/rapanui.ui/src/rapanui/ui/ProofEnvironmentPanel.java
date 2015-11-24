package rapanui.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

class ProofEnvironmentPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JPanel premisePanel;

	public ProofEnvironmentPanel(/* ProofEnvironment env */) {
		initializeContent();
	}

	private void initializeContent() {
		setOpaque(false);
		setLayout(new MultilineLayout());

		Font titleFont = new Font(getFont().getFamily(), Font.BOLD, 20);

		JPanel premiseHeader = new JPanel();
		premiseHeader.setOpaque(false);
		premiseHeader.setLayout(new BoxLayout(premiseHeader, BoxLayout.X_AXIS));

		premisePanel = new JPanel(new GridLayout(0, 4));
		premisePanel.setOpaque(false);

		JLabel premiseTitle = new JLabel("Voraussetzungen");
		premiseTitle.setFont(titleFont);

		JPanel newPremisePanel = new JPanel();
		newPremisePanel.setLayout(new BoxLayout(newPremisePanel, BoxLayout.Y_AXIS));
		newPremisePanel.setOpaque(false);
		newPremisePanel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5,5,5,5)));

		JPanel premiseContentPanel = new JPanel();
		premiseContentPanel.setOpaque(false);

		JPanel premiseTypePanel = new JPanel();
		premiseTypePanel.setLayout(new BoxLayout(premiseTypePanel, BoxLayout.Y_AXIS));
		premiseTypePanel.setOpaque(false);

		JPanel premiseInputPanel = new JPanel(new CardLayout());
		premiseInputPanel.setOpaque(false);

		JRadioButton formulaPremise = new JRadioButton("Formel");
		formulaPremise.setOpaque(false);
		formulaPremise.setSelected(true);
		formulaPremise.addActionListener((e) -> {
			((CardLayout)premiseInputPanel.getLayout()).first(premiseInputPanel);
		});

		JRadioButton definitionPremise = new JRadioButton("Definition");
		definitionPremise.setOpaque(false);
		definitionPremise.addActionListener((e) -> {
			((CardLayout)premiseInputPanel.getLayout()).last(premiseInputPanel);
		});

		ButtonGroup premiseTypeGroup = new ButtonGroup();
		premiseTypeGroup.add(formulaPremise);
		premiseTypeGroup.add(definitionPremise);

		premiseTypePanel.add(formulaPremise);
		premiseTypePanel.add(definitionPremise);

		JTextField formulaInput = new JTextField("R = S;R"); // TODO: remove dummy content

		JPanel defRefInputPanel = new JPanel(new FlowLayout());
		defRefInputPanel.setOpaque(false);
		defRefInputPanel.add(new JLabel("Sei"));
		defRefInputPanel.add(new JTextField("R;T")); // TODO: remove dummy content
		defRefInputPanel.add(new JComboBox<String>(new String[] { "reflexiv", "transitiv" })); // TODO: remove dummy content

		premiseInputPanel.add(formulaInput);
		premiseInputPanel.add(defRefInputPanel);

		JButton createPremise = new SimpleLink("\u2714", "Neue Voraussetzung erstellen");

		premiseContentPanel.add(premiseTypePanel);
		premiseContentPanel.add(premiseInputPanel);
		premiseContentPanel.add(createPremise);

		JLabel newPremiseLabel = new JLabel("Neue Voraussetzung:");
		newPremiseLabel.setAlignmentX(RIGHT_ALIGNMENT);

		newPremisePanel.add(newPremiseLabel);
		newPremisePanel.add(new JSeparator());
		newPremisePanel.add(premiseContentPanel);

		premiseHeader.add(new CollapseButton(premisePanel, newPremisePanel));
		premiseHeader.add(Box.createHorizontalStrut(5));
		premiseHeader.add(premiseTitle);
		premiseHeader.add(Box.createHorizontalGlue());

		JPanel conclusionHeader = new JPanel();
		conclusionHeader.setOpaque(false);
		conclusionHeader.setLayout(new BoxLayout(conclusionHeader, BoxLayout.X_AXIS));

		JLabel conclusionTitle = new JLabel("Folgerungen");
		conclusionTitle.setFont(titleFont);

		conclusionHeader.add(conclusionTitle);
		conclusionHeader.add(Box.createHorizontalGlue());

		JPanel newConclusionPanel = new JPanel();
		newConclusionPanel.setLayout(new BoxLayout(newConclusionPanel, BoxLayout.Y_AXIS));
		newConclusionPanel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5,5,5,5)));
		newConclusionPanel.setOpaque(false);

		JPanel newConclusionContent = new JPanel();
		newConclusionContent.setOpaque(false);

		newConclusionContent.add(new JLabel("Startterm:"));
		newConclusionContent.add(new JTextField("S;S;R")); // TODO: remove dummy content
		newConclusionContent.add(new SimpleLink("\u2714", "Neue Folgerung erstellen"));

		newConclusionPanel.add(new JLabel("Neue Folgerung:"));
		newConclusionPanel.add(new JSeparator());
		newConclusionPanel.add(newConclusionContent);

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
		label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, label.getFont().getSize()));
		return label;
	}
}