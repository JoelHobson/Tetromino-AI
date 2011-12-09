package tetromino.gui;

public class Block {
	public int x;
	public int y;
	public int colour;
	public static int SIZE = 20;
	public Block(int x, int y, int colour) {
		this.x = x;
		this.y = y;
		this.colour = colour;
	}
}