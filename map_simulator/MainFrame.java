package map_simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class MainFrame extends JFrame{

	BoardPanel board;
	ControlPanel controlPanel;
	
	public MainFrame(){				
		setTitle("Map Simulator");
		//setSize(700, 650);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		board = new BoardPanel();
		board.setPreferredSize(new Dimension(480,640));
		board.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.black));
		
		controlPanel = new ControlPanel(board);
		controlPanel.setPreferredSize(new Dimension(320,640));
		
		board.updateControlPanel(controlPanel);
		
		add(board,BorderLayout.CENTER);
		add(controlPanel,BorderLayout.EAST);
		this.pack();
		setLocationRelativeTo(null);
		this.setVisible(true);

	}
	
	public BoardPanel getBoard(){
		return board;
	}

}
