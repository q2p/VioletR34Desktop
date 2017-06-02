package q2p.violet34desktop.windows.updates;

import q2p.violet34desktop.InternalException;

@SuppressWarnings("serial")
public class UnknownTypeException extends InternalException {
	public UnknownTypeException() {
		super("Попытка загрузки зарезервированного типа \""+Updates.UNKNOWN_TYPE.storageName+"\"");
	}
}