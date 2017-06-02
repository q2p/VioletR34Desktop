package q2p.violet34desktop.windows.updates;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.events.Focusable;
import q2p.violet34desktop.windows.components.ScrollablePage;
import q2p.violet34desktop.windows.components.WaveTimer;
import q2p.violet34desktop.windows.components.editfields.DefaultValidator;
import q2p.violet34desktop.windows.components.editfields.LineEditField;

class Browser {
	private static ArrayList<Resource> showedResources;

	private static final byte SB_ADD = 0;
	
	private static LineEditField searchLine;
	private static void filter() {
		final String filter = searchLine.getText().toLowerCase();
		showedResources.clear();
		
		if(filter.equals(""))
			showedResources.addAll(Updates.allResources);
		else
			for(final Resource resource : Updates.allResources)
				if(resource.name.toLowerCase().contains(filter) || resource.address.toLowerCase().contains(filter))
					showedResources.add(resource);
		
		final int height = showedResources.size()*(Renderer.C_ICON_OFFSET);
		page.setPageHeight(height==0?0:height-Renderer.C_MARGIN);
		
		resourceTitleSurface = Assist.getOptimizedImage(page.getPageWidth() - Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE, Transparency.OPAQUE);
		resourceTitleGraphics = resourceTitleSurface.getGraphics();
		
		beginIndex = Math.min((page.getViewportOffset())/(Renderer.C_ICON_OFFSET), showedResources.size());
		endIndex = Math.min((page.getViewportOffset()+page.getHeight()-1)/(Renderer.C_ICON_OFFSET)+1, showedResources.size());
	}

	private static ScrollablePage page;

	static enum ItemParts {
		EDIT, TEXT, TIME
	};
	static ItemParts itemHover = null;
	static ItemParts itemFocused = null;
	
	private static BufferedImage resourceTitleSurface;
	private static Graphics resourceTitleGraphics;

	private static int beginIndex;
	private static int endIndex;
	private static int hoveredItem = -1;
	private static int focusedItem = -1;
	private static WaveTimer selectTimer;
	private static Resource previousSelected;
	
	static void openBrowser() {
		FOCUSABLE_ITEM = new Focusable(Updates.frame);
		Updates.mode = Updates.MODE_BROWSE;
		Updates.sidebar.setButtons(Renderer.T_ADD);
		page.setSizes(Updates.frame.width() - Renderer.C_ICON_SIZE - Renderer.C_MARGIN, Updates.frame.height() - Renderer.C_ICON_SIZE - Renderer.C_MARGIN);
		page.think();
		
		searchLine.setSizes(page.getWidth(), Renderer.C_ICON_SIZE);
		searchLine.grabKeyboardFocus();
		showedResources = new ArrayList<Resource>();
		filter();
		calculateSelection();
	}
	static void closeBrowser() {
		Updates.frame.releaseMouseFocus();
		FOCUSABLE_ITEM = FOCUSABLE_ITEM.dispose();
		
		showedResources = null;
		searchLine.forceHistorySave();
		resourceTitleSurface = null;
		resourceTitleGraphics = null;
		previousSelected = null;
	}
	
	private static Focusable FOCUSABLE_ITEM;
		
	static void think() {
		if(Updates.frame.isClosing()) {
			Updates.closeWindow();
			return;
		}
		
		if(Updates.frame.wasResized()) {
			page.setSizes(Updates.frame.width() - Renderer.C_ICON_SIZE - Renderer.C_MARGIN, Updates.frame.height() - Renderer.C_ICON_SIZE - Renderer.C_MARGIN);
			searchLine.setSizes(page.getWidth(), Renderer.C_ICON_SIZE);
		}
		
		searchLine.think();
		
		if(searchLine.wasChanged())
			filter();
		
		page.think();

		beginIndex = Math.min((page.getViewportOffset())/Renderer.C_ICON_OFFSET, showedResources.size());
		endIndex = Math.min((page.getViewportOffset()+page.getHeight()-1)/Renderer.C_ICON_OFFSET+1, showedResources.size());

		for(int i = beginIndex; i != endIndex; i++)
			showedResources.get(i).calculateTime();

		Updates.sidebar.think();
		
		final byte clicked = Updates.sidebar.clicked();
		switch(clicked) {
			// TODO: optimize: if(Updates.sidebar.clicked() == 0) {...}
		case SB_ADD:
			Editor.editResource(null);
			return;
		}
		
		thinkItems();
	}
	private static final void thinkItems() {
		calculateSelection();
		
		if(hoveredItem != -1) {
			if(showedResources.get(hoveredItem) != previousSelected) {
				selectTimer.reset();
				selectTimer.setLength(Font.sizeLine(showedResources.get(hoveredItem).fontName, 1));
				selectTimer.setWidth(page.getPageWidth() - Renderer.C_ICON_SIZE - 4*Renderer.C_MARGIN - showedResources.get(hoveredItem).timeSize);
			}
			if(itemHover == ItemParts.TEXT && !(FOCUSABLE_ITEM.holdingMouseFocus() && itemFocused != itemHover))
				selectTimer.tick();
			else
				selectTimer.reset();
		}
		previousSelected = hoveredItem == -1 ? null : showedResources.get(hoveredItem);
		
		if(Updates.frame.MOUSE_L.pressed() && hoveredItem != -1) {
			Updates.frame.grabMouseFocus(FOCUSABLE_ITEM);
			focusedItem = hoveredItem;
			itemFocused = itemHover;
		}
		
		if(FOCUSABLE_ITEM.holdingMouseFocus() && Updates.frame.MOUSE_L.released()) {
			Updates.frame.releaseMouseFocus();
			if(itemFocused == itemHover && focusedItem == hoveredItem) {
				switch(itemFocused) {
				case TEXT:
					showedResources.get(focusedItem).copyToClipboard();
					break;
				case TIME:
					update(showedResources.get(focusedItem));
					break;
				case EDIT:
					Editor.editResource(showedResources.get(focusedItem));
					break;
				}
			}
			focusedItem = -1;
			itemFocused = null;
		}
	}
		
