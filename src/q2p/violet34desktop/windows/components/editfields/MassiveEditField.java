package q2p.violet34desktop.windows.components.editfields;

import java.awt.Color;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.SeparatedFontText;
import q2p.violet34desktop.events.FrameAbstraction;

public final class MassiveEditField extends EditField {
	private final SeparatedFontText fontText;
	public int lastXpos;
	public int[] cursorPosition;
	public int[][] selectPosition;
	public boolean needToUpdateSelectionProjection = true;
	
	public final void setWidth(final int width) {
		fontText.setMaxWidth(width);

		this.width = width;
		
		updateHeight();
	}
	
	private final void updateHeight() {	
		setSizes(width, fontText.getHeight());
		
		projectCursor();
		needToUpdateSelectionProjection = true;
	}

	protected final void onTextChange(final boolean calledManualy) {
		frame.releaseMouseFocusIfHoldingIt(this);
		
		lastXpos = -1;
		
		if(!calledManualy && fontText != null) {
			fontText.setString(text);
			updateHeight();
		}
	}
		
	private final void projectCursor() {
		fontText.positionCursor(cursor, cursorPosition);
	}

	protected final void validateSelection() {
		super.validateSelection();
		needToUpdateSelectionProjection = true;
	}
	
	public MassiveEditField(final int x, final int y, final int width, final Color background, final String text, final Validator validator, final FrameAbstraction frame, final boolean editable) {
		super(x, y, width, 1, background, text, validator, frame, editable);
		fontText = new SeparatedFontText(text, width);
		cursorPosition = new int[2];
		selectPosition = new int[2][2];
		setWidth(width);
	}

	protected final void thinkMove(final boolean control, final boolean shift) {
		// TODO: когда курсор уходит за пределы, опускать скроллбар
		if(frame.HOME.pressed()) {
			if(selectCursor == -1) {
				if(shift)
					selectCursor = cursor;
			} else {
				if(!shift)
					selectCursor = -1;
			}

			if(control)
				cursor = 0;
			else
				cursor = fontText.leftCornerCursor(cursor);
			
			validateSelection();
			
			lastXpos = -1;
		}
		
		if(frame.END.pressed()) {
			if(selectCursor == -1) {
				if(shift)
					selectCursor = cursor;
			} else {
				if(!shift)
					selectCursor = -1;
			}

			if(control)
				cursor = text.length();
			else
				cursor = fontText.rightCornerCursor(cursor);
			
			validateSelection();
			
			lastXpos = -1;
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
			
			lastXpos = -1;
			
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
			
			lastXpos = -1;
			
			validateSelection();
		}

		projectCursor();
		if(frame.UP.ticked()) {
			if(selectCursor == -1) {
				if(shift)
					selectCursor = cursor;
			} else {
				if(!shift)
					selectCursor = -1;
			}
			
			if(lastXpos == -1) {
				lastXpos = Font.sizeIntervalWithSpace(fontText.fontRender[cursorPosition[1]], 0, cursorPosition[0]);
			}
			
			if(cursorPosition[1] > 0) {
				cursor = fontText.getCursorAt(lastXpos, cursorPosition[1]-1);
			}
			
			validateSelection();
			projectCursor();
		}

		if(frame.DOWN.ticked()) {
			if(selectCursor == -1) {
				if(shift)
					selectCursor = cursor;
			} else {
				if(!shift)
					selectCursor = -1;
			}
			
			if(lastXpos == -1) {
				lastXpos = Font.sizeIntervalWithSpace(fontText.fontRender[cursorPosition[1]], 0, cursorPosition[0]);
			}
			
			if(cursorPosition[1] < fontText.fontRender.length-1) {
				cursor = fontText.getCursorAt(lastXpos, cursorPosition[1]+1);
			}
			
			validateSelection();
		}
		
		if(control && frame.A.pressed()) {
			cursor = text.length();
			selectCursor = cursor==0?-1:0;
			validateSelection();
			lastXpos = -1;
		}
	}
		
