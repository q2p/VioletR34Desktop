package q2p.violet34desktop.windows.components.exceptionscreens;

import q2p.violet34desktop.events.FrameAbstraction;

public abstract class ExceptionScreen {
	protected final String description;
	protected final FrameAbstraction frame;
	
	public ExceptionScreen(final String description, final FrameAbstraction frame, final String newTitle) {
		this.description = description;
		this.frame = frame;
		frame.setTitle(newTitle);
	}
	
	public static final String getTitle(final String windowTitle) {
		return windowTitle+" - Ошибка";
	}
}