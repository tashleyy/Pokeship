package Pokeship;

public class SwitchGrassThread extends Thread {
	BattleshipFrame bf;
	
	SwitchGrassThread(BattleshipFrame bf) {
		this.bf = bf;
	}
	
	public void run() {
		while (true) {
			if (BattleshipFrame.currentGrass == BattleshipFrame.g1) {
				BattleshipFrame.currentGrass = BattleshipFrame.g2;
			} else {
				BattleshipFrame.currentGrass = BattleshipFrame.g1;
			}
			for (int i = 0; i < 10; i++) {
				for (int j = 1; j < 11; j++) {
					(new GrassThread((BackgroundLabel) bf.playerGridLabels[i][j])).start();
					(new GrassThread((BackgroundLabel) bf.computerGridLabels[i][j])).start();
				}
			}
			try {
				sleep(1600);
			} catch (InterruptedException ie) {
				System.out.println("InterruptedException: " + ie.getMessage());
			}
		}
	}
}
