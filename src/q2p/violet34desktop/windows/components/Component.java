package q2p.violet34desktop.windows.components;

import q2p.violet34desktop.events.Focusable;
import q2p.violet34desktop.events.FrameAbstraction;

public abstract class Component extends Focusable {
	protected int x;
	public int getX() {
		return x;
	}
	protected int y;
	public int getY() {
		return y;
	}
	public void setPosition(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	protected int width;
	public int getWidth() {
		return width;
	}
	protected int height;
	public int getHeight() {
		return height;
	}
	public void setSizes(final int width, final int height) {
		this.width = width;
		this.height = height;
	}

	protected Component(final int x, final int y, final int width, final int height, final FrameAbstraction frame) {
		super(frame);
		setPosition(x, y);
		setSizes(width, height);
	}
	
	protected boolean isMouseInside() {
		return frame().mouseInside(x, y, width, height);
	}
	public int getMouseDX() {
		return frame().mouseX()-x;
	}
	public int getMouseDY() {
		return frame().mouseY()-y;
	}
	public boolean isOnScreen() {
    return
    		(Math.abs(x) < width + frame.width()) &&
    		(Math.abs(y) < height + frame.height());
	}
}