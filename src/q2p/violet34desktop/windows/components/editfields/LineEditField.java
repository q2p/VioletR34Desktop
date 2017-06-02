package q2p.violet34desktop.windows.components.editfields;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.events.FrameAbstraction;

public class LineEditField extends EditField {
	private BufferedImage surface;
	private Graphics graphics;
	
	protected final void onTextChange(final boolean calledManualy) {
		frame.releaseMouseFocusIfHoldingIt(this);
		if(!calledManualy)
			fontText = Font.toArray(text);
	}
	
	public void setSizes(final int width, final int height) {
		super.setSizes(width, height);
		surface = Assist.getOptimizedImage(width, height, Transparency.OPAQUE);
		graphics = surface.getGraphics();
		topPadding = (height-Font.FONT_HEIGHT)/2;
	}
	
	private int offset;

	private short[] fontText;
		
	private int topPadding;

	private static final long JUMP_DELAY = 50;
	private long lastJump = 0;
	
	public LineEditField(final int x, final int y, final int width, final int height, final Color background, final String text, final Validator validator, final FrameAbstraction frame, final boolean editable) {
		super(x, y, width, height, background, text, validator, frame, editable);
		fontText = Font.toArray(text);
	}

	protected void thinkMove(final boolean control, final boolean shift) {
		if(frame.HOME.pressed()) {
			if(selectCursor == -1) {
				if(shift)
					selectCursor = cursor;
			} else {
				if(!shift)
					selectCursor = -1;
			}
			cursor = 0;
			validateSelection();
		}
		
		if(frame.END.pressed()) {
			if(selectCursor == -1) {
				if(shift)
					selectCursor = cursor;
			} else {
				if(!shift)
					selectCursor = -1;
			}
			cursor = text.length();
			validateSelection();
		}
		
		if(frame.LEFT.ticked()) {
			if(selectCursor == -1) {
				if(shift)
					selectCursor = cursor;
			} else {
				if(!shift)
					selectCursor = -1;
			}
			
			if(control)
				cursor = getJumpPositionLeft();
			else
				cursor = validatePosition(cursor-1);
			
			validateSelection();
		}
		
		if(frame.RIGHT.ticked()) {
			if(selectCursor == -1) {
				if(shift)
					selectCursor = cursor;
			} else {
				if(!shift)
					selectCursor = -1;
			}
			
			if(control)
				cursor = getJumpPositionRight();
			else
				cursor = validatePosition(cursor+1);
			
			validateSelection();
		}
		
		if(control && frame.A.pressed()) {
			cursor = text.length();
			selectCursor = cursor==0?-1:0;
			validateSelection();
		}
	}
	
	public void think() {
		wasChanged = false;
		
		final boolean control = frame.CTRL.holded();
		final boolean shift = frame.SHIFT.holded();
		
		if(holdingKeyboardFocus()) {
			thinkMove(control, shift);
			thinkEdit(control, shift);
			thinkHistory(control);
		}
		
		if(wasChanged)
			fontText = Font.toArray(text);
		
		validateSelection();
		checkOffset();

		thinkMoveMouse(control, shift);
		
		validateSelection();
		checkOffset();
	}
	
	protected final void thinkMoveMouse(final boolean control, final boolean shift) {
		int ps = Assist.limit(-1, getMouseDX()-Renderer.C_MARGIN, width-2*Renderer.C_MARGIN);
		final int dpos = (ps < 0 ? -1 : (ps > width-2*Renderer.C_MARGIN-1 ? + 1 : 0));
		ps = Font.curorAtPositionWithSpace(fontText, ps+offset);
		final int nps = Assist.limit(0, ps+dpos, fontText.length);
		
		if(!holdingMouseFocus()) {
			if(frame.MOUSE_L.pressed() && isMouseInside() && dpos == 0) {
				if(frame.canGrabMouseFocus()) {
					frame.grabKeyboardFocus(this);
					frame.grabMouseFocus(this);
					selectCursor = cursor = ps;
					validateSelection();
					lastJump = System.currentTimeMillis();
				}
			}
		} else {
			if(frame.MOUSE_L.holded()) {
				cursor = ps;
				if(dpos != 0 && System.currentTimeMillis() - lastJump >= JUMP_DELAY) {
					cursor = nps;
					lastJump = System.currentTimeMillis();
				}
				validateSelection();
			} else
				frame.releaseMouseFocus();
		}
	}
		
	private void checkOffset() {
		final int position = Font.sizeIntervalWithSpace(fontText, 0, cursor)-1;
		final int dPosition = position - offset;
		if(dPosition < 0)
			offset = position - 0;
		else if(dPosition >= width - 2*Renderer.C_MARGIN)
			offset = position - (width - 2*Renderer.C_MARGIN-1);
		
		final int lineSize = Font.sizeWithSpace(fontText);
		
		if(offset + width - 2*Renderer.C_MARGIN > lineSize)
			offset = lineSize - (width - 2*Renderer.C_MARGIN);
		
		if(offset < 0)
			offset = 0;
	}
	
	public void render() {
		graphics.setColor(background);
		graphics.fillRect(0, 0, width, height);
		if(selectCursor != -1) {
			graphics.setColor(Colors.ACCENT);
			graphics.fillRect(Renderer.C_MARGIN+Font.sizeIntervalWithSpace(fontText, 0, selectBegin)-offset, 0, Font.sizeIntervalWithSpace(fontText, selectBegin, selectEnd), height);
		}
		// TODO: optimize: не рендерить куски находящиеся за пределами экрана
		Renderer.drawLine(fontText, Renderer.C_MARGIN-offset, topPadding, 1, Font.WHITE, graphics);
		if(holdingKeyboardFocus()) {
			graphics.setColor(Colors.WHITE);
			// TODO:
			graphics.fillRect(Renderer.C_MARGIN+Font.sizeIntervalWithSpace(fontText, 0, cursor)-offset, 0, 1, height);
		}
		
		frame.graphics().drawImage(surface, x, y, null);
	}
	
	public final void onKeyboardFocusGrab() {}
	public final void onKeyboardFocusLoss() {
		selectCursor = -1;
		validateSelection();
	}
}