	public final void think() {
		wasChanged = false;

		final boolean control = frame.CTRL.holded();
		final boolean shift = frame.SHIFT.holded();
		
		if(holdingKeyboardFocus()) {
			thinkMove(control, shift);
			thinkEdit(control, shift);
			thinkHistory(control);
		}

		validateSelection();
		
		if(wasChanged) {
			fontText.setString(text);
			updateHeight();
		}
		
		thinkMoveMouse(control, shift);
		
		validateSelection();
	}
	
	protected final void thinkMoveMouse(final boolean control, final boolean shift) {
		if(!holdingMouseFocus()) {
			if(frame.MOUSE_L.pressed() && isMouseInside()) {
				if(frame.canGrabMouseFocus()) {
					frame.grabKeyboardFocus(this);
					frame.grabMouseFocus(this);
					selectCursor = cursor = getCursorAt(getMouseDX(), getMouseDY());
					lastXpos = -1;
					validateSelection();
				}
			}
		} else {
			if(frame.MOUSE_L.holded()) {
				cursor = getCursorAt(getMouseDX(), getMouseDY());
				
				lastXpos = -1;
				
				validateSelection();
			} else
				frame.releaseMouseFocus();
		}
	}
	
	private int getCursorAt(final int x, final int y) {
		return fontText.getCursorAt(x, y/Font.FONT_HEIGHT);
	}

	public final void render() {
		// TODO: FIXME: иногда здесь выкидывается Exception при переходе в Font.(...) Нужно отловить его. UPD: вроде получилось исправить его костылями
		frame.graphics().setColor(background);
		frame.graphics().fillRect(x, y, width, height);
		
		// TODO: не рендерить выделение за пределами экрана
		
		if(selectCursor != -1) {
			if(needToUpdateSelectionProjection) {
				needToUpdateSelectionProjection = false;
				fontText.positionCursor(selectBegin, selectPosition[0]);
				fontText.positionCursor(selectEnd, selectPosition[1]);
			}
			
			frame.graphics().setColor(Colors.ACCENT);
			if(selectPosition[0][1] == selectPosition[1][1]) {
				frame.graphics().fillRect(
						x+Font.sizeIntervalWithSpace(fontText.fontRender[selectPosition[0][1]], 0, selectPosition[0][0]),
						y+Font.FONT_HEIGHT*selectPosition[0][1],
						Font.sizeIntervalWithSpace(fontText.fontRender[selectPosition[0][1]], selectPosition[0][0], selectPosition[1][0]),
						Font.FONT_HEIGHT);
			} else {
				frame.graphics().fillRect(
						x+Font.sizeIntervalWithSpace(fontText.fontRender[selectPosition[0][1]], 0, selectPosition[0][0]),
						y+Font.FONT_HEIGHT*selectPosition[0][1],
						Font.sizeIntervalWithSpace(fontText.fontRender[selectPosition[0][1]], selectPosition[0][0], fontText.fontRender[selectPosition[0][1]].length),
						Font.FONT_HEIGHT);
				frame.graphics().fillRect(
						x,
						y+Font.FONT_HEIGHT*selectPosition[1][1],
						Font.sizeIntervalWithSpace(fontText.fontRender[selectPosition[1][1]], 0, selectPosition[1][0]),
						Font.FONT_HEIGHT);
			}
			for(int i = selectPosition[0][1]+1; i < selectPosition[1][1]; i++) {
				frame.graphics().fillRect(
						x,
						y+Font.FONT_HEIGHT*i,
						Font.sizeWithSpace(fontText.fontRender[i]),
						Font.FONT_HEIGHT);
			}
		}
		
		fontText.render(this, Font.WHITE, frame.graphics());
		
		if(holdingKeyboardFocus()) {
			projectCursor();
			frame.graphics().setColor(Colors.WHITE);
			frame.graphics().fillRect(
					x+Font.sizeIntervalWithSpace(fontText.fontRender[cursorPosition[1]], 0, cursorPosition[0]),
					y+Font.FONT_HEIGHT*cursorPosition[1],
					1,
					Font.FONT_HEIGHT);
		}
	}

	public void onKeyboardFocusLoss() {
		selectCursor = -1;
		validateSelection();
	}
	public void onKeyboardFocusGrab() {}
}