package entities.enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.Sound;

import tilemap.TileMap;
import entities.Animation;
import entities.Enemy;

public class Slugger extends Enemy {
	
	private BufferedImage[] sprites;
	private BufferedImage[] hitSprites;
	private boolean dying;
	
	public Slugger(TileMap tm) {
		super(tm);
		moveSpeed = 0.3;
		maxSpeed = 1.8;
		fallSpeed = 0.2;
		maxFallSpeed = 3.0;
		
		width = 30;
		height = 30;
		cWidth = 20;
		cHeight = 20;
		
		health = maxHealth = 50;
		damage = 1;
		setDiedFromFalling(false);
		
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemies/slugger.gif"));
			sprites = new BufferedImage[3];
			for (int i = 0; i < sprites.length; i++) {
				sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);
			}
			
			hitSprites = new BufferedImage[6];
			for (int i = 0; i < hitSprites.length; i++) {
				hitSprites[i] = spritesheet.getSubimage(i * width, height, width, height);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(300);
		facingRight = true;
		right = true;
	}
	
	private void getNextPosition() {
		if (left) {
			dx -= moveSpeed;
			if (dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		} else if (right) {
			dx += moveSpeed;
			if (dx > maxSpeed) {
				dx = maxSpeed;
			}
		}
		
		if (falling) {
			dy += fallSpeed;
		}
		
	}
	
	
	
	public void update() {
		getNextPosition();
		updatePosition();
		checkTileMapCollision();
		setPosition(xTemp, yTemp);
		
		if (flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if (elapsed > 400) {
				flinching = false;
			}
		}
		
		if (right && dx == 0) {
			right = false;
			left = true;
			facingRight = false;
		} else if (left && dx == 0) {
			right = true;
			left = false;
			facingRight = true;
		}
		if (falling && y > tilemap.getHeight()) {
			setDiedFromFalling(true);
			}
		
		if (dying && animation.hasPlayedOnce()) {
			dead = true;
		}
		
		animation.update();
		
	}
	
	@Override
	public void hit(int damage, boolean fromLeft) {
		if (dead || flinching) return;	
		this.addText("" + damage, x, y - 10, 1000, new Color(255, 137, 0));
		Sound.hit.play();
		health -= damage;
		if (health < 0) health = 0;
		if (health == 0) {
			dying = true;
			Sound.death.play();
			animation.setFrames(hitSprites);
			animation.setDelay(35);
			dx = 0;
		}
		flinching = true;
		flinchTimer = System.nanoTime();
	}
	
	@Override
	public boolean isDying() {
		return dying;
	}
	
	public void draw(Graphics2D g) {
		setMapPosition();
		
//		if (flinching) {
//			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
//			if (elapsed / 50 % 2 == 0) {
//				return;
//			}
//		}
		
		super.draw(g);
		
	}

}
