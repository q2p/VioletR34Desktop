package q2p.violet34desktop.windows.async;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import q2p.violet34desktop.windows.async.ImageRequester.Status;

final class ImagePool {
	BufferedImage image = null;
	final int size;
	Status status = ImageRequester.Status.IN_PROGRESS;
	boolean corruptedRequest = false;
	final String path;

	private final LinkedList<ImageRequester> imageRequesters = new LinkedList<ImageRequester>();

	ImagePool(final String path, final int size) {
		this.path = path;
		this.size = size;
	}
	
	final void request(final ImageRequester imageRequester) {
		synchronized(this) {
			imageRequesters.addLast(imageRequester);
		}
	}

	void unlink(final ImageRequester imageRequester) {
		synchronized(this) {
			imageRequester.pool = null;
			imageRequesters.remove(imageRequester);
			if(imageRequesters.isEmpty() && !corruptedRequest)
				AsynchronusImageLoader.remove(this);
		}
	}

	void flashStatus(final ImageRequester imageRequester) {
		synchronized(this) {
			switch(status) {
				case IN_PROGRESS: break;
				case LOADED:
					imageRequester.image = image;
					break;
				case FAILED_TO_LOAD:
				case NOT_FOUND:
					unlink(imageRequester);
					break;
			}
			imageRequester.status = status;
		}
	}
}