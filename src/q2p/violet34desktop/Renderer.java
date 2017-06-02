package q2p.violet34desktop;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

public class Renderer {
	// Текстуры
	public static final BufferedImage T_RETURN = Assist.loadImage("return.png", Transparency.BITMASK);
	public static final BufferedImage T_SAVE = Assist.loadImage("save.png", Transparency.BITMASK);
	public static final BufferedImage T_EDIT = Assist.loadImage("edit.png", Transparency.BITMASK);
	public static final BufferedImage T_ADD = Assist.loadImage("add.png", Transparency.BITMASK);
	public static final BufferedImage T_SETTINGS = Assist.loadImage("settings.png", Transparency.BITMASK);
	public static final BufferedImage T_RESTORE = Assist.loadImage("restore.png", Transparency.BITMASK);
	public static final BufferedImage T_DELTETE = Assist.loadImage("delete.png", Transparency.BITMASK);
	public static final BufferedImage T_MORE = Assist.loadImage("more.png", Transparency.BITMASK);
	public static final BufferedImage T_TO_POSTS = Assist.loadImage("toPosts.png", Transparency.BITMASK);
	public static final BufferedImage T_OPEN = Assist.loadImage("open.png", Transparency.BITMASK);
	public static final BufferedImage T_MOVE = Assist.loadImage("move.png", Transparency.BITMASK);
	public static final BufferedImage T_DESCRIPTOR = Assist.loadImage("descriptor.png", Transparency.BITMASK);
	
	// Стандартные значения
	public static final byte C_MARGIN = 4;
	public static final byte C_ICON_SIZE = 24;
	public static final byte C_ICON_OFFSET = C_ICON_SIZE + C_MARGIN;
	public static final byte C_ICON_PADDING = (byte)((C_ICON_SIZE - Font.FONT_HEIGHT)/2);
	
	public static void drawLine(final short[] line, int x, final int y, final int scale, final byte color, final Graphics graphics) {
		for(final short character : line) {
			graphics.drawImage(Font.FONTS[color][character], x, y, Font.FONTS_WIDTH[character]*scale, Font.FONT_HEIGHT*scale, null);
			x += (Font.FONTS_WIDTH[character]+1)*scale;
		}
	}

	public static void drawLines(final short[][] lines, final int x, int y, final int scale, final byte color, final Graphics graphics) {
		for(int i = 0; i != lines.length; i++) {
			drawLine(lines[i], x, y, scale, color, graphics);
			y += Font.FONT_HEIGHT*scale;
		}
	}
}