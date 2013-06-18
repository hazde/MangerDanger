package gamestate;

import handlers.KeyHandler;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.Sound;
import tilemap.Background;

public class MenuState extends GameState {
	private Background bg;

	private static final int START = 0;
	private static final int OPTIONS = 1;
	private static final int HELP = 2;
	private static final int QUIT = 3;

	private GamePanel panel;

	private String[] options = { "New game", "Options", "Help", "Exit" };
	private String[] description = { "Enter the Dainger Zone", "\"Resolution\": ", "Heeeeeelp!", "Just quit it..." };

	private BufferedImage mange;
	private int currentChoice = 0;

	private Color titleColor;
	private Font titleFont1;
	private Font titleFont2;
	private String title1 = "The adventures of";
	private String title2 = "Manger Danger";
	private Font font;
	private int setScale;

	public void terminate() {
		this.bg = null;
		this.panel = null;
		this.manager = null;
		Sound.stopAllMusic();
	}
	
	public MenuState(GameStateManager manager, GamePanel panel) {
		this.manager = manager;
		this.panel = panel;
		try {
			bg = new Background("/Backgrounds/clouds.png", 1, panel);
			mange = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/mange.png"));
			bg.setScroll(-0.5, 0);

			titleColor = new Color(126, 255, 0);
			titleFont1 = new Font("Eras Bold ITC", Font.BOLD, 18);
			titleFont2 = new Font("Eras Bold ITC", Font.BOLD, 40);
			font = new Font("Eras Bold ITC", Font.BOLD, 16);

		} catch(Exception e) {
			e.printStackTrace();
		}
		init();
	}

	public void init() {
		Sound.music4.play(true);
		setScale = panel.getScale();
	}

	public void update() {
		handleInput();
		
		bg.update();
	}

	public void draw(Graphics2D g) {
		// Rita bakgrundsbilden
//		bg.draw(g);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, panel.getWindowWidth(), panel.getWindowHeight());

		// Rita en alltmer nyfiken mange beroende på menyvalen
		g.drawImage(mange, (panel.getWindowWidth() - (panel.getWindowWidth() - 270)) + currentChoice * 45, 0, panel.getWindowWidth(), panel.getWindowHeight(), null);

		g.setFont(titleFont1);
		g.setColor(Color.BLACK);
		g.drawString(title1, 20, 41);
		g.setColor(titleColor);
		g.drawString(title1, 21, 40);

		g.setFont(titleFont2);
		g.setColor(Color.BLACK);
		g.drawString(title2, 20, 74);
		g.setColor(titleColor);
		g.drawString(title2, 21, 72);


		// Rita ut menyvalen
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		int ty = panel.getWindowHeight() / 2;
		for (int i = 0; i < options.length; i++) {
			if (i ==  currentChoice) {
				if (currentChoice == OPTIONS) {
					g.setColor(Color.BLACK);
					String str = options[i] + " - <" + setScale + ">";
					Rectangle2D r = fm.getStringBounds(str, g);
					int x = (panel.getWindowWidth() - (int) r.getWidth()) / 3;
					g.drawString(options[i] + " - <" + setScale + ">", x + 1, (ty + 1) + i * 20);
					g.setColor(titleColor);
					g.drawString(options[i] + " - <" + setScale + ">", x + 1, ty + i * 20);
				} else {
					g.setColor(Color.BLACK);
					Rectangle2D r = fm.getStringBounds(options[i], g);
					int x = (panel.getWindowWidth() - (int) r.getWidth()) / 3;
					g.drawString(options[i], x + 1, (ty + 1) + i * 20);
					g.setColor(titleColor);
					g.drawString(options[i], x, ty + i * 20);
				}
				
			} else {
				Rectangle2D r = fm.getStringBounds(options[i], g);
				int x = (panel.getWindowWidth() - (int) r.getWidth()) / 3;
				g.setColor(Color.BLACK);
				g.drawString(options[i], x + 1, (ty + 1) + i * 20);
				if (i == 2) {
					g.setColor(Color.DARK_GRAY);
					g.drawString(options[i], x, ty + i * 20);
				} else {
					g.setColor(Color.LIGHT_GRAY);
					g.drawString(options[i], x, ty + i * 20);
				}
			}

		}

		g.setFont(new Font("Calibri", Font.ITALIC, 12));
		fm = g.getFontMetrics();
		if (currentChoice == OPTIONS) {
			g.setColor(Color.BLACK);
			String str = description[currentChoice] + (panel.getWindowWidth() * panel.getScale()) + "x" + (panel.getWindowHeight() * panel.getScale()) + (panel.getFullscreen() ? " - Fullscreen": "");
			Rectangle2D r = fm.getStringBounds(str, g);
			int x = (int) (r.getWidth() / str.length() + 7);
			int y = (panel.getWindowHeight()) - (int) r.getHeight();
			g.drawString(description[currentChoice] + (panel.getWindowWidth() * panel.getScale()) + "x" + (panel.getWindowHeight() * panel.getScale()) + (panel.getFullscreen() ? " - Fullscreen": ""), x + 1 , y);
			g.setColor(Color.WHITE);
			g.drawString(description[currentChoice] + (panel.getWindowWidth() * panel.getScale()) + "x" + (panel.getWindowHeight() * panel.getScale()) + (panel.getFullscreen() ? " - Fullscreen": ""), x, y);
		} else {
			g.setColor(Color.BLACK);
			Rectangle2D r = fm.getStringBounds(description[currentChoice], g);
			int x = (int) (r.getWidth() / description[currentChoice].length() + 7);
			int y = (panel.getWindowHeight()) - (int) r.getHeight();
			g.drawString(description[currentChoice], x + 1 , y);
			g.setColor(Color.WHITE);
			g.drawString(description[currentChoice], x, y);
		}


	}
	
