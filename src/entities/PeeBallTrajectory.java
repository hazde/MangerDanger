package entities;

import java.awt.BasicStroke;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import tilemap.TileMap;
@SuppressWarnings("unused")
public class PeeBallTrajectory extends MapObject {
	private boolean hit;
	private boolean remove;
	
	private boolean calculated;
	private double ballFallSpeed;
	private double initialYSpeed;
	private Player player;
	private ArrayList<Point> points;
	private boolean drawn;

	public PeeBallTrajectory(TileMap tm, boolean right, Player player) {
		super(tm);

		this.player = player;
		drawn = false;
		reset(right);

		points = new ArrayList<Point>();
		width = 10;
		height = 10;

		cWidth = 2;
		cHeight = 2;
	}

	public void reset(boolean right) {
		if (drawn) {
			points.clear();
			x = player.x;
			y = player.y;

			moveSpeed = (player.moveSpeed * Math.abs(player.dx) * 2.3) + 6.3;
			ballFallSpeed = 0.15 + player.fallSpeed;
			initialYSpeed = -2.1;
			if (right) dx = moveSpeed;
			else dx = -moveSpeed;
			dy = initialYSpeed;
			calculated = false;
			hit = false;
			drawn = false;
			update();
		}
	}

	public void setHit() {
		if (hit) return;
		hit = true;
	}

	public boolean shouldRemove() {
		return remove;
	}

	public void update() {
		setMapPosition();
		while (!hit) {
			if (dx == 0 || dy == 0) setHit();	
			dy += ballFallSpeed;
			updatePosition();
			checkTileMapCollision();
			setPosition(xTemp, yTemp);
			//			System.out.println(this.getX() + " " + this.getY());
			points.add(new Point((int) (x + xMap), (int) (y + yMap -height / 2 + 4)));
			
		}
		reset(player.facingRight);
	}

	public void setDrawn() {
		drawn = true;
	}
	int c;
	public void draw(Graphics2D g) {
		if (points.size() != 0) {
			c = 255 / points.size();
		} else {
			c = 0;
		}
		for (int i = 0; i < points.size() - 2; i++) {
			g.setColor(new Color(i * c, 0, 0));
				g.drawLine((int) points.get(i).getX(), (int) points.get(i).getY(), (int) points.get(i + 1).getX(), (int) points.get(i + 1).getY());
		}



		//		g.drawLine((int) (player.x + player.xMap - player.width / 2 + player.width / 2), (int) (player.y + player.yMap - player.height / 2 + 20), (int) (x + xMap ), (int) (y + yMap -height / 2 + 4));
		setDrawn();

	}

}
