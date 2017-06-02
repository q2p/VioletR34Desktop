package q2p.violet34desktop.windows.updates;

import java.awt.Transparency;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.FailedToCreateFileException;
import q2p.violet34desktop.FileDoNotExistException;
import q2p.violet34desktop.FileTypeException;
import q2p.violet34desktop.InternalException;
import q2p.violet34desktop.events.FrameAbstraction;
import q2p.violet34desktop.windows.components.Sidebar;

public class Updates {
	private static final Object LOCK = new Object(); // TODO: если не вызывается из трея то этот лок не нужен. Так-же и с другими окнами
	private static boolean needToPopup = false;
	
	private static final String RESOURCES_PATH = "resources/resources";
	private static final String RESOURCE_TYPES_DIR = Assist.DATA_FOLDER + "resources/types/";

	static final ResourceType UNKNOWN_TYPE = new ResourceType("unknown", "Unknown", "", "", Assist.loadImage("unknownType.png", Transparency.OPAQUE));
	static ResourceType[] types = null;
	static ArrayList<Resource> allResources = null;
	private static boolean duringLoadingSomeResourcesWasSettedToUnknown = false;

	static Sidebar sidebar;
	
	static final byte MODE_BROWSE = 0;
	static final byte MODE_EDIT = 1;
	static final byte MODE_EXCEPTION = 2;
	static byte mode;
	
	static FrameAbstraction frame = null;
	private static final int MIN_WIDTH = 512;
	private static final int MIN_HEIGHT = 256;
	public static final String WINDOW_TITLE = "Обновления";
	static UnknownTypeExceptionScreen unknownTypeExceptionScreen = null;
	
	public static void think() {
		synchronized (LOCK) {
			if(needToPopup) {
				if(frame == null) {
					frame = new FrameAbstraction(MIN_WIDTH, MIN_HEIGHT, true, WINDOW_TITLE);
					
					sidebar = new Sidebar(frame);
					loadTypes();
					loadResources();
					if(duringLoadingSomeResourcesWasSettedToUnknown) {
						duringLoadingSomeResourcesWasSettedToUnknown = false;
						mode = MODE_EXCEPTION;
						unknownTypeExceptionScreen = new UnknownTypeExceptionScreen();
					} else {
						Browser.load();
						Browser.openBrowser();
					}
				} else
					frame.requestFocus();
				
				needToPopup = false;
			}
		}
		
		if(frame == null)
			return;
		
		frame.iterateNewActions();

		switch(mode) {
		case MODE_BROWSE:
			Browser.think();
			return;
		case MODE_EDIT:
			Editor.think();
			return;
		case MODE_EXCEPTION:
			if(unknownTypeExceptionScreen.think()) {
				Browser.load();
				Browser.openBrowser();
				save();
			}
		}
	}
	public static void render() {
		if(frame == null)
			return;
		
		switch(mode) {
		case MODE_BROWSE:
			Browser.render();
			break;
		case MODE_EDIT:
			Editor.render();
			break;
		case MODE_EXCEPTION:
			unknownTypeExceptionScreen.render();
			break;
		}
		frame.postActions();
	}
	
