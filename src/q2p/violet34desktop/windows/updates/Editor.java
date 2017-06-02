package q2p.violet34desktop.windows.updates;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.events.Focusable;
import q2p.violet34desktop.windows.components.ScrollablePage;
import q2p.violet34desktop.windows.components.WaveTimer;
import q2p.violet34desktop.windows.components.editfields.DefaultValidator;
import q2p.violet34desktop.windows.components.editfields.LineEditField;

public class Editor {
	private static final byte SB_RETURN = 0;
	private static final byte SB_SAVE = 1;
	private static final byte SB_RESTORE = 2;
	private static final byte SB_DELETE = 3;

	private static Focusable FOCUS_RESOURCE_TYPE_ITEM = null;
		
	private static Resource resource;
	static void editResource(final Resource resource) {
		// TODO: если изменения равны 0, то убирать кнопку сохранения и восстановления
		Browser.closeBrowser();
		Updates.mode = Updates.MODE_EDIT;

		FOCUS_RESOURCE_TYPE_ITEM = new Focusable(Updates.frame);
		
		Editor.resource = resource;
		if(resource == null)
			Updates.sidebar.setButtons(Renderer.T_RETURN, Renderer.T_ADD);
		else
			Updates.sidebar.setButtons(Renderer.T_RETURN, Renderer.T_SAVE, Renderer.T_RESTORE, Renderer.T_DELTETE);

		editTypeBegTimer = new WaveTimer(1, 0);
		editTypeEndTimer = new WaveTimer(1, 0);
		
		addressField = new LineEditField(Renderer.C_ICON_OFFSET, Renderer.C_ICON_OFFSET, Updates.frame.width() -Renderer.C_ICON_OFFSET, Renderer.C_ICON_SIZE, Colors.HINT, resource == null ? "" : resource.address, DefaultValidator.BLOCK_NEWLINE, Updates.frame, true);
		nameField = new LineEditField(Renderer.C_ICON_OFFSET, 3*Renderer.C_ICON_OFFSET, Updates.frame.width()-Renderer.C_ICON_OFFSET, Renderer.C_ICON_SIZE, Colors.HINT, resource == null ? "" : resource.name, UpdatesTitleValidator.VALIDATOR, Updates.frame, true);
		if(resource == null)
			selectType(Updates.types[0]);
		else
			selectType(resource.resourceType);
				
		resourcesPage = new ScrollablePage(Renderer.C_ICON_OFFSET, 5*Renderer.C_ICON_OFFSET, Updates.frame.width() - Renderer.C_ICON_OFFSET, Updates.frame.height()-5*Renderer.C_ICON_OFFSET, Updates.types.length*Renderer.C_ICON_OFFSET-Renderer.C_MARGIN, Updates.frame);
		resourcesSurface = Assist.getOptimizedImage(resourcesPage.getPageWidth(), resourcesPage.getHeight(), Transparency.OPAQUE);
		resourcesGraphics = resourcesSurface.getGraphics();
		
		resourceHovered = -1;
		resourceFocused = -1;
	}
	private static void closeEditor() {
		Updates.frame.releaseMouseFocus();
		FOCUS_RESOURCE_TYPE_ITEM = FOCUS_RESOURCE_TYPE_ITEM.dispose();
		
		resource = null;
		addressField = null;
		resourceType = null;
		editTypeBeg = null;
		editTypeEnd = null;

		editTypeBegTimer = null;
		editTypeEndTimer = null;
		
		Browser.openBrowser();
	}

	private static final short[] ADDRESS_FONT = Font.toArray("Адресс");
	private static final short[] NAME_FONT = Font.toArray("Название");
	private static final short[] RESOURCE_TYPE_FONT = Font.toArray("Ресурс");
	
