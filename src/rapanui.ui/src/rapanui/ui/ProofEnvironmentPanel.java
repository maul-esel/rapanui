package rapanui.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;

class ProofEnvironmentPanel extends JPanel implements ActionListener {
	private final static String CREATE_FORMULA_PREMISE = "create formula premise";
	private final static String CREATE_DEF_REF_PREMISE = "create definition reference premise";
	private final static String CREATE_CONCLUSION = "create conclusion process";

	private static final long serialVersionUID = 1L;

	private JPanel premisePanel;
	private JPanel conclusionPanel;

	private int premiseCount = 0; // TODO: abandon counter and use env

	public ProofEnvironmentPanel(/* ProofEnvironment env */) {
		initializeContent();
	}

	private void initializeContent() {
		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		Font titleFont = new Font(getFont().getFamily(), Font.BOLD, 20);

		JPanel premiseHeader = new JPanel();
		premiseHeader.setOpaque(false);
		premiseHeader.setLayout(new BoxLayout(premiseHeader, BoxLayout.X_AXIS));

		premisePanel = new JPanel();
		premisePanel.setOpaque(false);
		premisePanel.setLayout(new BoxLayout(premisePanel, BoxLayout.Y_AXIS));
		
		JLabel premiseTitle = new JLabel("Voraussetzungen");
		premiseTitle.setFont(titleFont);

		JButton newFormulaButton = new JButton("+F");
		newFormulaButton.setActionCommand(CREATE_FORMULA_PREMISE);
		newFormulaButton.addActionListener(this);

		JButton newDefRefButton = new JButton("+D");
		newDefRefButton.setActionCommand(CREATE_DEF_REF_PREMISE);
		newDefRefButton.addActionListener(this);

		premiseHeader.add(new CollapseButton(premisePanel, newFormulaButton, newDefRefButton));
		premiseHeader.add(Box.createHorizontalStrut(5));
		premiseHeader.add(premiseTitle);
		premiseHeader.add(Box.createHorizontalGlue());
		premiseHeader.add(newFormulaButton);
		premiseHeader.add(newDefRefButton);

		JPanel conclusionHeader = new JPanel();
		conclusionHeader.setOpaque(false);
		conclusionHeader.setLayout(new BoxLayout(conclusionHeader, BoxLayout.X_AXIS));

		JLabel conclusionTitle = new JLabel("Folgerungen");
		conclusionTitle.setFont(titleFont);

		JButton newConclusionButton = new JButton("+");
		newConclusionButton.setActionCommand(CREATE_CONCLUSION);
		newConclusionButton.addActionListener(this);

		conclusionHeader.add(conclusionTitle);
		conclusionHeader.add(Box.createHorizontalGlue());
		conclusionHeader.add(newConclusionButton);

		conclusionPanel = new JPanel();
		conclusionPanel.setLayout(new BoxLayout(conclusionPanel, BoxLayout.Y_AXIS));
		conclusionPanel.setOpaque(false);

		add(premiseHeader);
		add(premisePanel);
		add(Box.createVerticalStrut(30));
		add(conclusionHeader);
		add(conclusionPanel);
		add(Box.createVerticalGlue());

		// TODO: remove dummy content
		createConclusion();
		createConclusion();
		createFormulaPremise();
		createDefinitionReferencePremise();
	}

	private void createFormulaPremise() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setOpaque(false);
		
		panel.add(new JLabel(premiseCount == 0 ? "Sei " : "und "));
		
		JTextField textField = new JTextField();
		textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getMinimumSize().height));
		panel.add(textField);
		
		panel.add(new JButton("x"));
		
		panel.setBorder(new EmptyBorder(5,5,5,5));
		
		premisePanel.add(panel);
		premiseCount++;

		// TODO: setup validation
	}

	private void createDefinitionReferencePremise() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setOpaque(false);
		
		panel.add(new JLabel(premiseCount == 0 ? "Sei " : "und "));

		JTextField textField = new JTextField();
		textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getMinimumSize().height));
		panel.add(textField);
		
		JComboBox<String> combo = new JComboBox<String>();
		combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, combo.getMinimumSize().height));
		panel.add(combo);
		
		panel.add(new JButton("x"));

		panel.setBorder(new EmptyBorder(5,5,5,5));
		
		premisePanel.add(panel);
		premiseCount++;
	}
	
	private void createConclusion() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setOpaque(false);
		panel.setBorder(new CompoundBorder(new EmptyBorder(5,0,5,0),
				new LineBorder(Color.WHITE, 10, true)));

		String shortForm = "R = S;T"; // TODO: remove dummy data

		JPanel header = new JPanel();
		header.setBackground(Color.WHITE);
		header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));

		JPanel longForm = new JPanel();
		longForm.setLayout(new GridBagLayout());
		longForm.setBackground(Color.LIGHT_GRAY);

		header.add(new CollapseButton(longForm));
		header.add(new JLabel(shortForm));
		header.add(Box.createHorizontalGlue());
		header.add(new JButton("x"));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;

		constraints.gridx = 0;
		constraints.gridy = 0;
		longForm.add(new JTextField("R")); // TODO: remove dummy data

		// TODO: remove dummy data
		String[] steps = new String[] { "= R;I", "= R;(S;T)", "= (R;S);T", "= S;T)"};
		String[] reasons = new String[] { "Neutralität von I", "nach Voraussetzung", "Assoziativität", "nach Voraussetzung" };

		for (int i = 0; i < steps.length; ++i) {
			constraints.gridx = 1;
			longForm.add(new JLabel(steps[i]), constraints);
			constraints.gridx = 2;
			longForm.add(new JLabel("(" + reasons[i] + ")"), constraints);

			constraints.gridy++;
		}

		panel.add(header);
		panel.add(longForm);
		conclusionPanel.add(panel);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		switch (event.getActionCommand()) {
		case CREATE_FORMULA_PREMISE:
			createFormulaPremise();
			break;
		case CREATE_DEF_REF_PREMISE:
			createDefinitionReferencePremise();
			break;
		case CREATE_CONCLUSION:
			createConclusion();
			break;
		}

		revalidate();
	}
}