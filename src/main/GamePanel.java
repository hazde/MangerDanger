package main;

import gamestate.GameStateManager;
import handlers.KeyHandler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
//	public static final int WIDTH = 320;
//	public static final int HEIGHT = 240;
	public static final int SCALE = 2;
	public static final Dimension GAME_DIM = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
	public static final String NAME =  "Manger Danger";
	public JFrame window;

	private Thread thread;
	private boolean running;
	private int frames = 0;
	private int ticks = 0;
	private int fps = 0;
	private int tickCount = 0;
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
		init();
		if (thread == null) {
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}
	
	private void init() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
//		g.setColor(Color.BLACK);
//		g.fillRect(0, 0, getWindowWidth(), getWindowHeight());
//		g.setColor(Color.WHITE);
//		g.drawString("Loading...", getWindowWidth() / 2 - 20, getWindowHeight() / 2);
		running = true;
		manager = new GameStateManager(this);
	}

	public void run() {
		
//		init();	
		
		long lastTime = System.nanoTime();
		double unprocessed = 0;
		double nsPerTick = 1000000000.0 / 100;
		
		long lastTimer1 = System.currentTimeMillis();
		
		while(running) {
			
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
//			System.out.println(unprocessed);
			while (unprocessed >= 1) {
				ticks++;
				update();
				unprocessed -= 1;
				shouldRender = false;
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
				tickCount = ticks;
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	
	
	private void update() {
		manager.update();
		KeyHandler.update();
	}
	
	private void draw() {
		manager.draw(g);
		
		// fpsutskrift
		FontMetrics fm = g.getFontMetrics();
		String strFps = fps + " fps";
		Rectangle2D r = fm.getStringBounds(strFps, g);
		g.setFont(new Font("Calibri", Font.PLAIN, 12));
		g.setColor(Color.yellow);
		
		g.drawString(strFps, getWindowWidth() - (int) r.getWidth() - 10, getWindowHeight() - (int) r.getHeight());
		String strTicks = "Ticks: " + tickCount;
		r = fm.getStringBounds(strTicks, g);
		g.drawString(strTicks, getWindowWidth() - (int) r.getWidth() - 10, getWindowHeight() - (int) r.getHeight() - 10);
		
	}
	
	private void drawToScreen() {
		Graphics g2 = window.getGraphics();
		g2.drawImage(image, 0, 0, getWindowWidth() * scale, getWindowHeight() * scale, null);
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
			image = new BufferedImage(actualWidth / scale, actualHeight / scale, BufferedImage.TYPE_INT_RGB);
			g = (Graphics2D) image.getGraphics();
			System.out.println(this.getWidth() + " " + this.getHeight() + ", " + window.getWidth() + " " + window.getHeight());
		} else {
			window.setExtendedState(JFrame.NORMAL);
			fullscreen = false;
			actualWidth = WIDTH * scale;
			actualHeight = HEIGHT * scale;
			this.setSize(WIDTH * scale, HEIGHT * scale);
			window.setSize(WIDTH * scale, HEIGHT * scale);
			image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
			g = (Graphics2D) image.getGraphics();
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
		if (actualWidth == WIDTH) {
			return WIDTH;
		} else {
			return actualWidth / scale;
		}
	}
	
	public int getWindowHeight() {
		if (actualHeight == HEIGHT) {
			return HEIGHT;
		} else {
			return actualHeight / scale;
		}
	}
	
	public void keyPressed(KeyEvent key) {
		KeyHandler.keySet(key.getKeyCode(), true);
	}

	public void keyReleased(KeyEvent key) {
		KeyHandler.keySet(key.getKeyCode(), false);
	}

	public void keyTyped(KeyEvent arg0) {

	}
	
}
