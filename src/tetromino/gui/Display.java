package tetromino.gui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class Display extends JPanel {
	
	private Block[] blocks = new Block[1];
	private Color[] colours = {Color.BLACK, Color.GRAY, new Color(0x45505A), new Color(0x728C77), new Color(0xF0F2AE), new Color(0xF0F2AE), new Color(0xE8C48E), new Color(0xE8C48E), new Color(0xB2816B)};
	/**
	 * Create the panel.
	 */
	public Display() {
		this.setBorder(new BevelBorder(BevelBorder.LOWERED, colours[2], colours[2]));
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);		
		for (Block b : blocks) {
			if (b == null) continue;			
			g.setColor(colours[b.colour]);					
			g.fillRect(b.x * Block.SIZE, b.y * Block.SIZE, Block.SIZE, Block.SIZE); //Draw the block
			g.setColor(Color.BLACK);
			g.drawRect(b.x * Block.SIZE - 1, b.y * Block.SIZE - 1, Block.SIZE, Block.SIZE); //Draw the outline
		}
	}
	
	public void drawBlocks(Block[] blocks) {
		this.blocks = blocks;
		repaint();
	}
	
}
