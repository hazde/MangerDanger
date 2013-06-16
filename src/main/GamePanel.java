package main;

import gamestate.GameStateManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("unused")
public class GamePanel extends JPanel implements Runnable, KeyListener {
	private static final long serialVersionUID = 1L;
	public static int GetScreenWorkingWidth() {
	    return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}
	
	public static int GetScreenWorkingHeight() {
	    return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}
	
//	public static final int WIDTH = 540;
//	public static final int HEIGHT = 360;
	public static final int WIDTH = 640;
	public static final int HEIGHT = 400;
	public static final int SCALE = 2;
	public static final Dimension GAME_DIM = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
	public static final String NAME =  "Manger Danger";
	public JFrame window;

	private Thread thread;
	private boolean running;
	private int frames = 0;
	private int ticks = 0;
	private int fps = 0;
	private int scale = 2;
	public int actualWidth = 640;
	public int actualHeight = 400;
	public boolean fullscreen = false;

	private BufferedImage image;
	private Graphics2D g;
	
	private GameStateManager manager;
	
	public GamePanel() {
		super();
		setMinimumSize(GAME_DIM);
		setPreferredSize(GAME_DIM);
		setFocusable(true);
		requestFocus();
		
	}
	
	public void showWindow() {
		window = new JFrame(NAME);
		window.setContentPane(this);
		window.setUndecorated(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
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
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(Color.WHITE);
		g.drawString("LOADING...", WIDTH / 2 - 50, HEIGHT / 2);
		running = true;
		manager = new GameStateManager(this);
	}

	public void run() {
		
		
		long lastTime = System.nanoTime();
		double unprocessed = 0;
		double nsPerTick = 1000000000.0 / 100;
		
		long lastTimer1 = System.currentTimeMillis();
		
		init();	
		
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
				Thread.sleep(1);
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
		g2.drawImage(image, 0, 0, WIDTH * scale, HEIGHT * scale, null);
		g2.dispose();
	}
	
	public void setScale(int scale) {
		this.scale = scale;
		
		if (scale > 2) {
			fullscreen = true;
			actualWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			actualHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			this.setSize(actualWidth, actualHeight);
			window.setSize(actualWidth, actualHeight);
			window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			window.setExtendedState(JFrame.NORMAL);
			fullscreen = false;
			actualWidth = WIDTH * scale;
			actualHeight = HEIGHT * scale;
			this.setSize(WIDTH * scale, HEIGHT * scale);
			window.setSize(WIDTH * scale, HEIGHT * scale);
		}
		window.setLocationRelativeTo(null);
	}
	
	public boolean getFullscreen() {
		return fullscreen;
	}
	
	public int getScale() {
		return scale;
	}

	public int getWindowWidth() {
		if (actualWidth == WIDTH * scale) {
			return WIDTH;
		} else {
			return actualWidth / scale;
		}
	}
	
	public int getWindowHeight() {
		if (actualHeight == HEIGHT * scale) {
			return HEIGHT;
		} else {
			return actualHeight / scale;
		}
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
