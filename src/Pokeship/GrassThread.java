package Pokeship;

public class GrassThread extends Thread {
	private BackgroundLabel bl;
	
	GrassThread(BackgroundLabel bl) {
		this.bl = bl;
	}
	
	public void run() {
			bl.repaint();
	}
}