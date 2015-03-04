package map_simulator;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import map_simulator.Simulator;
import javax.swing.SwingConstants;
import java.awt.Color;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel{
	public boolean simulatorMode = true;
	
	private JPanel locPanel, stepPanel;
	private JLabel locLbl, noOfStepsLbl;
	private JLabel locXLbl, locXValueLbl, locYLbl, locYValueLbl, stepLbl, stepValueLbl;
	private JPanel minutePanel, secondPanel;
	private JPanel speedPanel, percentPanel;
	private JPanel string1Panel, string2Panel;
	
	private JLabel string1Lbl, string2Lbl;
	private JLabel minuteLbl, secondLbl, speedLbl, percentLbl;
	private JComboBox<String> speedCbx, percentCbx;
	private JButton explorerBtn, shortestPathBtn, resetBtn, loadFileBtn;
	
	public String speed;
	public int speed1, Percentage;
	public long f_speed;
	public static int time;
	private JTextField secondTfd, mapdescriptor1Tfd;
	private JTextField minuteTfd, mapdescriptor2Tfd;
	private static String file ="map layout/map.txt";
	static int interval;
	static Timer timer;
	
	BoardPanel board;
	public ControlPanel(BoardPanel board){
		this.board = board;
		setLayout(new GridLayout(0,1));
	
		//JPanel mapFilePanel = new JPanel();
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(new Color(135, 206, 235));
		
		locPanel = new JPanel();
		stepPanel = new JPanel();
		minutePanel = new JPanel();
		secondPanel = new JPanel();
		speedPanel = new JPanel();
		percentPanel = new JPanel();
		string1Panel = new JPanel();
		string2Panel = new JPanel();
		
		locXLbl = new JLabel("X:");
		locXLbl.setHorizontalAlignment(SwingConstants.CENTER);
		locXValueLbl = new JLabel(""); 
		locXValueLbl.setHorizontalAlignment(SwingConstants.CENTER);
		locYLbl = new JLabel("Y:");
		locYLbl.setHorizontalAlignment(SwingConstants.CENTER);
		locYValueLbl = new JLabel("");
		locYValueLbl.setHorizontalAlignment(SwingConstants.CENTER);
		stepLbl = new JLabel("Steps:");
		stepLbl.setHorizontalAlignment(SwingConstants.CENTER);
		stepValueLbl = new JLabel("");
		stepValueLbl.setHorizontalAlignment(SwingConstants.CENTER);
		
		minuteLbl = new JLabel("Minutes:");
		minuteLbl.setHorizontalAlignment(SwingConstants.CENTER);
		secondLbl = new JLabel("Seconds:");
		secondLbl.setHorizontalAlignment(SwingConstants.CENTER);
		string1Lbl = new JLabel("String 1:");
		string1Lbl.setHorizontalAlignment(SwingConstants.CENTER);
		string2Lbl = new JLabel("String 2:");
		string2Lbl.setHorizontalAlignment(SwingConstants.CENTER);
		speedLbl = new JLabel("Speed:");
		speedLbl.setHorizontalAlignment(SwingConstants.CENTER);
		percentLbl = new JLabel("Complete %");
		percentLbl.setHorizontalAlignment(SwingConstants.CENTER);
		secondTfd = new JTextField();
		secondTfd.setText("0");
		minuteTfd = new JTextField();
		minuteTfd.setText("6");
		mapdescriptor1Tfd = new JTextField();
		mapdescriptor2Tfd = new JTextField();
		
		//Percentage button
			percentCbx = new JComboBox();
			percentCbx.setBackground(new Color(135, 206, 250));
			percentCbx.setPreferredSize(new Dimension(200,50));
   		//combobox.addItem("--Select Speed--");
			percentCbx.addItem("100");
			percentCbx.addItem("90");
	   		percentCbx.addItem("80");
	   		percentCbx.addItem("70");
	   		percentCbx.addItem("60");
	   		percentCbx.setSelectedIndex(0);
              getPercentage(100);
	
             percentCbx.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		       String Percent_Explored = (String) percentCbx.getSelectedItem();
	           int Percent_Explored1 = Integer.parseInt(Percent_Explored);
	           getPercentage(Percent_Explored1);
	           System.out.println(Percent_Explored1);
		}

	});
		
		//loadfile
		loadFileBtn = new JButton("Load File");
		loadFileBtn.setPreferredSize(new Dimension(200,50));
		loadFileBtn.addActionListener(new ActionListener(){

		 final JFileChooser  fileDialog = new JFileChooser();
			public void actionPerformed(ActionEvent arg0) {
			 int returnVal = fileDialog.showOpenDialog(buttonsPanel);
		           if (returnVal == JFileChooser.APPROVE_OPTION) {
		              java.io.File file = fileDialog.getSelectedFile();
		              setFilename(file.getPath());
		              board.getMap().resetMap();
		              board.getMap().readSimulatedMap();
		              board.repaint();
		            }
		            else{
		                    
		            }      
		         }
		      });
					
		//choose map file panel
			speedCbx = new JComboBox();
			speedCbx.setBackground(new Color(135, 206, 250));
		    speedCbx.setPreferredSize(new Dimension(200,50));
	   		//combobox.addItem("--Select Speed--");
		   		  speedCbx.addItem("50");
		   		  speedCbx.addItem("100");
	              speedCbx.addItem("200");
	              speedCbx.addItem("300");
	              speedCbx.addItem("1000");
	              speedCbx.setSelectedIndex(0);
	              setspeed(50);
		
	        speedCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		           speed = (String) speedCbx.getSelectedItem();
		           speed1 = Integer.parseInt(speed);
		           setspeed(speed1);
		           
			}

		});
		
		
		//buttons panel
		//explorer button
		explorerBtn = new JButton("Start Exploration");
		explorerBtn.setPreferredSize(new Dimension(200,50));
		explorerBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				board.setMouseClick(false);
				String data = minuteTfd.getText(); 
			    String data1 = secondTfd.getText();
			    int seconds = Integer.parseInt(data1);
			    int minutes = Integer.parseInt(data);
			    time = seconds + minutes*60;
			    getTime(time);
			    
				Simulator.startExploration();
				timer = new Timer();
			    interval = time;
			    timer.scheduleAtFixedRate(new TimerTask() {

			        public void run() {
			            setInterval();
			            secondTfd.setText(""+interval%60);
		                minuteTfd.setText(""+interval/60);
			        }
			    }, 0, 1000);
			    explorerBtn.setEnabled(false);
