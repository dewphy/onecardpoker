package view;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class CardPanel extends JPanel {
	
	CardPanel(int rank, String suit) {
		super();
		add(new JLabel(CardImage.getImage(rank, suit)));
	}
}