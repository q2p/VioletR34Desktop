package q2p.violet34desktop;

@SuppressWarnings("serial")
public class InternalException extends Exception {
	public final String message;
	
	public InternalException(final String message) {
		this.message = message;
	}
}