package main;

public class FPSCounter {
	private long lastTimeChecked;
	private int frames;
	private int fps;
	
	public FPSCounter() {
	}
	
	public void initialize() {
		lastTimeChecked = System.nanoTime();
		frames = 0;
		fps = 0;
	}
	
	public void calculateFPS() {
		frames++;
		if (System.nanoTime() - lastTimeChecked >= 1000000000L) {
			fps = frames;
			frames = 0;
			lastTimeChecked = System.nanoTime();
		}
	}
	
	public int getFPS() {
		return fps;
	}
	
}