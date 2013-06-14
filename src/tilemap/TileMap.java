package tilemap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.GamePanel;

public class TileMap {
	// pos
	private double x;
	private double y;
	private int spawnPointX;
	private int spawnPointY;

	// bounds
	private int xMin;
	private int yMin;
	private int xMax;
	private int yMax;

	private double smoothCentering;

	// map
	private int[][] map;
	private int tileSize;
	private int numRows;
	private int numCols;
	private int width;
	private int height;

	// tileset
	private BufferedImage tileset;
	private int numTilesAcross;
	private Tile[][] tiles;

	// drawing
	private int rowOffset;
	private int colOffset;
	private int numRowsToDraw;
	private int numColsToDraw;

	public TileMap(int tileSize) {
		this.tileSize = tileSize;
		numRowsToDraw = GamePanel.HEIGHT / tileSize + 2;
		numColsToDraw = GamePanel.WIDTH  / tileSize  + 2;
		smoothCentering = 0.07;
		spawnPointX = 100;
		spawnPointY = 100;
	}

	public void loadTiles(String s) {
		try {
			tileset = ImageIO.read(getClass().getResourceAsStream(s));
			numTilesAcross = tileset.getWidth() / tileSize;
			tiles = new Tile[2][numTilesAcross];

			BufferedImage subimage;
			for (int col = 0; col < numTilesAcross; col++) {
				subimage = tileset.getSubimage(col * tileSize, 0, tileSize, tileSize);
				tiles[0][col] = new Tile(subimage, Tile.NORMAL);
				subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize, tileSize);
				tiles[1][col] = new Tile(subimage, Tile.BLOCKED);
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void loadMap(String s) {
		try {
			InputStream in = getClass().getResourceAsStream(s);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			numCols = Integer.parseInt(br.readLine());
			numRows = Integer.parseInt(br.readLine());
			map = new int[numRows][numCols];
			width = numCols * tileSize;
			height = numRows * tileSize;

			xMin = GamePanel.WIDTH - width;
			xMax = 0;
			yMin = GamePanel.HEIGHT - height;
			yMax = 0;

			String delims = "\\s+";
			for (int row = 0; row < numRows; row++) {
				String line = br.readLine();
				String[] tokens = line.split(delims);
				for (int col = 0; col < numCols; col++) {
					if (tokens[col].equals("sp")) {
						spawnPointX = col * tileSize;
						spawnPointY = row * tileSize;
						map[row][col] = 0;
					} else {
						map[row][col] = Integer.parseInt(tokens[col]);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getTileSize() {
		return tileSize;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getSpawnPointX() {
		return spawnPointX;
	}

	public int getSpawnPointY() {
		return spawnPointY;
	}

	public int getType(int row, int col) {
		try {
			int rc = map[row][col];
			int r = rc / numTilesAcross;
			int c = rc % numTilesAcross;
			return tiles[r][c].getType();
		} catch (ArrayIndexOutOfBoundsException e) {
			//			e.printStackTrace();
		}
		return 0;
	}

	public void setPosition(double x, double y) {
		this.x += (x - this.x) * smoothCentering;
		this.y += (y - this.y) * smoothCentering;
		fixBounds();

		colOffset = (int) -this.x / tileSize;
		rowOffset = (int) -this.y / tileSize;
	}

	private void fixBounds() {
		if (x < xMin) x = xMin;
		if (x > xMax) x = xMax;
		if (y < yMin) y = yMin;
		if (y > yMax) y = yMax;
	}

	public void draw(Graphics2D g) {
		for (int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {
			if (row >= numRows) break;
			for (int col = colOffset; col < colOffset + numColsToDraw; col++) {
				if (col >= numCols) break;
				if (map[row][col] == 0) continue;
				int rc = map[row][col];
				int r = rc / numTilesAcross;
				int c = rc % numTilesAcross;
				g.drawImage(tiles[r][c].getImage(), (int) x + col * tileSize, (int) y + row * tileSize, null);
			}
		}
	}

}
