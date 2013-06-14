package entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import main.GamePanel;
import main.Sound;
import tilemap.Tile;
import tilemap.TileMap;

public abstract class MapObject {
	protected TileMap tilemap;
	protected int tileSize;
	protected double xMap;
	protected double yMap;
	
	// position
	protected double x;
	protected double y;
	protected double dx;
	protected double dy;
	
	// dimension
	protected int width;
	protected int height;
	
	// collision box
	protected int cWidth;
	protected int cHeight;
	
	// collision
	protected int curRow;
	protected int curCol;
	protected double xDest;
	protected double yDest;
	protected double xTemp;
	protected double yTemp;
	protected boolean intersected;
	
	protected boolean topLeft;
	protected boolean topRight;
	protected boolean bottomLeft;
	protected boolean bottomRight;
	
	// animation
	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingRight;
	
	// movement
	
	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;
	protected boolean jumping;
	protected boolean falling;
	protected boolean active;
	
	// movement attributes
	protected double moveSpeed;
	protected double maxSpeed;
	protected double stopSpeed;
	protected double fallSpeed;
	protected double maxFallSpeed;
	protected double jumpStart;
	protected double stopJumpSpeed;
	
	public MapObject(TileMap tm) {
		tilemap = tm;
		tileSize = tm.getTileSize();
		intersected = false;
	}
	
	public boolean intersects(MapObject o) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getRectangle();
		if (r1.intersects(r2) && !intersected) {
			intersected = true;
			return true;
		} else if (r1.intersects(r2) && intersected) {
			return false;
		}
		return r1.intersects(r2);
	}
	
	public Rectangle getRectangle() {
		return new Rectangle((int)x - cWidth, (int)y - cHeight, cWidth, cHeight);
	}
	
	public void calculateCorners(double x, double y) {
		
		int leftTile = (int) (x - cWidth / 2) / tileSize;
		int rightTile = (int) (x + cWidth / 2 - 1) / tileSize;
		int topTile = (int) (y - cHeight / 2) / tileSize;
		int bottomTile = (int) (y + cWidth / 2 - 1) / tileSize;
		
		if (y < 0) {
			topTile = 0;
			bottomTile = 0;
		}
		
		int tl = tilemap.getType(topTile, leftTile);
		int tr = tilemap.getType(topTile, rightTile);
		int bl = tilemap.getType(bottomTile, leftTile);
		int br = tilemap.getType(bottomTile, rightTile);
		
		topLeft = tl == Tile.BLOCKED;
		topRight = tr == Tile.BLOCKED;
		bottomLeft = bl == Tile.BLOCKED;
		bottomRight = br == Tile.BLOCKED;
		
	}
	
	public void checkTileMapCollision() {
		curCol = (int) x / tileSize;
		curRow = (int) y / tileSize;
		
		xDest = x + dx;
		yDest = y + dy;
		
		xTemp = x;
		yTemp = y;
		
		calculateCorners(x, yDest);
		if (dy < 0) {
			if (topLeft || topRight) {
				dy = 0;
				yTemp = curRow * tileSize  + cHeight / 2;
			} else {
				yTemp += dy;
			}
		}
		
		if (dy > 0) {
			if (bottomLeft || bottomRight) {
				dy = 0;
				falling  = false;
				yTemp = (curRow + 1) * tileSize - cHeight / 2;
			} else {
				yTemp += dy;
			}
		}
		
		calculateCorners(xDest, y);
		if (dx < 0) {
			if (topLeft || bottomLeft) {
				dx = 0;
				xTemp = curCol * tileSize  + cWidth / 2;
			} else {
				xTemp += dx;
			}
		}
		if (dx > 0) {
			if (topRight || bottomRight) {
				dx = 0;
				xTemp = (curCol + 1) * tileSize - cWidth / 2;
			} else {
				xTemp += dx;
			}
		}
		
		if (!falling) {
			calculateCorners(x, yDest + 1);
			if (!bottomLeft && !bottomRight) {
				falling = true;
			}
		}
		
	}
	
	public int getX() {return (int) x;}
	
	public int getY() {return (int) y;}
	
	public int getWidth() {return width;}
	
	public int getHeight() {return height;}
	
	public int getCWidth() {return cWidth;}
	
	public int getHCeight() {return cHeight;}
	
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public void setMapPosition() {
		xMap = tilemap.getX();
		yMap = tilemap.getY();
	}
	
	public void setLeft(boolean b) {left = b;}
	public void setRight(boolean b) {right = b;}
	public void setUp(boolean b) {up = b;}
	public void setDown(boolean b) {down = b;}
	public void setJumping(boolean b) {
		if (!jumping && !falling && dy == 0) Sound.jump.play();
		jumping = b;
	}
	public double getDX() {return dx;}
	public double getDY() {return dy;}

	public boolean notOnScreen() {
		return x + xMap + width < 0 || x + xMap - width > GamePanel.WIDTH || y + yMap + height < 0 || y + yMap - height > GamePanel.HEIGHT;
	}
	
	public void draw(Graphics2D g) {
		if (facingRight) {
			g.drawImage(animation.getImage(), (int) (x + xMap - width / 2), (int) (y + yMap - height / 2) - 4, null);
		} else {
			g.drawImage(animation.getImage(), (int) (x + xMap - width / 2 + width), (int) (y + yMap - height / 2) - 4, -width, height, null);
		}
	}
	
}
