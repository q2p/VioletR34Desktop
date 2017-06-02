package q2p.violet34desktop.windows.components;

import java.awt.Graphics;
import q2p.violet34desktop.Colors;

public final class SquareLoading {
	private static final int SLEEP = 500;
	private static final int MOVE = 2000;
	private static final byte SIZE = 24;
	
	public static final void render(int x, int y, int diameter, final Graphics graphics) {
		int pos = (int)(System.currentTimeMillis()%(SLEEP+MOVE));
		
		x -= SIZE/2;
		y -= SIZE/2;
		graphics.setColor(Colors.WHITE);
		diameter = Math.min(diameter/2, 128)-(32+SIZE)/2;
		if(pos < SLEEP)
			pos = SLEEP;
		
		pos = (int)(diameter * Math.sin(Math.toRadians(180*(pos-SLEEP)/MOVE)));
		graphics.fillRect(x+pos+SIZE/2, y, SIZE/2, SIZE);
		graphics.fillRect(x-pos, y, SIZE/2, SIZE);
	}
}
