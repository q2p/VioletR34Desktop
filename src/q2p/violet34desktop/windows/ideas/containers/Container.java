package q2p.violet34desktop.windows.ideas.containers;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.events.FrameAbstraction;
import q2p.violet34desktop.windows.ideas.IdeaViewer;

public abstract class Container {
	protected static final int TITLE_OFFSET = Font.FONT_HEIGHT+Renderer.C_MARGIN;
	protected final short[] title;
	
	protected final IdeaViewer ideaViewer;
	protected final FrameAbstraction frame;
	protected int positionY;
	protected int height;
	
	protected boolean allocated = false;

	protected Container(final String title, final BufferedImage button, final IdeaViewer ideaViewer) {
		this.title = Font.toArray(title);
		this.ideaViewer = ideaViewer;
		this.frame = ideaViewer.frame;
		this.button = button;
	}
	
	protected final BufferedImage button;
	public abstract BufferedImage getButton();
	
	public abstract int calculateSizes(final int width, final int y);
	public abstract int updateSizes(final int width, final int y);

	public abstract void render();
	
	public abstract void think();
	
	public void dispose() {}

	public abstract void beginEditing();

	public abstract boolean changedButton();
	public abstract void onButtonPress();
	
	protected boolean wasResized;
	public final boolean wasResized() {
		return wasResized;
	}

	public abstract void load(final DataInputStream dis) throws IOException;

	public abstract int position(final int y);

	public abstract void restore();
}