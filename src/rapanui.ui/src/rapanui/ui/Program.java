package rapanui.ui;

import rapanui.core.Application;
import rapanui.ui.models.ApplicationModel;
import rapanui.ui.views.MainWindow;

public class Program {
	public static void main(String[] args) {
		Application instance = new Application();

		try {
			for (String ruleSystem : args)
				instance.getRuleSystems().load(ruleSystem);

			if (args.length == 0)
				instance.getRuleSystems().load(Program.class.getResourceAsStream("/library.raps"));
		} catch (Exception e) {
			System.out.println("Failed to load rule system: " + e.getMessage());
			return;
		}

		ApplicationModel model = new ApplicationModel(instance);
		new MainWindow(model);
	}
}
