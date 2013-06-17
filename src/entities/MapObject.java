package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import main.GamePanel;
import main.Sound;
import tilemap.Tile;
import tilemap.TileMap;

@SuppressWarnings("unused")
public abstract class MapObject {
	protected TileMap tilemap;
	private ArrayList<FloatingText> floatText;
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

	protected int standingOnTile;

	private Rectangle standingTileRect;

	// animation
	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingRight;
	protected long flinchTimer;

	// movement

	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;
	protected boolean jumping;
	protected boolean falling;
	protected boolean active;
	protected boolean flinching;

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
		floatText = new ArrayList<FloatingText>();
		standingTileRect = new Rectangle(0,0,30,30);
	}

	public boolean intersects(MapObject o) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getRectangle();
		//		if (r1.intersects(r2) && !intersected) {
		//			intersected = true;
		//			return true;
		//		} else if (r1.intersects(r2) && intersected) {
		//			return false;
		//		}
		return r1.intersects(r2);
	}

	public void addText(String text, double x, double y, int duration, Color color) {
		FloatingText t = new FloatingText(tilemap, text, duration, color, -999.0, false);
		t.setPosition(x, y - 20);
		floatText.add(t);
	}

	public void addText(String text, double x, double y, int duration, Color color, double fallingSpeed) {
		FloatingText t = new FloatingText(tilemap, text, 1500, color, fallingSpeed, false);
		t.setPosition(x, y - 20);
		floatText.add(t);
	}

	public void addText(String text, double x, double y, int duration, Color color, double fallingSpeed, boolean onlyVertical) {
		FloatingText t = new FloatingText(tilemap, text, 1500, color, fallingSpeed, onlyVertical);
		t.setPosition(x, y - 20);
		floatText.add(t);
	}

	public Rectangle getRectangle() {
		return new Rectangle((int)x - cWidth + 3, (int)y - cHeight, cWidth, cHeight);
	}

	public void calculateCorners(double x, double y) {

		int leftTile = (int)(x - cWidth / 2) / tileSize;
		int rightTile = (int)(x + cWidth / 2 - 1) / tileSize;
		int topTile = (int)(y - cHeight / 2) / tileSize;
		int bottomTile = (int)(y + cHeight / 2 - 1) / tileSize;

		//		int leftTile = (int) (x) / tileSize;
		//		int rightTile = (int) (x + 3) / tileSize;
		//		int topTile = (int) (y - cHeight / 2) / tileSize;
		//		int bottomTile = (int) (y + cWidth / 2 - 1) / tileSize;

		if (y < 0) {											// Entity är ovanför banans upper bound
			topTile = bottomTile = 0;
		} else if (y + cWidth / 2 > tilemap.getHeight()) {		// Entity är nedanför banans lower bound
			bottomTile = (tilemap.getHeight() / tileSize) - 1;
			topTile = bottomTile;		
		}

		int tl = tilemap.getType(topTile, leftTile);
		int tr = tilemap.getType(topTile, rightTile);
		int bl = tilemap.getType(bottomTile, leftTile);
		int br = tilemap.getType(bottomTile, rightTile);

		//		System.out.println("XY: " + (leftTile * tileSize) + ", " + (bottomTile * tileSize) +  " - " + (leftTile + rightTile) / 2 + " cHeight: " + cHeight + ", cWidth: " + cWidth);
		standingOnTile = tilemap.getType(bottomTile, (leftTile + rightTile) / 2);
		//		standingTileRect.x = leftTile + rightTile;
		//		standingTileRect.y = (bottomTile);

		//		topLeft = !tilemap.isWalkable(topTile, leftTile);
		//		topRight = !tilemap.isWalkable(topTile, rightTile);
		//		bottomLeft = !tilemap.isWalkable(bottomTile, leftTile);
		//		bottomRight = !tilemap.isWalkable(bottomTile, leftTile);

		topLeft = (tl == Tile.BLOCKED || tl == Tile.EVENT);
		topRight = (tr == Tile.BLOCKED || tr == Tile.EVENT);
		bottomLeft = (bl == Tile.BLOCKED || bl == Tile.EVENT);
		bottomRight = (br == Tile.BLOCKED || br == Tile.EVENT);

	}

	public void updatePosition() {
		curCol = (int) x / tileSize;
		curRow = (int) y / tileSize;

		xDest = x + dx;
		yDest = y + dy;

		xTemp = x;
		yTemp = y;



		for (int i = 0; i < floatText.size(); i++) {
			floatText.get(i).update();
		}
	}

	public void updateCollisionFreeEntities() {
		yTemp += dy;
		xTemp += dx;
	}

	public void checkTileMapCollision() {
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

	public boolean onScreen() {
		boolean res = ((x - cWidth) < (GamePanel.WIDTH - xMap) && ((x + cWidth) > Math.abs(xMap)));
		//		System.out.println("XY: " + x + ", " + y + " - " + res);
		//		System.out.println((tilemap.getWidth() + xMap));
		return res;
	}

	public void draw(Graphics2D g) {


		if (onScreen()) {

			if (flinching) {
				long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
				if (elapsed / 50 % 2 == 0) {
				} else {


					if (facingRight) {
						g.drawImage(animation.getImage(), (int) (x + xMap - width / 2), (int) (y + yMap - height / 2) - 4, null);
					} else {
						g.drawImage(animation.getImage(), (int) (x + xMap - width / 2 + width), (int) (y + yMap - height / 2) - 4, -width, height, null);
					}
				}
			} else {
				if (facingRight) {
					g.drawImage(animation.getImage(), (int) (x + xMap - width / 2), (int) (y + yMap - height / 2) - 4, null);
				} else {
					g.drawImage(animation.getImage(), (int) (x + xMap - width / 2 + width), (int) (y + yMap - height / 2) - 4, -width, height, null);
				}
			}
		}
			for (int i = 0; i < floatText.size(); i++) {
				floatText.get(i).draw(g);
				if (floatText.get(i).shouldRemove()) {
					floatText.remove(i);
					i--;
				}
			}

			//		g.setColor(Color.YELLOW);
			//		g.drawRect((int) ((x + xMap) + standingTileRect.x), (int) ((y + standingTileRect.y) + yMap), standingTileRect.width, standingTileRect.height);

			//		 rita ut hitboxar
			//		Rectangle temp = this.getRectangle();
			//		g.drawRect((int) (x + xMap - width / 2), (int) (y + yMap - height / 2) , (int) temp.getWidth(), (int) temp.getHeight());

		}

	}
