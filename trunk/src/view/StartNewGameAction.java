package view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;


public class StartNewGameAction extends AbstractAction {
	
	private MainFrame mainFrame;
	
	public StartNewGameAction(MainFrame mainFrame, String name) {
		super(name);
		this.mainFrame = mainFrame;
	}
	
	public void actionPerformed(ActionEvent e)  {
		mainFrame.getModelManager().initPlayers();
		mainFrame.getModelManager().init();
		mainFrame.startNewGame();
	}
}