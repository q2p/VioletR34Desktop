package q2p.violet34desktop;

@SuppressWarnings("serial")
public class FailedToCreateFileException extends InternalException {
	public final String path;

	FailedToCreateFileException(final String path) {
		super("Не удалось создать файл \""+path+"\".");
		this.path = path;
	}
}