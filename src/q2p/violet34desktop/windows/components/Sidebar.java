package q2p.violet34desktop.windows.components;

import java.awt.image.BufferedImage;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.events.FrameAbstraction;

public class Sidebar extends Component {
	private BufferedImage[] buttons = new BufferedImage[0];
	public void setButtons(final BufferedImage ... buttons) {
		this.buttons = buttons;

		if(hovered >= buttons.length)
			hovered = -1;
		if(focused >= buttons.length)
			frame.releaseMouseFocusIfHoldingIt(this);
	}
	
	public Sidebar(final FrameAbstraction frame, final BufferedImage ... buttons) {
		super(0,0,0,0,frame);
		setButtons(buttons);
	}

	private byte hovered = -1;
	private byte focused = -1;
	private byte clicked = -1;
	public byte clicked() {
		return clicked;
	}
	
	public void render() {
		frame.graphics().setColor(Colors.HINT);
		frame.graphics().fillRect(0, 0, Renderer.C_ICON_SIZE, frame.height());
		for(byte i = 0; i != buttons.length; i++) {
			if(Assist.focusHover(i, focused, hovered)) {
				frame.graphics().setColor(Colors.ACCENT);
				frame.graphics().fillRect(0, i*Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE);
			}
			frame.graphics().drawImage(buttons[i], 0, i*(Renderer.C_ICON_SIZE), null);
		}
	}
	
	protected void onMouseFocusLoss() {
		focused = -1;
	}
	
	public void think() {
		hovered = -1;
		if((canGrabMouseFocus() || holdingMouseFocus()) && frame.mouseX() >= 0 && frame.mouseX() < Renderer.C_ICON_SIZE)
			hovered = (byte)Assist.cellPointer(frame.mouseY(), Renderer.C_ICON_SIZE, buttons.length);
		
		
		if(frame.MOUSE_L.pressed() && frame.canGrabMouseFocus() && hovered != -1) {
			focused = hovered;
			frame.grabMouseFocus(this);
		}
		
		clicked = -1;
		if(frame.MOUSE_L.released() && holdingMouseFocus()) {
			if(hovered == focused)
				clicked = focused;
			
			frame.releaseMouseFocus();
		}
	}
}