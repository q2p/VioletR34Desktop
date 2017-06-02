package q2p.violet34desktop.windows.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.events.FrameAbstraction;

public final class ButtonsBlock extends Component {
	private final VisibilitySwitcher visibility;
	private final BufferedImage[] buttons;
	private final Color background;
	private int hovered = -1;
	private int focused = -1;
	private int clicked = -1;
	public final int clicked() {
		return clicked;
	}
	public final int focused() {
		return focused;
	}
		
	public ButtonsBlock(final FrameAbstraction frame, final int x, final int y, final Color background, final BufferedImage ... buttons) {
		super(x, y, 0, Renderer.C_ICON_SIZE, frame);
		this.buttons = buttons;
		this.background = background;
		visibility = new VisibilitySwitcher(buttons.length, false);
	}
	
	public final void think() {
		clicked = -1;
		
		checkHover();
		
		if(hovered != -1 && frame.MOUSE_L.pressed() && canGrabMouseFocus()) {
			focused = visibility.getRealId(hovered);
			frame.grabMouseFocus(this);
		}
		if(focused != -1 && !frame.MOUSE_L.holded()) {
			if(focused == visibility.getRealId(hovered))
				clicked = focused;
			focused = -1;
			frame.releaseMouseFocusIfHoldingIt(this);
		}
	}
	
	public final void render() {
		final Graphics g = frame.graphics();

		final int[] visible = visibility.getVisibleButtons();
		
		g.setColor(background);
		g.fillRect(x, y, visible.length*Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE);
		for(int i = 0; i != visible.length; i++) {
			if(Assist.focusHover(i, focused, hovered)) {
				g.setColor(Colors.ACCENT);
				g.fillRect(x+i*Renderer.C_ICON_SIZE, y, Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE);
			}
			g.drawImage(buttons[visible[i]], x + i*Renderer.C_ICON_SIZE, y, null);
		}
	}
	
	public final void setVisibility(final int id, final boolean visibility) {
		focused = -1;
		frame.releaseMouseFocusIfHoldingIt(this);
		checkHover();
		this.visibility.setVisible(id, visibility);
		setSizes(this.visibility.getVisibleButtons().length * Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE);
	}

	public final void setPosition(final int x, final int y) {
		super.setPosition(x, y);
		
		checkHover();
	}
	
	public final void checkHover() {
		hovered = -1;
		if(visibility != null && (canGrabMouseFocus() || focused != -1) && frame.mouseInside(x, y, visibility.getVisibleButtons().length*Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE))
			hovered = (frame.mouseX() - x)/Renderer.C_ICON_SIZE;
	}
	
	public final boolean isVisible(final int id) {
		return visibility.isVisible(id);
	}
	
	public final int getMaxWidth() {
		return Renderer.C_ICON_SIZE*buttons.length;
	}
}