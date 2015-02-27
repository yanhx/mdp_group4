package map_simulator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.swing.JLabel;

public class Countdown extends JPanel{
	private static Timer t;
	private int clockTick;
	private String clockTickString;
	private JLabel timeLbl;

	public Countdown(){
		clockTick = 10;
		clockTickString = Integer.toString(clockTick);
		
		timeLbl = new JLabel();
		timeLbl.setText(clockTickString);
		
		add(timeLbl);
		
		t = new Timer(1000, new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				clockTick--;
				clockTickString = Integer.toString(clockTick);
				timeLbl.setText(clockTickString);
				if(clockTick == 1){
					t.stop();
				}
				System.out.println("HERE");
			}
			
		});
			
	}
	
	public void start(){
		t.start();
	}
}
