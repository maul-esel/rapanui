package rapanui.ui.views;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

public class FontManager {
	private FontManager() {}

	private static Font mathFont;
	private static boolean fontsLoaded = false;

	public static void loadCustomFonts() {
		if (fontsLoaded)
			return;

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			ge.registerFont(mathFont = Font.createFont(Font.TRUETYPE_FONT,
					FontManager.class.getResourceAsStream("/DejaVuSansMono.ttf")
				).deriveFont(14f));
			fontsLoaded = true;
		} catch (Exception e) {
			throw new IllegalStateException("Couldn't load fonts", e);
		}
	}

	public static Font getMathFont() {
		loadCustomFonts();
		return mathFont;
	}

	public static String getMathFontFamily() {
		return getMathFont().getFamily();
	}
}
