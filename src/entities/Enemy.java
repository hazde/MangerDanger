package entities;

import main.Sound;
import tilemap.TileMap;

@SuppressWarnings("unused")
public class Enemy extends MapObject {
	
	protected int health;
	protected int maxHealth;
	protected boolean dead;
	protected int damage;
	protected boolean diedFromFalling;
	
//	protected boolean flinching;
//	protected long flinchTimer;
	
	public Enemy(TileMap tm) {
		super(tm);
	}
	
	public boolean isDead() {return dead;}
	public boolean isDying() {
		return false;
	}
	public int getDamage() {return damage;}
	
	public boolean getDiedFromFalling() {return diedFromFalling;}
	public void setDiedFromFalling(boolean b) {diedFromFalling = b;}
	
	public void hit(int damage, boolean fromLeft) {
		
	}
	
	public void update() {
		
	}
}
