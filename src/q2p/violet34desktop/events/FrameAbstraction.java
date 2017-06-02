package q2p.violet34desktop.events;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.windows.components.editfields.EditField;

public class FrameAbstraction {
	private Frame frame;
	private BufferedImage surface;
	private Graphics graphics;
	
	public Graphics graphics() {
		return graphics;
	}
	
	private int width = -1;
	private int height = -1;
	public int width() {
		return width;
	}
	public int height() {
		return height;
	}
	
	private boolean wasResized;
	public boolean wasResized() {
		return wasResized;
	}
	
	public void destroy() {
		frame.setVisible(false);
		frame.dispose();
		releaseMouseFocus();
		releaseKeyboardFocus();
		frames.remove(this);
	}

	private static final LinkedList<FrameAbstraction> frames = new LinkedList<FrameAbstraction>();
	
	public static final void destroyAllFrames() {
		while(!frames.isEmpty())
			frames.getFirst().destroy();
	}
	
	public void setTitle(final String title) {
		frame.setTitle(title);
	}
	
	private void render() {
		frame.panel.getGraphics().drawImage(surface, 0, 0, null);
	}
	private void initSizes() {
		wasResized = false;
		
		int cWidth = frame.panel.getWidth();
		int cHeight = frame.panel.getHeight();
		
		if(cWidth != width || cHeight != height) {
			wasResized = true;
			width = cWidth;
			height = cHeight;
			surface = Assist.getOptimizedImage(width, height, Transparency.OPAQUE);
			graphics = surface.getGraphics();
		}
		graphics.clearRect(0, 0, width, height);
	}
	
	// При нажатии на крест
	private boolean closing = false;
	public boolean isClosing() {
		return closing;
	}
	// Клавиши:
	public final BindRedirect ESCAPE	=  new BindRedirect(KeyEvent.VK_ESCAPE, false);
	public final BindRedirect ENTER		=  new BindRedirect(KeyEvent.VK_ENTER, false);
	public final BindRedirect CTRL		=  new BindRedirect(KeyEvent.VK_CONTROL, false);
	public final BindRedirect SHIFT		=  new BindRedirect(KeyEvent.VK_SHIFT, false);
	public final BindRedirect BACKSPACE	=  new BindRedirect(KeyEvent.VK_BACK_SPACE, false);
	public final BindRedirect DELETE	=  new BindRedirect(KeyEvent.VK_DELETE, false);
	public final BindRedirect LEFT		=  new BindRedirect(KeyEvent.VK_LEFT, false);
	public final BindRedirect RIGHT		=  new BindRedirect(KeyEvent.VK_RIGHT, false);
	public final BindRedirect UP		=  new BindRedirect(KeyEvent.VK_UP, false);
	public final BindRedirect DOWN		=  new BindRedirect(KeyEvent.VK_DOWN, false);
	public final BindRedirect HOME		=  new BindRedirect(KeyEvent.VK_HOME, false);
	public final BindRedirect END		=  new BindRedirect(KeyEvent.VK_END, false);
	public final BindRedirect PAGE_UP	=  new BindRedirect(KeyEvent.VK_PAGE_UP, false);
	public final BindRedirect PAGE_DOWN	=  new BindRedirect(KeyEvent.VK_PAGE_DOWN, false);

	public final BindRedirect A			=  new BindRedirect(KeyEvent.VK_A, false);
	public final BindRedirect C			=  new BindRedirect(KeyEvent.VK_C, false);
	public final BindRedirect V			=  new BindRedirect(KeyEvent.VK_V, false);
	public final BindRedirect X			=  new BindRedirect(KeyEvent.VK_X, false);
	public final BindRedirect Z			=  new BindRedirect(KeyEvent.VK_Z, false);
	public final BindRedirect Y			=  new BindRedirect(KeyEvent.VK_Y, false);
	// Мышь
	public final BindRedirect MOUSE_L = new BindRedirect(MouseEvent.BUTTON1, true);
	public final BindRedirect MOUSE_R = new BindRedirect(MouseEvent.BUTTON2, true);
	private int mouseX = -1;
	public int mouseX() {
		return mouseX;
	}
	private int mouseY = -1;
	public int mouseY() {
		return mouseY;
	}
	public final boolean mouseInside(final int x, final int y, final int width, final int height) {
		return mouseX >= x && mouseY >= y && mouseX < x+width && mouseY < y+height;
	}
	private int wheel = 0;
	public int wheel() {
		return wheel;
	}

	private final BindRedirect[] bindRedirects = {
		ESCAPE,
		ENTER,
		CTRL,
		SHIFT,
		BACKSPACE,
		DELETE,
		LEFT,
		RIGHT,
		UP,
		DOWN,
		HOME,
		END,
		PAGE_UP,
		PAGE_DOWN,
		
		A,
		C,
		V,
		X,
		Z,
		Y,
		
		MOUSE_L,
		MOUSE_R
	};

	private String input;
	public String input() {
		return input;
	}

	public final LinkedList<String> files = new LinkedList<String>();
	
	private boolean inputClosing = false;
	
	private final LinkedList<int[]> actions = new LinkedList<int[]>();
	private final LinkedList<char[]> inputCodes = new LinkedList<char[]>();
	private final int[] mouseCoords = new int[2];
	private int inputWheel = 0;
	private final LinkedList<String> inputFiles = new LinkedList<String>();
	
