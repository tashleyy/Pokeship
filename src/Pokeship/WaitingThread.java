package Pokeship;

import java.io.IOException;

public class WaitingThread extends Thread {
	BattleshipFrame bf;
	
	WaitingThread(BattleshipFrame bf) {
		this.bf = bf;
	}
	
	public void run() {
		try {
			String str = bf.sReader.readLine();
			if (str.equals("yes")) {
				bf.sf.wf.setVisible(false);
				bf.setUp();
				bf.setVisible(true);
				(new ClientThread(bf)).start();
				(new ConnectionThread(bf)).start();
			} else {
				bf.sf.wf.setVisible(false);
				bf.sf.setVisible(true);
				if (bf.sReader != null) {
					bf.sReader.close();
					bf.sReader = null;
				}
				if (bf.sWriter != null) {
					bf.sWriter.close();
					bf.sWriter = null;
				}
				for (int i = 0; i < 2; i++) {
					if (bf.sf.st.sht[i].br != null) {
						bf.sf.st.sht[i].br.close();
						bf.sf.st.sht[i].br = null;
					}
					if (bf.sf.st.sht[i].pw != null) {
						bf.sf.st.sht[i].pw.close();
						bf.sf.st.sht[i].pw = null;
					}
				}
				if (bf.sf.st.ss != null) {
					bf.sf.st.ss.close();
					bf.sf.st.ss = null;
				}
			}
		} catch (IOException ioe) {
			System.out.println("IOException in WaitingThread.run(): " + ioe.getMessage());
		}
	}
}
