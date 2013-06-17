package entities;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.GamePanel;

@SuppressWarnings("unused")
public class HUD {
	private Player player;
	private GamePanel panel;
	private BufferedImage stats;
	private BufferedImage toolbar;
	private BufferedImage toolItem;
	private BufferedImage toolItem2;
	private Font font;
	
	public HUD(Player player, GamePanel panel) {
		this.player = player;
		this.panel = panel;
		try {
			
			stats = ImageIO.read(getClass().getResourceAsStream("/HUD/hud2.png"));
			toolbar = ImageIO.read(getClass().getResourceAsStream("/HUD/toolbar.png"));
			toolItem = ImageIO.read(getClass().getResourceAsStream("/HUD/tool_item_pee.png"));
			toolItem2 = ImageIO.read(getClass().getResourceAsStream("/HUD/tool_item_ballon.png"));
			font = new Font("System", Font.PLAIN, 10);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics2D g) {
		g.drawImage(stats, 0, 5, null);
		g.drawImage(toolbar, panel.getWindowWidth() / 2 - 75 , panel.getWindowHeight() - 30, null);
//		g.drawImage(toolItem, (GamePanel.WIDTH / 2 - 75) + 3, (GamePanel.HEIGHT - 40) + 3, (GamePanel.WIDTH / 2 - 75) + 29, (GamePanel.HEIGHT - 3), 0, 0, 26, 26, null);
//		g.drawImage(toolItem2, (GamePanel.WIDTH / 2 - 75) + 29, (GamePanel.HEIGHT - 40) + 3, (GamePanel.WIDTH / 2 - 75) + 60, (GamePanel.HEIGHT - 3), 0, 0, 26, 26, null);
		g.setFont(font);
		g.drawString(player.getHealth() + "/" + player.getMaxHealth(), 23, 18);
		g.drawString(player.getPee() / 100 + "/" + player.getMaxPee() / 100, 20, 39);
		g.drawString(player.getExperience() + "/" + player.getNextLevel(), 29, 59);
	}
}
