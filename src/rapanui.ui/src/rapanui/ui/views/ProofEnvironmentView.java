package rapanui.ui.views;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import java.util.HashMap;
import java.util.Map;

import rapanui.dsl.DefinitionReference;
import rapanui.dsl.Predicate;
import rapanui.ui.controls.CollapseButton;
import rapanui.ui.controls.SimpleLink;
import rapanui.ui.controls.SyntaxTextField;
import rapanui.ui.models.ConclusionProcessModel;
import rapanui.ui.models.ProofEnvironmentModel;

class ProofEnvironmentView extends JPanel implements ProofEnvironmentModel.Observer {
	private static final long serialVersionUID = 1L;

	// ugly hack: use this instead of Integer.MAX_VALUE to avoid integer overflow
	private static final int MAX_WIDTH = 5000;

	private final ProofEnvironmentModel model;

	private final JPanel premisePanel = new JPanel(new GridLayout(0, 4));

	private final Map<Predicate, JPanel> premiseViewMap = new HashMap<Predicate, JPanel>();
	private final Map<ConclusionProcessModel, ConclusionProcessView> conclusionViewMap
		= new HashMap<ConclusionProcessModel, ConclusionProcessView>();

	public ProofEnvironmentView(ProofEnvironmentModel model) {
		assert model != null;

		this.model = model;
		initializeContent();

		for (Predicate premise : model.getPremises())
			displayPremise(premise);
		for (ConclusionProcessModel conclusion : model.getConclusions())
			displayConclusion(conclusion);

		model.addObserver(this);
	}

	private void initializeContent() {
		setOpaque(false);
		setLayout(new MultilineLayout());

		Font defaultFont = FontManager.getDefaultFont();
		Font titleFont = FontManager.getTitleFont();

		/* premises */
		premisePanel.setOpaque(false);

		/* creating new premises */
		JPanel newPremisePanel = new JPanel(new MultilineLayout());
		newPremisePanel.setOpaque(false);
		newPremisePanel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5,5,5,5)));

		JTextField termInput = new SyntaxTextField(SyntaxTextField.ParsingMode.Term, model.definitionPremiseInputModel);
		termInput.setMaximumSize(new Dimension(MAX_WIDTH, termInput.getMaximumSize().height));

		JComboBox<String> definitionSelection = new JComboBox<String>(model.definitionSelectionModel);
		definitionSelection.setFont(defaultFont);
		definitionSelection.setMaximumSize(new Dimension(MAX_WIDTH, definitionSelection.getMaximumSize().height));

		JLabel newPremiseLabel = new JLabel("Neue Voraussetzung:");
		newPremiseLabel.setAlignmentX(RIGHT_ALIGNMENT);
		newPremiseLabel.setFont(defaultFont);

		newPremisePanel.add(newPremiseLabel, (Integer)0);
		newPremisePanel.add(new JSeparator(), (Integer)1);

		JLabel formulaPremiseLabel = new JLabel("Sei ");
		formulaPremiseLabel.setFont(defaultFont);
		newPremisePanel.add(formulaPremiseLabel, (Integer)2);
		newPremisePanel.add(new SyntaxTextField(SyntaxTextField.ParsingMode.Formula, model.formulaPremiseInputModel));
		newPremisePanel.add(new SimpleLink(model.createFormulaPremiseCommand));

		JLabel definitionReferencePremiseLabel = new JLabel("Sei ");
		definitionReferencePremiseLabel.setFont(defaultFont);
		newPremisePanel.add(definitionReferencePremiseLabel, (Integer)3);
		newPremisePanel.add(termInput);
		newPremisePanel.add(definitionSelection);
		newPremisePanel.add(new SimpleLink(model.createDefinitionReferencePremiseCommand));

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

		JLabel newConclusionLabel = new JLabel("Neue Folgerung:");
		JLabel startTermLabel = new JLabel("Startterm: ");
		newConclusionLabel.setFont(defaultFont);
		startTermLabel.setFont(defaultFont);

		newConclusionPanel.add(newConclusionLabel, (Integer)0);
		newConclusionPanel.add(new JSeparator(), (Integer)1);
		newConclusionPanel.add(startTermLabel, (Integer)2);
		newConclusionPanel.add(new SyntaxTextField(SyntaxTextField.ParsingMode.Term, model.conclusionTermInputModel));
		newConclusionPanel.add(new SimpleLink(model.createConclusionCommand));

		/* complete panel layout */
		add(premiseHeader, (Integer)0);
		add(premisePanel, (Integer)1);
		add(newPremisePanel, (Integer)2);
		add(Box.createVerticalStrut(30), (Integer)3);
		add(conclusionHeader, (Integer)4);
		add(Box.createVerticalStrut(10), (Integer)5);
		add(newConclusionPanel, (Integer)6);
	}

	private void displayPremise(Predicate premise) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setOpaque(false);

		if (premise instanceof DefinitionReference) {
			DefinitionReference ref = (DefinitionReference)premise;

			JLabel definitionNameLabel = new JLabel(ref.getDefinition().getName());
			definitionNameLabel.setFont(FontManager.getDefaultFont());

			panel.add(createMathematicalLabel(ref.getTarget().serialize()));
			panel.add(Box.createHorizontalStrut(3));
			panel.add(definitionNameLabel);
		} else
			panel.add(createMathematicalLabel(premise.serialize()));

		panel.add(new SimpleLink(model.getDeletePremiseCommand(premise)));

		panel.setBorder(new EmptyBorder(5,5,5,5));
		premisePanel.add(panel);
		premiseViewMap.put(premise, panel);

		validate(); // make sure new premise is actually shown
	}

	private void displayConclusion(ConclusionProcessModel conclusionModel) {
		((MultilineLayout)getLayout()).newLine();
		ConclusionProcessView view = new ConclusionProcessView(conclusionModel);
		add(view);
		conclusionViewMap.put(conclusionModel, view);

		validate(); // make sure new conclusion is actually shown
	}

	static JLabel createMathematicalLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(FontManager.getMathFont());
		label.setAlignmentX(LEFT_ALIGNMENT);
		return label;
	}

	@Override
	public void premiseAdded(Predicate premise) {
		displayPremise(premise);
	}

	@Override
	public void premiseRemoved(Predicate premise) {
		if (!premiseViewMap.containsKey(premise))
			return;

		JPanel view = premiseViewMap.get(premise);
		premiseViewMap.remove(premise);
		premisePanel.remove(view);

		validate();
		repaint();
	}

	@Override
	public void conclusionStarted(ConclusionProcessModel conclusion) {
		displayConclusion(conclusion);
	}

	@Override
	public void conclusionRemoved(ConclusionProcessModel conclusionModel) {
		if (!conclusionViewMap.containsKey(conclusionModel))
			return;

		ConclusionProcessView view = conclusionViewMap.get(conclusionModel);
		conclusionViewMap.remove(conclusionModel);
		remove(view);

		validate();
		repaint();
	}
}