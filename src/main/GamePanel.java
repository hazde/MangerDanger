package main;

import gamestate.GameStateManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("unused")
public class GamePanel extends JPanel implements Runnable, KeyListener {
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 320;
	public static final int HEIGHT = 240;
	public static final int SCALE = 4;
	public static final Dimension GAME_DIM = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
	

	private Thread thread;
	private boolean running;
	private int frames = 0;
	private int ticks = 0;
	private int fps = 0;

	private BufferedImage image;
	private Graphics2D g;
	
	private Sound soundPlayer;
	
	private GameStateManager manager;
	
	public GamePanel() {
		super();
		setMinimumSize(GAME_DIM);
		setMaximumSize(GAME_DIM);
		setPreferredSize(GAME_DIM);
		setFocusable(true);
		requestFocus();
		
	}
	
	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}
	
	private void init() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		running = true;
		manager = new GameStateManager();
		soundPlayer = new Sound();
	}

	public void run() {
		
		
		long lastTime = System.nanoTime();
		double unprocessed = 0;
		double nsPerTick = 1000000000.0 / 100;
		
		long lastTimer1 = System.currentTimeMillis();
		
		init();	
		
		Sound.music2.play(true);
		
		while(running) {
			
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = false;
//			System.out.println(unprocessed);
			while (unprocessed >= 1) {
				ticks++;
				update();
				unprocessed -= 1;
				shouldRender = true;
			}
			
//			update();
			if (shouldRender) {
			frames++;
			draw();
			drawToScreen();
			}
			
			try {
				Thread.sleep(4);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (System.currentTimeMillis() - lastTimer1 >= 1000) {
				lastTimer1 += 1000;
//				System.out.println(ticks + " ticks, " + frames + " fps");
				fps = frames;
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	
	
	private void update() {
		manager.update();
	}
	
	private void draw() {
		manager.draw(g);
		
		// fpsutskrift
		g.setFont(new Font("Calibri", Font.PLAIN, 12));
		g.setColor(Color.yellow);
		String strFps = fps + " fps";
		g.drawString(strFps, GamePanel.WIDTH - strFps.length() * 6, HEIGHT - 5);
		
	}
	
	private void drawToScreen() {
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g2.dispose();
	}

	public void keyPressed(KeyEvent key) {
		manager.keyPressed(key.getKeyCode());
	}

	public void keyReleased(KeyEvent key) {
		manager.keyReleased(key.getKeyCode());
	}

	public void keyTyped(KeyEvent arg0) {

	}
	
}
