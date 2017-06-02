package q2p.violet34desktop.windows.ideas;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.FileDoNotExistException;
import q2p.violet34desktop.FileTypeException;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.events.FrameAbstraction;
import q2p.violet34desktop.windows.components.ScrollablePage;
import q2p.violet34desktop.windows.components.Sidebar;
import q2p.violet34desktop.windows.components.VisibilitySwitcher;
import q2p.violet34desktop.windows.components.editfields.DefaultValidator;
import q2p.violet34desktop.windows.components.editfields.LineEditField;
import q2p.violet34desktop.windows.ideas.containers.Container;
import q2p.violet34desktop.windows.ideas.containers.ImagesContainer;
import q2p.violet34desktop.windows.ideas.containers.LinksContainer;
import q2p.violet34desktop.windows.ideas.containers.MassiveTextContainer;
import q2p.violet34desktop.windows.ideas.containers.TextContainer;

// TODO: изменение заголовка окна при изменении названия поста
// TODO: возможность прикреплять тэги из тэггера
public class IdeaViewer {
	private boolean isInEditingMode;
	public boolean isInEditingMode() {
		return isInEditingMode;
	}
	
	static final String WINDOW_TITLE = "Идея";

	public static final BufferedImage T_TEXT = Assist.loadImage("text.png", Transparency.BITMASK);
	public static final BufferedImage T_TODO = Assist.loadImage("todo.png", Transparency.BITMASK);
	public static final BufferedImage T_ANCHOR = Assist.loadImage("anchor.png", Transparency.BITMASK);
	public static final BufferedImage T_IMAGE = Assist.loadImage("image.png", Transparency.BITMASK);
	public static final BufferedImage T_AUDIO = Assist.loadImage("audio.png", Transparency.BITMASK);
	public static final BufferedImage T_VIDEO = Assist.loadImage("video.png", Transparency.BITMASK);
	public static final BufferedImage T_FILE = Assist.loadImage("file.png", Transparency.BITMASK);
	public static final BufferedImage T_LINK = Assist.loadImage("link.png", Transparency.BITMASK);
	
	public final FrameAbstraction frame;

	public static final short MIN_ITEMS_WIDTH = (1366 - Renderer.C_ICON_OFFSET - ScrollablePage.SCROLL_BAR_WIDTH)/3-Renderer.C_MARGIN;
	public static final short MAX_ITEMS_WIDTH = 2*MIN_ITEMS_WIDTH+Renderer.C_MARGIN-1;
	public int collumns;
	public short itemsWidth;
	private static final int MIN_SIZE = Renderer.C_ICON_OFFSET+2*Renderer.C_MARGIN+MIN_ITEMS_WIDTH+ScrollablePage.SCROLL_BAR_OFFSET;
	
	private final Sidebar sidebar;
	private final ScrollablePage page;
	
	int number;
	private short[][] fontNumber;
	private boolean isNote;

	private String originalTitle;
	private String title;
	private final LineEditField titleField;
	
	private static final byte text = 0;
	private static final byte todo = 1;
	private static final byte images = 2;
	private static final byte audios = 3;
	private static final byte videos = 4;
	private static final byte links = 5;
	private static final byte files = 6;
	private final Container[] containers;
	private final VisibilitySwitcher buttonsPointers;
	
	public IdeaViewer(final int number, final boolean startInEditingMode) {
		isInEditingMode = startInEditingMode;
		this.number = number;
		isNote = number > Ideas.postsAmount;
		fontNumber = Ideas.toNumber(number);
		
		frame = new FrameAbstraction(MIN_SIZE, MIN_SIZE, true, WINDOW_TITLE+" "+Ideas.toStringNumber(number)+".");
		titleField = new LineEditField(Renderer.C_ICON_OFFSET, 0, MIN_SIZE-2*Renderer.C_ICON_OFFSET, Renderer.C_ICON_SIZE, Colors.LOW_CONTRAST, "", DefaultValidator.BLOCK_NEWLINE, frame, false);
		sidebar = new Sidebar(frame);
		page = new ScrollablePage(Renderer.C_ICON_OFFSET, 0, 1, 1, 1, frame);

		containers = new Container[] {
			new MassiveTextContainer("Текст", T_TEXT, this),
			new MassiveTextContainer("Заметки", T_TODO, this),

			new ImagesContainer(this),
			new TextContainer("Аудио", T_AUDIO, this),
			new TextContainer("Видео", T_VIDEO, this),
			new LinksContainer(this),
			new TextContainer("Файлы", T_FILE, this)
		};

		buttonsPointers = new VisibilitySwitcher(containers.length, false);
		
		loadIdea();
	}

