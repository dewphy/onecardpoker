package view;

import javax.swing.ImageIcon;

public class CardImage {
	
	public static final String PATH = "images/";
	
	public static ImageIcon getImage(int rank, String suit) {
		if (rank == 14) {
			rank = 1;
		}
		ImageIcon icon = new ImageIcon(PATH + suit + "_" + rank + ".png");
		return icon;
	}
}