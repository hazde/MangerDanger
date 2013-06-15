package entities;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class HUD {
	private Player player;
	private BufferedImage image;
	private Font font;
	
	public HUD(Player player) {
		this.player = player;
		try {
			
			image = ImageIO.read(getClass().getResourceAsStream("/HUD/hud2.png"));
			font = new Font("System", Font.PLAIN, 10);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics2D g) {
		g.drawImage(image, 0, 5, null);
		g.setFont(font);
		g.drawString(player.getHealth() + "/" + player.getMaxHealth(), 23, 18);
		g.drawString(player.getPee() / 100 + "/" + player.getMaxPee() / 100, 20, 39);
		g.drawString(player.getExperience() + "/" + player.getNextLevel(), 29, 59);
	}
}