	private void beginEditing() {
		isInEditingMode = true;
		
		for(final Container c : containers)
			c.beginEditing();
		
		calculateSizes();
		changeSidebarButtons();
	}

	private final void loadIdea() {
		final DataInputStream dis;
		try {
			dis = new DataInputStream(Assist.getDatStream(Ideas.IDEAS_PATH));
		} catch (FileTypeException e) {
			// TODO: обработка ошибок
			// TODO: Создать функцию для создания идентичных сообщений об ошибках
			Assist.abort("Файл для хранения идей не является файлом. Возможно он является директорией.\nИсправьте проблему и вернитесь.\n"+e.path);
			return;
		} catch (FileDoNotExistException e) {
			// TODO: обработка ошибок
			Assist.abort("Файл для хранения идей не найден.\nИсправьте проблему и вернитесь.\n"+e.path);
			return;
		}
				
		try {
			dis.readInt(); // Кол-во постов
			dis.readInt(); // Кол-во заметок
			
			for(int i = number; i != 1; i--)
				Ideas.skipPost(dis);

			title = originalTitle = Assist.readString(dis);
			frame.setTitle(WINDOW_TITLE+" "+Ideas.toStringNumber(number)+". "+title);
			titleField.setText(title);
			// TODO: titleField.setEditable(?);
			
			/*
			TODO: оптимизировать
			for(final Container c : containers)
				c.load(dis);
			*/
			containers[text].load(dis); // text
			containers[todo].load(dis); // todo
			
			for(int i = dis.readInt(); i != 0; i--)
				dis.readInt(); // TODO: anchors

			containers[images].load(dis); // images
			containers[audios].load(dis); // audios
			containers[videos].load(dis); // videos
			containers[links].load(dis); // links
			containers[files].load(dis); // files
			
			Assist.tryToCloseInput(dis);
		} catch (IOException e) {
			// TODO: обработка ошибок
			Assist.tryToCloseInput(dis);
			Assist.abort("Ошибка чтения файла хранящего идеи.\nИсправьте проблему и вернитесь.\n"+Assist.getDatPath(Ideas.IDEAS_PATH));
			return;
		}
		
		calculateSizes();
		changeSidebarButtons();
	}

	private final void calculateSizes() {
		page.setSizes(frame.width()-Renderer.C_ICON_OFFSET, frame.height());
		final int viewportOffset = page.getViewportOffset();
		calculatePageHeight(page.getWidth());
		
		if(page.getPageWidth() != page.getWidth())
			calculatePageHeight(page.getPageWidth());
		
		updateSizes();
		
		page.setViewportOffset(viewportOffset);
		
		scrollThings();
	}
	private void updateSizes() {
		titleField.setSizes(page.getPageWidth()-Ideas.numberRectangleWidth, Renderer.C_ICON_SIZE);
		
		int y = Renderer.C_ICON_SIZE;
		
		for(final Container c : containers)
			y = c.updateSizes(page.getPageWidth(), y);
	}

	private final void calculatePageHeight(final int width) {
		int y = Renderer.C_ICON_SIZE;
		
		collumns = (width+Renderer.C_MARGIN)/(MIN_ITEMS_WIDTH+Renderer.C_MARGIN);
		itemsWidth = (short)((width+Renderer.C_MARGIN)/collumns-Renderer.C_MARGIN);
		
		for(final Container c : containers)
			y = c.calculateSizes(width, y);
		
		page.setPageHeight(y);
	}
	
