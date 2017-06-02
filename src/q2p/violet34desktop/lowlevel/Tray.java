package q2p.violet34desktop.lowlevel;

import java.awt.AWTException;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Transparency;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.windows.main.Main;

public class Tray extends TrayIcon implements ActionListener {
	private static TrayIcon trayIcon = null;
	public static final BufferedImage iconImage = Assist.loadImage("icon.png", Transparency.BITMASK);
	static {
		final int ACCENT = Colors.ACCENT.getRGB();
		final int WHITE = Colors.WHITE.getRGB();
		for(int y = iconImage.getHeight()-1; y != -1; y--)
		for(int x = iconImage.getWidth()-1; x != -1; x--)
			if(iconImage.getRGB(x, y) == WHITE)
				iconImage.setRGB(x, y, ACCENT);
	}
	
	public Tray(final PopupMenu menu) {
		super(iconImage, VioletR34.TITLE, menu);
		addActionListener(this);
	}

	public static void initilize() {
		final PopupMenu menu = new PopupMenu();
		menu.add(new ShowTrayItem());
		menu.add(new QuitTrayItem());

		trayIcon = new Tray(menu);

		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (final AWTException e) {
			trayIcon = null;
			Assist.abort("Не удалось добавить значок в трей.");
		}
	}

	public static void destroy() {
		if(trayIcon != null)
			SystemTray.getSystemTray().remove(trayIcon);
	}

	public void actionPerformed(final ActionEvent ae) {
		Main.openWindow();
	}
}