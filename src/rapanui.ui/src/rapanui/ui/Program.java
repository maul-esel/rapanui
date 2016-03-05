package rapanui.ui;

import rapanui.core.Application;
import rapanui.ui.models.ApplicationModel;
import rapanui.ui.views.MainWindow;

public class Program {
	public static void main(String[] args) {
		Application instance = new Application();
		ApplicationModel model = new ApplicationModel(instance);
		new MainWindow(model);
	}
}
