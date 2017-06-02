package q2p.violet34desktop;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

public class Font {
	private static final String VALID = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя +-=~%@$&.,:;!`?()<>[]{}\'\"/\\_|*#^";
	public static final byte FONT_HEIGHT;
	public static final byte[] FONTS_WIDTH = new byte[VALID.length()+1];
	public static final int SPACE = VALID.indexOf(" ");
	
	public static final byte BLACK = 0;
	public static final byte WHITE = 1;
	public static final byte VIOLET = 2;
	
	private static final int[] COLORS = new int[] {
		Colors.BLACK.getRGB(),
		Colors.WHITE.getRGB(),
		Colors.ACCENT.getRGB()
	};
	public static final BufferedImage[][] FONTS = new BufferedImage[COLORS.length][VALID.length()+1];
	
	static {
		BufferedImage img = Assist.loadImage("ascii.png", Transparency.OPAQUE);
		
		FONT_HEIGHT = (byte)img.getHeight();
		
		final int RED = new Color(255, 0, 0).getRGB();
		final int WHITE = new Color(255, 255, 255).getRGB();
		int x1 = 0;
		int x2 = 0;
		int i = 0;
		while(i != FONTS_WIDTH.length) {
			while(img.getRGB(x2, 0) != RED)
				x2++;
			
			FONTS_WIDTH[i] = (byte)(x2-x1);
			for(int c = 0; c != COLORS.length; c++) {
				FONTS[c][i] = Assist.getOptimizedImage(FONTS_WIDTH[i], FONT_HEIGHT, Transparency.BITMASK);
				for(int y = FONT_HEIGHT-1; y != -1; y--) {
					for(int x = FONTS_WIDTH[i]-1; x != -1; x--) {
						FONTS[c][i].setRGB(x, y, (img.getRGB(x1+x, y) == WHITE)?COLORS[c]:new Color(0,0,0,0).getRGB());
					}
				}
			}
			x1 = ++x2;
			i++;
		}
	}

	public static final short[] toArray(final String string) {
		final short[] ret = new short[string.length()];
		short idx;
		for(int i = string.length()-1; i != -1; i--) {
			idx = (short)VALID.indexOf(string.charAt(i));
			if(idx == -1)
				idx = (short)(VALID.length());
			ret[i] = idx;
		}
		return ret;
	}

	public static int longestLineSize(final short[][] lines) {
		int max = 0;
		for(int i = 0; i != lines.length; i++)
			max = Math.max(sizeLine(lines[i], 1), max);
		return max;
	}
	public static int longestLineSizeWithSpace(final short[][] lines) {
		int ret = 0;
		for(int i = lines.length-1; i != -1; i--)
			ret = Math.max(sizeWithSpace(lines[i]), ret);

		return ret;
	}
	
	public static int sizeLine(final short[] line, final int scale) {
		return sizeInterval(line, 0, line.length, scale);
	}
	public static int sizeWithSpace(final short[] line) {
		return sizeIntervalWithSpace(line, 0, line.length);
	}

	public static int sizeInterval(final short[] line, final int from, final int to, final int scale) {
		int ret = 0;
		for(int i = from; i != to; i++)
			ret += FONTS_WIDTH[line[i]] + 1;
		
		return (ret == 0)?0:(ret-1)*scale;
	}
	public static int sizeIntervalWithSpace(final short[] line, final int from, final int to) {
		int ret = 0;
		for(int i = from; i != to; i++)
			ret += FONTS_WIDTH[line[i]] + 1;

		return ret;
	}
	
	public static int curorAtPositionWithSpace(final short[] line, int position) {
		int f;
		for(int i = 0; i != line.length; i++) {
			f = FONTS_WIDTH[line[i]]+1;
			position -= f/2;
			if(position <= 0)
				return i;
			position -= f-f/2;
		}
		
		return line.length;
	}
	
	public static boolean isValid(final String string) {
		for(int i = string.length()-1; i != -1; i--)
			if(VALID.indexOf(string.charAt(i)) == -1)
				return false;
		return true;
	}
	
	public static String fixIfInvalid(final String string) {
		if(!isValid(string)) {
			String ret = "";

			for(int i = 0; i != string.length(); i++)
				if(VALID.indexOf(string.charAt(i)) != -1)
					ret += string.charAt(i);
			
			return ret;
		}
		return string;
	}

	public static String fixNewLine(final String string) {
		if(string.contains("\n"))
			return string.replaceAll("\n", "");
		return string;
	}
}