	private static short[] editTypeBeg;
	private static int editTypeBegSize;
	private static WaveTimer editTypeBegTimer;
	private static BufferedImage editTypeBegSurface;
	private static Graphics editTypeBegGrpahics;
	private static short[] editTypeEnd;
	private static int editTypeEndSize;
	private static WaveTimer editTypeEndTimer;
	private static BufferedImage editTypeEndSurface;
	private static Graphics editTypeEndGrpahics;
	private static final int MINIMAL_EDITOR_ADDRESS_FIELD_SIZE = 256;
	private static LineEditField addressField = null;
	private static void selectType(final ResourceType type) {
		resourceType = type;
		editTypeBeg = Font.toArray(type.beg);
		editTypeEnd = Font.toArray(type.end);

		editTypeBegTimer.setLength(Font.sizeLine(editTypeBeg, 1));
		editTypeEndTimer.setLength(Font.sizeLine(editTypeEnd, 1));
		editTypeBegTimer.reset();
		editTypeEndTimer.reset();
		
		calculateEditorAdressSizes();
	}
	private static void calculateEditorAdressSizes() {
		editTypeBegSize = Font.sizeLine(editTypeBeg, 1);
		editTypeEndSize = Font.sizeLine(editTypeEnd, 1);
		
		int left = Updates.frame.width() - Renderer.C_ICON_OFFSET - MINIMAL_EDITOR_ADDRESS_FIELD_SIZE;
		
		if(editTypeBegSize != 0) left -= 2*Renderer.C_MARGIN;
		if(editTypeEndSize != 0) left -= 2*Renderer.C_MARGIN;
		
		int max1 = Math.min(left/2, editTypeBegSize);
		int max2 = Math.min(left - max1, editTypeEndSize);
		
		if(max2 != editTypeEndSize && max1 == editTypeBegSize)
			max2 = Math.min(left-max1, editTypeEndSize);
		else if(max1 != editTypeBegSize && max2 == editTypeEndSize)
			max1 = Math.min(left-max2, editTypeBegSize);

		editTypeBegSize = max1;
		editTypeEndSize = max2;

		editTypeBegTimer.setWidth(editTypeBegSize);
		editTypeEndTimer.setWidth(editTypeEndSize);
		
		if(editTypeBegSize != 0) {
			editTypeBegSize += 2*Renderer.C_MARGIN;
			editTypeBegSurface = Assist.getOptimizedImage(editTypeBegSize, Renderer.C_ICON_SIZE, Transparency.OPAQUE);
			editTypeBegGrpahics = editTypeBegSurface.getGraphics();
			editTypeBegGrpahics.setColor(Colors.LOW_CONTRAST);
		}
		if(editTypeEndSize != 0) {
			editTypeEndSize += 2*Renderer.C_MARGIN;
			editTypeEndSurface = Assist.getOptimizedImage(editTypeEndSize, Renderer.C_ICON_SIZE, Transparency.OPAQUE);
			editTypeEndGrpahics = editTypeEndSurface.getGraphics();
			editTypeEndGrpahics.setColor(Colors.LOW_CONTRAST);
		}

		addressField.setPosition(Renderer.C_ICON_OFFSET+editTypeBegSize, Renderer.C_ICON_OFFSET);
		addressField.setSizes(Updates.frame.width() - Renderer.C_ICON_OFFSET-editTypeBegSize-editTypeEndSize, Renderer.C_ICON_SIZE);
	}

	private static LineEditField nameField;
	private static ResourceType resourceType;
	private static BufferedImage resourcesSurface;
	private static Graphics resourcesGraphics;
	private static ScrollablePage resourcesPage;

	private static int resourceHovered;
	private static int resourceFocused;
	
	static void think() {
		if(Updates.frame.wasResized()) {
			calculateEditorAdressSizes();
			nameField.setSizes(Updates.frame.width() - Renderer.C_ICON_OFFSET, Renderer.C_ICON_SIZE);
			
			resourcesPage.setSizes(Updates.frame.width() - Renderer.C_ICON_OFFSET, Updates.frame.height()-5*Renderer.C_ICON_OFFSET);
			resourcesSurface = Assist.getOptimizedImage(resourcesPage.getPageWidth(), resourcesPage.getHeight(), Transparency.OPAQUE);
			resourcesGraphics = resourcesSurface.getGraphics();
		}

		editTypeBegTimer.tick();
		editTypeEndTimer.tick();
		
		addressField.think();
		nameField.think();
		
		resourcesPage.think();
				
		if(thinkSidebar())
			return;

		resourceHovered = -1;
		if(resourcesPage.isMouseInsideViewPortArea() && (Updates.frame.canGrabMouseFocus() || Updates.frame.holdingMouseFocus(FOCUS_RESOURCE_TYPE_ITEM)))
			resourceHovered = Assist.cellPointer(resourcesPage.getScrollMouseY(), Renderer.C_ICON_SIZE, Updates.types.length, Renderer.C_MARGIN);

		if(Updates.frame.MOUSE_L.pressed() && Updates.frame.canGrabMouseFocus() && resourceHovered != -1) {
			resourceFocused = resourceHovered;
			Updates.frame.grabMouseFocus(FOCUS_RESOURCE_TYPE_ITEM);
		}

		if(Updates.frame.MOUSE_L.released() && Updates.frame.holdingMouseFocus(FOCUS_RESOURCE_TYPE_ITEM)) {
			Updates.frame.releaseMouseFocus();
			if(resourceFocused == resourceHovered)
				selectType(Updates.types[resourceFocused]);
			resourceFocused = -1;
		}
	}
	