	private static final void calculateSelection() {
		hoveredItem = -1;
		itemHover = null;
		if(page.isMouseInsideViewPortArea() && (Updates.frame.canGrabMouseFocus() || Updates.frame.holdingMouseFocus(FOCUSABLE_ITEM)))
			hoveredItem = Assist.cellPointer(page.getScrollMouseY(), Renderer.C_ICON_SIZE, showedResources.size(), Renderer.C_MARGIN);
		
		if(hoveredItem != -1) {
			if(Updates.frame.mouseX() < Renderer.C_ICON_OFFSET + Renderer.C_ICON_SIZE)
				itemHover = ItemParts.EDIT;
			else if(Updates.frame.mouseX() < Renderer.C_ICON_OFFSET + page.getPageWidth() - (2*Renderer.C_MARGIN+showedResources.get(hoveredItem).timeSize))
				itemHover = ItemParts.TEXT;
			else
				itemHover = ItemParts.TIME;
		} else {
			itemHover = null;
		}
	}
	
	private static void update(final Resource resource) {
		resource.lastUpdate = Assist.getGMTmilisec();
		Updates.allResources.remove(resource);
		Updates.allResources.add(resource);
		Updates.save();
		filter();
	}
	
	static void render() {
		if(Updates.frame.wasResized()) {
			resourceTitleSurface = Assist.getOptimizedImage(page.getPageWidth() - Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE, Transparency.OPAQUE);
			resourceTitleGraphics = resourceTitleSurface.getGraphics();
		}
		
		page.render();
		
		for(int i = beginIndex; i != endIndex; i++)
			showedResources.get(i).render(Renderer.C_ICON_OFFSET + i*Renderer.C_ICON_OFFSET-page.getViewportOffset(), page.getPageWidth(), Assist.focusHover(i, focusedItem, hoveredItem), resourceTitleSurface, resourceTitleGraphics, selectTimer);
		
		searchLine.render();

		Updates.frame.graphics().setColor(Colors.BLACK);
		Updates.frame.graphics().fillRect(Renderer.C_ICON_OFFSET, Renderer.C_ICON_SIZE, Updates.frame.width(), Renderer.C_MARGIN);

		Updates.sidebar.render();
	}
	
	static void load() {
		showedResources = new ArrayList<Resource>();
		previousSelected = null;
		
		page = new ScrollablePage(Renderer.C_ICON_OFFSET, Renderer.C_ICON_OFFSET, Updates.frame.width() - Renderer.C_ICON_SIZE - Renderer.C_MARGIN, Updates.frame.height(), 0, Updates.frame);
		searchLine = new LineEditField(Renderer.C_ICON_OFFSET, 0, page.getWidth(), Renderer.C_ICON_SIZE, Colors.HINT, "", DefaultValidator.BLOCK_NEWLINE, Updates.frame, true);
		
		selectTimer = new WaveTimer(1, 1);
	}
	static void unload() {
		previousSelected = null;
		showedResources = null;
		searchLine = null;
		page = null;
		resourceTitleSurface = null;
		resourceTitleGraphics = null;
		selectTimer = null;
		FOCUSABLE_ITEM = Focusable.safeDispose(FOCUSABLE_ITEM);
	}
}