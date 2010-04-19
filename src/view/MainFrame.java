package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import model.Card;
import model.Flop;
import model.ModelManager;


public class MainFrame extends JFrame implements MainFrameListener {
	
	private final ModelManager modelManager;
	
	private Menu menu;
	
	private JPanel panel;
	private JPanel player1Panel;
	private JPanel player2Panel;
	private JPanel flopPanel;
	
	public MainFrame(ModelManager modelManager) {
		super();
		this.modelManager = modelManager;
		modelManager.addMainFrameListener(this);
		
		panel = new JPanel();
		player1Panel = new JPanel();
		player2Panel = new JPanel();
		
		panel.setLayout(new GridBagLayout());
		player1Panel.setLayout(new GridBagLayout());
		player1Panel.setLayout(new GridBagLayout());
		
		flopPanel = new JPanel();
		flopPanel.setLayout(new GridBagLayout());
		
		setContentPane(panel);
		setResizable(false);
		setLocationRelativeTo(null);
		setLocation(520,425);
		setVisible(true);
		
		menu = new Menu(this);
		setJMenuBar(menu);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		startNewGame();
	}
	
	public void init1() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5,5,5,5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridy++;
		
		init2();
		panel.add(flopPanel, constraints);
		constraints.gridy++;
		panel.add(player1Panel, constraints);
		constraints.gridx++;
		panel.add(player2Panel, constraints);
		pack();
	}
	
	public void init2() {
		initFlop();
		initPlayer1();
		initPlayer2(false);
	}
	
	public void initFlop()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5,5,5,5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		Flop flop = modelManager.getFlop();
		flopPanel.removeAll();
		for (int i=0; i<flop.getNbCards(); i++) {
			Card card = flop.getCard(i);
			flopPanel.add(new CardPanel(card.getRank(),card.getSuit()),constraints);
			constraints.gridx++;
		}
		JLabel totalbet = new JLabel("total bet :" + modelManager.getPot());
		constraints.gridy++;
		constraints.gridx = 0;
		flopPanel.add(totalbet,constraints);	
		JLabel turnumber = new JLabel("turn :"+this.modelManager.getTurnNumber());
		constraints.gridx++;
		flopPanel.add(turnumber,constraints);	
		pack();
		flopPanel.updateUI();
	}
	
	public void initPlayer1() {
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		player1Panel.removeAll();
		for(int i=0; i<modelManager.getPlayer1().getNbCards(); i++) {
			Card card = modelManager.getPlayer1().getCard(i);
			player1Panel.add(new CardPanel(card.getRank(),card.getSuit()),c);
			c.gridx++;
		}
		JLabel money = new JLabel("money :"+this.modelManager.getPlayer1().getMoney());
		JLabel bet = new JLabel("bet :"+this.modelManager.getPlayer1().getBet());
		JLabel name = new JLabel("player 1");
		c.gridx = 0;
		c.gridy++;
		player1Panel.add(name,c);
		c.gridy++;
		c.gridx = 0;
		player1Panel.add(money,c);
		c.gridx++;
		player1Panel.add(bet,c);
		this.pack();
		player1Panel.updateUI();
	}
	
	public void initPlayer2(boolean showCard) {
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		this.player2Panel.removeAll();
		for (int i=0;i<modelManager.getPlayer1().getNbCards();i++) {
			Card card = modelManager.getPlayer2().getCard(i);
			if (showCard == true)
				player2Panel.add(new CardPanel(card.getRank(),card.getSuit()),c);
			else
				player2Panel.add(new CardPanel(0,"empty"),c);
			c.gridx++;
		}
		JLabel money = new JLabel("money :" + modelManager.getPlayer2().getMoney());
		JLabel bet = new JLabel("bet :" + modelManager.getPlayer2().getBet());
		JLabel name = new JLabel("player 2");
		c.gridx = 0;
		c.gridy++;
		player2Panel.add(name,c);
		c.gridy++;
		c.gridx = 0;
		player2Panel.add(money,c);
		c.gridx++;
		player2Panel.add(bet,c);
		this.pack();
		player2Panel.updateUI();
	}
	
	public void AskDistribution() {
		if(modelManager.getPlayer1().getBet() == modelManager.getPlayer2().getBet()) {
			modelManager.distributeFlop();
			initFlop();
		}
	}
	
	public void reaction(){
		Object[] options = {"bet 1","bet 0"};
		int n = JOptionPane.showOptionDialog(null, "What do you want to do", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
		if (n == JOptionPane.CLOSED_OPTION) {
			//reaction();
		}
		else if (n == JOptionPane.YES_OPTION) {
			modelManager.increaseBet(modelManager.getPlayer1(), 1);
	  	initPlayer1();
	  	modelManager.changeTurn();
		}
		else if (n == JOptionPane.NO_OPTION) {
			modelManager.changeTurn();
		}
	}
	
	public void increaseBet() {
		String text = JOptionPane.showInputDialog("How many money do you want to add in your bet?");
		if (text != null && !text.equals("")) {
			try {
				int value = Integer.parseInt(text.replaceAll(" ",""));
				if(modelManager.getPlayer1().getMoney()-value<0 || value<0) {
					increaseBet();
				} else if(value==0 || modelManager.getPlayer1().getBet()+value<modelManager.getPlayer2().getBet()) {
					int result = JOptionPane.showConfirmDialog(this,"Do you give up?");
					if (result == JOptionPane.YES_OPTION) {
						modelManager.giveUp();
					} else if (result == JOptionPane.NO_OPTION) {
						increaseBet();
					}
				} else {
					modelManager.increaseBet(modelManager.getPlayer1(), value);
					initPlayer1();
					initFlop();
					modelManager.changeTurn();
				}
			} catch(Exception e) {
				reaction();
			}
		} else if (text == null) {
			reaction();
		}
	}
	
	public void nextstep() {
		init1();
	}
	
	public void play() {
		initFlop();
		initPlayer2(false);
		reaction();
	}
	
	public void startNewGame() {
		init1();
		if (modelManager.getTurnOfPlayer() == 1)
			reaction();
		else
			modelManager.fireAITurnRequest();
	}
	
	public void showcard(int winner,int value) {
		init1();
		initPlayer2(true);
		if (winner == 1)
			JOptionPane.showMessageDialog(null,"player 1 won "+value/2+" $" ,"End",JOptionPane.INFORMATION_MESSAGE) ;
		else if (winner == 2)
			JOptionPane.showMessageDialog(null,"player 1 lost "+value/2+" $","End",JOptionPane.INFORMATION_MESSAGE) ;
		else
			JOptionPane.showMessageDialog(null,"equality","End",JOptionPane.INFORMATION_MESSAGE) ;
	}
	
	public void end() {
		init1();
		if(modelManager.getPlayer1().getMoney() == 0)
			JOptionPane.showMessageDialog(null,"player 1 do not have any more money so he lose","End",JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(null,"player 2 do not have any more money so he lose","End",JOptionPane.INFORMATION_MESSAGE);
	}
	
	public ModelManager getModelManager() {
		return modelManager;
	}
}