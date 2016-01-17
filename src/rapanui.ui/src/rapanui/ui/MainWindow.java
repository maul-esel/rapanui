package rapanui.ui;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Map;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import rapanui.core.Transformation;
import rapanui.ui.controls.SimpleLink;
import rapanui.ui.models.ApplicationModel;
import rapanui.ui.models.ProofEnvironmentModel;
import rapanui.ui.views.ProofEnvironmentView;

class MainWindow extends JFrame implements PropertyChangeListener, ApplicationModel.Observer {
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "RAPA nui – Relational Algebra Proof Assistant";

	private final ApplicationModel appModel;

	private final SymbolKeyboard keyboard = new SymbolKeyboard();
	private final JPanel proofContainer = new JPanel(new CardLayout());
	private final JComboBox<String> proofList = new JComboBox<String>();

	private final Map<ProofEnvironmentModel, ProofEnvironmentView> environmentViewMap = new HashMap<ProofEnvironmentModel, ProofEnvironmentView>();

	public MainWindow(ApplicationModel appModel) {
		assert appModel != null;
		this.appModel = appModel;

		initializeContent();
		setTitle(TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);

		for (ProofEnvironmentModel environment : appModel.getEnvironments())
			createEnvironmentView(environment);
		appModel.addObserver(this);

		pack();
		setVisible(true);
	}

	private void initializeContent() {
		rootPane.getContentPane().setBackground(Color.WHITE);
		rootPane.getContentPane().setLayout(new BorderLayout());	

		JLabel title = new JLabel(TITLE);
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
		proofSelectionPanel.add(new SimpleLink(appModel.createEnvironmentCommand));
		proofSelectionPanel.add(new SimpleLink(appModel.deleteEnvironmentCommand));

		proofContainer.setOpaque(false);
		proofList.setModel(appModel.environmentNameModel);

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

	private void createEnvironmentView(ProofEnvironmentModel model) {
		ProofEnvironmentView view = new ProofEnvironmentView(model);

		environmentViewMap.put(model, view);
		proofContainer.add(view, model.getName());
	}

	/* ****************************************** *
	 * PropertyChangeListener                     *
	 * ****************************************** */

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("focusOwner".equals(evt.getPropertyName()))
			keyboard.setVisible(evt.getNewValue() instanceof JTextField);
	}

	/* ****************************************** *
	 * ApplicationModel.Observer                  *
	 * ****************************************** */

	@Override
	public void environmentCreated(ProofEnvironmentModel environment) {
		createEnvironmentView(environment);
	}

	@Override
	public void environmentDeleted(ProofEnvironmentModel environmentModel) {
		ProofEnvironmentView view = environmentViewMap.get(environmentModel);
		environmentViewMap.remove(environmentModel);
		proofContainer.remove(view);
	}

	@Override
	public void environmentActivated(ProofEnvironmentModel environmentModel) {
		String name = environmentModel.getName();
		((CardLayout)proofContainer.getLayout()).show(proofContainer, name);
	}

	@Override
	public void suggestionsLoaded(Transformation[] suggestions) {
		// TODO
	}
}
