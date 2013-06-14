package gamestate;

import java.awt.Graphics2D;
import java.util.ArrayList;

import tilemap.Background;
import tilemap.TileMap;
import entities.Enemy;
import entities.HUD;
import entities.Player;

public class Level extends GameState {

	protected TileMap tilemap;
	protected Background bg;
	
	protected Player player;
	protected ArrayList<Enemy> enemies;
	
	protected HUD hud;
	
	public Level(GameStateManager manager) {
		this.manager = manager;
	}
	
	public void init() {

	}

	public void update() {

	}


	public void draw(Graphics2D g) {

	}


	public void keyPressed(int k) {
	
	}


	public void keyReleased(int k) {

	}
	
}
