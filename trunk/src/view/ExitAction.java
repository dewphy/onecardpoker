package view;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ExitAction extends AbstractAction {
	
	public ExitAction(String name) {
		super(name);
	}
	
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}
}