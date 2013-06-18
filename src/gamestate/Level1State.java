package gamestate;

import handlers.KeyHandler;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.Sound;
import tilemap.Background;
import tilemap.Tile;
import tilemap.TileMap;
import entities.Enemy;
import entities.FloatingText;
import entities.HUD;
import entities.Player;
import entities.enemies.GirlyGirl;
import entities.enemies.Slugger;

@SuppressWarnings("unused")
public class Level1State extends Level {

	private BufferedImage overlay;
	private BufferedImage death;
	private boolean lightmap;
	private GamePanel panel;

	private int eventCount = 0;
	private boolean eventStart;
	private ArrayList<Rectangle> tb;
	private boolean eventFinish;
	private boolean eventDead;
	private float screenFade = 0;
	
	private boolean drawDebug = true;


	public Level1State(GameStateManager manager, GamePanel panel) {
		super(manager);
		this.panel = panel;
		init();
	}	

	private synchronized ArrayList<Enemy> getEnemies() {
		return enemies;
	}

	public void init() {
		tilemap = new TileMap(30, panel);
		tilemap.loadTiles("/Tilesets/grasstileset3.png");
		tilemap.loadMap("/Maps/level1-5.map");
		tilemap.setPosition(0, 0);

		bg = new Background("/Backgrounds/clouds.png", 0.1, panel);

		try {
			death = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/death2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		bg.setScroll(-0.2, 0);
		player = new Player(tilemap);
		player.setPosition(tilemap.getSpawnPointX() + (player.getWidth() / 2), tilemap.getSpawnPointY());
		enemies = new ArrayList<Enemy>();
		player.init(enemies);
		
		lightmap = false;
		hud = new HUD(player, panel);
		
		eventStart = true;
		tb = new ArrayList<Rectangle>();
		eventStart();
		spawnSomeSlugs();
	}

	public void spawnSomeSlugs() {
		Slugger[] s = new Slugger[25];
		for (int i = 0; i < s.length; i++) {
			s[i] = new Slugger(tilemap);
			s[i].setPosition((new Random().nextInt(2000) + 150) , 100);
			getEnemies().add(s[i]);
		}
	}



	public void update() {

		handleInput();
		
		if(eventStart) eventStart();
		if(eventDead) eventDead();
		if(eventFinish) eventFinish();

		if(player.isDead()) {
			eventDead = true;
			enemies.clear();
		}

		bg.update();
		
		player.update();
		
		if (tilemap.getHeight() > GamePanel.HEIGHT) {
		tilemap.setPosition(GamePanel.WIDTH / 2  - player.getX(), GamePanel.HEIGHT / 3 - player.getY() - 22);
		} else {
			tilemap.setPosition(GamePanel.WIDTH / 2  - player.getX(), GamePanel.HEIGHT / 2 - player.getY() - 22);
		}



		//	bg.setPosition(tilemap.getX(), tilemap.getY());

		for (int i = 0; i < getEnemies().size(); i++) {
			getEnemies().get(i).update();
		}
		
	}

	public void draw(Graphics2D g) {
		

		// draw tilemap
		
		bg.draw(g);
		tilemap.draw(g);


		for (int i = 0; i < getEnemies().size(); i++) {
			getEnemies().get(i).draw(g);
			if (getEnemies().get(i).isDead() || getEnemies().get(i).getDiedFromFalling()) {
				if (!getEnemies().get(i).getDiedFromFalling()) {
					int xp = new Random().nextInt(3) + 1;
//					player.setExperience(xp);
					player.addBeer(2500);
				} else {
					//					if (new Random().nextInt(6) % 5 == 0) {
					//						player.addText("Mähähä! Han ramlade ner!", player.getX() - player.getCWidth() * 2, player.getY() - 20, 500,  new Color(125, 255, 0), 0.09, true);
					//					}
				}
				getEnemies().remove(i);
				i--;
			}

		}

		player.draw(g);
		hud.draw(g);

		if (drawDebug) {
		g.setColor(Color.BLACK);
		g.fillRect(00, GamePanel.HEIGHT - 50, 220, GamePanel.HEIGHT);

		g.setColor(Color.BLACK);
		g.drawString("Tile @ feet (type): " + Tile.getTileProperty(player.getTileStandingOn()) , 6, GamePanel.HEIGHT - 34);
		g.setColor(Color.WHITE);
		g.drawString("Tile @ feet (type): " + Tile.getTileProperty(player.getTileStandingOn()) , 5, GamePanel.HEIGHT - 35);
		
		g.setColor(Color.BLACK);
		g.drawString("MAP Bounds: W: " + (int) Math.abs(tilemap.getX()) + " -> " + (int) (GamePanel.WIDTH - tilemap.getX() ) + ", H: " + (int) Math.abs(tilemap.getY()) + " -> " + (int) (tilemap.getHeight() + tilemap.getY()), 6, GamePanel.HEIGHT - 24);
		g.setColor(Color.WHITE);
		g.drawString("MAP Bounds: W: " + (int) Math.abs(tilemap.getX()) + " -> " + (int) (GamePanel.WIDTH - tilemap.getX()) + ", H: " + (int) Math.abs(tilemap.getY()) + " -> " + (int) (tilemap.getHeight() + tilemap.getY()), 5, GamePanel.HEIGHT - 25);

		g.setColor(Color.BLACK);
		g.drawString("XY: " + player.getX() + " " + player.getY(), 6, GamePanel.HEIGHT - 14);
		g.setColor(Color.WHITE);
		g.drawString("XY: " + player.getX() + " " + player.getY(), 5, GamePanel.HEIGHT - 15);

		g.setColor(Color.BLACK);
		g.drawString("LSW XY: " + player.getLSWX() + " " + player.getLSWY(), 6, GamePanel.HEIGHT - 4);
		g.setColor(Color.WHITE);
		g.drawString("LSW XY: " + player.getLSWX() + " " + player.getLSWY(), 5, GamePanel.HEIGHT - 5);

		g.setColor(Color.BLACK);
		g.drawString("Enemy (Linus) count: " + getEnemies().size(), 185 , 12);
		g.setColor(Color.WHITE);
		g.drawString("Enemy (Linus) count: " + getEnemies().size(), 184, 11);

		}
		
		g.setColor(java.awt.Color.BLACK);
		for(int i = 0; i < tb.size(); i++) {
			if (eventDead && eventCount > 250) {
				screenFade += 0.01;
				if (screenFade >= 1) {
					screenFade = 1;
				}
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, screenFade * 1f));
				g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
				g.drawImage(death, (int) tb.get(i).getX() + 30, 30, death.getWidth(), death.getHeight(), null);
			} else {
//				g.fill(tb.get(i));
			}
		}

	}

	public void respawn() {
		player.setPosition(tilemap.getSpawnPointX(), tilemap.getSpawnPointY());
	}
	
	
	
	public void handleInput() {
		if(KeyHandler.isPressed(KeyHandler.ESCAPE)) System.exit(0);
		if(player.isDead()) return;

		player.setLeft(KeyHandler.keyState[KeyHandler.LEFT]);
		player.setDown(KeyHandler.keyState[KeyHandler.DOWN]);
		player.setRight(KeyHandler.keyState[KeyHandler.RIGHT]);
		
		if (player.isPeeing() && !player.isJumping() && !player.isFalling() && !player.isMoving()) {
			boolean peeUp = KeyHandler.keyState[KeyHandler.UP];
			boolean peeDown = KeyHandler.keyState[KeyHandler.DOWN];
			if (peeUp) {
				player.changePeeArcY(-0.05);
			}
			if (peeDown) {
				player.changePeeArcY(0.05);
			}
		} else {
			if (player.isJumping() || player.isFalling()) {
				player.setGliding(KeyHandler.keyState[KeyHandler.UP]);
			} else {
				player.setJumping(KeyHandler.keyState[KeyHandler.UP]);
			}
			
			if (KeyHandler.isPressed(KeyHandler.UP)) {
				
				if ((!player.isFalling() && !player.isGliding())) {
					Sound.jump.play();
				} 
		
			}
			
		}
		
		
		player.setFiring(KeyHandler.keyState[KeyHandler.SPACE]);
		player.setThrowingPeeball(KeyHandler.keyState[KeyHandler.BUTTON_D]);
		player.setDrawTrajectory(KeyHandler.keyState[KeyHandler.SHIFT]);
		
//		if (KeyHandler.keyState[KeyHandler.UP] && !player.isJumping() && player.isGliding() && player.getDY() > 1.155) {
//			Sound.jump.play();
//		}
		
		
		
		
		
		if (KeyHandler.isPressed(KeyHandler.NUMBER_1)) {
			panel.setScale(1);
		} else if (KeyHandler.isPressed(KeyHandler.NUMBER_2)) {
			panel.setScale(2);
		} else if (KeyHandler.isPressed(KeyHandler.NUMBER_3)) {
			panel.setScale(3);
		}
		
		if (KeyHandler.isPressed(KeyHandler.F1)) {
			spawnSomeSlugs();
		}
		
		if (KeyHandler.isPressed(KeyHandler.F2)) {
			respawn();
		}
		
		if (KeyHandler.isPressed(KeyHandler.F3)) {
			player.setMaxPee(100000);
		}
		
		if (KeyHandler.isPressed(KeyHandler.F4)) {
			drawDebug = !drawDebug;
		}
		
		
		
	}

	private void eventStart() {
		eventCount++;
		if(eventCount == 1) {
			tb.clear();
			tb.add(new Rectangle(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT / 2));
			tb.add(new Rectangle(0, 0, GamePanel.WIDTH / 2, GamePanel.HEIGHT));
			tb.add(new Rectangle(0, GamePanel.HEIGHT / 2, GamePanel.WIDTH, GamePanel.HEIGHT / 2));
			tb.add(new Rectangle(GamePanel.WIDTH / 2, 0, GamePanel.WIDTH / 2, GamePanel.HEIGHT));
		}
		if(eventCount > 1 && eventCount < 60) {
			tb.get(0).height -= 4;
			tb.get(1).width -= 6;
			tb.get(2).y += 4;
			tb.get(3).x += 6;
		}
		if(eventCount == 60) {
			eventStart = false;
			eventCount = 0;
			tb.clear();
		}
	}

	// player has died
	private void eventDead() {
		eventCount++;
		if(eventCount == 1) {
			Sound.stopAllMusic();
			
			//			player.setDead();
			//			player.stop();
		}
		if(eventCount == 2) {
			Sound.setPosition(Sound.deathscreen3, 5110000);
			tb.clear();
			tb.add(new Rectangle(
					GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
		}
		else if(eventCount > 250) {
				if (tb.get(0).x > 0) {
					tb.get(0).x -= (GamePanel.WIDTH / 100) / 2;
				} else {
					tb.get(0).x = 0;
				}
				if ( tb.get(0).y > 0) {
					tb.get(0).y -= (GamePanel.HEIGHT / 100) / 2;
				} else {
					tb.get(0).y = 0;
				}
				if (tb.get(0).width < GamePanel.WIDTH) tb.get(0).width += (GamePanel.WIDTH / 100);
				if (tb.get(0).height < GamePanel.HEIGHT) tb.get(0).height += (GamePanel.HEIGHT / 100);
		}
			
		if(eventCount >= 520) {
			if(player.isDead()) {
				manager.setState(GameStateManager.DEATHSCREEN, true);
			}
		}
	}

	// finished level
	private void eventFinish() {
		eventCount++;
		if(eventCount == 120) {
			tb.clear();
			tb.add(new Rectangle(
					GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
		}
		else if(eventCount > 120) {
			tb.get(0).x -= 6;
			tb.get(0).y -= 4;
			tb.get(0).width += 12;
			tb.get(0).height += 8;
		}


	}

}
