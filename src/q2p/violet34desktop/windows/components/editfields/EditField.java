package q2p.violet34desktop.windows.components.editfields;

import java.awt.Color;
import java.util.ArrayList;
import q2p.violet34desktop.events.FrameAbstraction;
import q2p.violet34desktop.lowlevel.OpSys;
import q2p.violet34desktop.windows.components.Component;

public abstract class EditField extends Component {
	protected Color background;
	public final void setBackground(final Color backgroud) {
		this.background = backgroud;
	}
	
	protected String text = "";
	public final String getText() {
		return text;
	}
	public final void setText(final String text) {
		putText(0, this.text.length(), text, false);
		cursor = this.text.length();
		selectCursor = -1;
		validateSelection();
	}
	private final void putText(final int begin, final int end, final String text, boolean calledManualy) {
		final TextFieldFrame efh = validator.validate(this.text.substring(0, begin), text, this.text.substring(end), calledManualy);
		this.text = efh.text;
		cursor = efh.cursor;
		selectCursor = -1;
		validateSelection();
		wasChanged = true;
		wasEditedSinceHistorySave = true;
		historyIdleStart = System.currentTimeMillis();
		onTextChange(calledManualy);
	}
	abstract void onTextChange(final boolean calledManualy);
	
	private Validator validator;
	public final void setValidator(final Validator validator) {
		this.validator = validator;
	}
		
	private final ArrayList<TextFieldFrame> history = new ArrayList<TextFieldFrame>();
	private long historyIdleStart;
	private long historyLastSave;
	private boolean wasEditedSinceHistorySave;
	private int historyCursor;
	public final void clearHistory() {
		history.clear();
		historyCursor = -1;
		saveHistory();
	}
	private final void saveHistory() {
		historyIdleStart = -1;
		historyLastSave = System.currentTimeMillis();
		wasEditedSinceHistorySave = false;
		
		while(history.size() != historyCursor+1)
			history.remove(historyCursor+1);
		
		history.add(new TextFieldFrame(text, cursor));
		historyCursor++;
	}
	public final void forceHistorySave() {
		if(wasEditedSinceHistorySave)
			saveHistory();
	}
	protected final void thinkHistory(final boolean control) {
		if(control) {
			if(frame.Y.ticked() && historyCursor != history.size()-1 && editable) {
				if(wasEditedSinceHistorySave)
					saveHistory();
				
				historyCursor++;
				TextFieldFrame h = history.get(historyCursor);
				
				text = h.text;
				
				cursor = h.cursor;
									
				selectCursor = -1;
				validateSelection();
				
				wasChanged = true;
			} else if(frame.Z.ticked() && historyCursor != 0 && editable) {
				if(wasEditedSinceHistorySave)
					saveHistory();
				
				historyCursor--;
				TextFieldFrame h = history.get(historyCursor);
				
				text = h.text;
				
				cursor = h.cursor;
									
				selectCursor = -1;
				validateSelection();
				
				wasChanged = true;
			}
		}
		if(wasEditedSinceHistorySave && ((historyIdleStart != -1 && System.currentTimeMillis() - historyIdleStart >= TextFieldFrame.IDLE_TIME) || System.currentTimeMillis() - historyLastSave >= TextFieldFrame.TYPING_TIME)) {
			saveHistory();
		}
		while(history.size() > TextFieldFrame.MAX_HISTORY_SAVES) {
			history.remove(0);
			historyCursor--;
		}
		if(wasChanged)
			onTextChange(true);
	}
	
	protected int cursor = 0;
	protected final int getJumpPositionLeft() {
		int idx = indexOfWhiteSpace(0);
		
		if(idx == -1 || idx >= cursor)
			return 0;
		
		int nidx;
		while((nidx = indexOfWhiteSpace(idx+1)) != -1 && nidx < cursor)
			idx = nidx;
		
		while(idx != 0 && whiteSpaceAt(idx-1))
			idx--;
		
		return idx;
	}
	protected final int getJumpPositionRight() {
		int idx = indexOfWhiteSpace(cursor);
		
		if(idx == -1)
			return text.length();
		
		idx++;
		while(idx != text.length() && whiteSpaceAt(idx))
			idx++;
		
		return idx;
	}
	private final boolean whiteSpaceAt(final int index) {
		return text.charAt(index) == ' ' || text.charAt(index) == '\n';
	}
	private final int indexOfWhiteSpace(final int fromIndex) {
		int space = text.indexOf(' ', fromIndex);
		int newLine = text.indexOf('\n', fromIndex);
		if(space == -1)
			return newLine;
		
		if(space < newLine)
			return space;
		
		return newLine;
	}
	
