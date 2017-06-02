package q2p.violet34desktop.windows.components.editfields;

public class TextFieldFrame {
	public static final long IDLE_TIME = 600;
	public static final long TYPING_TIME = 10000;
	public static final short MAX_HISTORY_SAVES = 128;
	
	public final String text;
	public final int cursor;
	
	public TextFieldFrame(final String text, final int cursor) {
		this.text = text;
		this.cursor = cursor;
	}
}
