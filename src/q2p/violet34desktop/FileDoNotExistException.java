package q2p.violet34desktop;

@SuppressWarnings("serial")
public class FileDoNotExistException extends InternalException {
	public final String path;

	FileDoNotExistException(final String path) {
		super("Файл \""+path+"\" не существует.");
		this.path = path;
	}
}