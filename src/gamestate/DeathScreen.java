package gamestate;

import handlers.KeyHandler;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.Sound;
import tilemap.Background;

@SuppressWarnings("unused")
public class DeathScreen extends GameState {
	private Background bg;

	private static final int START = 0;
	private static final int OPTIONS = 1;
	private static final int QUIT = 2;

	private GamePanel panel;

	private String[] options = { "Try again", "Options", "Exit" };
	private String[] description = { "Enter the Dainger Zone... again.", "\"Resolution\": ", "Enough!" };

	private BufferedImage mange;
	private BufferedImage death;
	private int currentChoice = 0;

	private Color titleColor;
	
	private Font titleFont1;
	private Font titleFont2;
	private String title1 = "Åååååååååh";
	private String title2 = "SOOOOOPA!";
	private Font font;
	private int setScale;
	private double mangeX;
	private boolean mangeFlip;
	private boolean runOnce;
	private int waitCounter;

	public DeathScreen(GameStateManager manager, GamePanel panel) {
		this.manager = manager;
		this.panel = panel;

		try {
			bg = new Background("/Backgrounds/clouds.png", 1, panel);
			mange = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/mange2.png"));
			death = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/death2.png"));
			bg.setScroll(-0.5, 0);

			titleColor = new Color(225, 0, 0);
			titleFont1 = new Font("Calibri", Font.BOLD, 22);
			titleFont2 = new Font("Elephant", Font.BOLD, 40);
			font = new Font("Eras Bold ITC", Font.BOLD, 16);

		} catch(Exception e) {
			e.printStackTrace();
		}
		mangeX = 0;
		mangeFlip = true;
		runOnce = false;
		waitCounter = 0;
		init();
	}

	public void init() {
//		if (!Sound.isPlaying(Sound.deathscreen3)) Sound.deathscreen3.play(true);
		setScale = panel.getScale();
	}

	public void update() {
		handleInput();
		//		bg.update();
	}

	public void draw(Graphics2D g) {
		// Rita bakgrundsbilden

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, panel.getWindowWidth(), panel.getWindowHeight());

		// Rita en alltmer nyfiken mange beroende på menyvalen

		if (!runOnce) {
			if (!mangeFlip) {
				if (waitCounter > 2500) {
				mangeX -= 0.2;
				} else {
					waitCounter++;
				}
			} else {
				mangeX += 0.2;
			}

			if (mangeX <= 0) {
				mangeFlip = true;
				runOnce = true;
			} else if (mangeX >= 270) {
				mangeFlip = false;
			}
		}

		g.drawImage(mange, (int) (panel.getWindowWidth() - mangeX), 0, panel.getWindowWidth(), panel.getWindowHeight(), null);
		g.drawImage(death, 30, 30, null);

		// Rita ut menyvalen
		g.setFont(font);


		FontMetrics fm = g.getFontMetrics();
		int ty = panel.getWindowHeight() / 2;
		for (int i = 0; i < options.length; i++) {
			if (i ==  currentChoice) {
				if (currentChoice == OPTIONS) {
					String str = options[i] + " - <" + setScale + ">";
					Rectangle2D r = fm.getStringBounds(str, g);
					int x = (panel.getWindowWidth() - (int) r.getWidth()) / 3;
					g.setColor(Color.BLACK);
					g.drawString(options[i] + " - <" + setScale + ">", x , (ty + 1) + i * 20);
					g.setColor(titleColor);
					g.drawString(options[i] + " - <" + setScale + ">", x, ty + i * 20);
				} else {
					Rectangle2D r = fm.getStringBounds(options[i], g);
					int x = (panel.getWindowWidth() - (int) r.getWidth()) / 3;
					g.setColor(Color.BLACK);
					g.drawString(options[i], x, (ty + 1) + i * 20);
					g.setColor(titleColor);
					g.drawString(options[i], x, ty + i * 20);
				}

			} else {
				Rectangle2D r = fm.getStringBounds(options[i], g);
				int x = (panel.getWindowWidth() - (int) r.getWidth()) / 3;
				g.setColor(Color.BLACK);
				g.drawString(options[i], x, (ty + 1) + i * 20);

				g.setColor(Color.WHITE);
				g.drawString(options[i], x, ty + i * 20);

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

	}

	private void select() {
		switch (currentChoice) {
		case START:
			manager.setState(GameStateManager.LEVEL1STATE);
			Sound.music4.stop();
			Sound.music2.play(true);
			break;
		case OPTIONS:

			break;
		case QUIT:
			System.exit(0);
			break;
		}
	}


	public void keyReleased(int k) {
	}

	public void terminate() {

	}

}
