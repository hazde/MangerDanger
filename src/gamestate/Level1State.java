package gamestate;

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


	public Level1State(GameStateManager manager, GamePanel panel) {
		super(manager);
		this.panel = panel;
		init();
	}	

	private synchronized ArrayList<Enemy> getEnemies() {
		return enemies;
	}

	public void init() {
		tilemap = new TileMap(30);
		tilemap.loadTiles("/Tilesets/grasstileset3.png");
		tilemap.loadMap("/Maps/level1-4.map");
		tilemap.setPosition(0, 0);

		bg = new Background("/Backgrounds/clouds.png", 0.1);
		
		try {
			death = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/death.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		bg.setScroll(-0.2, 0);
		player = new Player(tilemap);
		player.setPosition(tilemap.getSpawnPointX() + (player.getWidth() / 2), tilemap.getSpawnPointY());
		enemies = new ArrayList<Enemy>();
		spawnSomeSlugs();
		lightmap = false;
		hud = new HUD(player);
		eventStart = true;
		tb = new ArrayList<Rectangle>();
		eventStart();
	}

	public void spawnSomeSlugs() {
		Slugger[] s = new Slugger[15];
		for (int i = 0; i < s.length; i++) {
			s[i] = new Slugger(tilemap);

			//			s[i].setPosition(50 + (i * 20) , 100);
			s[i].setPosition(tilemap.getSpawnPointX() + (new Random().nextInt(2000) + 150) , 100);
			getEnemies().add(s[i]);
		}
	}



	public void update() {
//		if (player.isDead()) {
//			manager.setState(GameStateManager.DEATHSCREEN);
//		}
		
		if(eventStart) eventStart();
		if(eventDead) eventDead();
		if(eventFinish) eventFinish();
		
		if(player.isDead()) {
			eventDead = true;
		}
		
		bg.update();
		player.update();
		tilemap.setPosition(GamePanel.WIDTH / 2  - player.getX(), GamePanel.HEIGHT / 3 - player.getY() - 22);



		//		bg.setPosition(tilemap.getX(), tilemap.getY());

		player.checkAttack(getEnemies());

		ArrayList<Enemy> toRemove = new ArrayList<Enemy>();
		for (int i = 0; i < getEnemies().size(); i++) {
			getEnemies().get(i).update();
		}
	}

	public void draw(Graphics2D g) {
		bg.draw(g);

		// draw tilemap

		tilemap.draw(g);

		


		for (int i = 0; i < getEnemies().size(); i++) {
			getEnemies().get(i).draw(g);
			if (getEnemies().get(i).isDead() || getEnemies().get(i).getDiedFromFalling()) {
				if (!getEnemies().get(i).getDiedFromFalling()) {
					int xp = new Random().nextInt(3) + 1;
					player.setExperience(xp);
					player.addText("+ " + xp +  "xp", player.getX(), player.getY() - 10, 1700,  new Color(255, 211, 109));
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
		
		g.setColor(Color.BLACK);
		g.fillRect(00, GamePanel.HEIGHT - 40, 200, GamePanel.HEIGHT);
		
		g.setColor(Color.BLACK);
		g.drawString("MAP Bounds: " + (int) tilemap.getX() + " -> " + (int) (tilemap.getX() + (tilemap.getWidth() - GamePanel.WIDTH)) + " " + (int) tilemap.getY() + " -> " + (int) (tilemap.getHeight() + tilemap.getY()), 6, GamePanel.HEIGHT - 24);
		g.setColor(Color.WHITE);
		g.drawString("MAP Bounds: " + (int) tilemap.getX() + " -> " + (int) (tilemap.getX() + (tilemap.getWidth() - GamePanel.WIDTH)) + " " + (int) tilemap.getY() + " -> " + (int) (tilemap.getHeight() + tilemap.getY()), 5, GamePanel.HEIGHT - 25);
		
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
		
		g.setColor(java.awt.Color.BLACK);
		for(int i = 0; i < tb.size(); i++) {
			g.fill(tb.get(i));
			if (eventDead) {
			g.drawImage(death, (int) (tb.get(i).getX() - (i * 6)), (int) tb.get(i).getY() - (i * 4), (int) tb.get(i).getWidth() + (i * 12), (int) tb.get(i).getHeight() + (i * 8), null);
			}
		}

	}

	public void respawn() {
		player.setPosition(tilemap.getSpawnPointX(), tilemap.getSpawnPointY());
	}

	public void keyPressed(int k) {
		if (k == KeyEvent.VK_LEFT) player.setLeft(true);
		if (k == KeyEvent.VK_RIGHT) player.setRight(true);


		if (player.isPeeing() && !player.isJumping() && !player.isFalling() && !player.isMoving()) {
			if (k == KeyEvent.VK_UP) player.changePeeArcY(-0.1);
			if (k == KeyEvent.VK_DOWN) player.changePeeArcY(0.1);
		} else {
			if (player.isJumping() || player.isFalling()) {
				if (k == KeyEvent.VK_UP) player.setGliding(true);
			} else {
				if (k == KeyEvent.VK_UP) player.setJumping(true);
			}
		}

		//		if (k == KeyEvent.VK_DOWN) player.setDown(true);
		if (k == KeyEvent.VK_CONTROL) player.setJumping(true);
		if (k == KeyEvent.VK_SPACE) player.setFiring(true);

		if (k == KeyEvent.VK_D) player.setThrowingPeeball(true);

		if (k == KeyEvent.VK_F1) spawnSomeSlugs();
		if (k == KeyEvent.VK_F2) respawn();
		
		if (k == KeyEvent.VK_1) panel.setScale(1);
		if (k == KeyEvent.VK_2)  panel.setScale(2);
		if (k == KeyEvent.VK_3)  panel.setScale(3);
		
		if (k == KeyEvent.VK_SHIFT) player.setDrawTrajectory(true);
		
		if (k == KeyEvent.VK_F4) player.setMaxPee(1000000);

		if (k == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}

		//		if (k == KeyEvent.VK_F4) {
		//			addText("Test", (double) player.getX(), (double) player.getY());
		//		}

	}

	public void keyReleased(int k) {
		if (k == KeyEvent.VK_LEFT) player.setLeft(false);
		if (k == KeyEvent.VK_RIGHT) player.setRight(false);

		if (player.isPeeing() && !player.isJumping() && !player.isFalling() && !player.isMoving()) {

		} else {
			if (player.isJumping() || player.isFalling()) {
				if (k == KeyEvent.VK_UP) player.setGliding(false);
			} else {
				if (k == KeyEvent.VK_UP) player.setJumping(false);
			}
		}


		if (k == KeyEvent.VK_CONTROL) player.setJumping(false);
		if (k == KeyEvent.VK_SPACE) player.setFiring(false);
		if (k == KeyEvent.VK_D) player.setThrowingPeeball(false);
		
		if (k == KeyEvent.VK_SHIFT) player.setDrawTrajectory(false);
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
//		if(eventCount == 30) title.begin();
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
		if(eventCount == 60) {
			
			Sound.deathscreen.play();
			tb.clear();
			tb.add(new Rectangle(
				GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
		}
		else if(eventCount > 60) {
			tb.get(0).x -= 6;
			tb.get(0).y -= 4;
			tb.get(0).width += 12;
			tb.get(0).height += 8;
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
