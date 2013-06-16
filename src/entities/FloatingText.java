package entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

import tilemap.TileMap;
@SuppressWarnings("unused")
public class FloatingText extends MapObject {
	private String text;
	private int duration;
	private boolean shouldRemove;
	private long startTime;
	private double moveSpeedY;
	private double textFallSpeed;
	private double textUpStart;
	private Color color;
	
	public FloatingText(TileMap tm, String msg, int duration, Color color, double fallingSpeed, boolean onlyVertical) {
		super(tm);
		text = msg;
		this.duration = duration;
		this.color = color;
		shouldRemove = false;
		
		moveSpeed = (0.3 * (new Random().nextInt(2) == 1 ? -1: 1)) * (new Random().nextGaussian() * 1.5);
		moveSpeedY = (-0.04 * new Random().nextGaussian() * 6);
		if (fallingSpeed == -999.0) {
			textFallSpeed = 0.4;
		} else {
			textFallSpeed = fallingSpeed;
		}
		textUpStart = -1.4;
		
		startTime = System.nanoTime();
		
		cWidth = 20;
		cHeight = 2;
		
		if (!onlyVertical) dx = -moveSpeed;
		dy = textUpStart;
		
	}
	
	public boolean shouldRemove() {
		return shouldRemove;
	}
	
	public void update() {
		dy += textFallSpeed * 0.1;
		updatePosition();
		updateCollisionFreeEntities();
		setPosition(xTemp, yTemp);
//		if (dx < 0 && dy < 0) {
//			shouldRemove = true;
//		}
		
		long elapsed = (System.nanoTime() - startTime) / 1000000;
		if (elapsed > duration) {
			shouldRemove = true;
		}
	}
	
	public void draw(Graphics2D g) {
		setMapPosition();

		
		g.setFont(new Font("Arial", Font.BOLD, 12));
		g.setColor(Color.BLACK);
		g.drawString(text, (float) (x + xMap - width / 2 + width), (float) (y + yMap - height / 2));
		g.setColor(color);
		g.drawString(text, (float) (x + xMap - width / 2 + width) - 1, (float) (y + yMap - height / 2) - 1);
		
		
//		g.fillRect((int) (x + xMap - width / 2 + width), (int) (y + yMap - height / 2), cWidth, cHeight);
		
//		Rectangle temp = this.getRectangle();
//		g.setColor(Color.BLACK);
//		g.drawRect((int) (x + xMap - width / 2) , (int) (y + yMap - height / 2), (int) temp.getWidth(), (int) temp.getHeight());
		
//		g.setColor(new Color(255, 245, 117));
//		g.fillRect((int) (x + xMap - width / 2 + width) - 1, (int) (y + yMap - height / 2), cWidth, cHeight);

		
	}
	

}
