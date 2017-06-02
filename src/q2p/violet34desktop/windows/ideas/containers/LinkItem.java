package q2p.violet34desktop.windows.ideas.containers;

import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.windows.components.WaveTimer;

class LinkItem extends Item {
	private final String path;
	
	private final WaveTimer timer = new WaveTimer(1, 1);
	private final String name;
	private final short[] nameFont;
	
	LinkItem(final LinksContainer container, final String path) {
		super(container, Renderer.C_ICON_SIZE);
		
		this.path = path;
		name = path.substring(Math.max(path.indexOf("/"), 0));
		nameFont = Font.toArray(name);
		timer.setLength(Font.sizeLine(nameFont, 1));
	}
	
	void think() {
		timer.setWidth(container.ideaViewer.itemsWidth);
		timer.tick(); // TODO: IF SELECTED
	}

	void render() {
		frame.graphics().setColor(Colors.HINT);
		frame.graphics().fillRect(0, 0, container.ideaViewer.itemsWidth, Renderer.C_ICON_SIZE);
		Renderer.drawLine(nameFont, Renderer.C_MARGIN+timer.getOffset(), Renderer.C_ICON_PADDING, 1, Font.WHITE, frame.graphics());
	}

	void onDrop() {
		// TODO Auto-generated method stub
		
	}
}