package q2p.violet34desktop.windows.updates;

import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.SeparatedFontText;
import q2p.violet34desktop.windows.components.exceptionscreens.ExceptionScreen;

public class UnknownTypeExceptionScreen extends ExceptionScreen {
	// TODO: фокусировка на ошибках
	private final SeparatedFontText fontDescrition;

	private static final short[] fontSetToUnknown = Font.toArray("Выставить на всех тип \""+Updates.UNKNOWN_TYPE.name+"\"");
	
	private static boolean selectedApply;
	
	private static final short OFFSET = 64;
	private int width;
	private int height;
	
	public UnknownTypeExceptionScreen() {
		super("Для некоторых ресурсов не удалось загрузить типы, так-как они не были найдены в списке.", Updates.frame, ExceptionScreen.getTitle(Updates.WINDOW_TITLE));
		
		fontDescrition = new SeparatedFontText(description, frame.width()-2*OFFSET);
		fontDescrition.setMaxWidth(width = Font.longestLineSizeWithSpace(fontDescrition.fontRender));

		height = fontDescrition.getHeight() + Renderer.C_ICON_OFFSET;
	}

	public boolean think() {
		if(frame.isClosing())
			Updates.closeWindow();
		
		if(frame.wasResized()) {
			fontDescrition.setMaxWidth(frame.width()-2*OFFSET);
			fontDescrition.setMaxWidth(width = Font.longestLineSizeWithSpace(fontDescrition.fontRender));

			height = fontDescrition.getHeight() + Renderer.C_ICON_OFFSET;
		}
				
		selectedApply = frame.mouseInside((frame.width()-width)/2, (frame.height()-height)/2+height-Renderer.C_ICON_SIZE, width, Renderer.C_ICON_SIZE);
		
		if(frame.MOUSE_L.released() && selectedApply)
			return true;
		
		return false;
	}

	public void render() {
		fontDescrition.render((frame.width() - width)/2, (frame.height() - height)/2, Font.WHITE, frame.graphics());
				
		frame.graphics().setColor(selectedApply?Colors.ACCENT:Colors.HIGH_CONTRAST);
		frame.graphics().fillRect((frame.width() - width)/2, (frame.height() - height)/2+height-Renderer.C_ICON_SIZE, width,Renderer.C_ICON_SIZE);
		Renderer.drawLine(fontSetToUnknown, (frame.width() - Font.sizeLine(fontSetToUnknown, 1))/2, (frame.height() - height)/2+height-Renderer.C_ICON_SIZE+Renderer.C_ICON_PADDING, 1, Font.WHITE, frame.graphics());
	}
}