package view;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu extends JMenuBar {
	
	public Menu(MainFrame mainFrame) {
		super();
		JMenu option = new JMenu("Option");
		JMenuItem newgame = new JMenuItem(new StartNewGameAction(mainFrame,"Start New Game"));
		JMenuItem exit = new JMenuItem(new ExitAction("Exit"));
		option.add(newgame);
		option.add(exit);
		add(option);
	}
}