package q2p.violet34desktop.windows.main;

import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.events.FrameAbstraction;
import q2p.violet34desktop.lowlevel.VioletR34;
import q2p.violet34desktop.windows.Windows;
import q2p.violet34desktop.windows.ideas.Browser;
import q2p.violet34desktop.windows.updates.Updates;

public class Main {
	private static final byte BUTTON_SCALE = 2;

	private static FrameAbstraction frame = null;
	private static final Object LOCK = new Object();
	private static boolean needToPopup = false;

	private static byte focused = -1;
	private static byte selected;
	private static final byte UPDATES = 0;
	private static final byte IDEAS = 1;
	private static final short[][] buttons = new short[][] {
		Font.toArray(Updates.WINDOW_TITLE),
		Font.toArray(Browser.WINDOW_TITLE)
	};

	private final static int WIDTH;
	private final static int HEIGHT = Renderer.C_MARGIN+buttons.length*(BUTTON_SCALE*Font.FONT_HEIGHT+Renderer.C_MARGIN);
	
	static {
		int max = 0;
		int s;
		for(byte i = (byte)(buttons.length-1); i != -1; i--) {
			s = Font.sizeLine(buttons[i], BUTTON_SCALE);
			if(s > max)
				max = s;
		}
		WIDTH = 2*Renderer.C_MARGIN+max;
	}
	
	public static void think() {
		synchronized(LOCK) {
			if(needToPopup) {
				needToPopup = false;
				if(frame == null)
					frame = new FrameAbstraction(WIDTH, HEIGHT, false, VioletR34.TITLE);
				else 
					frame.requestFocus();
				needToPopup = false;
				focused = -1;
			}
		}
		
		if(frame == null)
			return;
		
		frame.iterateNewActions();
		
		if(frame.isClosing()) {
			frame.destroy();
			frame = null;
			// TODO: заглушка
			Windows.quiting();
			return;
		}
		
		selected = (byte)Assist.cellPointer(frame.mouseY() - Renderer.C_MARGIN, BUTTON_SCALE*Font.FONT_HEIGHT, buttons.length, Renderer.C_MARGIN);
		
		if(frame.MOUSE_L.pressed())
			focused = selected;
		if(!frame.MOUSE_L.holded()) {
			if(focused == selected) {
				switch (focused) {
				case UPDATES:
					Updates.openWindow();
					break;
				case IDEAS:
					Browser.openWindow();
					break;
				}
			}
			focused = -1;
		}
	}

	public static void render() {
		if(frame == null)
			return;
		
		for(byte i = 0; i != buttons.length; i++)
			Renderer.drawLine(buttons[i], Renderer.C_MARGIN, Renderer.C_MARGIN+i*(BUTTON_SCALE*Font.FONT_HEIGHT+Renderer.C_MARGIN), BUTTON_SCALE,
				Assist.focusHover(i, focused, selected) ? Font.VIOLET : Font.WHITE,
				frame.graphics());
		
		frame.postActions();
	}

	public static void openWindow() {
		synchronized(LOCK) {
			needToPopup = true;
		}
	}
}