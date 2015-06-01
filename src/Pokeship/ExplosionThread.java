package Pokeship;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;

public class ExplosionThread extends Thread {
	private BattleshipFrame bf;
	private boolean isPlayerGrid;
	private int vert, hori;
	private ImageIcon finalIcon;
	private static ImageIcon expl1 = new ImageIcon(new ImageIcon("img/expl1.png").getImage().getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH)), 
			expl2 = new ImageIcon(new ImageIcon("img/expl2.png").getImage().getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH)), 
			expl3 = new ImageIcon(new ImageIcon("img/expl3.png").getImage().getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH)),
			expl4 = new ImageIcon(new ImageIcon("img/expl4.png").getImage().getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH)),
			expl5 = new ImageIcon(new ImageIcon("img/expl5.png").getImage().getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH));

	ExplosionThread(BattleshipFrame bf, boolean isPlayerGrid, int vert, int hori, ImageIcon finalIcon) {
		this.bf = bf;
		this.isPlayerGrid = isPlayerGrid;
		this.vert = vert;
		this.hori = hori;
		this.finalIcon = finalIcon;
	}

	public void run() {
		if (bf.sf.isMaps()) {
			BackgroundLabel bl = (BackgroundLabel) bf.getGridLabel(isPlayerGrid, vert, hori);
			Ship ship = bf.getShip(isPlayerGrid, vert+1, hori);

			try {
				bl.lock.lock();
				(new SoundThread("sound/explode.wav")).start();
				bl.setIcon(expl1);
				sleep(250);
				bl.setIcon(expl2);
				sleep(250);
				bl.setIcon(expl3);
				sleep(250);
				bl.setIcon(expl4);
				sleep(250);
				bl.setIcon(expl5);
				sleep(335);
				ship.hitAnims++;
				bl.setIcon(finalIcon);
			} catch (InterruptedException ie) {
				System.out.println("InterruptedException: " + ie.getMessage());
			} finally {
				bl.lock.unlock();
			}
			if (ship.hitAnims == ship.size) {
				ExecutorService executor = Executors.newCachedThreadPool();
				for (int i = 0; i < ship.size; i++) {
					executor.execute(new PokeballThread(bf, isPlayerGrid, ship.shipParts[i].coordinate.vert-1, ship.shipParts[i].coordinate.hori, finalIcon, true));
				}
				executor.shutdown();
				while (!executor.isTerminated()) {}
			}
			
			if (bf.started && bf.computerSea.allShipsSunken()) {
				bf.setEnabled(false);
				bf.started = false;
			} else if (bf.started && bf.playerSea.allShipsSunken()) {
				bf.setEnabled(false);
				bf.started = false;
			}
		} else {
			BackgroundLabel bl = (BackgroundLabel) bf.getGridLabel(isPlayerGrid, vert, hori);
			try {
				bl.lock.lock();
				(new SoundThread("sound/explode.wav")).start();
				bl.setIcon(expl1);
				sleep(250);
				bl.setIcon(expl2);
				sleep(250);
				bl.setIcon(expl3);
				sleep(250);
				bl.setIcon(expl4);
				sleep(250);
				bl.setIcon(expl5);
				sleep(335);
				bl.setIcon(finalIcon);
			} catch (InterruptedException ie) {
				System.out.println("InterruptedException: " + ie.getMessage());
			} finally {
				bl.lock.unlock();
			}
		}
	}
}
