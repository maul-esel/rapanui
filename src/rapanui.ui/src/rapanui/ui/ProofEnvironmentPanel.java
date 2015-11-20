package rapanui.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

class ProofEnvironmentPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JPanel premisePanel;
	private JPanel conclusionPanel;

	private int premiseCount = 0; // TODO: abandon counter and use env

	public ProofEnvironmentPanel(/* ProofEnvironment env */) {
		initializeContent();
	}

	private void initializeContent() {
		setBorder(new EmptyBorder(20,20,20,20));
		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Font titleFont = new Font(getFont().getFamily(), Font.BOLD, 20);
		
		JPanel premiseHeader = new JPanel();
		premiseHeader.setBackground(Color.WHITE);
		premiseHeader.setLayout(new BoxLayout(premiseHeader, BoxLayout.X_AXIS));
		
		JLabel premiseTitle = new JLabel("Voraussetzungen");
		premiseTitle.setFont(titleFont);
		premiseHeader.add(premiseTitle);

		premiseHeader.add(Box.createHorizontalGlue());
		premiseHeader.add(new JButton("+F"));
		premiseHeader.add(new JButton("+D"));
		add(premiseHeader);

		premisePanel = new JPanel();
		premisePanel.setLayout(new BoxLayout(premisePanel, BoxLayout.Y_AXIS));
		add(premisePanel);
		
		add(Box.createVerticalStrut(30));

		JPanel conclusionHeader = new JPanel();
		conclusionHeader.setBackground(Color.WHITE);
		conclusionHeader.setLayout(new BoxLayout(conclusionHeader, BoxLayout.X_AXIS));
		
		JLabel conclusionTitle = new JLabel("Folgerungen");
		conclusionTitle.setFont(titleFont);
		conclusionHeader.add(conclusionTitle);
		conclusionHeader.add(Box.createHorizontalGlue());
		conclusionHeader.add(new JButton("+"));
		add(conclusionHeader);

		conclusionPanel = new JPanel();
		conclusionPanel.setLayout(new BoxLayout(conclusionPanel, BoxLayout.Y_AXIS));
		add(conclusionPanel);

		// TODO: remove dummy content
		createConclusion();
		createConclusion();
		createFormulaPremise();
		createDefinitionReferencePremise();
	}

	private void createFormulaPremise() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBackground(Color.WHITE);
		
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
		panel.setBackground(Color.WHITE);
		
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
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(new CompoundBorder(new EmptyBorder(5,0,5,0),
				new CompoundBorder(new LineBorder(new Color(0x42aaff), 1, true), new EmptyBorder(10,10,10,10))));
		
		String shortForm = "R = S;T"; // TODO: remove dummy data
		
		panel.add(new JLabel(shortForm), BorderLayout.NORTH);
		
		JPanel longForm = new JPanel();
		longForm.setLayout(new GridBagLayout());
		longForm.setBackground(Color.LIGHT_GRAY);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		longForm.add(new JTextField("R")); // TODO: remove dummy data
		
		constraints.gridx = 1;

		// TODO: remove dummy data
		String[] steps = new String[] { "= R;I\t(Neutralität von I)", "= R;(S;T)\t(nach Voraussetzung)", "= (R;S);T\t(Assoziativität)", "= S;T\t(nach Voraussetzung)"};
		for (int i = 0; i < steps.length; ++i) {
			longForm.add(new JLabel(steps[i]), constraints);
			constraints.gridy++;
		}
		
		panel.add(longForm, BorderLayout.CENTER);
		conclusionPanel.add(panel);
	}
}