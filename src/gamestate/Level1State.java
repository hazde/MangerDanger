package gamestate;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import main.GamePanel;
import main.Sound;
import tilemap.Background;
import tilemap.TileMap;
import entities.Enemy;
import entities.HUD;
import entities.Player;
import entities.enemies.Slugger;

@SuppressWarnings("unused")
public class Level1State extends Level {
	
	
	
	public Level1State(GameStateManager manager) {
		super(manager);
		init();
	}	

	public void init() {
		tilemap = new TileMap(30);
		tilemap.loadTiles("/Tilesets/grasstileset3.png");
		tilemap.loadMap("/Maps/level1-4.map");
		tilemap.setPosition(0, 0);
		bg = new Background("/Backgrounds/menubg.gif", 0.1);
//		bg.setScroll(-0.1, 0);
		player = new Player(tilemap);
		player.setPosition(tilemap.getSpawnPointX() + (player.getWidth() / 2), tilemap.getSpawnPointY());
		enemies = new ArrayList<Enemy>();
		spawnSomeSlugs();
		
		hud = new HUD(player);
	}
	
	public void spawnSomeSlugs() {
		Slugger[] s = new Slugger[15];
		for (int i = 0; i < s.length; i++) {
			s[i] = new Slugger(tilemap);
			
//			s[i].setPosition(50 + (i * 20) , 100);
			s[i].setPosition(new Random().nextInt(player.getX() + (tilemap.getSpawnPointX() + 200) + 50) , 100);
			enemies.add(s[i]);
		}
	}

	public void update() {
		bg.update();
		player.update();
		tilemap.setPosition(GamePanel.WIDTH / 2  - player.getX(), GamePanel.HEIGHT / 2 - player.getY() - 22);
		
		
		
//		bg.setPosition(tilemap.getX(), tilemap.getY());
		
		player.checkAttack(enemies);
		
		
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).update();
		}
	}

	public void draw(Graphics2D g) {
		bg.draw(g);
		
		// draw tilemap
		
		tilemap.draw(g);
		player.draw(g);
		g.setColor(Color.BLACK);
		g.drawString("XY: " + player.getX() + " " + player.getY(), 6, GamePanel.HEIGHT - 14);
		g.setColor(Color.WHITE);
		g.drawString("XY: " + player.getX() + " " + player.getY(), 5, GamePanel.HEIGHT - 15);
		
		g.setColor(Color.BLACK);
		g.drawString("LSW XY: " + player.getLSWX() + " " + player.getLSWY(), 6, GamePanel.HEIGHT - 4);
		g.setColor(Color.WHITE);
		g.drawString("LSW XY: " + player.getLSWX() + " " + player.getLSWY(), 5, GamePanel.HEIGHT - 5);
		
		g.setColor(Color.BLACK);
		g.drawString("Enemy (Linus) count: " + enemies.size(), 185 , 12);
		g.setColor(Color.WHITE);
		g.drawString("Enemy (Linus) count: " + enemies.size(), 184, 11);
		
		
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(g);
			if (enemies.get(i).isDead()) {
				enemies.remove(i);
				i--;
			}
		}
		
		hud.draw(g);
		
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
		
		if (k == KeyEvent.VK_F1) spawnSomeSlugs();
		if (k == KeyEvent.VK_F2) respawn();
	}

	public void keyReleased(int k) {
		if (k == KeyEvent.VK_LEFT) player.setLeft(false);
		if (k == KeyEvent.VK_RIGHT) player.setRight(false);

		if (player.isJumping() || player.isFalling()) {
			if (k == KeyEvent.VK_UP) player.setGliding(false);
		} else {
			if (k == KeyEvent.VK_UP) player.setJumping(false);
		}

		
		if (k == KeyEvent.VK_CONTROL) player.setJumping(false);
		if (k == KeyEvent.VK_SPACE) player.setFiring(false);
	}
	
	
}
