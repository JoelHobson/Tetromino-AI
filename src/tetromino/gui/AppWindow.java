package tetromino.gui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import javax.swing.JFrame;

//The main window for the game
public class AppWindow {

	private JFrame tetGame;

	public static AppWindow window;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		launchWindow();
	}
	
	public static void launchWindow() {
		window = new AppWindow();
		window.tetGame.setVisible(true);		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public void addTDisplay(TDisplay td) {		
		tetGame.add(td);					
		tetGame.pack();
		td.setFocusable(true);
		td.requestFocusInWindow();
		
	}
	
	/**
	 * Create the application.
	 */
	public AppWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		tetGame = new JFrame();
		tetGame.setTitle("Falling Tetromino Game");
		tetGame.setBounds(100, 100, 450, 700);
		tetGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tetGame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
	}

}
