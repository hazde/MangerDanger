package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

import tilemap.TileMap;

public class Blood extends MapObject {
	private boolean hit;
	private boolean remove;
	private double moveSpeedY;
	private double bloodFallSpeed;
	private double bloodRise;
	private Color color;

	public Blood(TileMap tm, boolean right, boolean b, Color color) {
		super(tm);
			moveSpeed = (1.8 * (new Random().nextDouble() * 3.3)) * (new Random().nextInt(2) == 1 ? -1: 1) ;
			bloodFallSpeed = 0.05;
			bloodRise = -29.13 * (new Random().nextDouble() * 0.2);
			cWidth = 1 + (new Random().nextInt(2) + 1);
			cHeight = 1 + (new Random().nextInt(2) + 1);
	
		if (color == null) {
			this.color = new Color(0xFFEC1E);
		} else {
			this.color = color;
		}

		dx = moveSpeed;
		dy = bloodRise;

	}
	
	public void setHit() {
		if (hit) return;
		hit = true;
		dx = 0;
	}

	@Override
	public Rectangle getRectangle() {
		return new Rectangle((int)x - (cWidth / 2), (int)y - (cHeight / 2), cWidth, cHeight);
	}
	
	public boolean shouldRemove() {
		return remove;
	}

	public void update() {
		
		dy += bloodFallSpeed;

		updatePosition();
		checkTileMapCollision();
		setPosition(xTemp, yTemp);

		
		if (dy == 0 && dx == 0 && !hit) {
			setHit();
		} else {
			if (dx == 0 && dy != 0) {
				dx = -dy * (new Random().nextDouble() * 2.3);
			} 
//			else if (dy == 0) {
//				dx = 0;
//			}
		}
		
		if (y > tilemap.getHeight()) remove = true;
		if (hit) remove = true;

	}

	public void draw(Graphics2D g) {
		setMapPosition();
		
//		g.setColor(new Color(0x090909));
//		g.fillRect((int) (x + xMap - cWidth / 2 + cWidth), (int) (y + yMap - cHeight / 2) + 1, cWidth, cHeight);
//		g.setColor(new Color(0xFFEC1E));
//		g.fillRect((int) (x + xMap - cWidth / 2 + cWidth), (int) (y + yMap - cHeight / 2), cWidth, cHeight);
		
		g.setColor(new Color(0x090909));
		g.fillRect((int) (x + xMap), (int) (y + yMap), cWidth, cHeight);
		g.setColor(color);
		g.fillRect((int) (x + xMap), (int) (y + yMap), cWidth, cHeight);

	}

}
