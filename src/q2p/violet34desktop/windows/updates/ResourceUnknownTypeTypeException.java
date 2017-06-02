package q2p.violet34desktop.windows.updates;

import q2p.violet34desktop.InternalException;

@SuppressWarnings("serial")
class ResourceUnknownTypeTypeException extends InternalException {
	String address;
	String name;
	String storageName;

	ResourceUnknownTypeTypeException(final String address, final String name, final String storageName) {
		super("Не удалось загрузить ресурс. Не был найден соответствующий тип ресурса.\nИмя: "+name+"\nАдресс: "+address+"\nТип: "+storageName);
		this.address = address;
		this.name = name;
		this.storageName = storageName;
	}
}