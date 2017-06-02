package q2p.violet34desktop.windows.async;

import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.windows.async.ImageRequester.Status;

public final class AsynchronusImageLoader implements Runnable {
	private static final ArrayList<ImagePool> queue = new ArrayList<ImagePool>();
	private static int cursor = 0;
	private static final AsynchronusImageLoader instance = new AsynchronusImageLoader();
	private static final Thread thread = new Thread(instance);
	
	static {
		thread.start();
	}
	
	public static final void destroy() {
		thread.interrupt();
		try {
			thread.join();
		} catch(InterruptedException e) {}
	}
	
	public static final ImageRequester request(final String path) {
		synchronized(queue) {
			for(final ImagePool pool : queue) {
				if(pool.path.equals(path) && pool.size == -1) {
					return new ImageRequester(pool);
				}
			}
			queue.add(new ImagePool(path, -1));
			final ImageRequester ret = new ImageRequester(queue.get(queue.size()-1));
			queue.notify();
			return ret;
		}
	}
	
	public static final ImageRequester request(final String path, final int size) {
		if(size < 1)
			throw new IllegalArgumentException();
	
		synchronized(queue) {
			for(final ImagePool pool : queue) {
				if(pool.path.equals(path) && pool.size == size) {
					return new ImageRequester(pool);
				}
			}
			queue.add(new ImagePool(path, size));
			final ImageRequester ret = new ImageRequester(queue.get(queue.size()-1));
			queue.notify();
			return ret;
		}
	}

	public final void run() {
		ImagePool currentPool = null;
		BufferedImage tempImage = null;
		final int[] newSizes = new int[2];
		while (!Thread.interrupted()) {
			synchronized(queue) {
				try {
					while(cursor == queue.size())
						queue.wait();
				} catch (final InterruptedException e) {
					return;
				}
				currentPool = queue.get(cursor++);
			}
			
			if(new File(currentPool.path).exists()) {
				try {
					tempImage = ImageIO.read(new File(currentPool.path));
					
					if(currentPool.size == -1) {
						currentPool.image = tempImage;
					} else {
						Assist.getScaledSizes(tempImage.getWidth(), tempImage.getHeight(), currentPool.size, newSizes);
						
						currentPool.image = Assist.getOptimizedImage(newSizes[0], newSizes[1], Transparency.TRANSLUCENT);
						currentPool.image.getGraphics().clearRect(0, 0, newSizes[0], newSizes[1]);
						currentPool.image.getGraphics().drawImage(tempImage.getScaledInstance(newSizes[0], newSizes[1], Image.SCALE_SMOOTH), 0, 0, null);
					}
					currentPool.image.getGraphics().dispose();
					
					tempImage = null;
					
					synchronized(currentPool) {
						currentPool.status = Status.LOADED;
					}
				} catch (final Exception e) {
					synchronized(currentPool) {
						currentPool.corruptedRequest = true;
						currentPool.status = Status.FAILED_TO_LOAD;
						currentPool.image = null;
					}
				}
			} else {
				synchronized(currentPool) {
					currentPool.corruptedRequest = true;
					currentPool.status = Status.NOT_FOUND;
				}
			}
			if(currentPool.corruptedRequest)
				remove(currentPool);
			currentPool = null;
			tempImage = null;
		}
	}
	
	static final void remove(final ImagePool pool) {
		synchronized(queue) {
			int idx = queue.indexOf(pool);
			
			if(queue.remove(pool) && idx < cursor)
				cursor--;
		}
	}
}