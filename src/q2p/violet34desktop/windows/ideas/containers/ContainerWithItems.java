package q2p.violet34desktop.windows.ideas.containers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.windows.ideas.IdeaViewer;

public abstract class ContainerWithItems extends Container {
	private int rows;
	int itemsHeight;
	protected abstract int calculateItemsHeight();

	protected final ArrayList<Item> items = new ArrayList<Item>();
	protected final LinkedList<Item> removingItems = new LinkedList<Item>();
	
	protected Item grabbed = null;
		
	protected ContainerWithItems(final String title, final BufferedImage button, final IdeaViewer ideaViewer) {
		super(title, button, ideaViewer);
	}
	
	public final int calculateSizes(final int width, final int y) {					
		itemsHeight = calculateItemsHeight();
		
		if(!allocated) {
			rows = 0;
			height = 0;
			return y;
		}
		
		rows = items.size()/ideaViewer.collumns;
		if(items.size()%ideaViewer.collumns != 0)
			rows++;

		height = TITLE_OFFSET + rows*(itemsHeight + Renderer.C_MARGIN);
		
		return y + height;
	}
	public final int updateSizes(final int width, final int y) {
		return y + height;
	}
	public final int position(final int y) {
		positionY = y + Renderer.C_MARGIN;
		
		updatePossitionsOnItems();
		
		positionDrag();
		
		return y + height;
	}
	
	private final void updatePossitionsOnItems() {
		int c = 0;
		int dy = positionY + TITLE_OFFSET;
		for(int i = 0; i != items.size(); i++) {
			items.get(i).setPosition(Renderer.C_ICON_OFFSET+c*(ideaViewer.itemsWidth+Renderer.C_MARGIN), dy);
			if(++c == ideaViewer.collumns) {
				c = 0;
				dy += itemsHeight + Renderer.C_MARGIN;
			}
		}
	}

	public final void render() {
		if(!allocated)
			return;

		Renderer.drawLine(title, Renderer.C_ICON_OFFSET, positionY, 1, Font.WHITE, frame.graphics());
		
		for(final Item item : items) {
			if(item.isOnScreen()) { // TODO: заменить onscreen на более подходящую функцию. И ВООБЩЕ ВЫРЕЗАТЬ ЕЁ К ХУЯМ!!!
				item.render();
			}
		}
	}
	
	public final void think() {
		wasResized = false;
		// TODO: true, если был изменён размер
		
		for(final Item item : items)
			item.think();
		
		thinkRemoving();
		
		thinkDrag();
	}
	
	private void thinkRemoving() {
		if(removingItems.isEmpty())
			return;

		wasResized = true;
		while(!removingItems.isEmpty()) {
			final Item item = removingItems.removeFirst();
			if(grabbed == item)
				grabbed = null;
			item.dispose();
			items.remove(item);
		}
		if(items.isEmpty()) {
			// TODO:
			allocated = false;
		}
	}

	private final void positionDrag() {
		if(grabbed == null)
			return;
		
		final int hoveredId = Assist.gridPointer(frame.mouseX()-Renderer.C_ICON_OFFSET, frame.mouseY()-positionY-TITLE_OFFSET, ideaViewer.itemsWidth, itemsHeight, items.size(), ideaViewer.collumns, Renderer.C_MARGIN);
		if(hoveredId != -1 && items.get(hoveredId) != grabbed) {
			items.remove(grabbed);
			items.add(hoveredId, grabbed);
			updatePossitionsOnItems();
		}
	}
	
	final void thinkDrag() {
		if(grabbed != null)
			positionDrag();
	}
	
	public void dispose() {
		for(final Item item : items)
			item.dispose();
	}

	public final BufferedImage getButton() {
		return button;
	}

	public void beginEditing() {
		// TODO Auto-generated method stub
	}

	public final boolean changedButton() {
		return false;
	}

	public void onButtonPress() {
		items.add(getEmptyItem());
		wasResized = true;
	}

	protected abstract Item getEmptyItem();

	public void removeItemByItSelf(final Item item) {
		wasResized = true;
		removingItems.add(item);
	}
}