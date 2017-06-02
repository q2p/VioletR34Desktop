package q2p.violet34desktop.windows.async;

import java.awt.image.BufferedImage;

public final class ImageRequester {
	ImagePool pool;
	Status status = null;
	BufferedImage image = null;
	public static enum Status {
		NOT_FOUND, FAILED_TO_LOAD, IN_PROGRESS, LOADED
	};
	
	ImageRequester(final ImagePool pool) {
		this.pool = pool;
		pool.request(this);
	}
	
	public final void dispose() {
		if(pool != null)
			pool.unlink(this);
	}
	
	public final BufferedImage result() {
		requestStatus();
		return image;
	}
	
	public final Status status() {
		requestStatus();
		return status;
	}
	
	private final void requestStatus() {
		if(status == null || status == Status.IN_PROGRESS)
			pool.flashStatus(this);
	}
}