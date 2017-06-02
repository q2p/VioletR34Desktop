package q2p.violet34desktop.windows.ideas;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.FileDoNotExistException;
import q2p.violet34desktop.FileTypeException;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.events.Focusable;
import q2p.violet34desktop.events.FrameAbstraction;
import q2p.violet34desktop.windows.components.ScrollablePage;
import q2p.violet34desktop.windows.components.Sidebar;
import q2p.violet34desktop.windows.components.editfields.DefaultValidator;
import q2p.violet34desktop.windows.components.editfields.LineEditField;

public class Browser {
	/*
	TODO: wave timer
	*/
	static FrameAbstraction frame;
	private static final Object LOCK = new Object();
	private static boolean needToPopup = false;

	private static Focusable FOCUSABLE_ITEM;
	
	static final short[] POSTS_TEXT = Font.toArray("Посты");
	static ArrayList<TitleItem> postsTitles;
	private static int postsHeight;

	static final short[] NOTES_TEXT = Font.toArray("Заметки");
	static ArrayList<TitleItem> notesTitles;
	private static int notesHeight;

	private static int notesOffset;
	private static int totalHeight;

	private static final short[] NOTHING_WAS_FOUND = Font.toArray("По запросу ничего не было найдено");

	private static int focusedItem;
	private static int hoveredItem;
	private static boolean focusedNote;
	private static boolean hoveredNote;

	private static BufferedImage titleSurface;
	private static Graphics titleGraphics;
	
	private static ScrollablePage page;
	public static final String WINDOW_TITLE = "Идеи";

	private static LineEditField searchField;
	
	private static Sidebar sidebar;

	private static String filteredLastTime;
	static final int MAX_NUMBER_WIDTH;

	private static final int MIN_WIDTH = 512;
	private static final int MIN_HEIGHT = 256;
	
	static {
		int max = 0;
		for(byte i = 0; i != 10; i++)
			max = Math.max(max, Font.FONTS_WIDTH[Font.toArray(""+i)[0]]);
		
		MAX_NUMBER_WIDTH = max;
	}
		
	static void think() {
		synchronized (LOCK) {
			if(needToPopup) {
				needToPopup = false;
				
				if(frame == null) {
					focusedItem = -1;
					focusedNote = false;
					
					Ideas.ideaViewers = new ArrayList<IdeaViewer>();
					
					frame = new FrameAbstraction(MIN_WIDTH, MIN_HEIGHT, true, WINDOW_TITLE);
					FOCUSABLE_ITEM = new Focusable(frame);
					
					searchField = new LineEditField(Renderer.C_ICON_OFFSET, 0, frame.width() - Renderer.C_ICON_OFFSET, Renderer.C_ICON_SIZE, Colors.HINT, "", DefaultValidator.BLOCK_NEWLINE, frame, true);
					searchField.grabKeyboardFocus();
					
					page = new ScrollablePage(Renderer.C_ICON_OFFSET, 2*Renderer.C_ICON_SIZE, frame.width()-Renderer.C_ICON_OFFSET, frame.height()-2*Renderer.C_ICON_SIZE, 1, frame);
					
					loadTitles("");
	
					calculateSelection();
							
					titleSurface = Assist.getOptimizedImage(page.getPageWidth(), Renderer.C_ICON_SIZE, Transparency.OPAQUE);
					titleGraphics = titleSurface.getGraphics();
					
					sidebar = new Sidebar(frame, Renderer.T_ADD);
					// TODO: sidebar.think(true, false);
				} else {
					frame.requestFocus();
				}
			}
		}
		
		if(frame == null)
			return;
		
		frame.iterateNewActions();
		
		if(frame.isClosing() && Ideas.ideaViewers.isEmpty()) {
			closeWindow();
			return;
		}
		
		if(frame.wasResized()) {
			searchField.setSizes(frame.width()-Renderer.C_ICON_OFFSET, Renderer.C_ICON_SIZE);
			
			checkViewportSize();
		}
		searchField.think();
		
		page.think();
		
		sidebar.think();
		
		if(frame.ENTER.pressed())
			loadTitles(searchField.getText());

		thinkSelection();
	}
	
	private static final void thinkSelection() {
		calculateSelection();
		
		if(frame.MOUSE_L.pressed() && hoveredItem != -1 && frame.canGrabMouseFocus()) {
			focusedItem = hoveredItem;
			focusedNote = hoveredNote;
			frame.grabMouseFocus(FOCUSABLE_ITEM);
		}
		
		if(frame.MOUSE_L.released() && frame.holdingMouseFocus(FOCUSABLE_ITEM)) {
			frame.releaseMouseFocus();
			if(focusedItem == hoveredItem) {
				Ideas.openPost(focusedNote?notesTitles.get(focusedItem).number:postsTitles.get(focusedItem).number);
			}
			focusedItem = -1;
		}
	}
	
