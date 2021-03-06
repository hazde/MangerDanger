package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

import tilemap.TileMap;

public class Pee extends MapObject {
	private boolean hit;
	private boolean remove;
	private double moveSpeedY;
	private double peeFallSpeed;
	private double peeRise;
	private boolean fromPeeBall;
	private boolean fromDirectionRight;
	private Color color;

	public Pee(TileMap tm, boolean right, Player player, boolean b, Color color) {
		super(tm);
		if (player != null) {
			if (!player.isJumping() && !player.isFalling()) {
				moveSpeed = 2.3 + ((right ? -1 : 1) * player.getDX() + player.getPeeArcX()) ;
				moveSpeedY = -0.36 + (-0.04 * new Random().nextGaussian() * 6) - (player.getDY() * 0.04) + player.getPeeArcY();
				peeFallSpeed = 0.037;
			} else {
				moveSpeed = 2.3 + ((right ? -1 : 1) * player.getDX() + player.getPeeArcX()) ;
				moveSpeedY = (-0.04 * new Random().nextGaussian() * 6) - (player.getDY() * 0.04) + player.getPeeArcY();
				peeFallSpeed = 0.09;
			}
			cWidth = 2;
			cHeight = 2;
			fromPeeBall = false;
		} else {
			moveSpeed = (3.8 * (new Random().nextDouble() * 1.3)) ;
			peeFallSpeed = 0.29;
			peeRise = -26.13 * (new Random().nextDouble() * 0.2);
			fromPeeBall = true;
			cWidth = 2; // + (new Random().nextInt(2) + 1);
			cHeight = 2; // + (new Random().nextInt(2) + 1);
		}
		
		if (color == null) {
			this.color = new Color(0xFFEC1E);
		} else {
			this.color = color;
		}
		
		fromDirectionRight = b;
		

		//		dx = moveSpeed;
		if (right) {
			dx = moveSpeed;
		} else {
			dx = -moveSpeed;
		}
		
		if (player != null) {
			dy = moveSpeedY;
		} else {
			dy = peeRise;
		}

	}
	
	public boolean isFromPeeBall() {return fromPeeBall;}
	
	public boolean getFromDirectionRight() {
		return fromDirectionRight;
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
		
		dy += peeFallSpeed;

		updatePosition();
		checkTileMapCollision();
		setPosition(xTemp, yTemp);

		if (dx == 0 || dy == 0 && !hit) setHit();
		
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
