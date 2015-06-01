package Pokeship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class AcceptThread extends Thread {
	private ServerThread st;
	private int i = 0;
	
	AcceptThread(ServerThread st, int i) {
		this.st = st;
		this.i = i;
	}
	
	public void run() {
		try {
			st.sht[i] = new ServerHelperThread(i, st);
			st.sht[i].s = null;
			st.sht[i].s = st.ss.accept();
			st.sht[i].br = new BufferedReader(new InputStreamReader(st.sht[i].s.getInputStream()));
			st.sht[i].pw = new PrintWriter(st.sht[i].s.getOutputStream());
			st.sht[i].start();
		} catch (IOException ioe) {
			System.out.println("IOException in AcceptThread: " + ioe.getMessage());
		}
	}
}
