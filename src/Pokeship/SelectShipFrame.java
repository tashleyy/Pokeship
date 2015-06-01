package Pokeship;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class SelectShipFrame extends JFrame {
	private static final long serialVersionUID = 1;
	private BattleshipFrame bf;
	private JButton placeShipButton;
	private JRadioButton northButton, southButton, eastButton, westButton;
	public JComboBox<String> selectShipCombo;
	public char orient = 'N';
	public int size = 5;
	public int vert, hori;

	public SelectShipFrame(String title, BattleshipFrame bf) {
		super(title);
		this.bf = bf;
		initializeVariables();
		createGUI();
		addListeners();
	}
	
	private void initializeVariables() {
		String [] shipTypes = {"Charmander", "Cyndaquil", "Torchic", "Chimchar"};
		selectShipCombo = new JComboBox<String>(shipTypes);
		northButton = new JRadioButton("North");
		southButton = new JRadioButton("South");
		eastButton = new JRadioButton("East");
		westButton = new JRadioButton("West");
		placeShipButton = new JButton("Hide Pokemon");
		
	}
	
	private void createGUI() {
		setSize(300, 200);
		setLocationRelativeTo(bf);
		JLabel selectShipLabel = new JLabel("Select Pokemon:");
		JPanel northPanel = new JPanel();
		northPanel.add(selectShipLabel);
		northPanel.add(selectShipCombo);
		ButtonGroup group = new ButtonGroup();
		group.add(northButton);
		group.add(southButton);
		group.add(eastButton);
		group.add(westButton);
		northButton.setSelected(true);
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		JPanel topPanel = new JPanel();
		topPanel.add(northButton);
		topPanel.add(southButton);
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(eastButton);
		bottomPanel.add(westButton);
		centerPanel.add(topPanel);
		centerPanel.add(bottomPanel);
		add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(placeShipButton, BorderLayout.SOUTH);
		checkValidity();
	}
	
	private void addListeners() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				bf.setEnabled(true);
			}
		});
		selectShipCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (((String)e.getItem()).equals("Charmander")) {
						size = 5;
					} else if (((String)e.getItem()).equals("Cyndaquil")) {
						size = 4;
					} else if (((String)e.getItem()).equals("Torchic")) {
						size = 3;
					} else {
						size = 2;
					}
					checkValidity();
				}
			}
		});
		class OrientButtonListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				orient = e.getActionCommand().charAt(0);
				checkValidity();
			}
		}
		OrientButtonListener obl = new OrientButtonListener();
		northButton.setActionCommand("N");
		southButton.setActionCommand("S");
		eastButton.setActionCommand("E");
		westButton.setActionCommand("W");
		northButton.addActionListener(obl);
		southButton.addActionListener(obl);
		eastButton.addActionListener(obl);
		westButton.addActionListener(obl);
		placeShipButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bf.placeShip();
			}
		});
	}
	
	public void checkValidity() {
		if (bf.isValidShipPlacement(size, vert, hori, orient)) {
			placeShipButton.setEnabled(true);
		} else {
			placeShipButton.setEnabled(false);
		}
	}
}
