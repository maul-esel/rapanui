package rapanui.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private static final char[] keyboardSymbols = { '˘', '*', '⁺', 'Π', '∅', '⊆', '∩', '∪' };
	
	public MainWindow(Application app) {
		initializeContent();
		setTitle("RAPA nui – Relational Algebra Proof Assistant");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setExtendedState(MAXIMIZED_BOTH);
		pack();
		setVisible(true);
	}
	
	private void initializeContent() {
		rootPane.getContentPane().setBackground(Color.WHITE);
		rootPane.getContentPane().setLayout(new BorderLayout());	

		JLabel title = new JLabel("RAPA nui – Relational Algebra Proof Assistant");
		title.setFont(new Font(title.getFont().getFamily(), Font.BOLD, 26));
		title.setBorder(new EmptyBorder(15,15,15,15));
		rootPane.getContentPane().add(title, BorderLayout.NORTH);

		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBackground(Color.WHITE);

		JPanel proofSelectionPanel = new JPanel();
		proofSelectionPanel.setOpaque(false);
		proofSelectionPanel.setLayout(new BoxLayout(proofSelectionPanel, BoxLayout.X_AXIS));

		JComboBox<String> proofList = new JComboBox<String>(new String[] { "Beweis 1" });

		proofSelectionPanel.add(new JLabel("Aktueller Beweis:"));
		proofSelectionPanel.add(Box.createHorizontalStrut(5));
		proofSelectionPanel.add(proofList);
		proofSelectionPanel.add(new SimpleLink("\u2A01", "Neuen Beweis starten"));
		proofSelectionPanel.add(new SimpleLink("\u2718", "Aktuellen Beweis löschen"));

		JPanel proofContainer = new JPanel(new CardLayout());
		proofContainer.setOpaque(false);

		// TODO: remove dummy content
		for (int i = 1; i < 4; ++i) {
			JScrollPane tab = new JScrollPane(new ProofEnvironmentPanel());
			tab.setBorder(null);
			tab.setOpaque(false);
			tab.getViewport().setOpaque(false);
			proofContainer.add(tab);
		}

		leftPanel.setBorder(new EmptyBorder(10,10,10,10));
		leftPanel.add(proofSelectionPanel, BorderLayout.NORTH);
		leftPanel.add(proofContainer, BorderLayout.CENTER);

		JPanel suggestionPanel = new JPanel();
		suggestionPanel.setBackground(Color.WHITE);
		suggestionPanel.add(new JLabel("Vorschläge"), BorderLayout.NORTH);

		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, suggestionPanel);
		rootPane.getContentPane().add(splitter, BorderLayout.CENTER);
	}
}
