package entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import tilemap.TileMap;

@SuppressWarnings("unused")
public class PeeBall extends MapObject {
	private boolean hit;
	private boolean remove;
	private boolean thrownFrom;
	private double moveSpeedY;
	private double ballFallSpeed;
	private double initialYSpeed;
	private ArrayList<Pee> peeList;
	private Player player;
	private BufferedImage[] sprites;
	
	private int directHitDamage;
	
	public PeeBall(TileMap tm, boolean right, Player player, boolean b) {
		super(tm);
		moveSpeed = (player.moveSpeed * Math.abs(player.dx) * 2.3) + 6.3;
		moveSpeedY = -0.36;
		ballFallSpeed = 0.15  + player.fallSpeed;
		initialYSpeed = -2.1;
		
		if (right) dx = moveSpeed;
		else dx = -moveSpeed;
		dy = initialYSpeed;
		
		directHitDamage = 35;
		
		thrownFrom = b;
		
		width = 10;
		height = 10;
		
		cWidth = 10;
		cHeight = 10;
		
		
		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/peeball2.png"));
			
			sprites = new BufferedImage[10];
			for (int i = 0; i < sprites.length; i++) {
				sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(20);
		
	}
	
	public int getDirectHitDamage() {
		return directHitDamage;
	}
	
	public boolean getThrownFrom() {
		return thrownFrom;
	}
	
	public void setHit() {
		if (hit) return;
		hit = true;
//		animation.setFrames(hitSprites);
//		animation.setDelay(70);
		dx = 0;
	}
	
	public boolean shouldRemove() {
		return remove;
	}
	
	public void update() {
		dy += ballFallSpeed;
		updatePosition();
		checkTileMapCollision();
		setPosition(xTemp, yTemp);
		
		if (dx == 0 || dy == 0 && !hit) setHit();
		
		animation.update();
		
		if (hit) remove = true;
		
	}
	
	public void draw(Graphics2D g) {
		setMapPosition();
		
		super.draw(g);
		
		
	}
	
}
