package rapanui.ui;

public class Application {
	public static void main(String[] args) {
		new Application().run();
	}

	public void run() {
		new MainWindow(this);
	}
}