	public final boolean think() {
		frame.iterateNewActions();
		
		if(frame.isClosing()) {
			// TODO: не закрывать, если не завершенно редактирование
			frame.destroy();
			dispose();
			Ideas.ideaViewers.remove(this);
			return false;
		}
		
		if(frame.wasResized())
			calculateSizes();
		
		page.think();
		
		scrollThings();
		
		boolean sidebarButtonsChanged = false;
		
		titleField.think();
		
		for(int i = 0; i != containers.length; i++) {
			containers[i].think();
			if(containers[i].wasResized())
				calculateSizes();
			sidebarButtonsChanged |= containers[i].changedButton();
		}
		
		if(sidebarButtonsChanged)
			changeSidebarButtons();
		
		thinkSidebar();
		
		return true; // TODO: после удаления отправлять false
	}

	private void scrollThings() {
		int y = -page.getViewportOffset();
		
		titleField.setPosition(Renderer.C_ICON_OFFSET+Ideas.numberRectangleWidth, y);
		
		y += Renderer.C_ICON_SIZE;
		
		for(final Container c : containers)
			y = c.position(y);
	}

	private final void thinkSidebar() {
		sidebar.think();
		byte clicked = sidebar.clicked();
		
		if(clicked == -1)
			return;
		
		if(isInEditingMode) {
			if(clicked == 0)
				cancelEditing();
			else if(clicked == 1)
				save();
			else if(clicked == 2)
				restore();
			else if(clicked == 3)
				delete();
			else if(isNote && clicked == 4)
				moveFromNotes();
			else {
				final Container container = containers[buttonsPointers.getRealId(clicked-(isNote?5:4))];
				container.onButtonPress();
				if(container.wasResized())
					calculateSizes();
				if(container.changedButton())
					changeSidebarButtons();
			}
		} else {
			beginEditing();
		}
	}

	private void moveFromNotes() {
		// TODO Auto-generated method stub
		// TODO: изметить названия якорей в других окнах
		isNote = false;
		loadIdea();
		changeSidebarButtons();
	}

	private final void cancelEditing() {
		isInEditingMode = false;
		restore();
	}

	private final void save() {
		// TODO: при сохранении не обязательно загружать идею снова
		// TODO: изметить названия якорей в других окнах
		loadIdea();
		changeSidebarButtons();
	}

	private final void restore() {
		title = originalTitle;
		frame.setTitle(WINDOW_TITLE+" "+Ideas.toStringNumber(number)+". "+title);
		titleField.setText(title);
		
		for(final Container c : containers)
			c.restore();
		
		calculateSizes();
		changeSidebarButtons();
	}

	private final void delete() {
		// TODO Auto-generated method stub
		// TODO: при удалении смещать аддресса других файлов и якоря
		
	}

	private final void changeSidebarButtons() {
		if(isInEditingMode) {
			final LinkedList<BufferedImage> buttons = new LinkedList<BufferedImage>();
			
			buttons.addLast(Renderer.T_RETURN);
			buttons.addLast(Renderer.T_SAVE);
			buttons.addLast(Renderer.T_RESTORE);
			buttons.addLast(Renderer.T_DELTETE);
			if(isNote)
				buttons.addLast(Renderer.T_TO_POSTS);
			
			for(byte i = 0; i != containers.length; i++) {
				final BufferedImage button = containers[i].getButton();
				buttonsPointers.setVisible(i, button != null);
				if(button != null)
					buttons.addLast(button);
			}
			
			sidebar.setButtons(buttons.toArray(new BufferedImage[buttons.size()]));
		} else {
			sidebar.setButtons(Renderer.T_EDIT);
		}
	}

	public final void render() {
		sidebar.render();
		page.render();
		
		Ideas.renderNumber(fontNumber, frame.graphics(), false, Renderer.C_ICON_OFFSET, -page.getViewportOffset());
		titleField.render();
		
		for(int i = 0; i != containers.length; i++)
			containers[i].render();
		
		frame.postActions();
	}
	
	public final void dispose() {
		for(final Container c : containers)
			c.dispose();
	}
}