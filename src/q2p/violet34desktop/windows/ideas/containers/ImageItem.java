package q2p.violet34desktop.windows.ideas.containers;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.events.Focusable;
import q2p.violet34desktop.lowlevel.OpSys;
import q2p.violet34desktop.windows.async.AsynchronusImageLoader;
import q2p.violet34desktop.windows.async.ImageRequester;
import q2p.violet34desktop.windows.components.ButtonsBlock;
import q2p.violet34desktop.windows.components.SquareLoading;
import q2p.violet34desktop.windows.components.editfields.DefaultValidator;
import q2p.violet34desktop.windows.components.editfields.LineEditField;
import q2p.violet34desktop.windows.ideas.IdeaViewer;

final class ImageItem extends Item {
	private String path;
	// TODO: не позволять сохранять изменения, если изображения находятся в режиме редактирования
	private BufferedImage image = null;
	private boolean isGif = false;
	// open, copy, edit, remove, save, move in order, open in descriptor
	private ImageRequester requester = null;
	private static final BufferedImage NOT_FOUND = Assist.loadImage("notFound.png", Transparency.BITMASK);
	private static final BufferedImage FAILED_TO_LOAD = Assist.loadImage("failedToLoad.png", Transparency.BITMASK);
	private static final BufferedImage IS_GIF = Assist.loadImage("isGif.png", Transparency.BITMASK);
	private final LineEditField field;
	
	private static enum State {
		VIEW, ADDING, EDITING, DELETING;
	}
	private State state;

	private static final byte B_OPEN = 0;
	private static final byte B_OPEN_IN_EXPLORER = 1;
	private static final byte B_OPEN_IN_DESCRIPTOR = 2;
	private static final byte B_MOVE = 3;
	private static final byte B_CHANGE = 4;
	private static final byte B_ADD = 5;
	private static final byte B_SAVE = 6;
	private static final byte B_CANCEL = 7;
	private static final byte B_DELETE = 8;
	private ButtonsBlock buttonsBlock = new ButtonsBlock(frame, 0, 0, Colors.HINT,
		Renderer.T_OPEN, Renderer.T_ADD, Renderer.T_DESCRIPTOR, Renderer.T_MOVE, Renderer.T_EDIT, Renderer.T_ADD, Renderer.T_SAVE, Renderer.T_RETURN, Renderer.T_DELTETE
	);
	
	ImageItem(final ImagesContainer container, String path) {
		super(container, IdeaViewer.MIN_ITEMS_WIDTH);
		
		if(path == null) {
			this.path = null;
			path = "";
			state = State.ADDING;
		} else {
			this.path = path;
			requester = AsynchronusImageLoader.request(Assist.MAIN_FOLDER+path, IdeaViewer.MAX_ITEMS_WIDTH);
			state = State.VIEW;
		}
		field = new LineEditField(0, 0, IdeaViewer.MIN_ITEMS_WIDTH, Renderer.C_ICON_SIZE, Colors.HINT, path, DefaultValidator.BLOCK_NEWLINE, container.ideaViewer.frame, container.ideaViewer.isInEditingMode());
		
		checkButtonsBlock();
		checkField();
		
		checkIfLoaded();
	}

