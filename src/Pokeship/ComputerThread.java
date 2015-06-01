package Pokeship;

public class ComputerThread extends Thread {
	BattleshipFrame bf;
	
	ComputerThread(BattleshipFrame bf) {
		this.bf = bf;
	}
	
	public void run() {
		bf.computerMove();
	}
}
