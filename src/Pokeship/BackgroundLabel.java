package Pokeship;

import java.awt.Graphics;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class BackgroundLabel extends JLabel {
	private static final long serialVersionUID = 1;
	Lock lock = new ReentrantLock();

	public BackgroundLabel() {
		super();
	}
	
	public BackgroundLabel(String string) {
		super(string);
	}

	public BackgroundLabel(ImageIcon ii) {
		super(ii);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (getIcon() == null) {
			g.drawImage(BattleshipFrame.currentGrass, 0, 0, getWidth(), getHeight(), this);							
		}
	}
}
