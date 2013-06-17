package gamestate;

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

	private String[] options = { "Försök igen", "Alternativ", "Avsluta" };
	private String[] description = { "Enter the Dainger Zone... again.", "\"Upplösning\": ", "Avsluta möget" };

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
			bg = new Background("/Backgrounds/clouds.png", 1);
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
		//		bg.update();
	}

	public void draw(Graphics2D g) {
		// Rita bakgrundsbilden

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

		// Rita en alltmer nyfiken mange beroende på menyvalen

		if (!runOnce) {
			if (!mangeFlip) {
				if (waitCounter > 500) {
				mangeX -= 0.3;
				} else {
					waitCounter++;
				}
			} else {
				mangeX += 0.3;
			}

			if (mangeX <= 0) {
				mangeFlip = true;
				runOnce = true;
			} else if (mangeX >= 270) {
				mangeFlip = false;
			}
		}

		g.drawImage(mange, (int) (GamePanel.WIDTH - mangeX), 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
		g.drawImage(death, 30, 30, null);
		//		g.setFont(titleFont1);
		//		g.setColor(titleColor);
		//		g.drawString(title1, 30, 60);
		//
		//		g.setFont(titleFont2);
		//		g.setColor(titleColor);
		//		g.drawString(title2, 40, 100);


		// Rita ut menyvalen
		g.setFont(font);


		FontMetrics fm = g.getFontMetrics();
		for (int i = 0; i < options.length; i++) {
			if (i ==  currentChoice) {
				if (currentChoice == OPTIONS) {
					String str = options[i] + " - <" + setScale + ">";
					Rectangle2D r = fm.getStringBounds(str, g);
					int x = (GamePanel.WIDTH - (int) r.getWidth()) / 2;
					g.setColor(Color.BLACK);
					g.drawString(options[i] + " - <" + setScale + ">", x , 271 + i * 20);
					g.setColor(titleColor);
					g.drawString(options[i] + " - <" + setScale + ">", x, 270 + i * 20);
				} else {
					Rectangle2D r = fm.getStringBounds(options[i], g);
					int x = (GamePanel.WIDTH - (int) r.getWidth()) / 2;
					g.setColor(Color.BLACK);
					g.drawString(options[i], x, 271 + i * 20);
					g.setColor(titleColor);
					g.drawString(options[i], x, 270 + i * 20);
				}

			} else {
				Rectangle2D r = fm.getStringBounds(options[i], g);
				int x = (GamePanel.WIDTH - (int) r.getWidth()) / 2;
				g.setColor(Color.BLACK);
				g.drawString(options[i], x, 271 + i * 20);

				g.setColor(Color.WHITE);
				g.drawString(options[i], x, 270 + i * 20);

			}

		}

		g.setFont(new Font("Calibri", Font.ITALIC, 12));
		fm = g.getFontMetrics();
		if (currentChoice == OPTIONS) {
			g.setColor(Color.BLACK);
			String str = description[currentChoice] + (panel.getWindowWidth() * panel.getScale()) + "x" + (panel.getWindowHeight() * panel.getScale()) + (panel.getFullscreen() ? " - Fullskärm": "");
			Rectangle2D r = fm.getStringBounds(str, g);
			int x = (int) (r.getWidth() / str.length() + 7);
			int y = (GamePanel.HEIGHT) - (int) r.getHeight();
			g.drawString(description[currentChoice] + (panel.getWindowWidth() * panel.getScale()) + "x" + (panel.getWindowHeight() * panel.getScale()) + (panel.getFullscreen() ? " - Fullskärm": ""), x + 1 , y);
			g.setColor(Color.WHITE);
			g.drawString(description[currentChoice] + (panel.getWindowWidth() * panel.getScale()) + "x" + (panel.getWindowHeight() * panel.getScale()) + (panel.getFullscreen() ? " - Fullskärm": ""), x, y);
		} else {
			g.setColor(Color.BLACK);
			Rectangle2D r = fm.getStringBounds(description[currentChoice], g);
			int x = (int) (r.getWidth() / description[currentChoice].length() + 7);
			int y = (GamePanel.HEIGHT) - (int) r.getHeight();
			g.drawString(description[currentChoice], x + 1 , y);
			g.setColor(Color.WHITE);
			g.drawString(description[currentChoice], x, y);
		}


	}

	public void keyPressed(int k) {
		if (k == KeyEvent.VK_ENTER || k == KeyEvent.VK_E) {
			select();
		}

		if (k == KeyEvent.VK_UP || k == KeyEvent.VK_W) {
			Sound.menu.play();
			if (--currentChoice < 0) {
				currentChoice = options.length - 1;
			}
		}

		if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) {
			if (currentChoice == OPTIONS) {
				if (setScale > 1) {
					setScale--;
					Sound.menu.play();
					panel.setScale(setScale);
				}
			}
		}

		if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) {
			if (currentChoice == OPTIONS) {
				if (setScale < 3) {
					setScale++;
					Sound.menu.play();
					panel.setScale(setScale);
				}
			}
		}

		if (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) {
			Sound.menu.play();
			if (++currentChoice == options.length) {
				currentChoice = 0;
			}
		}
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
