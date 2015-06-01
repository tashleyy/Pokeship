package Pokeship;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThread extends Thread{
	ServerSocket ss;
	ServerHelperThread[] sht;
	boolean b = true;
	
	ServerThread(int port) {
		try {
			ss = new ServerSocket(port);
		} catch (IOException ioe) {
			System.out.println("IOException in ServerThread();" + ioe.getMessage());
		}
		sht = new ServerHelperThread[2];
		for (int i = 0; i < 2; i++) sht[i] = null;
	}
	
	public void run() {
		AcceptThread at = new AcceptThread(this, 1);
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(at);
		executor.shutdown();
		long startTime = System.currentTimeMillis();
		while (!executor.isTerminated() && System.currentTimeMillis() < startTime + 30000) {}
		if (sht[1].s == null) {
			b = false;
			sht[0].pw.println("no");
			sht[0].pw.flush();
			return;
		} else {
			sht[0].pw.println("yes");
			sht[0].pw.flush();
			sht[1].pw.println("yes");
			sht[1].pw.flush();
		}
		while (true) {
			if (ss == null) break;
			ExecutorService executor2 = Executors.newCachedThreadPool();
			executor2.execute(new RejectThread(this));
			executor2.shutdown();
			while (!executor2.isTerminated() && b) {}
			if (!b) {
				try {
					ss.close();
					ss = null;
				} catch (IOException ioe) {
					System.out.println("IOException in ServerThread.run(): " + ioe.getMessage());
				}
				break;
			}
		}
	}
}
