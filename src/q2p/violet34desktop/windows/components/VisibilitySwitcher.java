package q2p.violet34desktop.windows.components;

public final class VisibilitySwitcher {
	private boolean[] switcher;
	
	public VisibilitySwitcher(final int amountOfSwitches, final boolean visibility) {
		setButtons(amountOfSwitches, visibility);
	}
	
	public final void setButtons(final int amountOfSwitches, final boolean visibility) {
		switcher = new boolean[amountOfSwitches];
		setVisibilityToAll(visibility);
	}
	
	public final void setVisibilityToAll(final boolean visibility) {
		for(int i = switcher.length - 1; i != -1; i--)
			switcher[i] = visibility;
	}
	
	public final void setVisible(final int id, final boolean visibility) {
		switcher[id] = visibility;
	}
	
	public final boolean isVisible(final int id) {
		return switcher[id];
	}
	
	public final int[] getVisibleButtons() {
		int visible = 0;
		for(int i = switcher.length-1; i != -1; i--)
			if(switcher[i])
				visible++;
		
		final int[] ids = new int[visible];
		visible = 0;
		for(int i = 0; i != switcher.length; i++)
			if(switcher[i])
				ids[visible++] = i;
			
		return ids;
	}
	
	public final int getRealId(final int visibleId) {
		int vid = 0;
		for(int i = 0; i != switcher.length; i++) {
			if(switcher[i]) {
				if(vid == visibleId)
					return i;
				vid++;
			}
		}
		throw new IndexOutOfBoundsException();
	}
	public final int getVisibleId(final int realId) {
		int vid = 0;
		for(int i = 0; i != switcher.length; i++) {
			if(i == realId)
				return vid;
			if(switcher[i])
				vid++;
		}
		throw new IndexOutOfBoundsException();
	}
}