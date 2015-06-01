package Pokeship;

public class CountdownThread extends Thread {
	private WaitingFrame wf;
	
	CountdownThread (WaitingFrame wf) {
		this.wf = wf;
	}
	
	public void run() {
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() < startTime + 30000) {
			wf.jl.setText("Waiting for another player... " + (30 - (System.currentTimeMillis() - startTime) / 1000) + "s until timeout.");
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("InterruptedException in CountdownThread.run(): " + e.getMessage());
			}
		}
	}
}