	private static void loadResources() {
		allResources = new ArrayList<Resource>();
		final DataInputStream dis;
		try {
			dis = new DataInputStream(Assist.getDatStream(RESOURCES_PATH));
		} catch (FileTypeException e) {
			Assist.abort("Файл хранящий информацию о ресурсах не является файлом. Возможно он является директорией.\nИсправьте проблему и вернитесь.\n" + e.path);
			return;
		} catch (FileDoNotExistException e) {
			Assist.abort("Файл хранящий информацию о ресурсах не найден.\nИсправьте проблему и вернитесь.\n" + e.path);
			return;
		}
		try {
			Resource resource;
			while(dis.available() > 0) {
				resource = new Resource(Assist.readString(dis), Assist.readString(dis), Assist.readString(dis), dis.readLong());
				allResources.add(resource);
				if(resource.wasSetToUnkown)
					duringLoadingSomeResourcesWasSettedToUnknown = true;
			}
			Assist.tryToCloseInput(dis);
		} catch (IOException e) {
			Assist.tryToCloseInput(dis);
			Assist.abort("Ошибка чтения файла хранящего информацию о ресурсах.\nИсправьте проблему и вернитесь.\n" + Assist.getDatPath(RESOURCES_PATH));
		}
	}
	private static boolean loadTypes() {
		final File dir = new File(RESOURCE_TYPES_DIR);
		if(!dir.exists())
			Assist.abort("Директория с файлами описывающими типы не найдена.\nИсправьте проблему и вернитесь.\n" + Assist.normalizePath(dir.getAbsolutePath(), true));
		
		if(!dir.isDirectory())
			Assist.abort("Файл необхадимый для хранения файлов описывающих типы не является директорией.\nИсправьте проблему и вернитесь.\n" + Assist.normalizePath(dir.getAbsolutePath(), true));
		
		final String[] list = dir.list();
		int amount = 0;
		for(int i = 0; i != list.length; amount += (list[i++].endsWith(".res")?1:0));
		
		types = new ResourceType[amount+1];
		amount = 1;
		FileInputStream fis = null;
		types[0] = UNKNOWN_TYPE;
		for(int i = 0; i != list.length; i++) {
			if(!list[i].endsWith(".res")) continue;
			try {
				fis = new FileInputStream(RESOURCE_TYPES_DIR + list[i]);
				final byte[] buff = new byte[fis.available()];
				fis.read(buff);
				Assist.tryToCloseInput(fis);
				
				final String total = new String(buff, StandardCharsets.UTF_8);
				types[amount] = new ResourceType(
						list[i].substring(0, list[i].lastIndexOf(".res")),
						total.substring(0, total.indexOf("\n")).trim(),
						total.substring(total.indexOf("\n")+1, total.lastIndexOf("\n")).trim(),
						total.substring(total.lastIndexOf("\n")+1).trim()
				);
			} catch (UnknownTypeException e) {
				// TODO: обработка всех исключений красивым способом
				Assist.abort("В директории с файлами описывающими типы находится файл описывающий зарезервированный тип \""+UNKNOWN_TYPE.storageName+"\".\nИсправьте проблему и вернитесь.\n" + Assist.normalizePath(dir.getAbsolutePath(), true)+UNKNOWN_TYPE.storageName+".res");
			} catch (IconLoadException e) {
				Assist.abort("Не удалось загрузить "+(e.hoverImage?"контрастное":"обычное")+" изображение для типа \""+e.storageName+"\".\nИсправьте проблему и вернитесь.\n" + Assist.normalizePath(dir.getAbsolutePath(), true)+e.storageName+(e.hoverImage?".h.png":".png"));
			} catch (IndexOutOfBoundsException e) {
				Assist.abort("Использовалось не правильное наименование файла для описания типа, или описание внутри файла было составленно с ошибками.\nИсправьте проблему и вернитесь.\n" + Assist.normalizePath(dir.getAbsolutePath(), true)+list[i]);
			} catch (IOException e) {
				Assist.tryToCloseInput(fis);
				Assist.abort("Произошла ошибка при чтении файла.\nИсправьте проблему и вернитесь.\n" + Assist.normalizePath(dir.getAbsolutePath(), true)+list[i]);
			}
			amount++;
		}
		return true;
	}

	static ResourceType getTypeByStorageName(final String storageName) throws InternalException {
		for(final ResourceType type : types)
			if(type.storageName.equals(storageName))
				return type;
		
		throw new InternalException("Не допустимый тип ресурса: \""+storageName+"\".");
	}

	public static void openWindow() {
		synchronized (LOCK) {
			needToPopup = true;
		}
	}
	static void closeWindow() {
		frame.destroy();
		frame = null;
		types = null;
		allResources = null;
		sidebar = null;
		unknownTypeExceptionScreen = null;
		Browser.unload();
		Editor.unload();
	}
		
	static void save() {
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(Assist.getTwrStream(RESOURCES_PATH));
			for(int i = 0; i != allResources.size(); i++)
				allResources.get(i).save(dos);
			Assist.tryToCloseOutput(dos);
		} catch (FileTypeException e) {
			Assist.abort("Временный файл для хранения информации о ресурсах не является файлом.\nИсправьте проблему и вернитесь.\n" + e.path);
		} catch (FailedToCreateFileException e) {
			Assist.abort("Не удалось создать временный файл для хранения информации о ресурсах.\nИсправьте проблему и вернитесь.\n" + e.path);
		} catch (IOException e) {
			Assist.tryToCloseOutput(dos);
			Assist.abort("Ошибка записи в файл для хранения информации о ресурсах.\nИсправьте проблему и вернитесь.\n" + Assist.getTwrPath(RESOURCES_PATH));
		}
		try {
			Assist.swapTwrAndDatFiles(RESOURCES_PATH);
		} catch (IOException e) {
			Assist.abort("При замене [DATa] файла на [ToWRite] файл произошла ошибка.\n Исправьте проблему и вернитесь\n"+Assist.getDatPath(RESOURCES_PATH)+"\n"+Assist.getTwrPath(RESOURCES_PATH));
		}
	}
}