package Pokeship;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class RejectThread extends Thread {
	private ServerThread st;
	
	RejectThread(ServerThread st) {
		this.st = st;
	}
	
	public void run() {
		Socket s = null;
		try {
			s = st.ss.accept();
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(s.getOutputStream());
				pw.println("no");
				pw.flush();
			} catch (IOException ioe) {
				System.out.println("IOException in RejectThread.run(): " + ioe.getMessage());
			} finally {
				if (pw != null) pw.close();						
				if (s != null) s.close();
			}
		} catch (IOException ioe) {
			System.out.println("IOException in RejectThread.run(): " + ioe.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException ioe) {
					System.out.println("IOException3 in RejectThread.run(): " + ioe.getMessage());
				}
				s = null;
			}
		}
	}
}