	protected final int validatePosition(final int cursor) {
		if(cursor > text.length()) return text.length();
		if(cursor < 0) return 0;
		return cursor;
	}
		
	protected int selectCursor = -1;
	protected int selectBegin = -1;
	protected int selectEnd = -1;
	protected void validateSelection() {
		if(!holdingKeyboardFocus())
			selectCursor = -1;
		
		if(selectCursor == -1)
			selectBegin = selectEnd = -1;
		else {
			selectBegin = Math.min(selectCursor, cursor);
			selectEnd = Math.max(selectCursor, cursor);
		}
	}
	
	protected boolean wasChanged = false;
	public final boolean wasChanged() {
		return wasChanged;
	}
		
	public final boolean holdingKeyboardFocus() {
		return frame.holdingKeyboardFocus(this);
	}
	public EditField dispose() {
		super.dispose();
		if(frame != null) {
			frame.releaseKeyboardFocusIfHoldingIt(this);
			frame = null;
		}
		return null;
	}
	public static final EditField safeDispose(final EditField editField) {
		if(editField != null)
			editField.dispose();
		return null;
	}
	public abstract void onKeyboardFocusLoss();
	public abstract void onKeyboardFocusGrab();
	public void grabKeyboardFocus() {
		frame.grabKeyboardFocus(this);
	}
	
	private boolean editable;
	public final void setEditable(final boolean editable) {
		this.editable = editable;
	}
	public final boolean isEditable() {
		return editable;
	}
	
	public EditField(final int x, final int y, final int width, final int height, final Color background, final String text, final Validator validator, final FrameAbstraction frame, final boolean editable) {
		super(x, y, width, height, frame);
		setSizes(width, height);
		setBackground(background);
		setValidator(validator);
		setText(text);
		clearHistory();
		setEditable(editable);
	}

	protected final void thinkEdit(final boolean control, final boolean shift) {
		if(control && frame.C.pressed()) {
			if(selectCursor == -1)
				OpSys.copyToClipboard("");
			else
				OpSys.copyToClipboard(text.substring(selectBegin, selectEnd));
		}
		
		if(control && frame.V.ticked() && editable) {
			final String flavor = OpSys.getFromClipBoard();
			if(selectCursor == -1)
				putText(cursor, cursor, flavor, true);
			else
				putText(selectBegin, selectEnd, flavor, true);
		}
		
		if(control && frame.X.pressed()) {
			if(selectCursor == -1)
				OpSys.copyToClipboard("");
			else {
				OpSys.copyToClipboard(text.substring(selectBegin, selectEnd));
				if(editable)
					putText(selectBegin, selectEnd, "", true);
			}
		}
		
		if(frame.input().length() != 0 && editable) {
			if(selectCursor == -1)
				putText(cursor, cursor, frame.input(), true);
			else
				putText(selectBegin, selectEnd, frame.input(), true);
		}
		
		if(frame.BACKSPACE.ticked() && editable) {
			if(selectCursor == -1) {
				selectCursor = cursor;
				if(control)
					cursor = getJumpPositionLeft();
				else
					cursor = validatePosition(cursor-1);
			
				validateSelection();
			}
			putText(selectBegin, selectEnd, "", true);
		}
		if(frame.DELETE.ticked() && editable) {
			if(selectCursor == -1) {
				if(control)
					selectCursor = getJumpPositionRight();
				else
					selectCursor = validatePosition(cursor+1);
				validateSelection();
			}
			putText(selectBegin, selectEnd, "", true);
		}
	}
		
	protected abstract void thinkMove(final boolean control, final boolean shift);
	protected abstract void thinkMoveMouse(final boolean control, final boolean shift);
	
	public abstract void think();
	public abstract void render();
}