	private static void calculateSelection() {
		hoveredItem = -1;
		
		if(page.isMouseInsideViewPortArea() && (frame.canGrabMouseFocus() || frame.holdingMouseFocus(FOCUSABLE_ITEM))) {
			int y = page.getViewportOffset()+frame.mouseY()-page.getY();
			hoveredNote = y>=notesOffset;
			if(hoveredNote) {
				if(y >= notesOffset &&
					(y-notesOffset) % Renderer.C_ICON_OFFSET < Renderer.C_ICON_SIZE)
					hoveredItem = (y-notesOffset)/Renderer.C_ICON_OFFSET;
					
				if(hoveredItem >= notesTitles.size())
					hoveredItem = -1;
			} else {
				if(y % Renderer.C_ICON_OFFSET < Renderer.C_ICON_SIZE)
					hoveredItem = y/Renderer.C_ICON_OFFSET;
				if(hoveredItem >= postsTitles.size())
					hoveredItem = -1;
			}
		}
	}
	
	static void render() {
		page.render();
		
		final int postsTextOffset = postsTitles.isEmpty()?
			-Renderer.C_ICON_SIZE:
			Math.min(Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE+postsHeight - page.getViewportOffset());
		
		final int notesTextOffset = notesTitles.isEmpty()?
			-Renderer.C_ICON_SIZE:
			Math.max(Renderer.C_ICON_SIZE, Renderer.C_ICON_SIZE+notesOffset - page.getViewportOffset());

		final int postsBeg = Assist.limit(0, (page.getViewportOffset()+Renderer.C_MARGIN)/Renderer.C_ICON_OFFSET, postsTitles.size());
		final int postsEnd = Assist.limit(0, (page.getViewportOffset()+page.getHeight()-1)/Renderer.C_ICON_OFFSET+1, postsTitles.size());

		final int notesBeg = Assist.limit(0, (page.getViewportOffset()-notesOffset+Renderer.C_MARGIN)/Renderer.C_ICON_OFFSET, notesTitles.size());
		final int notesEnd = Assist.limit(0, (page.getViewportOffset()-notesOffset+page.getHeight()-1)/Renderer.C_ICON_OFFSET+1, notesTitles.size());
		
		for(int i = postsBeg; i != postsEnd; i++)
			postsTitles.get(i).render(2*Renderer.C_ICON_SIZE+i*(Renderer.C_MARGIN+Renderer.C_ICON_SIZE)-page.getViewportOffset(), titleSurface, titleGraphics, (i == hoveredItem && !hoveredNote) && (focusedItem == -1 || (i == focusedItem && !focusedNote)));
		
		for(int i = notesBeg; i != notesEnd; i++)
			notesTitles.get(i).render(2*Renderer.C_ICON_SIZE+notesOffset+i*(Renderer.C_MARGIN+Renderer.C_ICON_SIZE)-page.getViewportOffset(), titleSurface, titleGraphics, (i == hoveredItem && hoveredNote) && (focusedItem == -1 || (i == focusedItem && focusedNote)));
		
		frame.graphics().setColor(Colors.BLACK);
		
		frame.graphics().fillRect(Renderer.C_ICON_OFFSET, postsTextOffset, page.getPageWidth(), Renderer.C_ICON_SIZE);
		Renderer.drawLine(POSTS_TEXT, Renderer.C_ICON_OFFSET+(frame.width() - Renderer.C_ICON_OFFSET - Font.sizeLine(POSTS_TEXT, 1))/2, postsTextOffset+Renderer.C_ICON_PADDING, 1, Font.WHITE, frame.graphics());
		frame.graphics().fillRect(Renderer.C_ICON_OFFSET, notesTextOffset, page.getPageWidth(), Renderer.C_ICON_SIZE);
		Renderer.drawLine(NOTES_TEXT, Renderer.C_ICON_OFFSET+(frame.width() - Renderer.C_ICON_OFFSET - Font.sizeLine(NOTES_TEXT, 1))/2, notesTextOffset+Renderer.C_ICON_PADDING, 1, Font.WHITE, frame.graphics());

		if(postsTitles.isEmpty() && notesTitles.isEmpty()) {
			Renderer.drawLine(NOTHING_WAS_FOUND, Renderer.C_ICON_OFFSET+(frame.width() - Renderer.C_ICON_OFFSET - Font.sizeLine(NOTHING_WAS_FOUND, 1))/2, Renderer.C_ICON_SIZE+Renderer.C_ICON_PADDING, 1, Font.WHITE, frame.graphics());
		}
		
		searchField.render();
		
		sidebar.render();
		
		frame.postActions();
	}
	
	private static void checkViewportSize() {
		page.setSizes(frame.width()-Renderer.C_ICON_OFFSET, frame.height()-2*Renderer.C_ICON_SIZE);
		titleSurface = Assist.getOptimizedImage(page.getPageWidth(), Renderer.C_ICON_SIZE, Transparency.OPAQUE);
		titleGraphics = titleSurface.getGraphics();
	}
	
