package q2p.violet34desktop.windows.ideas.containers;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.windows.ideas.IdeaViewer;

public final class LinksContainer extends ContainerWithItems {
	private final ArrayList<String> originalLinks = new ArrayList<String>();
	
	public LinksContainer(final IdeaViewer ideaViewer) {
		super("Ссылки", IdeaViewer.T_LINK, ideaViewer);
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
		originalLinks.clear();
		
		Assist.readStrings(dis, originalLinks);
		
		restore();
	}

	public void restore() {
		for(final Item item : items)
			item.dispose();
		items.clear();
		
		for(final String s : originalLinks)
			items.add(new LinkItem(this, s));

		allocated = !items.isEmpty();
		
		if(ideaViewer.isInEditingMode()) {
			// TODO Auto-generated method stub
		} else {
			// TODO Auto-generated method stub
		}
	}

	
	protected final Item getEmptyItem() {
		return new LinkItem(this, null);
	}
}