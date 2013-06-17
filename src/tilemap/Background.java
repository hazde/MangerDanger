package tilemap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.GamePanel;

public class Background {
	private BufferedImage image;
	private double x;
	private double y;
	private double dx;
	private double dy;

	private GamePanel panel;
	
	private double moveScale;

	public Background(String path, double moveScale, GamePanel panel) {
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path));
			this.moveScale = moveScale;
			this.panel = panel;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setPosition(double x, double y) {
		this.x = (x * moveScale) % GamePanel.WIDTH;
		this.y = (y * moveScale) % GamePanel.HEIGHT;
	}

	public void setScroll(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public void update() {
		x += dx;
		y += dy;
	}

	public void draw(Graphics2D g) {
//		System.out.println(GamePanel.WIDTH + " " + GamePanel.HEIGHT + " - " +  panel.getWindowWidth() + " " + panel.getWindowHeight());
		g.drawImage(image, (int) x, (int) y, panel.getWindowWidth(), panel.getWindowHeight(), null);

		if (x < 0) {
			g.drawImage(image, (int) x + panel.getWindowWidth(), (int) y, panel.getWindowWidth(), panel.getWindowHeight(), null);
		}
		
		if (x > 0) {
			g.drawImage(image, (int) x - panel.getWindowWidth(), (int) y, panel.getWindowWidth(), panel.getWindowHeight(), null);
		}
		
		if ((x + image.getWidth()) < 0 || (x - image.getWidth() > 0)) {
			x = 0;
		}

	}

}
