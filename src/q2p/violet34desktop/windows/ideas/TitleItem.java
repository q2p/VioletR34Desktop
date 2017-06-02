package q2p.violet34desktop.windows.ideas;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.Renderer;

class TitleItem {
	final int number;
	final short[][] numberFont;
	final short[] title;
	final boolean filteredInside;
	
	TitleItem(final int number, final String title, final boolean filteredInside) {
		this.number = number;
		numberFont = Ideas.toNumber(number);
		
		this.title = Font.toArray(title);
		
		this.filteredInside = filteredInside;
	}
	
	void render(final int y, final BufferedImage surface, final Graphics graphics, boolean selected) {
		graphics.setColor(selected?Colors.ACCENT:Colors.LOW_CONTRAST);
		graphics.fillRect(0, 0, surface.getWidth(), Renderer.C_ICON_SIZE);
		Renderer.drawLine(title, Ideas.numberRectangleWidth+Renderer.C_MARGIN, Renderer.C_ICON_PADDING, 1, Font.WHITE, graphics);
		
		if(filteredInside) {
			graphics.fillRect(surface.getWidth() - Renderer.C_ICON_SIZE, 0, Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE);
			graphics.drawImage(Renderer.T_MORE, surface.getWidth() - Renderer.C_ICON_SIZE, 0, null);
		}
		
		Ideas.renderNumber(numberFont, graphics, selected, 0, 0);
		
		Browser.frame.graphics().drawImage(surface, Renderer.C_ICON_OFFSET, y, null);
	}
}