	private static void loadTitles(final String filter) {
		filteredLastTime = filter.toLowerCase();
		
		postsTitles = new ArrayList<TitleItem>();
		notesTitles = new ArrayList<TitleItem>();
		
		final DataInputStream dis;
		try {
			dis = new DataInputStream(Assist.getDatStream(Ideas.IDEAS_PATH));
		} catch (FileTypeException e) {
			// TODO: Создать функцию для создания идентичных сообщений об ошибках
			Assist.abort("Файл для хранения идей не является файлом. Возможно он является директорией.\nИсправьте проблему и вернитесь.\n"+e.path);
			return;
		} catch (FileDoNotExistException e) {
			Assist.abort("Файл для хранения идей не найден.\nИсправьте проблему и вернитесь.\n"+e.path);
			return;
		}
				
		try {
			Ideas.postsAmount = dis.readInt();
			Ideas.notesAmount = dis.readInt();

			Ideas.longestNumberLength = (""+(Ideas.postsAmount+Ideas.notesAmount)).length();
			Ideas.numberPreset = "";
			for(int i = Ideas.longestNumberLength; i != 1; i--)
				Ideas.numberPreset += "0";
			
			int number = 1;
			
			for(byte pn = 0; pn != 2; pn++) {
				final ArrayList<TitleItem> container = (pn==0?postsTitles:notesTitles);
				for(int i = (pn==0?Ideas.postsAmount:Ideas.notesAmount); i!= 0; i--) {
					final TitleItem item = filterIdea(dis, number);
					if(item != null)
						container.add(item);
					number++;
				}
			}
			
			Assist.tryToCloseInput(dis);
		} catch (IOException e) {
			Assist.tryToCloseInput(dis);
			Assist.abort("Ошибка чтения файла хранящего идеи.\nИсправьте проблему и вернитесь.\n"+Assist.getDatPath(Ideas.IDEAS_PATH));
			return;
		}

		if(postsTitles.isEmpty())
			postsHeight = 0;
		else
			postsHeight = postsTitles.size()*(Renderer.C_MARGIN+Renderer.C_ICON_SIZE)-Renderer.C_MARGIN;
		
		if(notesTitles.isEmpty())
			notesHeight = 0;
		else
			notesHeight = notesTitles.size()*(Renderer.C_MARGIN+Renderer.C_ICON_SIZE)-Renderer.C_MARGIN;
		
		totalHeight = postsHeight + notesHeight;
		
		if(notesHeight != 0 && postsHeight != 0)
			totalHeight += Renderer.C_ICON_SIZE;
		
		notesOffset = totalHeight - notesHeight;
				
		Ideas.numberRectangleWidth = 2*Renderer.C_MARGIN+Ideas.longestNumberLength*(MAX_NUMBER_WIDTH+1)-1;

		page.setPageHeight(totalHeight);
		page.scrollTo(0);
		
		checkViewportSize();
	}
	private static TitleItem filterIdea(final DataInputStream dis, final int number) throws IOException {
		if(filteredLastTime.equals(""))
			return new TitleItem(number, Ideas.getOnlyTitle(dis), false);

		boolean filteredTitle = false;
		boolean filteredInside = false;
		String name = Assist.readString(dis); // title
		
		if(name.toLowerCase().contains(filteredLastTime))
			filteredTitle = true;

		if(filterString(dis)) // text
			filteredInside = true;
		if(filterString(dis)) // todo
			filteredInside = true;
		
		for(int i = dis.readInt(); i != 0; i--)
			dis.readInt(); // skip anchors

		if(filterStrings(dis)) // images
			filteredInside = true;
		if(filterStrings(dis)) // audios
			filteredInside = true;
		if(filterStrings(dis)) // videos
			filteredInside = true;
		if(filterStrings(dis)) // links
			filteredInside = true;
		if(filterStrings(dis)) // files
			filteredInside = true;
		
		if(filteredTitle || filteredInside)
			return new TitleItem(number, name, !filteredTitle);
		
		return null;
	}
	private static boolean filterString(final DataInputStream dis) throws IOException {
		return Assist.readString(dis).toLowerCase().contains(filteredLastTime);
	}
	private static boolean filterStrings(final DataInputStream dis) throws IOException {
		boolean filtered = false;
		
		for(int i = dis.readInt(); i != 0; i--)
			if(filterString(dis))
				filtered = true;
			
		return filtered;
	}

	private static void closeWindow() {
		if(frame != null) {
			frame.destroy();
			frame = null;
		}
		postsTitles = null;
		notesTitles = null;
		titleSurface = null;
		titleGraphics = null;
		page = null;
		filteredLastTime = null;
		FOCUSABLE_ITEM = FOCUSABLE_ITEM.dispose();
		Ideas.unload();
	}

	public static void openWindow() {
		synchronized (LOCK) {
			needToPopup = true;
		}
	}
}