	public void iterateNewActions() {
		initSizes();
		synchronized (actions) {
			closing = inputClosing;
			inputClosing = false;
			
			wheel = inputWheel;
			inputWheel = 0;
			
			mouseX = mouseCoords[0];
			mouseY = mouseCoords[1];

			if(mouseX != -1) {
				if(mouseX < 0) mouseX = 0;
				if(mouseY < 0) mouseY = 0;
				if(mouseX >= width) mouseX = width-1;
				if(mouseY >= height) mouseY = height-1;
			}
			
			input = "";
			while(!inputCodes.isEmpty())
				input += inputCodes.removeFirst()[0];
			
			int code;
			int state;
			boolean isMouse;
			
			while(!actions.isEmpty()) {
				code = actions.getFirst()[0];
				state = actions.getFirst()[1];
				isMouse = actions.removeFirst()[2]!=0;

				for(byte i = 0; i != bindRedirects.length; i++) {
					if(bindRedirects[i].IS_MOUSE == isMouse && bindRedirects[i].VIRTUAL == code) {
						
						if(state == 1) // Нажата
							bindRedirects[i].state = 
									(bindRedirects[i].state == BindRedirect.FREE || bindRedirects[i].state == BindRedirect.RELEASED)?
										BindRedirect.PRESSED:
										BindRedirect.TICKED;
						else // Отпущена
							if(bindRedirects[i].state != BindRedirect.FREE)
								bindRedirects[i].state = BindRedirect.RELEASED;
						
						break;
					}
				}
			}
			
			if(ENTER.ticked())
				input += "\n";
			
			files.clear();
			files.addAll(inputFiles);
			inputFiles.clear();
		}
	}
	
	public void postActions() {
		for(byte i = 0; i != bindRedirects.length; i++) {
			if(bindRedirects[i].state == BindRedirect.PRESSED || bindRedirects[i].state == BindRedirect.TICKED)
				bindRedirects[i].state = BindRedirect.HOLD;
			if(bindRedirects[i].state == BindRedirect.RELEASED)
				bindRedirects[i].state = BindRedirect.FREE;
		}
		closing = false;
		render();
	}

	void push(final int code, final boolean isMouse) {
		synchronized (actions) {
			actions.addLast(new int[]{code, 1, isMouse?1:0});
		}
	}
	
	void pull(final int code, final boolean isMouse) {
		synchronized (actions) {
			actions.addLast(new int[]{code, 0, isMouse?1:0});
		}
	}
	
	void type(final char ch) {
		synchronized (actions) {
			inputCodes.addLast(new char[]{ch});
		}
	}

	void close() {
		synchronized(actions) {
			inputClosing = true;
		}
	}

	void mouseMoved(final int x, final int y) {
		synchronized (actions) {
			mouseCoords[0] = x;
			mouseCoords[1] = y;
		}
	}

	void mouseExited() {
		synchronized (actions) {
			mouseCoords[0] = -1;
			mouseCoords[1] = -1;
		}
	}
	
	void wheel(int wheel) {
		synchronized (actions) {
			inputWheel += wheel;
		}
	}
	
	void putFiles(final LinkedList<File> files) {
		synchronized (actions) {
			inputFiles.addLast(files.remove(0).getAbsolutePath().substring(Assist.MAIN_FOLDER.length()));
		}
	}

	public FrameAbstraction(final int minWidth, final int minHeight, final boolean resizable, final String title) {	
		frame = new Frame(this, minWidth, minHeight, resizable, title);
		frames.add(this);
		iterateNewActions();
	}
	public void requestFocus() {
		frame.requestFocus();
	}

	// FocusHandler
	private Focusable mouseFocusPointer = null;
	
	public final void grabMouseFocus(final Focusable focusable) {
		releaseMouseFocus();
		
		mouseFocusPointer = focusable;
		mouseFocusPointer.onMouseFocusGrab();
	}
	
	public final void releaseMouseFocus() {
		if(mouseFocusPointer != null) {
			mouseFocusPointer.onMouseFocusLoss();
			mouseFocusPointer = null;
		}
	}
	
	public final void releaseMouseFocusIfHoldingIt(final Focusable focusable) {
		if(holdingMouseFocus(focusable))
			releaseMouseFocus();
	}

	public final boolean canGrabMouseFocus() {
		return mouseFocusPointer == null;
	}
	
	public final boolean holdingMouseFocus(final Focusable focusable) {
		if(focusable == null)
			return false;
		return mouseFocusPointer == focusable;
	}

	// KeyboardFocusHandler
	private EditField keyboardFocusHandler = null;
	
	public final void grabKeyboardFocus(final EditField editField) {
		releaseKeyboardFocus();
		
		keyboardFocusHandler = editField;
		keyboardFocusHandler.onKeyboardFocusGrab();
	}
	
	public final void releaseKeyboardFocus() {
		if(keyboardFocusHandler != null) {
			keyboardFocusHandler.onKeyboardFocusLoss();
			keyboardFocusHandler = null;
		}
	}
	
	public final void releaseKeyboardFocusIfHoldingIt(final EditField editField) {
		if(holdingKeyboardFocus(editField))
			releaseKeyboardFocus();
	}

	public final boolean canGrabKeyboardFocus() {
		return keyboardFocusHandler == null;
	}
	
	public final boolean holdingKeyboardFocus(final EditField editField) {
		if(editField == null)
			return false;
		return keyboardFocusHandler == editField;
	}
}