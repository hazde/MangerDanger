package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import tilemap.TileMap;

public class Pee extends MapObject {
	private boolean hit;
	private boolean remove;
	private double moveSpeedY;
	private double peeFallSpeed;
	
	public Pee(TileMap tm, boolean right, Player player) {
		super(tm);
		moveSpeed = 2.3 + ((right ? -1 : 1) * player.getDX() + player.getPeeArcX()) ;
		moveSpeedY = (-0.04 * new Random().nextGaussian() * 6) - (player.getDY() * 0.04) + player.getPeeArcY();
		peeFallSpeed = 0.09;
		
		cWidth = 2;
		cHeight = 2;
		
		if (right) dx = moveSpeed;
		else dx = -moveSpeed;
		dy = moveSpeedY;
	}
	
	public void setHit() {
		if (hit) return;
		hit = true;
		dx = 0;
	}
	
	public boolean shouldRemove() {
		return remove;
	}
	
	public void update() {
		dy += peeFallSpeed;
		checkTileMapCollision();
		setPosition(xTemp, yTemp);
		
		if (dx == 0 || dy == 0 && !hit) setHit();
		
		if (hit) remove = true;
		
	}
	
	public void draw(Graphics2D g) {
		setMapPosition();

		g.setColor(new Color(255, 224, 102));
		g.fillRect((int) (x + xMap - width / 2 + width), (int) (y + yMap - height / 2), cWidth, cHeight);
		g.setColor(new Color(255, 245, 117));
		g.fillRect((int) (x + xMap - width / 2 + width) - 1, (int) (y + yMap - height / 2), cWidth, cHeight);

		
	}
	
}
