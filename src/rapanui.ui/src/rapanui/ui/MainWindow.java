package rapanui.ui;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.List;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.moai.RuleSystem;

class MainWindow extends JFrame implements PropertyChangeListener, ApplicationObserver {
	private static final long serialVersionUID = 1L;

	private final Application app;
	private final SymbolKeyboard keyboard = new SymbolKeyboard();

	private final JPanel proofContainer = new JPanel(new CardLayout());
	private final JComboBox<String> proofList = new JComboBox<String>();
	private final List<JScrollPane> environmentViews = new LinkedList<JScrollPane>();

	public MainWindow(Application app) {
		this.app = app;
		app.addObserver(this);

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

		proofSelectionPanel.add(new JLabel("Aktueller Beweis:"));
		proofSelectionPanel.add(Box.createHorizontalStrut(5));
		proofSelectionPanel.add(proofList);
		proofSelectionPanel.add(new SimpleLink("\u2A01", "Neuen Beweis starten"));
		proofSelectionPanel.add(new SimpleLink("\u2718", "Aktuellen Beweis löschen"));

		proofContainer.setOpaque(false);

		for (ProofEnvironment environment : app.getEnvironments())
			createEnvironmentView(environment);

		proofList.addItemListener((e) -> {
			((CardLayout)proofContainer.getLayout()).show(proofContainer, e.getItem().toString());
		});

		leftPanel.setBorder(new EmptyBorder(10,10,10,10));
		leftPanel.add(proofSelectionPanel, BorderLayout.NORTH);
		leftPanel.add(proofContainer, BorderLayout.CENTER);
		leftPanel.add(keyboard, BorderLayout.SOUTH);

		JPanel suggestionPanel = new JPanel();
		suggestionPanel.setBackground(Color.WHITE);
		suggestionPanel.add(new JLabel("Vorschläge"), BorderLayout.NORTH);

		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, suggestionPanel);
		rootPane.getContentPane().add(splitter, BorderLayout.CENTER);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(this);
	}

	private void createEnvironmentView(ProofEnvironment environment) {
		JScrollPane tab = new JScrollPane(new ProofEnvironmentPanel());
		tab.setBorder(null);
		tab.setOpaque(false);
		tab.getViewport().setOpaque(false);

		String name = "Beweis " + (environmentViews.size()+1);
		proofContainer.add(tab, name);
		proofList.addItem(name);
		environmentViews.add(tab);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("focusOwner".equals(evt.getPropertyName()))
			keyboard.setVisible(evt.getNewValue() instanceof JTextField);
	}

	@Override
	public void ruleSystemLoaded(RuleSystem loaded) { /* nothing to do here */ }

	@Override
	public void environmentAdded(ProofEnvironment environment) {
		createEnvironmentView(environment);
	}

	@Override
	public void environmentRemoved(ProofEnvironment environment) {
		// TODO
	}
}