//			    explorerBtn.setVisible(false);
			}
		});
		
		//shortest path button
		shortestPathBtn = new JButton("Start Shortest Path");
		shortestPathBtn.setPreferredSize(new Dimension(200,50));
		shortestPathBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				//perform shortest path
				Simulator.startShortestPath();
//				String a = "map layout/map 1.txt";
//				setFilename(a);
			}
		});	
				

		//reset button
		resetBtn = new JButton("Reset");
		resetBtn.setPreferredSize(new Dimension(200,50));
		resetBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				//perform reset
				file = "map layout/map.txt";
				Simulator.reset();	
				if(timer != null) {
					timer.cancel();
				}
				explorerBtn.setVisible(true);
				secondTfd.setText(""+time%60);
				minuteTfd.setText(""+time/60);
			}
		});
		
		locPanel.setLayout(new GridLayout(0, 4, 0, 0));
		locPanel.add(locXLbl);
		locPanel.add(locXValueLbl);
		locPanel.add(locYLbl);
		locPanel.add(locYValueLbl);
		
		stepPanel.setLayout(new GridLayout(0, 4, 0, 0));
		stepPanel.add(stepLbl);
		stepPanel.add(stepValueLbl);
		
		minutePanel.setLayout(new GridLayout(2, 0, 0, 0));
		minutePanel.add(minuteLbl);
		minutePanel.add(minuteTfd);
		
		secondPanel.setLayout(new GridLayout(2, 0, 0, 0));
		secondPanel.add(secondLbl);
		secondPanel.add(secondTfd);
		
		string1Panel.setLayout(new GridLayout(2, 0, 0, 0));
		string1Panel.add(string1Lbl);
		string1Panel.add(mapdescriptor1Tfd);
		
		string2Panel.setLayout(new GridLayout(2, 0, 0, 0));
		string2Panel.add(string2Lbl);
		string2Panel.add(mapdescriptor2Tfd);
		
		speedPanel.setLayout(new GridLayout(2, 0, 0, 0));
		speedPanel.add(speedLbl);
		speedPanel.add(speedCbx);

		percentPanel.setLayout(new GridLayout(2, 0, 0, 0));
		percentPanel.add(percentLbl);
		percentPanel.add(percentCbx);
		
		buttonsPanel.setLayout(new GridLayout(0, 2, 0, 0));
		buttonsPanel.add(locPanel);
		buttonsPanel.add(stepPanel);
		buttonsPanel.add(string1Panel);
		buttonsPanel.add(string2Panel);
		buttonsPanel.add(minutePanel);
		buttonsPanel.add(secondPanel);
		buttonsPanel.add(speedPanel);
		buttonsPanel.add(percentPanel);
		buttonsPanel.add(explorerBtn);
		buttonsPanel.add(shortestPathBtn);
		buttonsPanel.add(loadFileBtn);
		buttonsPanel.add(resetBtn);

		add(buttonsPanel);
	}
	
	public void setString1 (String string1) {
		mapdescriptor1Tfd.setText(string1);
	}
	
	public void setString2 (String string2) {
		mapdescriptor2Tfd.setText(string2);
	}
	
	public void setMouseLoc (int x, int y) {
		locXValueLbl.setText(Integer.toString(x));
		locYValueLbl.setText(Integer.toString(y));
	}
	
	public void setNoOfSteps (int steps) {
		stepValueLbl.setText(Integer.toString(steps));
	}
	
	public void getPercentage(int Percentage) {
		this.Percentage = Percentage;
	}
	
	public int setPercentage(){
		return Percentage;
	}

	public void getTime(int time) {
		this.time = time;
//		System.out.println(time);
	}

	public int setTime(){
		return time;
	}
	
	public void setspeed(long speed1) {
		//System.out.println(time);
		 f_speed = speed1;
//		 System.out.println(f_speed);
	}
	
	public long getspeed() {
		// TODO Auto-generated method stub
//		System.out.println(f_speed);
			return f_speed;
	}
	
	public void setFilename(String file) {
		this.file = file;
	}
	
	public static String getFilename(){
		return file;
		
	}
	
	public void stopTimer(){
		timer.cancel();
	}
	
	private final int setInterval() {
	    if (interval == 1){
	    	Simulator.stopExploration();
	        timer.cancel();
	    }
	    return --interval;
	}
	
	public int getInterval() {
		return interval;
	}
	
	public void performExploration() {
		explorerBtn.doClick();
	}
	
	public void performShortestPath() {
		shortestPathBtn.doClick();
	}
}