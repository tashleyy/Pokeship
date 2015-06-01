package Pokeship;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

public class TimerThread extends Thread {
	private long roundStartTime = 0;
	private BattleshipFrame bf;
	
	TimerThread(BattleshipFrame bf) {
		this.bf = bf;
	}
	
	public void run() {
		Random rand = new Random();
		ExecutorService executor = Executors.newCachedThreadPool();
		int compMove = 0;
		roundStartTime = 0;
		while (true) {
			if (!bf.started) break;
			try {
				if (bf.sf.isMaps()) {
					if (System.currentTimeMillis() > roundStartTime+15000 || bf.playerMoved && bf.computerMoved) {
						compMove = rand.nextInt(14500);
						roundStartTime = System.currentTimeMillis();
						bf.startNextRound();
					}
					if (System.currentTimeMillis()-roundStartTime > compMove) {
						if (!bf.computerMoved) {
							executor.execute(new ComputerThread(bf));
						}
					}					
				} else {
					if (System.currentTimeMillis() > roundStartTime+15000 || bf.playerMoved && bf.computerMoved) {
						roundStartTime = System.currentTimeMillis();
						bf.startNextRound();						
					}
				}
				if (15-(int) Math.ceil((System.currentTimeMillis()-roundStartTime)/1000) < 10) {
					bf.timeText = "0:0" + (15-(int) Math.ceil((System.currentTimeMillis()-roundStartTime)/1000));
				} else {
					bf.timeText = "0:" + (15-(int) Math.ceil((System.currentTimeMillis()-roundStartTime)/1000));
				}
				bf.setTimeText();
				sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
		while (!executor.isTerminated()) {}
		if (bf.suddenQuit) {
			bf.suddenQuit = false;
			return;
		}
		if (bf.sf.isMaps()) {
			if (bf.playerSea.allShipsSunken()) {
				JOptionPane.showMessageDialog(bf, "You lost!", "Game Over", JOptionPane.PLAIN_MESSAGE);			
			} else {
				JOptionPane.showMessageDialog(bf, "You won!", "Game Over", JOptionPane.PLAIN_MESSAGE);			
			}			
		} else {
			if (bf.playerWon) {
				JOptionPane.showMessageDialog(bf, "You won!", "Game Over", JOptionPane.PLAIN_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(bf, "You lost!", "Game Over", JOptionPane.PLAIN_MESSAGE);
			}
		}
		int result = JOptionPane.showConfirmDialog(bf, "Would you like to play again?", "Rematch", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (result == JOptionPane.YES_OPTION) {
			if (bf.sf.isMaps()) {
				bf.reset();
			} else {
				bf.sWriter.println("playagain");
				bf.sWriter.flush();
			}
		} else {
			if (bf.sf.isMaps()) {
				bf.reset();
				bf.setVisible(false);
				bf.sf.setVisible(true);
			} else {
				bf.sWriter.println("stop");
				bf.sWriter.flush();
			}
		}
	}
}