	public void handleInput() {
		if(KeyHandler.isPressed(KeyHandler.ESCAPE)) System.exit(0);
		if(KeyHandler.isPressed(KeyHandler.ENTER) || KeyHandler.isPressed(KeyHandler.BUTTON_E)) {
			select();
		}
		
		if(KeyHandler.isPressed(KeyHandler.UP) || KeyHandler.isPressed(KeyHandler.BUTTON_W)) {
			Sound.menu.play();
			if (--currentChoice < 0) {
				currentChoice = options.length - 1;
			}
		}
		
		if(KeyHandler.isPressed(KeyHandler.LEFT) || KeyHandler.isPressed(KeyHandler.BUTTON_A)) {
			if (currentChoice == OPTIONS) {
				if (setScale > 1) {
					setScale--;
					Sound.menu.play();
					panel.setScale(setScale);
				}
			}
		}
		
		if(KeyHandler.isPressed(KeyHandler.RIGHT) || KeyHandler.isPressed(KeyHandler.BUTTON_D)) {
			if (currentChoice == OPTIONS) {
				if (setScale < 3) {
					setScale++;
					Sound.menu.play();
					panel.setScale(setScale);
				}
			}
		}
		
		if(KeyHandler.isPressed(KeyHandler.DOWN) || KeyHandler.isPressed(KeyHandler.BUTTON_S)) {
			Sound.menu.play();
			if (++currentChoice == options.length) {
				currentChoice = 0;
			}
		}
		
		
	}
	

	public void keyPressed(int k) {
//		if (k == KeyEvent.VK_ENTER || k == KeyEvent.VK_E) {
//			
//		}
//
//		if (k == KeyEvent.VK_UP || k == KeyEvent.VK_W) {
//			
//		}
//
//		if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) {
//			
//		}
//
//		if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) {
//			
//		}
//
//		if (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) {
//			
//		}
	}

	private void select() {
		switch (currentChoice) {
		case START:
			manager.setState(GameStateManager.LEVEL1STATE);
			Sound.stopAllMusic();
			Sound.setVolume(Sound.music2, Sound.DECREASE_15DB);
			Sound.music2.play(true);
			
			break;
		case OPTIONS:

			break;
		case HELP:
			break;
		case QUIT:
			System.exit(0);
			break;
		}
	}


	public void keyReleased(int k) {
	}

	

}
