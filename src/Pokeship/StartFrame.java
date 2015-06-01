package Pokeship;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StartFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextField nameTF, enterIPTF, portTF, mapsTF;
	private JButton refreshButton, connectButton;
	private JCheckBox hostCB, customCB, mapsCB;
	private JLabel IPLabel;
	private BattleshipFrame bf;
	private JPanel mainPanel;
	WaitingFrame wf;
	ServerThread st;
	
	StartFrame(BattleshipFrame bf) {
		super("Battleship Menu");
		this.bf = bf;
		initializeVariables();
		createGUI();
		addListeners();
	}
	
	void initializeVariables() {
		nameTF = new JTextField("Player 1", 10);
		enterIPTF = new JTextField("localhost", 10);
		portTF = new JTextField("3469", 5);
		mapsTF = new JTextField("", 5);
		refreshButton = new JButton("Refresh");
		connectButton = new JButton("Connect");
		hostCB = new JCheckBox("Host Game");
		customCB = new JCheckBox("Custom Port");
		mapsCB = new JCheckBox("201 Maps");
		mainPanel = new JPanel();
		wf = new WaitingFrame();
	}
	
	void createGUI() {
		setSize(400, 300);
		setLocation(300, 300);

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		JPanel jp1 = new JPanel();
		JLabel yourIPLabel  = new JLabel("Your IP:");
		String str = "Error";

		refreshButton.setEnabled(false);
		try {
			URL toCheckIp = new URL("http://checkip.amazonaws.com");
			BufferedReader br = new BufferedReader(new InputStreamReader(toCheckIp.openStream()));
			str = br.readLine();
		} catch (MalformedURLException mue) {
			System.out.println("MalformedURLException: " + mue.getMessage());
		} catch (IOException ioe) {
			System.out.println("IOException in StartFrame.createGUI(): " + ioe.getMessage());
			refreshButton.setEnabled(true);
		}
		IPLabel = new JLabel(str);
		jp1.add(yourIPLabel);
		jp1.add(IPLabel);
		JPanel jp2 = new JPanel();
		JLabel nameLabel = new JLabel("Name:");
		jp2.add(nameLabel);
		jp2.add(nameTF);
		JPanel jp3 = new JPanel();
		JLabel enterIPLabel = new JLabel("Enter an IP:");
		hostCB.setSelected(true);
		enterIPTF.setEnabled(false);
		jp3.add(hostCB);
		jp3.add(enterIPLabel);
		jp3.add(enterIPTF);
		JPanel jp4 = new JPanel();
		JLabel portLabel = new JLabel("Port:");
		portTF.setEnabled(false);
		jp4.add(customCB);
		jp4.add(portLabel);
		jp4.add(portTF);
		JPanel jp5 = new JPanel();
		mapsTF.setEnabled(false);
		jp5.add(mapsCB);
		jp5.add(mapsTF);
		JPanel jp6 = new JPanel();
		jp6.add(refreshButton);
		jp6.add(connectButton);
		mainPanel.add(jp1);
		mainPanel.add(jp2);
		mainPanel.add(jp3);
		mainPanel.add(jp4);
		mainPanel.add(jp5);
		mainPanel.add(jp6);
		add(mainPanel, BorderLayout.CENTER);
	}
	
	void addListeners() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		hostCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (hostCB.isSelected()) {
					enterIPTF.setEnabled(false);
					enterIPTF.setText("localhost");
				}
				else enterIPTF.setEnabled(true);
			}
		});
		customCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (customCB.isSelected()) portTF.setEnabled(true);
				else {
					portTF.setText("3469");
					portTF.setEnabled(false);
				}
			}
		});
		mapsCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mapsCB.isSelected()) {
					mapsTF.setEnabled(true);
					hostCB.setEnabled(false);
					enterIPTF.setEnabled(false);
					customCB.setEnabled(false);
					portTF.setEnabled(false);
				} else {
					mapsTF.setEnabled(false);
					hostCB.setEnabled(true);
					if (hostCB.isSelected()) enterIPTF.setEnabled(false);
					else enterIPTF.setEnabled(true);
					customCB.setEnabled(true);
					if (customCB.isSelected()) portTF.setEnabled(true);
					else portTF.setEnabled(false);					
				}
			}
		});
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = "Error";
				try {
					URL toCheckIp = new URL("http://checkip.amazonaws.com");
					BufferedReader br = new BufferedReader(new InputStreamReader(toCheckIp.openStream()));
					s = br.readLine();
					IPLabel.setText(s);
					refreshButton.setEnabled(false);
				} catch (MalformedURLException mue) {
					System.out.println("MalformedURLException: " + mue.getMessage());
				} catch (IOException ioe) {
					System.out.println("IOException in refreshButton listener: " + ioe.getMessage());
					refreshButton.setEnabled(true);
				}
			}
		});
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					if (mapsCB.isSelected()) {
						setVisible(false);
						bf.setUp();
						bf.setVisible(true);
					} else {
						if (hostCB.isSelected()) {
							st = new ServerThread(Integer.parseInt(portTF.getText()));
							if (st.ss == null) return;
							(new AcceptThread(st, 0)).start();
							bf.s = new Socket(enterIPTF.getText(), Integer.parseInt(portTF.getText()));
							bf.sReader = new BufferedReader(new InputStreamReader(bf.s.getInputStream()));
							bf.sWriter = new PrintWriter(bf.s.getOutputStream());
							setVisible(false);
							wf.setVisible(true);
							st.start();
							(new WaitingThread(bf)).start();
							(new CountdownThread(wf)).start();
						} else {
							bf.s = new Socket(enterIPTF.getText(), Integer.parseInt(portTF.getText()));
							bf.sReader = new BufferedReader(new InputStreamReader(bf.s.getInputStream()));
							bf.sWriter = new PrintWriter(bf.s.getOutputStream());
							String str = bf.sReader.readLine();
							if (str.equals("yes")) {
								setVisible(false);
								bf.setUp();
								bf.setVisible(true);
								(new ClientThread(bf)).start();
								(new ConnectionThread(bf)).start();
							} else {
								if (bf.sReader != null) {
									try {
										bf.sReader.close();
									} catch (IOException ioe) {
										System.out.println("IOException in connectButton listener: " + ioe.getMessage());
									}
									bf.sReader = null;
								}
								if (bf.sWriter != null) {
									bf.sWriter.close();
									bf.sWriter = null;
								}
								bf.s.close();
								bf.s = null;
								JOptionPane.showMessageDialog(StartFrame.this, "Connection to the host failed!", "Connection Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				} catch (NumberFormatException | IOException e) {
					if (bf.sReader != null) {
						try {
							bf.sReader.close();
						} catch (IOException ioe) {
							System.out.println("IOException in connectButton listener: " + ioe.getMessage());
						}
						bf.sReader = null;
					}
					if (bf.sWriter != null) {
						bf.sWriter.close();
						bf.sWriter = null;
					}
					String str = "Error";
					try {
						URL toCheckIp = new URL("http://checkip.amazonaws.com");
						BufferedReader br = new BufferedReader(new InputStreamReader(toCheckIp.openStream()));
						str = br.readLine();
					} catch (MalformedURLException mue) {
						System.out.println("MalformedURLException: " + mue.getMessage());
					} catch (IOException ioe) {
						System.out.println("IOException in connectButton listener: " + ioe.getMessage());
						refreshButton.setEnabled(true);
					}
					IPLabel.setText(str);
				}
			}
		});
	}

	void setUp() {
		String str = "Error";
		try {
			URL toCheckIp = new URL("http://checkip.amazonaws.com");
			BufferedReader br = new BufferedReader(new InputStreamReader(toCheckIp.openStream()));
			str = br.readLine();
		} catch (MalformedURLException mue) {
			System.out.println("MalformedURLException: " + mue.getMessage());
		} catch (IOException ioe) {
			System.out.println("IOException in setUp: " + ioe.getMessage());
			refreshButton.setEnabled(true);
		}
		IPLabel.setText(str);
	}

	String getPlayerName() {
		return nameTF.getText();
	}

	boolean isHost() {
		return hostCB.isSelected();
	}

	boolean isMaps() {
		return mapsCB.isSelected();
	}

	String getMap() {
		return mapsTF.getText();
	}
}
