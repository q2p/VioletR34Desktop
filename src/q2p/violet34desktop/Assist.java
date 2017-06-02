package q2p.violet34desktop;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import q2p.violet34desktop.events.FrameAbstraction;
import q2p.violet34desktop.lowlevel.Tray;
import q2p.violet34desktop.windows.async.AsynchronusImageLoader;

public final class Assist {
	public static final String MAIN_FOLDER; // .../p/
	public static final String DATA_FOLDER; // .../p/VioletR34/data/
	
	static {
		// TODO: при компиляции ставить нормальный путь
		// final String path = normalizeURL(new File("test.file").getAbsolutePath());
		// MAIN_FOLDER = path.substring(0, path.indexOf("/p/"+3));
		MAIN_FOLDER = "D:/@MyFolder/p/";
		DATA_FOLDER = MAIN_FOLDER + "VioletR34/data/";
	}
	
	private static final Random random = new Random();
	
	public static final InputStream getResourceStream(final String path) throws FileNotFoundException {
		final InputStream inputStream = Assist.class.getClassLoader().getResourceAsStream("res/"+path);
		if(inputStream == null) throw new FileNotFoundException();
		return inputStream;
	}
	
	public static final void abort(final String message) {
		destroyEverything();
		
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
			
		System.exit(1);
	}
	
	public static final int random(final int bounds) {
		return random.nextInt(bounds);
	}

	public static final BufferedImage loadImage(final String path, final int transparency) {
		final BufferedImage image;
		try {
			image = ImageIO.read(getResourceStream(path));
		} catch (Exception e) {
			abort("Не удалось загрузить изображение \""+path+"\". Возможно архив был повреждён.");
			return null;
		}
		final BufferedImage optimizedImage = getOptimizedImage(image.getWidth(), image.getHeight(), transparency);
		optimizedImage.getGraphics().drawImage(image, 0, 0, null);
		optimizedImage.getGraphics().dispose();
		
		return optimizedImage;
	}
		
	public static final FileOutputStream getTwrStream(final String name) throws FileTypeException, FailedToCreateFileException {
		File twr = new File(getTwrPath(name));
		try {
			if(!twr.exists()) twr.createNewFile();
		} catch(IOException e) {
			throw new FailedToCreateFileException(getTwrPath(name));
		}
		if(!twr.isFile()) throw new FileTypeException(getTwrPath(name));
		try {
			return new FileOutputStream(twr);
		} catch (FileNotFoundException e) {
			throw new FailedToCreateFileException(getTwrPath(name));
		}
	}
	public static final String getTwrPath(String name) {
		return DATA_FOLDER+name+".twr";
	}
	public static final FileInputStream getDatStream(final String name) throws FileTypeException, FileDoNotExistException {
		File dat = new File(getDatPath(name));
		if(dat.exists() && !dat.isFile())
			throw new FileTypeException(getDatPath(name));
		try {
			return new FileInputStream(dat);
		} catch (FileNotFoundException e) {
			throw new FileDoNotExistException(getDatPath(name));
		}
	}
	public static final String getDatPath(final String name) {
		return DATA_FOLDER+name+".dat";
	}
	public static final void swapTwrAndDatFiles(final String name) throws IOException {
		final File twr = new File(DATA_FOLDER+name+".twr");
		final File dat = new File(DATA_FOLDER+name+".dat");
		if(!twr.exists() || !twr.isFile()) throw new IOException();
		
		if(!dat.exists()) dat.createNewFile();
		if(!dat.isFile()) throw new IOException();
		
		final FileInputStream twrStream = new FileInputStream(twr);
		final FileOutputStream datStream;
		try {
			datStream = new FileOutputStream(dat);
		} catch (FileNotFoundException e) {
			Assist.tryToCloseInput(twrStream);
			throw new IOException();
		}
		try {
			datStream.getChannel().transferFrom(twrStream.getChannel(), 0, twrStream.getChannel().size());
			datStream.flush();
		} catch (IOException e) {
			Assist.tryToClose(twrStream, datStream);
			throw new IOException();
		}
		Assist.tryToClose(twrStream, datStream);
		
		if(twr.exists() && !twr.delete()) throw new IOException();
	}

	public static final String normalizeURL(final String URL) {
		return URL.replace("\\", "/");
	}
	public static final String normalizePath(String path, final boolean isDirectory) {
		path = normalizeURL(path);
		if(isDirectory && !path.endsWith("/"))
			return path+"/";
		if(!isDirectory && path.endsWith("/"))
			return path.substring(0, path.length()-1);
		return path;
	}