	private void checkButtonsBlock() {
		switch(state) {
			case VIEW:
				checkViewButton();
				buttonsBlock.setVisibility(B_OPEN_IN_DESCRIPTOR, true);
				buttonsBlock.setVisibility(B_MOVE, true);
				buttonsBlock.setVisibility(B_CHANGE, true);
				buttonsBlock.setVisibility(B_ADD, false);
				buttonsBlock.setVisibility(B_SAVE, false);
				buttonsBlock.setVisibility(B_CANCEL, false);
				buttonsBlock.setVisibility(B_DELETE, true);
				break;
			case ADDING:
				checkAddSaveButton();
				buttonsBlock.setVisibility(B_OPEN, false);
				buttonsBlock.setVisibility(B_OPEN_IN_EXPLORER, false);
				buttonsBlock.setVisibility(B_OPEN_IN_DESCRIPTOR, false);
				buttonsBlock.setVisibility(B_MOVE, true);
				buttonsBlock.setVisibility(B_CHANGE, false);
				buttonsBlock.setVisibility(B_ADD, true);
				buttonsBlock.setVisibility(B_SAVE, false);
				buttonsBlock.setVisibility(B_CANCEL, false);
				buttonsBlock.setVisibility(B_DELETE, true);
				break;
			case EDITING:
				checkAddSaveButton();
				buttonsBlock.setVisibility(B_OPEN, false);
				buttonsBlock.setVisibility(B_OPEN_IN_EXPLORER, false);
				buttonsBlock.setVisibility(B_OPEN_IN_DESCRIPTOR, false);
				buttonsBlock.setVisibility(B_MOVE, true);
				buttonsBlock.setVisibility(B_CHANGE, false);
				buttonsBlock.setVisibility(B_ADD, false);
				buttonsBlock.setVisibility(B_SAVE, true);
				buttonsBlock.setVisibility(B_CANCEL, true);
				buttonsBlock.setVisibility(B_DELETE, false);
				break;
			case DELETING:
				buttonsBlock.setVisibility(B_OPEN, false);
				buttonsBlock.setVisibility(B_OPEN_IN_EXPLORER, false);
				buttonsBlock.setVisibility(B_OPEN_IN_DESCRIPTOR, false);
				buttonsBlock.setVisibility(B_MOVE, true);
				buttonsBlock.setVisibility(B_CHANGE, false);
				buttonsBlock.setVisibility(B_ADD, false);
				buttonsBlock.setVisibility(B_SAVE, false);
				buttonsBlock.setVisibility(B_CANCEL, true);
				buttonsBlock.setVisibility(B_DELETE, true);
				break;
		}
	}
	
	final void render() {
		Graphics graphics = frame.graphics();
		
		graphics.setColor(Colors.HINT);
		graphics.fillRect(x, y, x+container.ideaViewer.itemsWidth, y+container.itemsHeight);
		
		buttonsBlock.render();
		// VIEW, ADDING, EDITING, DELETING
		if(state != State.EDITING)
			field.render();
				
		if(image == null) {
			if(state == State.VIEW || state == State.DELETING)
				SquareLoading.render(x+container.ideaViewer.itemsWidth/2, y+container.ideaViewer.itemsWidth/2, container.ideaViewer.itemsWidth, graphics);
		} else {
			if(state == State.VIEW || state == State.DELETING) {
				final int[] newSizes = new int[2];
				Assist.getScaledSizes(image.getWidth(), image.getHeight(), container.itemsHeight, newSizes);
				
				graphics.drawImage(image, x+(container.itemsHeight-newSizes[0])/2, y+Renderer.C_ICON_SIZE+(container.itemsHeight-newSizes[1])/2, x+(container.itemsHeight-newSizes[0])/2 + newSizes[0], y+(container.itemsHeight-newSizes[1])/2 + newSizes[1], 0, 0, image.getWidth(), image.getHeight(), null);
				
				if(isGif)
					graphics.drawImage(IS_GIF, x, y+Renderer.C_ICON_SIZE, null);
			} else {
				graphics.setColor(Colors.HIGH_CONTRAST);
				graphics.fillRect(x, y+Renderer.C_ICON_SIZE, container.ideaViewer.itemsWidth, container.ideaViewer.itemsWidth);
			}
		}
	}

	void think() {
		checkIfLoaded();
		
		thinkButtonBlock();
	}
	
	private final void checkViewButton() {
		if(state == State.VIEW) {
			if(OpSys.canShowInExplorer && frame.CTRL.holded()) {
				buttonsBlock.setVisibility(B_OPEN, false);
				buttonsBlock.setVisibility(B_OPEN_IN_EXPLORER, true);
			} else {
				buttonsBlock.setVisibility(B_OPEN, true);
				buttonsBlock.setVisibility(B_OPEN_IN_EXPLORER, false);
			}
		}
	}
	
