package q2p.violet34desktop.windows.updates;

import q2p.violet34desktop.InternalException;

@SuppressWarnings("serial")
public class IconLoadException extends InternalException {
	public final String storageName;
	public final boolean hoverImage;

	public IconLoadException(final String storageName, boolean hoverImage) {
		super("Не удалось загрузить изображение типа.\nТип: "+storageName);
		this.storageName = storageName;
		this.hoverImage = hoverImage;
	}
}