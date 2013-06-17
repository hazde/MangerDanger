package tilemap;

import java.awt.image.BufferedImage;

public class Tile {
	public static final int NORMAL = 0;
	public static final int BLOCKED = 1;
	public static final int EVENT = 2;
	public static final int GET = 3;

	private BufferedImage image;
	private int type;
	private boolean walkable;
	
	public Tile(BufferedImage image, int type, boolean walkable) {
		this.image = image;
		this.type = type;
		this.walkable = walkable;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public int getType() {
		return type;
	}
	
	public boolean isWalkable() {
		return walkable;
	}
	
	public static String getTileProperty(int tileType) {
		String res = "";
		if (tileType == NORMAL) {
			res = "Air/void - passable";
		} else if (tileType == BLOCKED) {
			res = "Blocked";
		} else if (tileType == EVENT) {
			res = "Event block";
		} else if (tileType == GET) {
			res = "Get block";
		}
		return res;
	}
	
	
}
