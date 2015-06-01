package Pokeship;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WaitingFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	JLabel jl;

	WaitingFrame() {
		super("Battleship Menu");
		setSize(400, 300);
		setLocation(300, 300);
		JPanel jp = new JPanel();
		jl = new JLabel("Waiting for another player... 30s until timeout.");
		jp.add(jl);
		add(jp);
	}
}
