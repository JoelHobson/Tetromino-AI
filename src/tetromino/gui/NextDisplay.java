package tetromino.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;


//Displays the next block to appear
public class NextDisplay extends JPanel {
	
	private Block[] blocks = null;
	private Color blockColor = new Color(0x00AACC);
	
	/**
	 * Create the panel.
	 */
	public NextDisplay() {
		setBorder(null);
		this.setPreferredSize(new Dimension(6 * Block.SIZE, 6 * Block.SIZE));		
		this.setBackground(Color.BLACK);	
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (blocks != null) {
			int x = 0;
			for (Block b : blocks) {
				x = b.x > x ? b.x : x;
			}
			x += 1;
			int width = this.getWidth();
			int origin = (width - x * Block.SIZE)/2;
						
			for (Block b : blocks) {
				if (b == null) continue;	
				if (b.colour == 0) {
					g.setColor(Color.BLACK);
				} else {
					g.setColor(blockColor);
				}
				g.fillRect(b.x * Block.SIZE + origin, b.y * Block.SIZE + origin, Block.SIZE, Block.SIZE);
				g.setColor(Color.BLACK);
				g.drawRect(b.x * Block.SIZE + origin, b.y * Block.SIZE + origin, Block.SIZE, Block.SIZE);
			}
		}
	}
	
	public void drawBlocks(Block[] blocks) {
		this.blocks = blocks;
		repaint();
	}
	
}
