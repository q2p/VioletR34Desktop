package q2p.violet34desktop.windows.components;

import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.events.FrameAbstraction;

public final class ScrollablePage extends Component {
	private int viewportOffset = 0;
	public final void setSizes(final int width, final int height) {
		super.setSizes(width, height);
		checkNeedScrollBar();
	}
	public int getViewportOffset() {
		return viewportOffset;
	}
	public final void setViewportOffset(final int offset) {
		viewportOffset = offset;
		validateOffset();
	}
	public final void scrollTo(final int position) {
		frame.releaseMouseFocusIfHoldingIt(this);
		setViewportOffset(position);
	}
	
	private int pageWidth;
	public final int getPageWidth() {
		return pageWidth;
	}
	private int pageHeight;
	public final int getPageHeight() {
		return pageHeight;
	}
	public final void setPageHeight(final int height) {
		pageHeight = height;
		checkNeedScrollBar();
	}

	private int pageScrollableSpace;
	private boolean mouseInViewPort = false;
	public final boolean isMouseInsideViewPortArea() {
		return mouseInViewPort;
	}
	
	private boolean needScrollBar;
	private boolean mouseOverScrollBar;
	
	public static final byte SCROLL_BAR_WIDTH = 16;
	public static final byte SCROLL_BAR_OFFSET = Renderer.C_MARGIN + SCROLL_BAR_WIDTH;
	private int scrollHeight = 1;
	private int scrollOffset = 0;
	private int scrollFreeSpace;
	
	private static final short MIN_SCROLL_SCALE = 128;

	public ScrollablePage(final int x, final int y, final int width, final int height, final int pageHeight, final FrameAbstraction frame) {
		super(x, y, width, height, frame);
		setPageHeight(pageHeight);
	}
	
	private final void checkNeedScrollBar() {
		needScrollBar = pageHeight > height;
		if(needScrollBar) {
			pageWidth = width - SCROLL_BAR_OFFSET;
			pageScrollableSpace = pageHeight - height;
			scrollHeight = Math.max(SCROLL_BAR_WIDTH, height * height / pageHeight);
			scrollFreeSpace = height - scrollHeight;
		} else {
			pageWidth = width;
		}
		validateOffset();
	}
		
	public final void think() {
		if(needScrollBar) {
			if(!frame.MOUSE_L.holded())
				frame.releaseMouseFocusIfHoldingIt(this);

			viewportOffset += frame.wheel() * MIN_SCROLL_SCALE;
			
			if(frame.PAGE_UP.ticked())
				viewportOffset -= height-MIN_SCROLL_SCALE;
			if(frame.PAGE_DOWN.ticked())
				viewportOffset += height-MIN_SCROLL_SCALE;
				mouseOverScrollBar = frame.mouseInside(x+width-SCROLL_BAR_WIDTH, y, SCROLL_BAR_WIDTH, height);
			
			if(frame.canGrabMouseFocus() && frame.MOUSE_L.pressed() && mouseOverScrollBar)
				frame.grabMouseFocus(this);
			
			if(holdingMouseFocus()) {
				mouseOverScrollBar = true;
				viewportOffset = pageScrollableSpace * (frame.mouseY()-y-scrollHeight/2) / scrollFreeSpace;
			}
				
			if(!holdingMouseFocus() && !frame.canGrabMouseFocus())
				mouseOverScrollBar = false;
		}
		
		validateOffset();
		
		mouseInViewPort = frame.mouseX() != -1 && frame.mouseX() >= x && frame.mouseX() < x+pageWidth && frame.mouseY() >= y && frame.mouseY() < y+height;
	}
	
	private final void validateOffset() {
		if(viewportOffset > pageHeight-height)
			viewportOffset = pageHeight-height;
		if(viewportOffset < 0)
			viewportOffset = 0;
		
		if(needScrollBar)
			scrollOffset = scrollFreeSpace * viewportOffset / pageScrollableSpace;
	}
	
	public final void render() {
		if(!needScrollBar)
			return;

		frame.graphics().setColor(mouseOverScrollBar?Colors.LOW_CONTRAST:Colors.HINT);
		frame.graphics().fillRect(x+width-SCROLL_BAR_WIDTH, y, SCROLL_BAR_WIDTH, height);
		frame.graphics().setColor(mouseOverScrollBar?Colors.ACCENT:Colors.LOW_CONTRAST);
		frame.graphics().fillRect(x+width-SCROLL_BAR_WIDTH, y+scrollOffset, SCROLL_BAR_WIDTH, scrollHeight);
	}

	public final int getScrollMouseY() {
		return frame().mouseY()-y+viewportOffset;
	}
}