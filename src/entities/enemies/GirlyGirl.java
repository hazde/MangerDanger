package entities.enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;

import main.Sound;

import tilemap.TileMap;
import entities.Animation;
import entities.Enemy;

public class GirlyGirl extends Enemy {
	
	private BufferedImage[] sprites;
//	private BufferedImage[] hitSprites;
	private boolean dying;
	
	public GirlyGirl(TileMap tm) {
		super(tm);
		moveSpeed = 0.68;
		maxSpeed = 0.7;
		fallSpeed = 0.2;
		maxFallSpeed = 3.0;
		jumpStart = -3.4;
		
		width = 30;
		height = 30;
		cWidth = 20;
		cHeight = 20;
		
		health = maxHealth = 4;
		damage = 1;
		setDiedFromFalling(false);
		
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Girls/girl1.png"));
			sprites = new BufferedImage[16];
			for (int i = 0; i < sprites.length; i++) {
					sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);				
			}
			
//			hitSprites = new BufferedImage[6];
//			for (int i = 0; i < hitSprites.length; i++) {
//				hitSprites[i] = spritesheet.getSubimage(i * width, height, width, height);
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(3);
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
		
			if (jumping  && !falling) {
				dy = jumpStart;
				falling = true;
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
		
		if ((new Random().nextInt(474748) + new Random().nextInt(3367346) + 1) % 365 == 0) {
			jumping = true;
		}
		
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
		
		if (dy > 0) jumping = false;
		if (dy < 0 && !jumping) dy += stopJumpSpeed;

		if (dy > maxFallSpeed) dy = maxFallSpeed;
		
		animation.update();
		
	}
	
	public void hit(int damage) {
		if (dead || flinching) return;
		this.addText("" + damage, x, y - 10, 1000, new Color(255, 125, 0));
		Sound.hit.play();
		health -= damage;
		if (health < 0) health = 0;
		if (health == 0) {
			dying = true;
//			Sound.death.play();
//			animation.setFrames(hitSprites);
//			animation.setDelay(35);
			dx = 0;
		}
		flinching = true;
		flinchTimer = System.nanoTime();
	}
	
	public void draw(Graphics2D g) {
//		if (notOnScreen()) return;
		
		setMapPosition();
		super.draw(g);
		
	}

}
