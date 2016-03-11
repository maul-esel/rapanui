package rapanui.ui.views;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Map;
import java.util.function.Consumer;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

import rapanui.ui.controls.*;
import rapanui.ui.models.*;

import rapanui.core.Justification;
import rapanui.core.Transformation;

public class MainWindow extends JFrame implements PropertyChangeListener, ApplicationModel.Observer {
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "RAPA NUI – Relational Algebra Proof Assistant 'N User Interface";

	private final ApplicationModel appModel;

	private final SymbolKeyboard keyboard = new SymbolKeyboard();
	private final JustificationViewer justificationViewer = new JustificationViewer();
	private final JPanel proofContainer = new JPanel(new CardLayout());
	private final JComboBox<String> proofList = new JComboBox<String>();
	private final JList<Transformation> suggestionList = new JList<Transformation>();

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
		environmentActivated(appModel.getActiveEnvironment());
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
		proofSelectionPanel.add(new SimpleLink(appModel.exportEnvironmentCommand));
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

		suggestionList.setModel(appModel.suggestionListModel);
		suggestionList.setCellRenderer(new SuggestionListCellRenderer());
		suggestionList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2) {
					int index = suggestionList.locationToIndex(e.getPoint());
					if (index >= 0 && suggestionList.getCellBounds(index, index).contains(e.getPoint())) {
						Transformation suggestion = suggestionList.getModel().getElementAt(index);
						appModel.applySuggestion(suggestion);
					}
				}
			}
		});
		suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		suggestionList.addListSelectionListener(this::onSuggestionSelected);

		JScrollPane suggestionScroller = new JScrollPane();
		suggestionScroller.setViewportView(suggestionList);

		JSplitPane suggestionPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, suggestionScroller, justificationViewer);
		suggestionPanel.setBackground(Color.WHITE);

		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, suggestionPanel);
		rootPane.getContentPane().add(splitter, BorderLayout.CENTER);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(this);
	}

	private void createEnvironmentView(ProofEnvironmentModel model) {
		ProofEnvironmentView view = new ProofEnvironmentView(model);

		environmentViewMap.put(model, view);
		proofContainer.add(view, model.getName());
	}

	private void onSuggestionSelected(ListSelectionEvent e) {
		Transformation suggestion = suggestionList.getSelectedValue();
		if (suggestion != null)
			justificationViewer.loadJustification(suggestion.getJustification());
		else
			justificationViewer.clear();
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
		proofContainer.setVisible(environmentModel != null);
		if (environmentModel != null) {
			String name = environmentModel.getName();
			((CardLayout)proofContainer.getLayout()).show(proofContainer, name);
		}
	}

	@Override
	public void justificationOpened(Justification justification) {
		suggestionList.clearSelection();
		justificationViewer.loadJustification(justification);
	}

	@Override
	public void confirmationRequested(String message, Consumer<Boolean> handler) {
		int result = JOptionPane.showConfirmDialog(this, message, "Confirmation requested", JOptionPane.OK_CANCEL_OPTION);
		handler.accept(result == JOptionPane.OK_OPTION);
	}

	@Override
	public void filePathRequested(boolean isWrite, String[] extensions, Consumer<String> callback) {
		JFileChooser chooser = new JFileChooser();

		chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
		for (String ext : extensions)
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(ext + " files", ext));

		chooser.setMultiSelectionEnabled(false);
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			callback.accept(chooser.getSelectedFile().getAbsolutePath());
	}
}
