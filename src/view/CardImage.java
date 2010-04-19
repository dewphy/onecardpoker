package view;

import javax.swing.ImageIcon;

public class CardImage {
	
	public static final String PATH = "./images/card";
	
	public CardImage() {
	}
	
	public ImageIcon getImage() {
		ImageIcon icon = new ImageIcon(PATH + ".png");
		return icon;
	}
	
	public ImageIcon getImage(int rank, String suit) {
		if (rank == 14) {
			rank = 1;
		}
		ImageIcon icon = new ImageIcon(PATH + "_" + suit + "_" + rank + ".png");
		return icon;
	}
}