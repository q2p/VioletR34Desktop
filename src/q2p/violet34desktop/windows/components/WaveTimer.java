package q2p.violet34desktop.windows.components;

public class WaveTimer {
	private static final int PIXELS_PER_SECOUND = 64;
	private int offset;
	public int getOffset() {
		return offset;
	}
	private int tpos;
	private int diffNeeded;
	private int freeSpace;
	private int length;
	public void setLength(final int length) {
		this.length = length;
		validateOffset();
	}
	private int width;
	public void setWidth(final int width) {
		this.width = width;
		validateOffset();
	}
	private void validateOffset() {
		freeSpace = length - width;
		lineSize = PIXELS_PER_SECOUND/2+freeSpace;
		diffNeeded = 2*(freeSpace+PIXELS_PER_SECOUND);
		tpos = Math.min(diffNeeded, tpos);
	}
	private long lastTick;
	private int lineSize;
	
	public WaveTimer(final int length, final int width) {
		this.length = length;
		this.width = width;
		reset();
		setLength(length);
		setWidth(width);
	}
	
	public void reset() {
		offset = 0;
		tpos = 0;
		lastTick = System.currentTimeMillis();
	}
	
	public void tick() {
		if(freeSpace > 0) {
			long diff = (System.currentTimeMillis() - lastTick)*PIXELS_PER_SECOUND/1000;
			if(diff != 0) {
				lastTick = System.currentTimeMillis();
			
				if(tpos + diff >= diffNeeded) {
					diff -= diffNeeded-tpos;
					tpos = (int)(diff % diffNeeded);
				} else {
					tpos += diff;
				}
			}
		}
				
		offset = Math.max(0, Math.min(-Math.abs(tpos-lineSize)+lineSize, freeSpace));
	}
}