package Pokeship;

import javax.swing.ImageIcon;

public class PokeballThread extends Thread {
	private BattleshipFrame bf;
	private boolean isPlayerGrid, isSunkAnim;
	private int vert, hori;
	private ImageIcon finalIcon;
	private static ImageIcon poke1 = new ImageIcon(new ImageIcon("img/pokeball-l.jpg").getImage().getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH)),
			poke2 = new ImageIcon(new ImageIcon("img/pokeball-lm.jpg").getImage().getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH)),
			poke3 = new ImageIcon(new ImageIcon("img/pokeball-m.jpg").getImage().getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH)),
			poke4 = new ImageIcon(new ImageIcon("img/pokeball-mr.jpg").getImage().getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH)),
			poke5 = new ImageIcon(new ImageIcon("img/pokeball-r.jpg").getImage().getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH));

	PokeballThread(BattleshipFrame bf, boolean isPlayerGrid, int vert, int hori, ImageIcon finalIcon, boolean isSunkAnim) {
		this.bf = bf;
		this.isPlayerGrid = isPlayerGrid;
		this.vert = vert;
		this.hori = hori;
		this.finalIcon = finalIcon;
		this.isSunkAnim = isSunkAnim;
	}
	
	public void run() {
		BackgroundLabel bl = (BackgroundLabel) bf.getGridLabel(isPlayerGrid, vert, hori);
		try {
			bl.lock.lock();
			bl.setIcon(poke3);
			sleep(50);
			bl.setIcon(poke2);
			sleep(50);
			bl.setIcon(poke1);
			sleep(100);
			bl.setIcon(poke2);
			sleep(150);
			if (isSunkAnim) (new SoundThread("sound/sinking.wav")).start();
			else (new SoundThread("sound/splash.wav")).start();
			bl.setIcon(poke3);
			sleep(250);
			bl.setIcon(poke4);
			sleep(50);
			bl.setIcon(poke5);
			sleep(100);
			bl.setIcon(poke4);
			sleep(50);
			bl.setIcon(poke3);
			sleep(100);
			bl.setIcon(finalIcon);
		} catch (InterruptedException ie) {
			System.out.println("InterruptedException: " + ie.getMessage());
		} finally {
			bl.lock.unlock();
		}
	}
}
