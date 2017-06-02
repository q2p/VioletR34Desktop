package q2p.violet34desktop.windows.ideas.containers;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.windows.ideas.IdeaViewer;

public final class TextContainer extends ContainerWithItems {
	private final ArrayList<String> originalItems = new ArrayList<String>();
	
	public TextContainer(final String title, final BufferedImage button, final IdeaViewer ideaViewer) {
		super(title, button, ideaViewer);
	}

	protected final int calculateItemsHeight() {
		return Renderer.C_ICON_SIZE;
	}

	public final void beginEditing() {
		// TODO Auto-generated method stub
		
	}
	
	public final void onButtonPress() {
		// TODO Auto-generated method stub
		
	}

	public void load(final DataInputStream dis) throws IOException {
		originalItems.clear();
		
		Assist.readStrings(dis, originalItems);
		
		restore();
	}

	public void restore() {
		for(final Item item : items)
			item.dispose();
		items.clear();
		
		for(final String s : originalItems)
			items.add(new TextItem(this, s));

		allocated = !items.isEmpty();
		
		if(ideaViewer.isInEditingMode()) {
			// TODO Auto-generated method stub
		} else {
			// TODO Auto-generated method stub
		}
	}

	
	protected final Item getEmptyItem() {
		return new TextItem(this, null);
	}
}