	public static final String readString(final DataInputStream dis) throws IOException {
		final byte[] buff = new byte[dis.readInt()];
		dis.read(buff);
		return new String(buff, StandardCharsets.UTF_8);
	}
	public static final void readStrings(final DataInputStream dis, final List<String> container) throws IOException {
		for(int i = dis.readInt(); i != 0; i--)
			container.add(readString(dis));
	}
	public static final void writeString(final DataOutputStream dos, final String string) throws IOException {
		final byte[] buff = string.getBytes(StandardCharsets.UTF_8);
		dos.writeInt(buff.length);
		dos.write(buff);
	}
	public static final void writeStrings(final DataOutputStream dos, final List<String> strings) throws IOException {
		dos.writeInt(strings.size());
		for(final String s : strings)
			writeString(dos, s);
	}
	public static final void copyStrings(final DataInputStream dis, final DataOutputStream dos) throws IOException {
		int i = dis.readInt();
		dos.writeInt(i);
		for(;i != 0; i--)
			writeString(dos, readString(dis));
	}
	public static final int sizeString(final String string) {
		return Integer.BYTES + string.getBytes(StandardCharsets.UTF_8).length;
	}
	public static final int sizeStrings(final List<String> strings) {
		int ret = Integer.BYTES;
		for(final String s : strings)
			ret += sizeString(s);
		return ret;
	}
	public static final void skipString(final DataInputStream dis) throws IOException {
		dis.skipBytes(dis.readInt());
	}
	public static final void skipStrings(final DataInputStream dis) throws IOException {
		for(int i = dis.readInt(); i != 0; i--)
			skipString(dis);
	}

	public static final void tryToClose(final InputStream is, final OutputStream os) {
		tryToCloseInput(is);
		tryToCloseOutput(os);
	}
	public static final void tryToCloseInput(final InputStream is) {
		try { if(is != null) is.close(); }
		catch (IOException e) {}
	}
	public static final void tryToCloseOutput(final OutputStream os) {
		try { if(os != null) os.close(); }
		catch (IOException e) {}
	}

	public static final String decline(final int amount, final String one, final String two, final String five) {
		if (amount > 10 && ((amount % 100) / 10) == 1)
			return five;
	
		switch (amount % 10) {
			case 1:
				return one;
			case 2:
			case 3:
			case 4:
				return two;
			default: // 0, 5-9
				return five;
		}
	}

	public static final BufferedImage getOptimizedImage(final int width, final int height, final int transparency) {
		return GraphicsEnvironment.
			getLocalGraphicsEnvironment().
			getDefaultScreenDevice().
			getDefaultConfiguration().
			createCompatibleImage(
				width,
				height,
				transparency
		);
	}

	public static final int limit(int min, int value, int max) {
		return Math.max(Math.min(max, value), min);
	}
	
	public static final void getScaledSizes(final int width, final int height, int desired, final int[] out) {
		desired = Math.max(desired, 1);
		if(height > width) {
			out[1] = desired;
			out[0] = Math.max((int)(
					(float)(desired*width) /
					(float)height
				), 1);
		} else if(width > height) {
			out[0] = desired;
			out[1] = Math.max((int)(
					(float)(desired*height) /
					(float)width
				), 1);
		} else {
			out[0] = out[1] = desired;
		}
	}
	
	public static final long getGMTmilisec() {
		return Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
	}

	public static final boolean requires(final boolean current, final boolean requires) {
		return requires?current:false;
	}

	public static final boolean focusHover(final int id, final int focused, final int hovered) {
		return (id == hovered && (focused == -1 || id == focused));
	}
	
	public static final void destroyEverything() {
		Tray.destroy();
		AsynchronusImageLoader.destroy();
		FrameAbstraction.destroyAllFrames();
	}
	
	public static final void openFile(final String absolutePath) {
		try {
			Desktop.getDesktop().open(new File(absolutePath));
		} catch(final Exception e) {}
	}

	public static final int cellPointer(final int pointer, final int cellSize, final int amountOfCells, final int margin) {
		if(pointer < 0 || pointer % (cellSize+margin) >= cellSize)
			return -1;
		final int p = pointer / (cellSize+margin);
		return p >= amountOfCells ? -1 : p;
	}
	public static final int cellPointer(final int pointer, final int cellSize, final int amountOfCells) {
		if(pointer < 0)
			return -1;
		final int p = pointer / cellSize;
		return p >= amountOfCells ? -1 : p;
	}
	public static final int gridPointer(final int pointerX, final int pointerY, final int cellSizeX, final int cellSizeY, final int amountOfCells, final int cellsInLine, final int margin) {
		if(pointerX < 0 || pointerY < 0 || pointerX % (cellSizeX+margin) >= cellSizeX || pointerY % (cellSizeY+margin) >= cellSizeY)
			return -1;
		
		final int x = pointerX / (cellSizeX+margin);
		
		if(x >= cellsInLine)
			return -1;
		
		final int id = (pointerY / (cellSizeY+margin))*cellsInLine+x;
		
		if(id >= amountOfCells)
			return -1;
		
		return id;
	}
}