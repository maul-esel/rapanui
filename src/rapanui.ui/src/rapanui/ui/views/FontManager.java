package rapanui.ui.views;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;

public class FontManager {
	private FontManager() {}

	private static Font mathFont;
	private static Font defaultFont;
	private static boolean fontsLoaded = false;

	public static void loadCustomFonts() {
		if (fontsLoaded)
			return;

		try {
			defaultFont = loadFont("/DejaVuSans.ttf").deriveFont(14f);
			loadFont("/DejaVuSans-Bold.ttf");
			mathFont = loadFont("/DejaVuSansMono.ttf").deriveFont(14f);
			loadFont("/DejaVuSansMono-Bold.ttf");

			fontsLoaded = true;
		} catch (Exception e) {
			throw new IllegalStateException("Couldn't load fonts", e);
		}
	}

	private static Font loadFont(String resourceName) throws java.io.IOException, FontFormatException {
		Font font = Font.createFont(
				Font.TRUETYPE_FONT,
				FontManager.class.getResourceAsStream(resourceName)
		);
		GraphicsEnvironment.getLocalGraphicsEnvironment()
			.registerFont(font);
		return font;
	}

	public static Font getDefaultFont() {
		loadCustomFonts();
		return defaultFont;
	}

	public static Font getButtonFont() {
		return getDefaultFont().deriveFont(20f);
	}

	public static Font getTitleFont() {
		return getDefaultFont().deriveFont(Font.BOLD, 20);
	}

	public static Font getMathFont() {
		loadCustomFonts();
		return mathFont;
	}

	public static String getMathFontFamily() {
		return getMathFont().getFamily();
	}
}
