package q2p.violet34desktop.windows.ideas.containers;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.windows.ideas.IdeaViewer;

public class ImagesContainer extends ContainerWithItems {
	private final ArrayList<String> originalImages = new ArrayList<String>();
	
	public ImagesContainer(final IdeaViewer ideaViewer) {
		super("Изображения", IdeaViewer.T_IMAGE, ideaViewer);
	}

	protected int calculateItemsHeight() {
		return ideaViewer.itemsWidth+Renderer.C_ICON_SIZE;
	}

	public void beginEditing() {
		// TODO Auto-generated method stub
	}

	public void load(final DataInputStream dis) throws IOException {
		originalImages.clear();
		
		Assist.readStrings(dis, originalImages);
		
		restore();
	}
	
	public void restore() {
		for(final Item item : items)
			item.dispose();
		items.clear();

		for(final String s : originalImages)
			items.add(new ImageItem(this, s));

		allocated = !items.isEmpty();
		
		if(ideaViewer.isInEditingMode()) {
			// TODO:
		} else {
			// TODO:
		}
	}

	
	protected final Item getEmptyItem() {
		return new ImageItem(this, null);
	}
}