	private static final boolean thinkSidebar() {
		Updates.sidebar.think();
		
		final byte clicked = Updates.sidebar.clicked();
		switch(clicked) {
		case SB_RETURN:
			closeEditor();
			return true;
		case SB_SAVE:
			if(resource == null)
				Updates.allResources.add(0, new Resource(addressField.getText(), nameField.getText(), resourceType));
			else {
				resource.address = addressField.getText();
				resource.updateName(nameField.getText());
				resource.resourceType = resourceType;
			}
			Updates.save();
			closeEditor();
			return true;
		case SB_RESTORE:
			addressField.setText(resource.address);
			addressField.clearHistory();
			nameField.setText(resource.name);
			nameField.clearHistory();
			selectType(resource.resourceType);
			return true;
		case SB_DELETE:
			Updates.allResources.remove(resource);
			Updates.save();
			closeEditor();
			return true;
		}
		return false;
	}
	
	static void render() {
		// Адресс
		Renderer.drawLine(ADDRESS_FONT, Renderer.C_ICON_OFFSET, Renderer.C_ICON_PADDING, 1, Font.WHITE, Updates.frame.graphics());
		addressField.render();

		if(editTypeBegSize != 0) {
			editTypeBegGrpahics.fillRect(0, 0, editTypeBegSize, Renderer.C_ICON_SIZE);
			Renderer.drawLine(editTypeBeg, Renderer.C_MARGIN-editTypeBegTimer.getOffset(), Renderer.C_ICON_PADDING, 1, Font.WHITE, editTypeBegGrpahics);
			Updates.frame.graphics().drawImage(editTypeBegSurface, Renderer.C_ICON_OFFSET, addressField.getY(), null);
		}

		if(editTypeEndSize != 0) {
			editTypeEndGrpahics.fillRect(0, 0, editTypeEndSize, Renderer.C_ICON_SIZE);
			Renderer.drawLine(editTypeEnd, Renderer.C_MARGIN-editTypeEndTimer.getOffset(), Renderer.C_ICON_PADDING, 1, Font.WHITE, editTypeEndGrpahics);
			Updates.frame.graphics().drawImage(editTypeEndSurface, Renderer.C_ICON_OFFSET+editTypeBegSize+addressField.getWidth(), addressField.getY(), null);
		}

		// Название
		Renderer.drawLine(NAME_FONT, Renderer.C_ICON_OFFSET, 2*Renderer.C_ICON_OFFSET + Renderer.C_ICON_PADDING, 1, Font.WHITE, Updates.frame.graphics());
		nameField.render();

		// Типы
		Renderer.drawLine(RESOURCE_TYPE_FONT, Renderer.C_ICON_OFFSET, 4*Renderer.C_ICON_OFFSET + Renderer.C_ICON_PADDING, 1, Font.WHITE, Updates.frame.graphics());
		resourcesPage.render();
		
		resourcesGraphics.clearRect(0, 0, resourcesSurface.getWidth(), resourcesSurface.getHeight());
		for(int i = 0; i != Updates.types.length; i++) {
			// TODO: color: resourcesGraphics.setColor(Assist.focusHover(i, resourceFocused, resourceHovered) ? Colors.ACCENT : Updates.types[i] == resourceType ? Colors.HIGH_CONTRAST : Colors.LOW_CONTRAST);
			resourcesGraphics.drawImage(Assist.focusHover(i, resourceFocused, resourceHovered) ? Updates.types[i].hoverImage : Updates.types[i].image, 0, i*Renderer.C_ICON_OFFSET-resourcesPage.getViewportOffset(), null);
			resourcesGraphics.setColor(Assist.focusHover(i, resourceFocused, resourceHovered) ? Colors.ACCENT : Updates.types[i] == resourceType ? Colors.LOW_CONTRAST : Colors.HINT);
			resourcesGraphics.fillRect(Renderer.C_ICON_SIZE, i*Renderer.C_ICON_OFFSET-resourcesPage.getViewportOffset(), resourcesSurface.getWidth()-Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE);
			// TODO: waveTImer, если название не будет влезать целиком
			Renderer.drawLine(Updates.types[i].nameFont, Renderer.C_ICON_OFFSET, i*Renderer.C_ICON_OFFSET+Renderer.C_ICON_PADDING-resourcesPage.getViewportOffset(), 1, Font.WHITE, resourcesGraphics);
		}
		
		Updates.frame.graphics().drawImage(resourcesSurface, Renderer.C_ICON_OFFSET, 5*Renderer.C_ICON_OFFSET, null);

		Updates.sidebar.render();
	}

	static void unload() {
		resource = null;
		editTypeBeg = null;
		editTypeBegTimer = null;
		editTypeBegSurface = null;
		editTypeBegGrpahics = null;
		editTypeEnd = null;
		editTypeEndTimer = null;
		editTypeEndSurface = null;
		editTypeEndGrpahics = null;
		addressField = null;
		nameField = null;
		resourceType = null;
		resourcesSurface = null;
		resourcesGraphics = null;
		resourcesPage = null;
		
		FOCUS_RESOURCE_TYPE_ITEM = Focusable.safeDispose(FOCUS_RESOURCE_TYPE_ITEM);
	}
}