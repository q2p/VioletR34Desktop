package q2p.violet34desktop;

@SuppressWarnings("serial")
public class FileTypeException extends InternalException {
	public final String path;

	FileTypeException(final String path) {
		super("Файл \""+path+"\" не является файлом.");
		this.path = path;
	}
}