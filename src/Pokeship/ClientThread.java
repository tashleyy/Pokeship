package Pokeship;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

public class ClientThread extends Thread {
	private BattleshipFrame bf;
	
	ClientThread(BattleshipFrame bf) {
		this.bf = bf;
	}
	
	public void run() {
		while (true) {
			try {
				String str = bf.sReader.readLine();
				if (str != null) {
					StringTokenizer tokens = new StringTokenizer(str, ":");
					String command = tokens.nextToken();
					if (command.equals("go")) {
						(new TimerThread(bf)).start();
					} else if (command.equals("chat")) { // chat
						String chat = tokens.nextToken() + ":" + tokens.nextToken();
						bf.cString += "\n" + chat;
						bf.ecString += "\n" + chat;
						bf.updateLog();
					} else if (command.equals("playsound")) { // play cannon sound
						int vert = Integer.parseInt(tokens.nextToken());
						int hori = Integer.parseInt(tokens.nextToken());
						(new LockedSoundThread("sound/cannon.wav", (BackgroundLabel) bf.computerGridLabels[vert-1][hori])).start();
					} else if (command.equals("enemyplaysound")) {
						int vert = Integer.parseInt(tokens.nextToken());
						int hori = Integer.parseInt(tokens.nextToken());
						(new LockedSoundThread("sound/cannon.wav", (BackgroundLabel) bf.playerGridLabels[vert-1][hori])).start();
					} else if (command.equals("hit")) { // hit
						int vert = Integer.parseInt(tokens.nextToken());
						int hori = Integer.parseInt(tokens.nextToken());
						String suf = tokens.nextToken();
						String aim = Coordinate.toChar(vert) + "" + hori;
						(new ExplosionThread(bf, false, vert-1, hori, BattleshipFrame.getIcon(suf))).start();
						bf.playerMoved = true;
						bf.eString += "\n" + bf.playerLabel.getText() + " hit " + aim + " and hurt a " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "! (" + bf.timeText + ")";
						bf.ecString += "\n" + bf.playerLabel.getText() + " hit " + aim + " and hurt a " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "! (" + bf.timeText + ")";
						bf.updateLog();
					} else if (command.equals("enemyhit")) {
						int vert = Integer.parseInt(tokens.nextToken());
						int hori = Integer.parseInt(tokens.nextToken());
						String suf = tokens.nextToken();
						String aim = Coordinate.toChar(vert) + "" + hori;
						(new ExplosionThread(bf, true, vert-1, hori, BattleshipFrame.getIcon("Hit!"))).start();
						bf.computerMoved = true;
						bf.eString += "\n" + bf.computerLabel.getText() + " hit " + aim + " and hurt a " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "! (" + bf.timeText + ")";
						bf.ecString += "\n" + bf.computerLabel.getText() + " hit " + aim + " and hurt a " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "! (" + bf.timeText + ")";
						bf.updateLog();
					} else if (command.equals("hitsink")) { // hit sink
						int vert = Integer.parseInt(tokens.nextToken());
						int hori = Integer.parseInt(tokens.nextToken());
						String suf = tokens.nextToken();
						int beginV = Integer.parseInt(tokens.nextToken());
						int beginH = Integer.parseInt(tokens.nextToken());
						int endV = Integer.parseInt(tokens.nextToken());
						int endH = Integer.parseInt(tokens.nextToken());
						String aim = Coordinate.toChar(vert) + "" + hori;
						ExecutorService executor = Executors.newCachedThreadPool();
						executor.execute(new ExplosionThread(bf, false, vert-1, hori, BattleshipFrame.getIcon(suf)));
						executor.shutdown();
						bf.playerMoved = true;
						bf.eString += "\n" + bf.playerLabel.getText() + " hit " + aim + " and hurt a " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "! (" + bf.timeText + ")";
						bf.ecString += "\n" + bf.playerLabel.getText() + " hit " + aim + " and hurt a " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "! (" + bf.timeText + ")";
						bf.eString += "\n" + bf.playerLabel.getText() + " caught " + bf.computerLabel.getText() + "'s " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "!";
						bf.ecString += "\n" + bf.playerLabel.getText() + " caught " + bf.computerLabel.getText() + "'s " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "!";
						bf.updateLog();
						while (!executor.isTerminated()) {}
						ExecutorService executor2 = Executors.newCachedThreadPool();
						if (beginH != endH) {
							while (beginH != endH+1) {
								executor2.execute(new PokeballThread(bf, false, beginV-1, beginH, BattleshipFrame.getIcon(suf), true));
								beginH++;
							}						
						} else {
							while (beginV != endV+1) {
								executor2.execute(new PokeballThread(bf, false, beginV-1, beginH, BattleshipFrame.getIcon(suf), true));
								beginV++;
							}						
						}
					} else if (command.equals("enemyhitsink")) {
						int vert = Integer.parseInt(tokens.nextToken());
						int hori = Integer.parseInt(tokens.nextToken());
						String suf = tokens.nextToken();
						int beginV = Integer.parseInt(tokens.nextToken());
						int beginH = Integer.parseInt(tokens.nextToken());
						int endV = Integer.parseInt(tokens.nextToken());
						int endH = Integer.parseInt(tokens.nextToken());
						String aim = Coordinate.toChar(vert) + "" + hori;
						ExecutorService executor = Executors.newCachedThreadPool();
						executor.execute(new ExplosionThread(bf, true, vert-1, hori, BattleshipFrame.getIcon(suf)));
						executor.shutdown();
						bf.computerMoved = true;
						bf.eString += "\n" + bf.computerLabel.getText() + " hit " + aim + " and hurt a " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "! (" + bf.timeText + ")";
						bf.ecString += "\n" + bf.computerLabel.getText() + " hit " + aim + " and hurt a " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "! (" + bf.timeText + ")";
						bf.eString += "\n" + bf.computerLabel.getText() + " caught " + bf.playerLabel.getText() + "'s " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "!";
						bf.ecString += "\n" + bf.computerLabel.getText() + " caught " + bf.playerLabel.getText() + "'s " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "!";
						bf.updateLog();
						while (!executor.isTerminated()) {}
						ExecutorService executor2 = Executors.newCachedThreadPool();
						if (beginH != endH) {
							while (beginH != endH+1) {
								executor2.execute(new PokeballThread(bf, true, beginV-1, beginH, BattleshipFrame.getIcon("Hit!"), true));
								beginH++;
							}						
						} else {
							while (beginV != endV+1) {
								executor2.execute(new PokeballThread(bf, true, beginV-1, beginH, BattleshipFrame.getIcon("Hit!"), true));
								beginV++;
							}
						}
					} else if (command.equals("hitgameover")) { // hit gameover
						int vert = Integer.parseInt(tokens.nextToken());
						int hori = Integer.parseInt(tokens.nextToken());
						String suf = tokens.nextToken();
						int beginV = Integer.parseInt(tokens.nextToken());
						int beginH = Integer.parseInt(tokens.nextToken());
						int endV = Integer.parseInt(tokens.nextToken());
						int endH = Integer.parseInt(tokens.nextToken());
						String aim = Coordinate.toChar(vert) + "" + hori;
						ExecutorService executor = Executors.newCachedThreadPool();
						executor.execute(new ExplosionThread(bf, false, vert-1, hori, BattleshipFrame.getIcon(suf)));
						executor.shutdown();
						bf.playerMoved = true;
						bf.eString += "\n" + bf.playerLabel.getText() + " hit " + aim + " and hurt a " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "! (" + bf.timeText + ")";
						bf.ecString += "\n" + bf.playerLabel.getText() + " hit " + aim + " and hurt a " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "! (" + bf.timeText + ")";
						bf.eString += "\n" + bf.playerLabel.getText() + " caught " + bf.computerLabel.getText() + "'s " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "!";
						bf.ecString += "\n" + bf.playerLabel.getText() + " caught " + bf.computerLabel.getText() + "'s " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "!";
						bf.updateLog();
						while (!executor.isTerminated()) {}
						ExecutorService executor2 = Executors.newCachedThreadPool();
						if (beginH != endH) {
							while (beginH != endH+1) {
								executor2.execute(new PokeballThread(bf, false, beginV-1, beginH, BattleshipFrame.getIcon(suf), true));
								beginH++;
							}						
						} else {
							while (beginV != endV+1) {
								executor2.execute(new PokeballThread(bf, false, beginV-1, beginH, BattleshipFrame.getIcon(suf), true));
								beginV++;
							}
						}
						executor2.shutdown();
						while (!executor2.isTerminated()) {}
						bf.setEnabled(false);
						bf.started = false;
						bf.playerWon = true;
						bf.suddenQuit = false;
					} else if (command.equals("enemyhitgameover")) { 	
						int vert = Integer.parseInt(tokens.nextToken());
						int hori = Integer.parseInt(tokens.nextToken());
						String suf = tokens.nextToken();
						int beginV = Integer.parseInt(tokens.nextToken());
						int beginH = Integer.parseInt(tokens.nextToken());
						int endV = Integer.parseInt(tokens.nextToken());
						int endH = Integer.parseInt(tokens.nextToken());
						String aim = Coordinate.toChar(vert) + "" + hori;
						ExecutorService executor = Executors.newCachedThreadPool();
						executor.execute(new ExplosionThread(bf, true, vert-1, hori, BattleshipFrame.getIcon(suf)));
						executor.shutdown();
						bf.computerMoved = true;
						bf.eString += "\n" + bf.computerLabel.getText() + " hit " + aim + " and hurt a " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "! (" + bf.timeText + ")";
						bf.ecString += "\n" + bf.computerLabel.getText() + " hit " + aim + " and hurt a " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "! (" + bf.timeText + ")";
						bf.eString += "\n" + bf.computerLabel.getText() + " caught " + bf.playerLabel.getText() + "'s " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "!";
						bf.ecString += "\n" + bf.computerLabel.getText() + " caught " + bf.playerLabel.getText() + "'s " + BattleshipFrame.fromCharToString(suf.charAt(0)) + "!";
						bf.updateLog();
						while (!executor.isTerminated()) {}
						ExecutorService executor2 = Executors.newCachedThreadPool();
						if (beginH != endH) {
							while (beginH != endH+1) {
								executor2.execute(new PokeballThread(bf, true, beginV-1, beginH, BattleshipFrame.getIcon("Hit!"), true));
								beginH++;
							}						
						} else {
							while (beginV != endV+1) {
								executor2.execute(new PokeballThread(bf, true, beginV-1, beginH, BattleshipFrame.getIcon("Hit!"), true));
								beginV++;
							}
						}
						executor2.shutdown();
						while (!executor2.isTerminated()) {}
						bf.setEnabled(false);
						bf.started = false;
						bf.playerWon = false;
						bf.suddenQuit = false;
					} else if (command.equals("miss")) {
						int vert = Integer.parseInt(tokens.nextToken());
						int hori = Integer.parseInt(tokens.nextToken());
						String aim = Coordinate.toChar(vert) + "" + hori;
						(new PokeballThread(bf, false, vert-1, hori, BattleshipFrame.getIcon("MISS!"), false)).start();
						bf.playerMoved = true;
						bf.eString += "\n" + bf.playerLabel.getText() + " hit " + aim + " and missed! (" + bf.timeText + ")";
						bf.ecString += "\n" + bf.playerLabel.getText() + " hit " + aim + " and missed! (" + bf.timeText + ")";
						bf.updateLog();
					} else if (command.equals("enemymiss")) {
						int vert = Integer.parseInt(tokens.nextToken());
						int hori = Integer.parseInt(tokens.nextToken());
						String aim = Coordinate.toChar(vert) + "" + hori;
						(new PokeballThread(bf, true, vert-1, hori, BattleshipFrame.getIcon("MISS!"), false)).start();
						bf.computerMoved = true;
						bf.eString += "\n" + bf.computerLabel.getText() + " hit " + aim + " and missed! (" + bf.timeText + ")";
						bf.ecString += "\n" + bf.computerLabel.getText() + " hit " + aim + " and missed! (" + bf.timeText + ")";
						bf.updateLog();
					} else if (command.equals("playagain")) {
						bf.reset();
					} else if (command.equals("stop")) {
						bf.reset();
						bf.s.close();
						bf.sWriter.close();
						bf.sReader.close();
						bf.setVisible(false);
						bf.sf.setVisible(true);
					} else if (command.equals("quit")) {
						JOptionPane.showMessageDialog(bf, "The other player has quit!", "Connection Error", JOptionPane.ERROR_MESSAGE);
						bf.suddenQuit = true;
						bf.reset();
						bf.s.close();
						bf.sWriter.close();
						bf.sReader.close();
						bf.setVisible(false);
						bf.sf.setVisible(true);
					}
				} else {
					break;
				}
			} catch (IOException ioe) {
				System.out.println("IOException in ClientThread.run(): " + ioe.getMessage());
				break;
			}
		}
	}
}
