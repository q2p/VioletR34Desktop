package q2p.violet34desktop.windows.updates;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.Renderer;

class ResourceType {
	final String storageName;
	final String name;
	final short[] nameFont;
	final String beg;
	final String end;
	final BufferedImage image;
	final BufferedImage hoverImage;
	
	ResourceType(final String storageName, final String name, final String beg, final String end) throws IconLoadException, UnknownTypeException {
		if(storageName.equals(Updates.UNKNOWN_TYPE.storageName))
			throw new UnknownTypeException();
		
		this.storageName = storageName;
		this.name = name;
		nameFont = Font.toArray(name);
		this.beg = beg;
		this.end = end;
		image = Assist.getOptimizedImage(Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE, Transparency.OPAQUE);
		hoverImage = Assist.getOptimizedImage(Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE, Transparency.OPAQUE);
		try {
			image.getGraphics().drawImage(ImageIO.read(new File(Assist.MAIN_FOLDER+"VioletR34/data/resources/types/"+storageName+".png")), 0, 0, null);
		} catch (IOException e) {
			throw new IconLoadException(storageName, false);
		}
		try {
			hoverImage.getGraphics().drawImage(ImageIO.read(new File(Assist.MAIN_FOLDER+"VioletR34/data/resources/types/"+storageName+".h.png")), 0, 0, null);
		} catch (IOException e) {
			throw new IconLoadException(storageName, true);
		}
		
		final int ACCENT = Colors.ACCENT.getRGB();
		final int WHITE = Colors.WHITE.getRGB();
		for(int y = Renderer.C_ICON_SIZE-1; y != -1; y--)
		for(int x = Renderer.C_ICON_SIZE-1; x != -1; x--)
			hoverImage.setRGB(x, y, (hoverImage.getRGB(x, y) == WHITE)?WHITE:ACCENT);
	}
	ResourceType(final String storageName, final String name, final String beg, final String end, final BufferedImage image) {
		this.storageName = storageName;
		this.name = name;
		nameFont = Font.toArray(name);
		this.beg = beg;
		this.end = end;
		this.image = image;
		
		hoverImage = Assist.getOptimizedImage(Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE, Transparency.OPAQUE);
		final int ACCENT = Colors.ACCENT.getRGB();
		final int WHITE = Colors.WHITE.getRGB();
		for(int y = Renderer.C_ICON_SIZE-1; y != -1; y--)
		for(int x = Renderer.C_ICON_SIZE-1; x != -1; x--)
			hoverImage.setRGB(x, y, (image.getRGB(x, y) == WHITE)?WHITE:ACCENT);
	}
}