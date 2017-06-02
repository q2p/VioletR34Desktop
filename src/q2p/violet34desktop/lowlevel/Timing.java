package q2p.violet34desktop.lowlevel;

public class Timing {
	private static final short milisecPerFrame = 1000/100;
	public static float delta;
	private static long lastTime;
	// TODO: При компиляции убирать бенчмарк
	private static long lastFPS;
	public static int currentFPS;
	
	static void sleep() {
		long toSleep = lastTime+milisecPerFrame-System.currentTimeMillis();
		
		if(toSleep > 1) {
			try {
				Thread.sleep(toSleep);
			} catch (InterruptedException e) {}
		}
		
		delta = (float)(System.currentTimeMillis()-lastTime)/1000f;
		
		if(delta > 2) delta = 2;
		
		lastTime += milisecPerFrame;
		
		if(System.currentTimeMillis() - lastTime > 250) lastTime = System.currentTimeMillis();
				
		lastFPS = (System.currentTimeMillis() - lastFPS);
		if(lastFPS == 0) lastFPS = 1000;
		currentFPS = (int)(1000/lastFPS);
		// TODO:
	// TODO:System.out.println(currentFPS);
		lastFPS = System.currentTimeMillis();
	}
	
	static void init() {
		lastTime = System.currentTimeMillis();
		lastFPS = System.currentTimeMillis();
	}
}
