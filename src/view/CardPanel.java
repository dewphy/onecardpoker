package view;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class CardPanel extends JPanel {
	
	CardPanel() {
		super();
		add(new JLabel(new CardImage().getImage()));
	}
	
	CardPanel(int rank, String suit) {
		super();
		add(new JLabel(new CardImage().getImage(rank, suit)));
	}
}