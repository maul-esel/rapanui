package rapanui.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class ConclusionProcessView extends JPanel {
	private static final long serialVersionUID = 1L;

	public ConclusionProcessView(/* ConclusionProcess conclusion */) {
		initializeContent();
	}

	private void initializeContent() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);
		setBorder(new CompoundBorder(new EmptyBorder(5,0,5,0),
				new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5,5,5,5))));

		String shortForm = "R = S;T"; // TODO: remove dummy data

		JPanel header = new JPanel();
		header.setBackground(Color.WHITE);
		header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));

		JPanel longForm = new JPanel(new GridBagLayout());
		longForm.setOpaque(false);

		JSeparator separator = new JSeparator();

		header.add(new CollapseButton(longForm, separator));
		header.add(new JLabel(shortForm));
		header.add(Box.createHorizontalGlue());
		header.add(new SimpleLink("\u2B06", "Nach oben"));
		header.add(new SimpleLink("\u2B07", "Nach unten"));
		header.add(new SimpleLink("\u27F2", "Letzter Schritt rückgängig"));
		header.add(new SimpleLink("\u2718", "Folgerung entfernen"));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;

		constraints.gridx = 0;
		constraints.gridy = 0;
		longForm.add(ProofEnvironmentPanel.createMathematicalLabel("R ")); // TODO: remove dummy data

		// TODO: remove dummy data
		String[] steps = new String[] { "= R;I", "= R;(S;T)", "= (R;S);T", "= S;T"};
		String[] reasons = new String[] { "Neutralität von I", "nach Voraussetzung", "Assoziativität", "nach Voraussetzung" };

		for (int i = 0; i < steps.length; ++i) {
			constraints.gridx = 1;
			longForm.add(ProofEnvironmentPanel.createMathematicalLabel(steps[i]), constraints);
			constraints.gridx = 2;
			longForm.add(new JLabel("(" + reasons[i] + ")"), constraints);

			constraints.gridy++;
		}

		add(header);
		add(separator);
		add(longForm);
	}
}
