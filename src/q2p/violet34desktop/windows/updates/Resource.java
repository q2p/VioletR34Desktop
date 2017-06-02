package q2p.violet34desktop.windows.updates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.InternalException;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.lowlevel.OpSys;
import q2p.violet34desktop.windows.components.WaveTimer;

class Resource {
	ResourceType resourceType;
	boolean wasSetToUnkown = false;
	String address;
	String name;
	short[] fontName;
	long lastUpdate;
	
	short[] time = null;
	short timeSize = 0;

	Resource(final String address, final String name, final String storageName, final long lastUpdate) {
		this.address = address;
		updateName(name);
		this.lastUpdate = lastUpdate;
		try {
			resourceType = Updates.getTypeByStorageName(storageName);
		} catch (InternalException e) {
			wasSetToUnkown = true;
			resourceType = Updates.UNKNOWN_TYPE;
		}
	}
	
	Resource(final String address, final String name, final ResourceType type) {
		this.address = address;
		updateName(name);
		this.resourceType = type;
		this.lastUpdate = -1;
	}

	void save(final DataOutputStream dos) throws IOException {
		Assist.writeString(dos, address);
		Assist.writeString(dos, name);
		Assist.writeString(dos, resourceType.storageName);
		dos.writeLong(lastUpdate);
	}
	
	void updateName(final String name) {
		this.name = name;
		fontName = Font.toArray(name);
	}
	
	void copyToClipboard() {
		OpSys.copyToClipboard(resourceType.beg+address+resourceType.end);
	}
	
	private String getTimeString() {
		if(lastUpdate == -1) return "Никогда";
		long diff = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()-lastUpdate;

		int i = (int)(diff/(1000*60*60*24*7));
		if(i != 0) return i+Assist.decline(i, " неделю", " недели", " недель")+" назад";
		i = (int)(diff/(1000*60*60*24) % 7);
		if(i != 0) return i+Assist.decline(i, " день", " дня", " дней")+" назад";
		i = (int)((long)(diff/(1000*60*60)) % 24);
		if(i != 0) return i+Assist.decline(i, " час", " часа", " часов")+" назад";
		i = (int)((long)(diff/(1000*60)) % 60);
		if(i != 0) return i+Assist.decline(i, " минуту", " минуты", " минут")+" назад";
		
		return "Меньше минуты назад";
	}
	
	void calculateTime() {
		time = Font.toArray(getTimeString());
		timeSize = (short) Font.sizeLine(time, 1);
	}

	void render(final int y, final int width, final boolean isSelected, final BufferedImage textSurface, final Graphics gr, final WaveTimer timer) {
		calculateTime();
		
		final Color textColor;
		if(isSelected)
			textColor =  (Browser.ItemParts.TEXT == Browser.itemHover && (Browser.itemFocused == null || Browser.itemFocused == Browser.ItemParts.TEXT)) ? Colors.ACCENT : Colors.LOW_CONTRAST;
		else
			textColor = Colors.HINT;
		
		final Color timeColor;
		if(isSelected)
			timeColor =  (Browser.ItemParts.TIME == Browser.itemHover && (Browser.itemFocused == null || Browser.itemFocused == Browser.ItemParts.TIME)) ? Colors.ACCENT : Colors.HIGH_CONTRAST;
		else
			timeColor = Colors.LOW_CONTRAST;
		
		if(isSelected && Browser.ItemParts.EDIT == Browser.itemHover && (Browser.itemFocused == null || Browser.itemFocused == Browser.ItemParts.EDIT)) {
			Updates.frame.graphics().setColor(Colors.ACCENT);
			Updates.frame.graphics().fillRect(Renderer.C_ICON_OFFSET, y, Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE);
			Updates.frame.graphics().drawImage(Renderer.T_EDIT, Renderer.C_ICON_OFFSET, y, null);
		} else {
			Updates.frame.graphics().drawImage(resourceType.image, Renderer.C_ICON_OFFSET, y, null);
		}

		gr.setColor(textColor);
		gr.fillRect(0, 0, width-Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE);
		Renderer.drawLine(fontName, Renderer.C_MARGIN-(isSelected?timer.getOffset():0), Renderer.C_ICON_PADDING, 1, Font.WHITE, gr);
		Updates.frame.graphics().drawImage(textSurface, Renderer.C_ICON_OFFSET+Renderer.C_ICON_SIZE, y, null);
		
		Updates.frame.graphics().setColor(timeColor);
		Updates.frame.graphics().fillRect(Renderer.C_ICON_OFFSET + width-2*Renderer.C_MARGIN-timeSize, y, 2*Renderer.C_MARGIN+timeSize, Renderer.C_ICON_SIZE);
		Renderer.drawLine(time, Renderer.C_ICON_OFFSET + width-Renderer.C_MARGIN-timeSize, y+Renderer.C_ICON_PADDING, 1, Font.WHITE, Updates.frame.graphics());

		time = null;
	}
}