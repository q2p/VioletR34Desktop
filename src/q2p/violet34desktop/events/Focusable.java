package q2p.violet34desktop.events;

public class Focusable {
	protected FrameAbstraction frame;
	public final FrameAbstraction frame() {
		return frame;
	}
	public Focusable(final FrameAbstraction frame) {
		this.frame = frame;
	}
	public final boolean holdingMouseFocus() {
		return frame.holdingMouseFocus(this);
	}
	public final boolean canGrabMouseFocus() {
		return frame.canGrabMouseFocus();
	}
	public Focusable dispose() {
		if(frame != null) {
			frame.releaseMouseFocusIfHoldingIt(this);
			frame = null;
		}
		return null;
	}
	public static final Focusable safeDispose(final Focusable focusable) {
		if(focusable != null)
			focusable.dispose();
		return null;
	}
	protected void onMouseFocusLoss() {}
	protected void onMouseFocusGrab() {}
}