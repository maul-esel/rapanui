package rapanui.ui;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Map;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import rapanui.core.ProofEnvironment;

class MainWindow extends JFrame implements PropertyChangeListener, ApplicationObserver {
	private static final long serialVersionUID = 1L;

	private final Application app;
	private ProofEnvironment activeEnvironment;

	private final SymbolKeyboard keyboard = new SymbolKeyboard();

	private final JPanel proofContainer = new JPanel(new CardLayout());
	private final JComboBox<String> proofList = new JComboBox<String>();

	// use a counter instead of counting existing ones so there are no duplicates after a deletion
	private int environmentCounter = 1;
	private final Map<ProofEnvironment, String> environmentNameMap = new HashMap<ProofEnvironment, String>();
	private final Map<String, ProofEnvironmentPanel> environmentViewMap = new HashMap<String, ProofEnvironmentPanel>();

	public MainWindow(Application app) {
		assert app != null;
		this.app = app;

		initializeContent();
		setTitle("RAPA nui – Relational Algebra Proof Assistant");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);

		for (ProofEnvironment environment : app.getEnvironments())
			createEnvironmentView(environment);
		app.addObserver(this);

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
		proofSelectionPanel.add(new SimpleLink("\u2A01", "Neuen Beweis starten",
				UICommand.createProofEnvironment(app)));
		proofSelectionPanel.add(new SimpleLink("\u2718", "Aktuellen Beweis löschen",
				UICommand.removeProofEnvironment(app, () -> activeEnvironment)));

		proofContainer.setOpaque(false);
		proofList.addItemListener((e) -> activateEnvironmentView(e.getItem().toString()));

		JScrollPane scrollContainer = new JScrollPane(proofContainer);
		scrollContainer.setBorder(null);
		scrollContainer.setOpaque(false);
		scrollContainer.getViewport().setOpaque(false);

		leftPanel.setBorder(new EmptyBorder(10,10,10,10));
		leftPanel.add(proofSelectionPanel, BorderLayout.NORTH);
		leftPanel.add(scrollContainer, BorderLayout.CENTER);
		leftPanel.add(keyboard, BorderLayout.SOUTH);

		JPanel suggestionPanel = new JPanel();
		suggestionPanel.setBackground(Color.WHITE);
		suggestionPanel.add(new JLabel("Vorschläge"), BorderLayout.NORTH);

		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, suggestionPanel);
		rootPane.getContentPane().add(splitter, BorderLayout.CENTER);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(this);
	}

	private void createEnvironmentView(ProofEnvironment environment) {
		ProofEnvironmentPanel view = new ProofEnvironmentPanel(app, environment);
		String name = "Beweis " + environmentCounter++;

		environmentNameMap.put(environment, name);
		environmentViewMap.put(name, view);

		proofContainer.add(view, name);
		proofList.addItem(name);

		activateEnvironmentView(name);
	}

	private void activateEnvironmentView(String name) {
		((CardLayout)proofContainer.getLayout()).show(proofContainer, name);
		proofList.setSelectedItem(name);
		activeEnvironment = environmentViewMap.get(name).getModel();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("focusOwner".equals(evt.getPropertyName()))
			keyboard.setVisible(evt.getNewValue() instanceof JTextField);
	}

	@Override
	public void environmentAdded(ProofEnvironment environment) {
		createEnvironmentView(environment);
	}

	@Override
	public void environmentRemoved(ProofEnvironment environment) {
		String name = environmentNameMap.get(environment);
		ProofEnvironmentPanel view = environmentViewMap.get(name);
		if (name != null && view != null) {
			proofContainer.remove(view);
			proofList.removeItem(name);

			// first remove from UI so that any reactions to UI modification still know the objects.
			environmentNameMap.remove(environment);
			environmentViewMap.remove(name);
		}
	}
}
