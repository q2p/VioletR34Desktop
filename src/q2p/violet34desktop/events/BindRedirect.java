package q2p.violet34desktop.events;

public class BindRedirect {
	// States
	static final byte FREE = 0;
	static final byte HOLD = 1;
	static final byte PRESSED = 2;
	static final byte TICKED = 3;
	static final byte RELEASED = 4;
	
	public final int VIRTUAL;
	byte state = FREE;
	final boolean IS_MOUSE;
	
	BindRedirect(final int virtual, final boolean isMouse) {
		this.VIRTUAL = virtual;
		this.IS_MOUSE = isMouse;
	}

	public boolean pressed() {
		return state == PRESSED;
	}
	
	public boolean released() {
		return state == RELEASED;
	}
	
	public boolean holded() {
		return (state == HOLD ||
				state == PRESSED ||
				state == TICKED);
	}
	
	public boolean ticked() {
		return (state == PRESSED ||
				state == TICKED);
	}
}