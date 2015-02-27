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

public class ControlPanel extends JPanel{
	public boolean simulatorMode = true;
	
	private JButton explorerBtn;
	private JButton shortestPathBtn;
	private JLabel mouseLoc, noOfSteps;
	private JLabel Minutes, String1, Speed1, Explored;
	private JLabel Seconds, String2;
	private JButton resetBtn;
	private JButton LoadFile;
	private JComboBox combobox, Percentage_combobox;
	public String speed;
	public int speed1, Percentage;
	public long f_speed;
	public static int time;
	private JTextField textField, mapdescriptor1;
	private JTextField textField_1, mapdescriptor2;
	private static String file ="map layout/map.txt";
	static int interval;
	static Timer timer;
	BoardPanel board;
	public ControlPanel(BoardPanel board){
		this.board = board;
		setLayout(new GridLayout(0,1));
	
		//JPanel mapFilePanel = new JPanel();
		JPanel buttonsPanel = new JPanel();
		Minutes = new JLabel("Minutes:");
		Seconds = new JLabel("Seconds:");
		String1 = new JLabel("String 1:");
		String2 = new JLabel("String 2:");
		Speed1 = new JLabel("Speed:");
		Explored = new JLabel("Exploration %");
		textField = new JTextField();
		textField.setText("0");
		textField_1 = new JTextField();
		textField_1.setText("6");
		mapdescriptor1 = new JTextField();
		mapdescriptor2 = new JTextField();
		
		//X and Y label
		mouseLoc = new JLabel("X:            Y: ");
		noOfSteps = new JLabel("Steps: 0");
		
		//Percentage button
			Percentage_combobox = new JComboBox();
			Percentage_combobox.setPreferredSize(new Dimension(200,50));
   		//combobox.addItem("--Select Speed--");
			Percentage_combobox.addItem("100");
			Percentage_combobox.addItem("90");
	   		Percentage_combobox.addItem("80");
	   		Percentage_combobox.addItem("70");
	   		Percentage_combobox.addItem("60");
	   		Percentage_combobox.setSelectedIndex(0);
              getPercentage(100);
	
             Percentage_combobox.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		       String Percent_Explored = (String) Percentage_combobox.getSelectedItem();
	           int Percent_Explored1 = Integer.parseInt(Percent_Explored);
	           getPercentage(Percent_Explored1);
	           System.out.println(Percent_Explored1);
		}

	});
		
		//loadfile
		LoadFile = new JButton("Load File");
		LoadFile.setPreferredSize(new Dimension(200,50));
		LoadFile.addActionListener(new ActionListener(){

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
		//combobox 
			combobox = new JComboBox();
		    combobox.setPreferredSize(new Dimension(200,50));
	   		//combobox.addItem("--Select Speed--");
		   		  combobox.addItem("50");
		   		  combobox.addItem("100");
	              combobox.addItem("200");
	              combobox.addItem("300");
	              combobox.addItem("1000");
	              combobox.setSelectedIndex(0);
	              setspeed(50);
		
	        combobox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		           speed = (String) combobox.getSelectedItem();
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
				String data = textField_1.getText(); 
			    String data1 = textField.getText();
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
			            textField.setText(""+interval%60);
		                textField_1.setText(""+interval/60);
			        }
			    }, 0, 1000);
			    explorerBtn.setVisible(false);
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
				textField.setText(""+time%60);
				textField_1.setText(""+time/60);
			}
		});
		
		
				
		buttonsPanel.setLayout(new GridLayout(9,4));
		buttonsPanel.add(mouseLoc);
		buttonsPanel.add(noOfSteps);
		buttonsPanel.add(String1);
		buttonsPanel.add(mapdescriptor1);
		buttonsPanel.add(String2);
		buttonsPanel.add(mapdescriptor2);
		buttonsPanel.add(Minutes);
		buttonsPanel.add(textField_1);
		buttonsPanel.add(Seconds);
		buttonsPanel.add(textField);
		buttonsPanel.add(Speed1);
		buttonsPanel.add(combobox);
		buttonsPanel.add(Explored);
		buttonsPanel.add(Percentage_combobox);
		buttonsPanel.add(explorerBtn);
		buttonsPanel.add(shortestPathBtn);
		buttonsPanel.add(LoadFile);
		buttonsPanel.add(resetBtn);


		
		
		//add all panels into control panel
		//add(mapFilePanel);
		add(buttonsPanel);
	}
	
	public void setString1 (String string1) {
		mapdescriptor1.setText(string1);
	}
	
	public void setString2 (String string2) {
		mapdescriptor2.setText(string2);
	}
	
	public void setMouseLoc (int x, int y) {
		mouseLoc.setText("X: " + Integer.toString(x) + "        Y: " + Integer.toString(y));
	}
	
	public void setNoOfSteps (int steps) {
		noOfSteps.setText("Steps: " + Integer.toString(steps));
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
