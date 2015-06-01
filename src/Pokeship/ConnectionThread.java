package Pokeship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

public class ConnectionThread extends Thread {
	BattleshipFrame bf;
	
	ConnectionThread(BattleshipFrame bf) {
		this.bf = bf;
	}

	public void run() {
		while (true) {
			try {
				URL toCheckIp = new URL("http://checkip.amazonaws.com");
				BufferedReader br = new BufferedReader(new InputStreamReader(toCheckIp.openStream()));
				br.readLine();
				sleep(1000);
			} catch (MalformedURLException mue) {
				System.out.println("MalformedURLException: " + mue.getMessage());
			} catch (IOException ioe) {
				System.out.println("IOException: " + ioe.getMessage());
				JOptionPane.showMessageDialog(bf, "You have lost the connection!", "Connection Error", JOptionPane.WARNING_MESSAGE);
				bf.sWriter.println("quit");
				bf.sWriter.flush();
				bf.suddenQuit = true;
				bf.reset();
				bf.setVisible(false);
				bf.sf.setUp();
				bf.sf.setVisible(true);
				break;
			} catch (InterruptedException ie) {
				System.out.println("InterruptedException: " + ie.getMessage());
			}
		}
	}
}
