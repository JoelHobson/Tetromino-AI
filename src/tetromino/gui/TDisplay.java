package tetromino.gui;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

//Displays the game information
public class TDisplay extends JPanel {

		
	private Block[] blocks = new Block[1];
	
	private JLabel name;
	private JLabel score;
	private JLabel level;
	private Display display;
	private NextDisplay nextPiece;
	private JPanel p;
	
	
	/**
	 * Create the panel.
	 */
	public TDisplay(Dimension size) {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));
				
		display = new Display();
		display.setPreferredSize(size);
		add(display, BorderLayout.CENTER);
		
		nextPiece = new NextDisplay();		
		p = new JPanel();
		p.setBackground(Color.BLACK);
		p.setLayout(new BorderLayout(0, 0));
		p.setPreferredSize(new Dimension(6 * Block.SIZE, size.height));
		add(p, BorderLayout.EAST);
		
		p.add(nextPiece, BorderLayout.NORTH);
		
		
		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(Color.BLACK);
		add(infoPanel, BorderLayout.SOUTH);
		infoPanel.setLayout(new BorderLayout(0, 0));

		level = new JLabel("Level: 1");
		level.setForeground(new Color(0x728C77));
		level.setFont(new Font("Arial Black", Font.PLAIN, 20));
		infoPanel.add(level, BorderLayout.EAST);		
		name = new JLabel("Name");				
		name.setForeground(new Color(0x728C77));
		name.setHorizontalAlignment(SwingConstants.LEFT);
		name.setFont(new Font("Arial Black", Font.PLAIN, 20));
		infoPanel.add(name, BorderLayout.CENTER);
		score = new JLabel("0");
		score.setForeground(new Color(0x728C77));
		score.setHorizontalAlignment(SwingConstants.RIGHT);
		score.setFont(new Font("Batang", Font.PLAIN, 30));
		infoPanel.add(score, BorderLayout.SOUTH);
								
		blocks[0] = new Block(3, 4, 0);
		setVisible(true);
		
		
	}
	
	public void setName(String text) {
		name.setText(text);
	}
	
	public void setScore(int newScore) {
		score.setText("" + newScore*100);
	}

	public void setLevel(int newLevel) {
		level.setText("Level: " + newLevel);
	}
	
	
	public void drawBlocks(Block[] blocks) {
		display.drawBlocks(blocks);		
	}
	
	public void drawNextBlock(Block[] blocks) {
		nextPiece.drawBlocks(blocks);
	}
}
