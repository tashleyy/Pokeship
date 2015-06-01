package Pokeship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import org.json.JSONArray;
import org.json.JSONObject;

public class BattleshipFrame extends JFrame {
	public static final long serialVersionUID = 1;
	boolean started, playerMoved, computerMoved, playerWon, suddenQuit;
	String timeText;
	static Image g1 = new ImageIcon("img/grass1.jpg").getImage(),
			g2 = new ImageIcon("img/grass2.jpg").getImage(),
			currentGrass = g1;
	private JPanel southPanel;
	JLabel[][] playerGridLabels, computerGridLabels;
	private JLabel logLabel, timeLabel;
	JLabel playerLabel, computerLabel;
	private JButton startButton, sendButton;
	Sea playerSea, computerSea;
	private String playerAim, computerAim;
	private SelectShipFrame ssf;
	private boolean warned;
	private int numFree = 100, roundNum = 0;
	private static ImageIcon a = new ImageIcon(new ImageIcon("img/charmander.jpg").getImage().getScaledInstance(35,  35, java.awt.Image.SCALE_SMOOTH)),
			b = new ImageIcon(new ImageIcon("img/cyndaquil.jpg").getImage().getScaledInstance(35,  35, java.awt.Image.SCALE_SMOOTH)),
			c = new ImageIcon(new ImageIcon("img/torchic.jpg").getImage().getScaledInstance(35,  35, java.awt.Image.SCALE_SMOOTH)),
			d = new ImageIcon(new ImageIcon("img/chimchar.jpg").getImage().getScaledInstance(35,  35, java.awt.Image.SCALE_SMOOTH)),
			h = new ImageIcon(new ImageIcon("img/pokeball-m.jpg").getImage().getScaledInstance(35,  35, java.awt.Image.SCALE_SMOOTH)),
			m = new ImageIcon(new ImageIcon("img/openpokeball.jpg").getImage().getScaledInstance(35,  35, java.awt.Image.SCALE_SMOOTH));
	private JMenuItem howToMenuItem, aboutMenuItem;
	private JDialog howToDialog, aboutDialog;
	private JTextArea logArea;
	private JScrollPane logPane;
	private URL mapURL;
	String eString, cString, ecString;
	StartFrame sf;
	Socket s = null;
	BufferedReader sReader = null;
	PrintWriter sWriter = null;
	private JCheckBox chatBox, eventBox;
	private JTextField chatField;
	
	public BattleshipFrame() {
		super("Pokeship");
		initializeVariables();
		createGUI();
		addListeners();
	}
	
	private void initializeVariables() {
		String[] aToJ = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
		
		howToMenuItem = new JMenuItem("How To");
		aboutMenuItem = new JMenuItem("About");

		playerSea = new Sea();
		southPanel = new JPanel();
		playerGridLabels = new JLabel[11][11];
		computerGridLabels = new JLabel[11][11];
		for (int i = 0; i < 10; i++) {
			playerGridLabels[i][0] = new JLabel(aToJ[i]);
			computerGridLabels[i][0] = new JLabel(aToJ[i]);
			for (int j = 1; j < 11; j++) {
				playerGridLabels[i][j] = new BackgroundLabel();
				computerGridLabels[i][j] = new BackgroundLabel();
				playerGridLabels[i][j].setBorder(BorderFactory.createLineBorder(Color.WHITE));
				computerGridLabels[i][j].setBorder(BorderFactory.createLineBorder(Color.WHITE));
			}
		}
		playerGridLabels[10][0] = new JLabel(" ");
		computerGridLabels[10][0] = new JLabel(" ");
		for (int i = 1; i < 11; i++) {
			playerGridLabels[10][i] = new JLabel("" + i);
			computerGridLabels[10][i] = new JLabel("" + i);
		}
		playerLabel = new JLabel("Player");
		computerLabel = new JLabel("Computer");
		playerAim = "N/A";
		computerAim = "N/A";
		timeText = "";
		started = false;
		playerMoved = false;
		computerMoved = false;
		playerWon = false;
		suddenQuit = false;
		logLabel = new JLabel("Log: You are in edit mode, click to hide your Pokemon.");
		startButton = new JButton("Start");
		
		ssf = new SelectShipFrame("Select ship at XX", BattleshipFrame.this);
		howToDialog = new JDialog();
		aboutDialog = new JDialog();
		
		timeLabel = new JLabel("Time - 0:15");
		logArea = new JTextArea();
		logPane = new JScrollPane(logArea);
		
		(new SwitchGrassThread(this)).start();
		
		sf = new StartFrame(this);
		sf.setVisible(true);
		
		chatBox = new JCheckBox("Chat");
		eventBox = new JCheckBox("Event");
		sendButton = new JButton("Send");
		chatField = new JTextField(15);
	}
	
