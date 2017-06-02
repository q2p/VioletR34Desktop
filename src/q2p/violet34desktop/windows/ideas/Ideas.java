package q2p.violet34desktop.windows.ideas;

import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.Renderer;

public class Ideas {
	/* TODO
	После перемещения/переименования поста обновить все открытые посты.
	Одновременно редактировать можно только 1 пост
	*/
	static final String IDEAS_PATH = "ideas/ideas";

	// TODO: при добавлении / удалении поста изменять эти переменные и кол-во нулей перед числами ВЕЗДЕ(дескриптор, вкладки, редакторы)
	static int numberRectangleWidth;
	
	static ArrayList<IdeaViewer> ideaViewers = null;

	public static int postsAmount;
	public static int notesAmount;
	
	static int longestNumberLength;
	static String numberPreset = null;

	private static final LinkedList<Integer> openRequests = new LinkedList<Integer>();

	static short[][] toNumber(final int number) {
		final String pre = numberPreset+number;
		short[] preNumber = Font.toArray(pre);
		short[][] ret = new short[longestNumberLength][1];
		for(int i = 0; i != longestNumberLength; i++)
			ret[i][0] = preNumber[pre.length()-(longestNumberLength-i)];
		
		return ret;
	}
	static String toStringNumber(final int number) {
		final String pre = numberPreset+number;
		return pre.substring(pre.length()-longestNumberLength);
	}
	static void renderNumber(final short[][] number, final Graphics graphics, final boolean selected, final int x, final int y) {
		graphics.setColor(selected?Colors.WHITE:Colors.HINT);
		graphics.fillRect(x, y, numberRectangleWidth, Renderer.C_ICON_SIZE);
		
		for(int i = 0; i != number.length; i++)
			Renderer.drawLine(number[i], x+Renderer.C_MARGIN+i*(Browser.MAX_NUMBER_WIDTH+1)+(Browser.MAX_NUMBER_WIDTH-Font.sizeLine(number[i], 1))/2, y+Renderer.C_ICON_PADDING, 1, selected?Font.VIOLET:Font.WHITE, graphics);
	}
	
	public static void think() {
		if(!openRequests.isEmpty()) {
			if(Browser.frame == null) {
				Browser.openWindow();
			}
		}
		Browser.think();
		if(Browser.frame != null) {
			while(!openRequests.isEmpty()) {
				int number = openRequests.removeFirst();
				for(final IdeaViewer ideaViewer : ideaViewers) {
					if(ideaViewer.number == number) {
						number = 0;
						break;
					}
				}
				if(number != 0)
					ideaViewers.add(new IdeaViewer(number, false));
			}
			for(int i = 0; i != ideaViewers.size();)
				if(ideaViewers.get(i).think())
					i++;
		}
	}
	public static void render() {
		if(Browser.frame != null) {
			Browser.render();
			
			for(final IdeaViewer ideaViewer : ideaViewers)
				ideaViewer.render();
		}
	}
	
	public static void openPost(final int number) {
		openRequests.add(number);
	}

	static void unload() {
		for(final IdeaViewer iv : ideaViewers) {
			iv.dispose();
		}
		ideaViewers = null;
		numberPreset = null;
	}
	
	// Вспомогательные функции
 	static String getOnlyTitle(final DataInputStream dis) throws IOException {
 		final String title = Assist.readString(dis);
 		
		Assist.skipString(dis); // text
		Assist.skipString(dis); // todo
		
		for(int i = dis.readInt(); i != 0; i--)
			dis.readInt();

		Assist.skipStrings(dis); // images
		Assist.skipStrings(dis); // audios
		Assist.skipStrings(dis); // videos
		Assist.skipStrings(dis); // links
		Assist.skipStrings(dis); // files
		
		return title;
 	}
 	static void skipPost(final DataInputStream dis) throws IOException {
		Assist.skipString(dis); // title
		Assist.skipString(dis); // text
		Assist.skipString(dis); // todo
		
		for(int i = dis.readInt(); i != 0; i--)
			dis.readInt(); // anchors

		Assist.skipStrings(dis); // images
		Assist.skipStrings(dis); // audios
		Assist.skipStrings(dis); // videos
		Assist.skipStrings(dis); // links
		Assist.skipStrings(dis); // files
 	}
	static void copyPost(final DataInputStream dis, final DataOutputStream dos) throws IOException {
		Assist.writeString(dos, Assist.readString(dis)); // title
		Assist.writeString(dos, Assist.readString(dis)); // text
		Assist.writeString(dos, Assist.readString(dis)); // todo
		
		int i = dis.readInt();
		dos.writeInt(i);
		for(;i != 0; i--)
			dos.writeInt(dis.readInt()); // anchors
		
		Assist.copyStrings(dis, dos); // images
		Assist.copyStrings(dis, dos); // audios
		Assist.copyStrings(dis, dos); // videos
		Assist.copyStrings(dis, dos); // links
		Assist.copyStrings(dis, dos); // files
		dos.flush();
	}
	static void copyPostAndFixAnchorsFromRemoving(final DataInputStream dis, final DataOutputStream dos, final int removedId) throws IOException {
		Assist.writeString(dos, Assist.readString(dis)); // title
		Assist.writeString(dos, Assist.readString(dis)); // text
		Assist.writeString(dos, Assist.readString(dis)); // todo
		
		final ArrayList<Integer> anchors = new ArrayList<Integer>();
		for(int i = dis.readInt(); i != 0; i--) {
			int t = dis.readInt();
			
			if(t == removedId)
				continue;
			if(t > removedId)
				anchors.add(t - 1);
			else
				anchors.add(t);
		}
		
		dos.writeInt(anchors.size());
		while(!anchors.isEmpty())
			dos.writeInt(anchors.remove(0)); // anchors
		
		Assist.copyStrings(dis, dos); // images
		Assist.copyStrings(dis, dos); // audios
		Assist.copyStrings(dis, dos); // videos
		Assist.copyStrings(dis, dos); // links
		Assist.copyStrings(dis, dos); // files
		dos.flush();
	}
	static void copyPostAndFixAnchorsFromReplacing(final DataInputStream dis, final DataOutputStream dos, final int oldId, final int newId) throws IOException {
		Assist.writeString(dos, Assist.readString(dis)); // title
		Assist.writeString(dos, Assist.readString(dis)); // text
		Assist.writeString(dos, Assist.readString(dis)); // todo
		
		int i = dis.readInt();
		dos.writeInt(i);
		for(; i != 0; i--) {
			int t = dis.readInt();
			
			if(t == oldId)
				dos.writeInt(newId);
			else if(t > newId-1 && t < oldId)
				dos.writeInt(t+1);
			else
				dos.writeInt(t);
		}
		
		Assist.copyStrings(dis, dos); // images
		Assist.copyStrings(dis, dos); // audios
		Assist.copyStrings(dis, dos); // videos
		Assist.copyStrings(dis, dos); // links
		Assist.copyStrings(dis, dos); // files
		dos.flush();
	}
}