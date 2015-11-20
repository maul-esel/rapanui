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
		doLayout();
		setVisible(true);
	}
	
	private void initializeContent() {
		rootPane.getContentPane().setBackground(Color.WHITE);
		rootPane.getContentPane().setLayout(new BorderLayout());	

		JLabel title = new JLabel("RAPA nui – Relational Algebra Proof Assistant");
		title.setFont(new Font(title.getFont().getFamily(), Font.BOLD, 26));
		title.setBorder(new EmptyBorder(15,15,15,15));
		rootPane.getContentPane().add(title, BorderLayout.NORTH);

		JTabbedPane proofTabs = new JTabbedPane();
		proofTabs.setTabPlacement(JTabbedPane.LEFT);

		// TODO: remove dummy content
		proofTabs.addTab("1", new ProofEnvironmentPanel());
		proofTabs.addTab("2", new ProofEnvironmentPanel());
		proofTabs.addTab("3", new ProofEnvironmentPanel());
		
		JPanel suggestionPanel = new JPanel();
		suggestionPanel.setBackground(Color.WHITE);
		suggestionPanel.add(new JLabel("Vorschläge"), BorderLayout.NORTH);
		
		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, proofTabs, suggestionPanel); 
		rootPane.getContentPane().add(splitter, BorderLayout.CENTER);
	}
}