	private void createGUI() {
		setSize(900, 650);
		setLocation(300, 200);
		createDialogs();
		createMenuBar();
		createNorthGUI();
		createCenterGUI();
		createSouthGUI();
	}
	
	private void createDialogs() {
		howToDialog.setTitle("Battleship Instructions");
		howToDialog.setSize(400, 220);
		howToDialog.setLocationRelativeTo(BattleshipFrame.this);
		JTextArea ta = new JTextArea();
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setEditable(false);
		try {
			FileReader fr = new FileReader("howto.txt");
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				ta.setText(ta.getText() + line + "\n");
			}
			br.close();
		} catch (IOException ioe) {
			System.out.println("IOException: " + ioe.getMessage());
		}
		JScrollPane sp = new JScrollPane(ta);
		howToDialog.add(sp, BorderLayout.CENTER);

		aboutDialog.setTitle("About");
		aboutDialog.setSize(300, 200);
		aboutDialog.setLocationRelativeTo(BattleshipFrame.this);
		JLabel northLabel = new JLabel("<html><center>Made by Tiffany Tjahjadi<br>2 May 2015</center></html>", SwingConstants.CENTER);
		JLabel southLabel = new JLabel("CSCI201 USC: Assignment 5", SwingConstants.CENTER);
		aboutDialog.add(northLabel, BorderLayout.NORTH);
		aboutDialog.add(new JLabel(new ImageIcon(new ImageIcon("img/usc.gif").getImage().getScaledInstance(90, 90, java.awt.Image.SCALE_SMOOTH))), BorderLayout.CENTER);
		aboutDialog.add(southLabel , BorderLayout.SOUTH);
	}
	
	private void createMenuBar() {
		JMenuBar jmb = new JMenuBar();
		JMenu infoMenu = new JMenu("Info");
		infoMenu.add(howToMenuItem);
		infoMenu.add(aboutMenuItem);
		jmb.add(infoMenu);
		setJMenuBar(jmb);
	}
	
	private void createNorthGUI() {
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(1, 3));
		JPanel playerLabelPanel = new JPanel();
		playerLabelPanel.add(playerLabel);
		JPanel timeLabelPanel = new JPanel();
		timeLabelPanel.add(timeLabel);
		JPanel computerLabelPanel = new JPanel();
		computerLabelPanel.add(computerLabel);
		playerLabelPanel.setBackground(Color.DARK_GRAY);
		timeLabelPanel.setBackground(Color.DARK_GRAY);
		computerLabelPanel.setBackground(Color.DARK_GRAY);
		playerLabel.setForeground(Color.WHITE);
		timeLabel.setForeground(Color.WHITE);
		computerLabel.setForeground(Color.WHITE);
		northPanel.add(playerLabelPanel);
		northPanel.add(timeLabelPanel);
		northPanel.add(computerLabelPanel);
		add(northPanel, BorderLayout.NORTH);
	}
	
	private void createCenterGUI() {
		JPanel playerGrid = new JPanel();
		playerGrid.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		playerGrid.setBackground(Color.WHITE);
		JPanel computerGrid = new JPanel();
		computerGrid.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		computerGrid.setBackground(Color.WHITE);
		playerGrid.setLayout(new GridLayout(11, 11));
		computerGrid.setLayout(new GridLayout(11, 11));
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				playerGridLabels[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				computerGridLabels[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				playerGridLabels[i][j].setVerticalAlignment(SwingConstants.CENTER);
				computerGridLabels[i][j].setVerticalAlignment(SwingConstants.CENTER);
				playerGrid.add(playerGridLabels[i][j]);
				computerGrid.add(computerGridLabels[i][j]);
			}
		}
		JPanel centerPanel = new JPanel();
		GridLayout gl = new GridLayout(1,2);
		gl.setHgap(10);
		centerPanel.setLayout(gl);
		centerPanel.add(playerGrid);
		centerPanel.add(computerGrid);
		centerPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
		centerPanel.setBackground(Color.DARK_GRAY);
		add(centerPanel, BorderLayout.CENTER);
	}
	
	private void createSouthGUI() {
		startButton.setEnabled(false);
		southPanel.add(logLabel);
		southPanel.add(startButton);
		logLabel.setForeground(Color.WHITE);
		southPanel.setBackground(Color.DARK_GRAY);
		add(southPanel, BorderLayout.SOUTH);
	}
	
	private void addListeners() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (!sf.isMaps()) {
					sWriter.println("quit");
					sWriter.flush();
					try {
						s.close();
						sReader.close();
						sWriter.close();
					} catch (IOException ioe) {
						System.out.println("IOException in window listener: " + ioe.getMessage());
					}
				}
				System.exit(0);
			}
		});
		addMenuItemListeners();
		addPlayerGridLabelListeners();
		addComputerGridLabelListeners();
		addStartButtonListener();
		addCheckBoxListeners();
		addSendButtonListener();
	}

	private void addMenuItemListeners() {
		howToMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		howToMenuItem.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				howToDialog.setVisible(true);
			}
		});
		aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		aboutMenuItem.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aboutDialog.setVisible(true);
			}
		});
	}
	
	private void addPlayerGridLabelListeners() {
		class GridLabelAdapter extends MouseAdapter {
			int vert, hori;
			public GridLabelAdapter(int vert, int hori) {
				this.vert = vert;
				this.hori = hori;
			}
			public void mouseClicked(MouseEvent e) {
				if (started) return;
				if (!playerSea.isPartOfShip(vert, hori)) {
					if (playerSea.numShips == 5) {
						return;
					}
					ssf.setTitle("Select Pokemon at " + Coordinate.toChar(vert) + hori);
					ssf.vert = vert;
					ssf.hori = hori;
					ssf.checkValidity();
					ssf.setVisible(true);
					setEnabled(false);
				} else {
					Ship s = playerSea.getShipAt(vert, hori);
					for (int k = 0; k < s.size; k++) {
						updateCoordinate(playerGridLabels[s.shipParts[k].coordinate.vert-1][s.shipParts[k].coordinate.hori], "?");
					}
					if (!s.toString().equals("Chimchar") || playerSea.numDs == 2) {
						ssf.selectShipCombo.addItem(s.toString());						
					}
					playerSea.removeShipAt(vert, hori);
					startButton.setEnabled(false);
				}
			}
		}
		for (int i = 0; i < 10; i++) {
			for (int j = 1; j < 11; j++) {
				playerGridLabels[i][j].addMouseListener(new GridLabelAdapter(i+1, j));
			}
		}
	}
	
	private void addComputerGridLabelListeners() {
		class GridLabelAdapter extends MouseAdapter {
			int vert, hori;
			public GridLabelAdapter(int vert, int hori) {
				this.vert = vert;
				this.hori = hori;
			}
			public void mouseClicked(MouseEvent e) {
				if (sf.isMaps()) {
					if (!started || computerSea.shotAtBefore[vert-1][hori-1]) return;
					if (playerMoved) return;
					(new LockedSoundThread("sound/cannon.wav", (BackgroundLabel) computerGridLabels[vert-1][hori])).start();
					char suf = computerSea.getShipUnderFire(vert, hori);
					computerSea.shotAtBefore[vert-1][hori-1] = true;
					playerAim = Coordinate.toChar(vert) + "" + hori;
					if (suf == 'Z') {
						(new PokeballThread(BattleshipFrame.this, false, vert-1, hori, getIcon("MISS!"), false)).start();
						eString += "\nPlayer hit " + playerAim + " and missed! (" + timeText + ")";
						ecString += "\nPlayer hit " + playerAim + " and missed! (" + timeText + ")";
						updateLog();
					} else {
						(new ExplosionThread(BattleshipFrame.this, false, vert-1, hori, getIcon(Character.toString(suf)))).start();
						eString += "\nPlayer hit " + playerAim + " and hurt a " + fromCharToString(suf) + "! (" + timeText + ")";
						ecString += "\nPlayer hit " + playerAim + " and hurt a " + fromCharToString(suf) + "! (" + timeText + ")";
						updateLog();
						if (computerSea.getShipAt(vert, hori).isSunk) {
							eString += "\nPlayer caught Computer's " + fromCharToString(suf) + "!";
							ecString += "\nPlayer caught Computer's " + fromCharToString(suf) + "!";
							updateLog();
						}
					}
					playerMoved = true;					
				} else {
					if (!started || playerMoved) return;
					sWriter.println("press:" + vert + ":" + hori);
					sWriter.flush();
				}
			}
		}
		for (int i = 0; i < 10; i++) {
			for (int j = 1; j < 11; j++) {
				computerGridLabels[i][j].addMouseListener(new GridLabelAdapter(i+1, j));
			}
		}
	}
	
	private void addStartButtonListener() {
		startButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!sf.isMaps()) {
					Ship[] ships = playerSea.getAllShips();
					JSONObject obj = new JSONObject();
					JSONArray jsonShips = new JSONArray();
					for (int i = 0; i < 5; i++) {
						Ship ship = ships[i];
						JSONObject jsonShip = new JSONObject();
						jsonShip.put("size", ship.size);
						jsonShip.put("vert", ship.shipParts[0].coordinate.vert);
						jsonShip.put("hori", ship.shipParts[0].coordinate.hori);
						jsonShip.put("isVertical", ship.isVertical);
						jsonShips.put(jsonShip);
					}
					obj.put("ships", jsonShips);
					sWriter.println(obj.toString());
					sWriter.flush();
				} else {
					computerSea = new Sea(mapURL);
					if (!computerSea.locateShips()) {
						System.out.println("Invalid grid.");
						setVisible(false);
						sf.setVisible(true);
						playerSea = new Sea();
						for (int i = 0; i < 10; i++) {
							for (int j = 1; j < 11; j++) {
								playerGridLabels[i][j].setIcon(null);
								computerGridLabels[i][j].setIcon(null);
							}
						}
						startButton.setEnabled(false);
						ssf = new SelectShipFrame("Select Pokemon at XX", BattleshipFrame.this);
						return;
					}
					(new TimerThread(BattleshipFrame.this)).start();
				}
				started = true;
				cString = "";
				eString = "Round 1";
				ecString = "Round 1";
				updateLog();
				logArea.setEditable(false);
				((DefaultCaret)logArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
				getContentPane().remove(southPanel);
				logPane.setPreferredSize(new Dimension(300, 80));
				logPane.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 10, Color.DARK_GRAY));
				southPanel.setBorder(BorderFactory.createTitledBorder(null, "Game Log", TitledBorder.LEFT, TitledBorder.TOP, null, Color.WHITE));
				southPanel.removeAll();
				southPanel.setLayout(new BorderLayout());
				southPanel.add(logPane, BorderLayout.CENTER);
				JPanel filterPanel = new JPanel();
				filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
				filterPanel.setBackground(Color.DARK_GRAY);
				JLabel filterLabel = new JLabel("Filter: ");
				filterLabel.setForeground(Color.WHITE);
				chatBox.setForeground(Color.WHITE);
				eventBox.setForeground(Color.WHITE);
				filterPanel.add(filterLabel);
				filterPanel.add(chatBox);
				filterPanel.add(eventBox);
				southPanel.add(filterPanel, BorderLayout.EAST);
				JPanel chatPanel = new JPanel();
				chatPanel.setLayout(new FlowLayout());
				chatPanel.setBackground(Color.DARK_GRAY);
				JLabel chatLabel = new JLabel(playerLabel.getText() + ":");
				chatLabel.setForeground(Color.WHITE);
				chatPanel.add(chatLabel);
				chatPanel.add(chatField);
				chatPanel.add(sendButton);
				southPanel.add(chatPanel, BorderLayout.SOUTH);
				southPanel.revalidate();
				southPanel.repaint();
				add(southPanel, BorderLayout.SOUTH);
			}
		});
	}
	
	private void addCheckBoxListeners() {
		chatBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateLog();
			}
		});
		eventBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateLog();
			}
		});
	}

	private void addSendButtonListener() {
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!sf.isMaps()) {
					sWriter.println("chat:" + playerLabel.getText() + ":" + chatField.getText());
					sWriter.flush();
				}
				cString += "\n" + playerLabel.getText() + ":" + chatField.getText();
				ecString += "\n" + playerLabel.getText() + ":" + chatField.getText();
				updateLog();
				chatField.setText("");				
			}
		});
	}

	public void computerMove() {
		Random rand = new Random();
		int randNum = rand.nextInt(numFree) + 1;
		int counter = 0;
		int randVert = -1, randHori = -1;
		boolean end = false;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (!playerSea.shotAtBefore[i][j]) {
					counter++;
				}
				if (counter == randNum) {
					randVert = i+1;
					randHori = j+1;
					numFree--;
					end = true;
					break;
				}
			}
			if (end) {
				break;
			}
		}
		(new LockedSoundThread("sound/cannon.wav", (BackgroundLabel) playerGridLabels[randVert-1][randHori])).start();
		char suf = playerSea.getShipUnderFire(randVert, randHori);
		playerSea.shotAtBefore[randVert-1][randHori-1] = true;
		computerAim = Coordinate.toChar(randVert) + "" + randHori;
		if (suf == 'Z') {
			(new PokeballThread(BattleshipFrame.this, true, randVert-1, randHori, getIcon("MISS!"), false)).start();
			eString += "\nComputer hit " + computerAim + " and missed! (" + timeText + ")";
			ecString += "\nComputer hit " + computerAim + " and missed! (" + timeText + ")";
			updateLog();
		} else {
			(new ExplosionThread(BattleshipFrame.this, true, randVert-1, randHori, getIcon("Hit!"))).start();
			eString += "\nComputer hit " + computerAim + " and hurt a " + fromCharToString(suf) + "! (" + timeText + ")";
			ecString += "\nComputer hit " + computerAim + " and hurt a " + fromCharToString(suf) + "! (" + timeText + ")";
			updateLog();
			if (playerSea.getShipAt(randVert, randHori).isSunk) {
				eString += "\nComputer caught Player's " + fromCharToString(suf) + "!";
				ecString += "\nComputer caught Player's " + fromCharToString(suf) + "!";
				updateLog();
			}
		}
		computerMoved = true;
	}
		
	public void placeShip() {
		boolean isVertical = (ssf.orient == 'N' || ssf.orient == 'S');
		int vert = ssf.vert, hori = ssf.hori;
		if (ssf.orient == 'N') {
			vert -= ssf.size - 1;
		}
		if (ssf.orient == 'W') {
			hori -= ssf.size - 1;
		}
		playerSea.addShip(vert, hori, isVertical, ssf.size);
		if (playerSea.numShips == 5) {
			startButton.setEnabled(true);
		}
		String s = "?";
		int shipSize = ssf.size;
		switch (shipSize) {
		case 5:
			s = "A";
			ssf.selectShipCombo.removeItem("Charmander");
			break;
		case 4:
			s = "B";
			ssf.selectShipCombo.removeItem("Cyndaquil");
			break;
		case 3:
			s = "C";
			ssf.selectShipCombo.removeItem("Torchic");
			break;
		case 2:
			s = "D";
			if (playerSea.numDs == 2) {
				ssf.selectShipCombo.removeItem("Chimchar");
			}
			break;
		}
		for (int i = 0; i < shipSize; i++) {
			updateCoordinate(playerGridLabels[vert-1][hori], s);
			if (isVertical) {
				vert++;
			} else {
				hori++;
			}
		}
		ssf.setVisible(false);
		setEnabled(true);
	}
	
	public boolean isValidShipPlacement(int size, int vert, int hori, char orient) {
		switch (orient) {
		case 'N':
			for (int i = 1; i < size; i++) {
				if (vert-i < 1) break;
				if (playerSea.isPartOfShip(vert-i, hori)) break;
				if (i == size-1) return true;
			}
			break;
		case 'E':
			for (int i = 1; i < size; i++) {
				if (hori+i > 10) break;
				if (playerSea.isPartOfShip(vert, hori+i)) break;
				if (i == size-1) return true;
			}
			break;
		case 'S':
			for (int i = 1; i < size; i++) {
				if (vert+i > 10) break;
				if (playerSea.isPartOfShip(vert+i, hori)) break;
				if (i == size-1) return true;
			}
			break;
		case 'W':
			for (int i = 1; i < size; i++) {
				if (hori-i < 1) break;
				if (playerSea.isPartOfShip(vert, hori-i)) break;
				if (i == size-1) return true;
			}
			break;
		default:
			System.out.println("Error in BattleshipFrame.isValidShipPlacement(int, int, char)");
			break;
		}
		return false;
	}

	private void updateCoordinate(JLabel label, String text) {
		label.setIcon(getIcon(text));
	}
	
	static ImageIcon getIcon(String text) {
		if (text.equals("A")) {
			return a;
		} else if (text.equals("B")) {
			return b;
		} else if (text.equals("C")) {
			return c;
		} else if (text.equals("D")) {
			return d;
		} else if (text.equals("Hit!")) {
			return h;
		} else if (text.equals("MISS!")) {
			return m;
		} else if (text.equals("?")) {
			return null;
		} else {
			System.out.println(text);
			return null;
		}
	}
	
	void reset() {
		setEnabled(true);
		playerSea = new Sea();
		for (int i = 0; i < 10; i++) {
			for (int j = 1; j < 11; j++) {
				playerGridLabels[i][j].setIcon(null);
				computerGridLabels[i][j].setIcon(null);
			}
		}
		logLabel.setText("Log: You are in edit mode, click to hide your Pokemon.");
		timeLabel.setText("Time - 0:15");
		southPanel.removeAll();
		southPanel.setBorder(null);
		southPanel.setLayout(new FlowLayout());
		southPanel.add(logLabel);
		southPanel.add(startButton);
		southPanel.revalidate();
		southPanel.repaint();
		started = false;
		playerAim = "N/A";
		computerAim = "N/A";
		playerWon = false;
		numFree = 100;
		roundNum = 0;
		startButton.setEnabled(false);
		ssf = new SelectShipFrame("Select Pokemon at XX", BattleshipFrame.this);
	}
	
	public void setTimeText() {
		timeLabel.setText("Time - " + timeText);
		if (!warned && timeText.equals("0:03")) {
			warned = true;
			eString += "\nWarning - 3 seconds left in the round!";
			ecString += "\nWarning - 3 seconds left in the round!";
			updateLog();
		}
	}
	
	public void startNextRound() {
		playerMoved = false;
		computerMoved = false;
		warned = false;
		roundNum++;
		if (roundNum != 1) {
			eString += "\nRound " + roundNum;
			ecString += "\nRound " + roundNum;
			updateLog();
		}
	}
	
	static String fromCharToString(char c) {
		switch (c) {
		case 'A':
			return "Charmander";
		case 'B':
			return "Cyndaquil";
		case 'C':
			return "Torchic";
		case 'D':
			return "Chimchar";
		}
		return "";
	}
		
	JLabel getGridLabel(boolean isPlayerGrid, int vert, int hori) {
		if (isPlayerGrid) {
			return playerGridLabels[vert][hori];
		} else {
			return computerGridLabels[vert][hori];
		}
	}
	
	Ship getShip(boolean isPlayerGrid, int vert, int hori) {
		if (isPlayerGrid) {
			return playerSea.getShipAt(vert, hori);
		} else {
			return computerSea.getShipAt(vert, hori);
		}
	}

	public void setUp() {
		if (sf.isMaps()) {
			try {
				mapURL = new URL("http://www-scf.usc.edu/~csci201/assignments/" + sf.getMap() + ".battle");
			} catch (MalformedURLException mue) {
				System.out.println("MalformedURLException: " + mue.getMessage());
			} 
			playerLabel.setText(sf.getPlayerName());
		} else {
			try {
				String str;
				sWriter.println(sf.getPlayerName());
				sWriter.flush();
				str = sReader.readLine();
				playerLabel.setText(sf.getPlayerName());
				computerLabel.setText(str);
			} catch (IOException ioe) {
				System.out.println("IOException1 in BattleshipFrame.setUp(): " + ioe.getMessage());
			}			
		}
	}
	
	void updateLog() {
		if (chatBox.isSelected() && eventBox.isSelected()) logArea.setText(ecString);
		else if (chatBox.isSelected()) logArea.setText(cString);
		else if (eventBox.isSelected()) logArea.setText(eString);
		else logArea.setText("");
	}
}
