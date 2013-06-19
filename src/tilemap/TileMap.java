package tilemap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.GamePanel;

@SuppressWarnings("unused")
public class TileMap {
	private class MapTile {
		private int tile;
		private boolean walkable;
		
		public MapTile(int tile, boolean walkable) {
			this.tile = tile;
			this.walkable = walkable;
		}
		
		public int getTile() {
			return tile;
		}
		
		public boolean isWalkable() {
			return walkable;
		}
	}
	private GamePanel panel;
	
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
	private ArrayList<MapTile[][]> mapList;
	private MapTile[][] map;
	private int tileSize;
	private int numRows;
	private int numCols;
	private int layers;
	private int width;
	private int height;

	// tileset
	private BufferedImage tileset;
	private BufferedImage renderedMap;
	private int numTilesAcross;
	private int numTilesVertical;
	private Tile[][] tiles;

	// drawing
	private int rowOffset;
	private int colOffset;
	private int numRowsToDraw;
	private int numColsToDraw;

	public TileMap(int tileSize, GamePanel panel) {
		this.panel = panel;
		this.tileSize = tileSize;
		numRowsToDraw = panel.getWindowHeight() / tileSize + 5;
		numColsToDraw = panel.getWindowWidth()  / tileSize  + 5;
		smoothCentering = 0.07;
		spawnPointX = 100;
		spawnPointY = 100;
		mapList = new ArrayList<MapTile[][]>();
	}

