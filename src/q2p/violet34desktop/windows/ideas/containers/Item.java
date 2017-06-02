package q2p.violet34desktop.windows.ideas.containers;

import q2p.violet34desktop.windows.components.Component;
import q2p.violet34desktop.windows.ideas.IdeaViewer;

public abstract class Item extends Component {
	protected final ContainerWithItems container;
	// TODO: перемещение
	
	protected Item(final ContainerWithItems container, final int height) {
		super(0,0,IdeaViewer.MIN_ITEMS_WIDTH,height,container.frame);
		this.container = container;
	}
	
	abstract void render();
	abstract void think();
}