package Pokeship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerHelperThread extends Thread {
	Socket s;
	PrintWriter pw;
	BufferedReader br;
	private int i;
	private ServerThread st;
	private Sea enemySea;
	String username, otherUsername;
	private boolean playAgain = true, choseOption = false;
	
	ServerHelperThread(int i, ServerThread st) {
		this.i = i;
		this.st = st;
	}
	
	public void run() {
		try {
			// Get usernames
			String str = br.readLine();
			if (str != null) {
				username = str;
				st.sht[Math.abs(i-1)].otherUsername = str;
				st.sht[Math.abs(i-1)].pw.println(str);
				st.sht[Math.abs(i-1)].pw.flush();				
			} else {
				return;
			}
		} catch (IOException ioe) {
			System.out.println("IOException in ServerHelperThread.run(): " + ioe.getMessage());
		}
		while (true) {
			// Get ships
			st.sht[Math.abs(i-1)].enemySea = new Sea();
			String str;
			try {
				str = br.readLine();
				if (str == null) return;
				if (str.equals("quit")) {
					st.sht[Math.abs(i-1)].pw.println("quit");
					st.sht[Math.abs(i-1)].pw.flush();
					st.b = false;
					s.close();
					br.close();
					pw.close();
					return;
				}
				JSONObject obj = new JSONObject(str);
				JSONArray jsonShips = obj.getJSONArray("ships");
				for (int j = 0; j < 5; j++) {
					JSONObject jsonShip = jsonShips.getJSONObject(j);
					int vert = jsonShip.getInt("vert");
					int hori = jsonShip.getInt("hori");
					int size = jsonShip.getInt("size");
					boolean isVertical = jsonShip.getBoolean("isVertical");
					st.sht[Math.abs(i-1)].enemySea.addShip(vert, hori, isVertical, size);
				}
				if (st.sht[0].enemySea.numShips == 5 && st.sht[1].enemySea.numShips == 5) {
					st.sht[0].pw.println("go");
					st.sht[0].pw.flush();
					st.sht[1].pw.println("go");
					st.sht[1].pw.flush();
				}
				while (st.b) {
					str = br.readLine();
					if (str != null) {
						StringTokenizer tokens = new StringTokenizer(str, ":");
						String command = tokens.nextToken();
						if (command.equals("press")) {
							int vert = Integer.parseInt(tokens.nextToken());
							int hori = Integer.parseInt(tokens.nextToken());
							if (!enemySea.shotAtBefore[vert-1][hori-1]) {
								st.sht[i].pw.println("playsound:" + vert + ":" + hori);
								st.sht[i].pw.flush();
								st.sht[Math.abs(i-1)].pw.println("enemyplaysound:" + vert + ":" + hori);
								st.sht[Math.abs(i-1)].pw.flush();
								char suf = enemySea.getShipUnderFire(vert, hori);
								enemySea.shotAtBefore[vert-1][hori-1] = true;
								if (suf == 'Z') {
									st.sht[i].pw.println("miss:" + vert + ":" + hori);
									st.sht[Math.abs(i-1)].pw.println("enemymiss:" + vert + ":" + hori);
								} else {
									if (enemySea.allShipsSunken()) {
										st.sht[i].pw.println("hitgameover:" + vert + ":" + hori + ":" + suf + ":"
												+ enemySea.getShipAt(vert,  hori).shipParts[0].coordinate.vert + ":" + enemySea.getShipAt(vert,  hori).shipParts[0].coordinate.hori + ":"
												+ enemySea.getShipAt(vert, hori).shipParts[enemySea.getShipAt(vert, hori).size-1].coordinate.vert + ":" + enemySea.getShipAt(vert, hori).shipParts[enemySea.getShipAt(vert, hori).size-1].coordinate.hori);
										st.sht[Math.abs(i-1)].pw.println("enemyhitgameover:" + vert + ":" + hori + ":" + suf + ":"
												+ enemySea.getShipAt(vert,  hori).shipParts[0].coordinate.vert + ":" + enemySea.getShipAt(vert,  hori).shipParts[0].coordinate.hori + ":"
												+ enemySea.getShipAt(vert, hori).shipParts[enemySea.getShipAt(vert, hori).size-1].coordinate.vert + ":" + enemySea.getShipAt(vert, hori).shipParts[enemySea.getShipAt(vert, hori).size-1].coordinate.hori);
									} else if (enemySea.getShipAt(vert, hori).isSunk) {
										st.sht[i].pw.println("hitsink:" + vert + ":" + hori + ":" + suf + ":"
												+ enemySea.getShipAt(vert,  hori).shipParts[0].coordinate.vert + ":" + enemySea.getShipAt(vert,  hori).shipParts[0].coordinate.hori + ":"
												+ enemySea.getShipAt(vert, hori).shipParts[enemySea.getShipAt(vert, hori).size-1].coordinate.vert + ":" + enemySea.getShipAt(vert, hori).shipParts[enemySea.getShipAt(vert, hori).size-1].coordinate.hori);
										st.sht[Math.abs(i-1)].pw.println("enemyhitsink:" + vert + ":" + hori + ":" + suf + ":"
												+ enemySea.getShipAt(vert,  hori).shipParts[0].coordinate.vert + ":" + enemySea.getShipAt(vert,  hori).shipParts[0].coordinate.hori + ":"
												+ enemySea.getShipAt(vert, hori).shipParts[enemySea.getShipAt(vert, hori).size-1].coordinate.vert + ":" + enemySea.getShipAt(vert, hori).shipParts[enemySea.getShipAt(vert, hori).size-1].coordinate.hori);
									} else {
										st.sht[i].pw.println("hit:" + vert + ":" + hori + ":" + suf);
										st.sht[Math.abs(i-1)].pw.println("enemyhit:" + vert + ":" + hori + ":" + suf);
									}
								}			
								st.sht[i].pw.flush();
								st.sht[Math.abs(i-1)].pw.flush();
							}
						} else if (command.equals("chat")) {
							st.sht[Math.abs(i-1)].pw.println(str);
							st.sht[Math.abs(i-1)].pw.flush();
						} else if (command.equals("playagain")) {
							playAgain = true;
							choseOption = true;
							break;
						} else if (command.equals("stop")) {
							playAgain = false;
							choseOption = true;
							break;
						} else if (command.equals("quit")) {
							st.sht[Math.abs(i-1)].pw.println("quit");
							st.sht[Math.abs(i-1)].pw.flush();
							st.b = false;
							st.sht[0].s.close();
							st.sht[0].pw.close();
							st.sht[0].br.close();
							st.sht[1].s.close();
							st.sht[1].pw.close();
							st.sht[1].br.close();
						}
					}
				}			
			} catch (IOException ioe) {
				System.out.println("IOException in ServerHelperThread.run(): " + ioe.getMessage());
				break;
			}
			if (st.sht[Math.abs(i-1)].choseOption) {
				st.sht[Math.abs(i-1)].choseOption = false;
				choseOption = false;
				if (st.sht[0].playAgain && st.sht[1].playAgain) {
					st.sht[0].pw.println("playagain");
					st.sht[0].pw.flush();
					st.sht[1].pw.println("playagain");
					st.sht[1].pw.flush();
				} else {
					st.sht[0].pw.println("stop");
					st.sht[0].pw.flush();
					st.sht[1].pw.println("stop");
					st.sht[1].pw.flush();
					st.b = false;
				}
			}
		}
	}
}