	public void loadTiles(String s) {
		try {
			tileset = ImageIO.read(getClass().getResourceAsStream(s));
			numTilesAcross = tileset.getWidth() / tileSize;
			numTilesVertical = tileset.getHeight() / tileSize;
			tiles = new Tile[numTilesVertical][numTilesAcross];

			BufferedImage subimage;
			for (int col = 0; col < numTilesAcross; col++) {
				for (int row = 0; row < numTilesVertical; row++) {
					subimage = tileset.getSubimage(col * tileSize, row * tileSize, tileSize, tileSize);
					tiles[row][col] = new Tile(subimage);
//					subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize, tileSize);
//					tiles[1][col] = new Tile(subimage, Tile.NORMAL, true);
//					subimage = tileset.getSubimage(col * tileSize, 60, tileSize, tileSize);
//					tiles[2][col] = new Tile(subimage, Tile.NORMAL, true);
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void loadMap(String s) {
		try {
			InputStream in = getClass().getResourceAsStream(s);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			numCols = Integer.parseInt(br.readLine().trim());
			numRows = Integer.parseInt(br.readLine().trim());
			layers = Integer.parseInt(br.readLine().trim());
			
			for (int i = 0; i < layers; i++) {
				mapList.add(new MapTile[numRows][numCols]);
			}

			map = new MapTile[numRows][numCols];
			width = numCols * tileSize;
			height = numRows * tileSize;
			
			renderedMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			xMin = panel.getWindowWidth() - width;
			xMax = 0;
			yMin = panel.getWindowHeight() - height;
			yMax = 0;

			String delims = "\\s+";
			for (int row = 0; row < numRows; row++) {
				String line = br.readLine().trim();
				String[] tokens = line.split(delims);
				for (int col = 0; col < numCols; col++) {
					String[] current = tokens[col].trim().split(":");
					int layer = Integer.parseInt(current[1].trim().substring(1));
					
					if (current[0].trim().equals("sp")) {
						spawnPointX = col * tileSize;
						spawnPointY = row * tileSize;
						mapList.get(layer - 1)[row][col] = new MapTile(0, true);
//						map[row][col] = new MapTile(0, true);
					} else {
						int tile = Integer.parseInt(current[0].trim());
						boolean walkable = false;

						if (current[2].trim().equals("WW")) {
							tiles[tile / numTilesAcross][tile % numTilesAcross].setType(0);
							walkable = true;
						} else if (current[2].trim().equals("NW")) {
							tiles[tile / numTilesAcross][tile % numTilesAcross].setType(1);
							walkable = false;
						}
						
//						for (int i = 0; i < layer; i++) {
//							
//						}
						
						if (layer == 1) {
						mapList.get(layer - 1)[row][col] = new MapTile(tile, walkable);
						} else if (layer == 2) {
							mapList.get(layer - 2)[row][col] = new MapTile(0, walkable);
							mapList.get(layer - 1)[row][col] = new MapTile(tile, walkable);
						}
						MapTile test = mapList.get(0)[row][col];
//						map[row][col] = new MapTile(tile, walkable);
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public void loadMap(String s) {
//		try {
//			InputStream in = getClass().getResourceAsStream(s);
//			BufferedReader br = new BufferedReader(new InputStreamReader(in));
//			numCols = Integer.parseInt(br.readLine().trim());
//			numRows = Integer.parseInt(br.readLine().trim());
//			map = new int[numRows][numCols];
//			width = numCols * tileSize;
//			height = numRows * tileSize;
//			
//			renderedMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
//			xMin = panel.getWindowWidth() - width;
//			xMax = 0;
//			yMin = panel.getWindowHeight() - height;
//			yMax = 0;
//
//			String delims = "\\s+";
//			for (int row = 0; row < numRows; row++) {
//				String line = br.readLine().trim();
//				String[] tokens = line.split(delims);
//				for (int col = 0; col < numCols; col++) {
//					if (tokens[col].equals("sp")) {
//						spawnPointX = col * tileSize;
//						spawnPointY = row * tileSize;
//						map[row][col] = 0;
//					} else {
//						map[row][col] = Integer.parseInt(tokens[col]);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

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
			MapTile rc = mapList.get(0)[row][col];
//			System.out.println(row + " " + col + " " + mapList.get(0)[row][col].getTile());
			int r = rc.getTile() / numTilesAcross;
			int c = rc.getTile() % numTilesAcross;
			return tiles[r][c].getType();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Actual [row : columns]: " + numRows + ":" + numCols + " -  Referred: " + row + ":" + col);
						e.printStackTrace();
		}
		return 0;
	}
	
//	public int getType(int row, int col) {
//		try {
//			int rc = map[row][col];
//			int r = rc / numTilesAcross;
//			int c = rc % numTilesAcross;
//			return tiles[r][c].getType();
//		} catch (ArrayIndexOutOfBoundsException e) {
//			System.out.println("Actual [row : columns]: " + numRows + ":" + numCols + " -  Referred: " + row + ":" + col);
//						e.printStackTrace();
//		}
//		return 0;
//	}
	
	public boolean isWalkable(int row, int col) {
		try {
//			MapTile rc = map[row][col];
//			int r = rc.getTile() / numTilesAcross;
//			int c = rc.getTile() % numTilesAcross;
			return mapList.get(0)[row][col].isWalkable();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(numRows + ":" + numCols + " - " + row + ":" + col);
						e.printStackTrace();
		}
		return false;
	}

	public void setPosition(double x, double y) {
		xMin = panel.getWindowWidth() - width;
		yMin = panel.getWindowHeight() - height;
		
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
	
	public void draw(Graphics2D g, int layer) {
		for (int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {
			if (row >= numRows) break;
			for (int col = colOffset; col < colOffset + numColsToDraw; col++) {
				if (col >= numCols) break;
				if (mapList.get(layer - 1)[row][col] == null) continue;
				if (mapList.get(layer - 1)[row][col].getTile() == 0) continue;
				MapTile rc = mapList.get(layer - 1)[row][col];
				int r = rc.getTile() / numTilesAcross;
				int c = rc.getTile() % numTilesAcross;
				g.drawImage(tiles[r][c].getImage(), (int) x + col * tileSize, (int) y + row * tileSize, null);
			}
		}
	}
	
//	public void draw(Graphics2D g) {
//		for (int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {
//			if (row >= numRows) break;
//			for (int col = colOffset; col < colOffset + numColsToDraw; col++) {
//				if (col >= numCols) break;
//				if (map[row][col].getTile() == 0) continue;
//				MapTile rc = map[row][col];
//				int r = rc.getTile() / numTilesAcross;
//				int c = rc.getTile() % numTilesAcross;
//				g.drawImage(tiles[r][c].getImage(), (int) x + col * tileSize, (int) y + row * tileSize, null);
//			}
//		}
//	}

}