	private final void checkField() {
		switch(state) {
			case EDITING:
			case ADDING:
				field.setPosition(x, y+Renderer.C_ICON_SIZE + (container.ideaViewer.itemsWidth-Renderer.C_ICON_SIZE)/2);
				field.setSizes(container.ideaViewer.itemsWidth, Renderer.C_ICON_SIZE);
				field.setEditable(true);
				break;
			case VIEW:
				int blockWidth = buttonsBlock.getWidth();
				field.setPosition(x+blockWidth, y);
				field.setSizes(container.ideaViewer.itemsWidth-blockWidth, Renderer.C_ICON_SIZE);
				field.setEditable(false);
				break;
			case DELETING:
				break;
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	private void thinkButtonBlock() {
		checkViewButton();
		buttonsBlock.think();
		final int focused = buttonsBlock.focused();
		if(focused == B_MOVE)
			container.grabbed = this;
		
		final int clicked = container.grabbed==this?-1:buttonsBlock.clicked();
		
		if(!frame.MOUSE_L.holded() && container.grabbed == this)
			container.grabbed = null;
		
		System.out.println(clicked);
		
		switch(clicked) {
			case B_OPEN:
				Assist.openFile(Assist.MAIN_FOLDER+path);
				break;
			case B_OPEN_IN_EXPLORER:
				OpSys.showInExplorer(Assist.MAIN_FOLDER+path);
				break;
			case B_OPEN_IN_DESCRIPTOR:
				// TODO: добавить дескриптор
				break;
			case B_MOVE:
				break;
			case B_CHANGE:
				setState(State.EDITING);
				break;
			case B_ADD:
			case B_SAVE:
				final String newPath = field.getText();
				if(!newPath.equals(path)) {
					path = newPath;
					requester.dispose();
					requester = AsynchronusImageLoader.request(Assist.MAIN_FOLDER+path, IdeaViewer.MAX_ITEMS_WIDTH);
					image = null;
					checkIfLoaded();
				}
				setState(State.VIEW);
				break;
			case B_CANCEL:
				switch(state) {
					case EDITING:
						setState(State.VIEW);
						field.setText(path);
						break;
					case DELETING:
						setState(State.VIEW);
						break;
				}
				break;
			case B_DELETE:
				switch(state) {
					case VIEW:
						setState(State.DELETING);
						break;
					case ADDING:
						container.removeItemByItSelf(this);
						break;
					case DELETING:
						container.removeItemByItSelf(this);
						break;
				}
				break;
		}
		checkField();
		
		if(state != State.DELETING)
			field.think();
		
		checkAddSaveButton();
	}

	private void checkAddSaveButton() {
		if(state == State.ADDING || state == State.EDITING) {
			if(field.getText().length() == 0) {
				buttonsBlock.setVisibility(B_ADD, false);
				buttonsBlock.setVisibility(B_SAVE, false);
			} else {
				if(state == State.ADDING) {
					buttonsBlock.setVisibility(B_ADD, false);
					buttonsBlock.setVisibility(B_SAVE, false);
				} else {
					buttonsBlock.setVisibility(B_ADD, false);
					buttonsBlock.setVisibility(B_SAVE, true);
				}
			}
		}
	}

	private final void setState(final State state) {
		this.state = state;
		checkButtonsBlock();
		checkField();
	}

	void checkIfLoaded() {
		if(image != null || requester == null)
			return;
		
		switch(requester.status()) {
			case IN_PROGRESS:
				return;
			case FAILED_TO_LOAD:
				image = FAILED_TO_LOAD;
				return;
			case NOT_FOUND:
				image = NOT_FOUND;
				return;
			case LOADED:
				isGif = path.endsWith(".gif");
				image = requester.result();
				// TODO:
				// BufferedImage t = requester.result();
				// image = Assist.getOptimizedImage(t.getWidth(), t.getHeight(), t.getTransparency());
				// image.getGraphics().drawImage(t.getScaledInstance(1, 1, Image.SCALE_SMOOTH), 0, 0, image.getWidth(), t.getHeight(), 0, 0, 1, 1, null);
		}
	}
	
	public final void setPosition(final int x, final int y) {
		super.setPosition(x, y);
		
		if(buttonsBlock != null) {
			buttonsBlock.setPosition(x, y);
			checkField();
		}
	}
	
	public Focusable dispose() {
		if(requester != null)
			requester.dispose();
		buttonsBlock.dispose();
		field.dispose();
		return super.dispose();
	}
}