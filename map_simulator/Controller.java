package map_simulator;

import java.util.Observable;

public class Controller extends Observable{
	
	public Controller(){
		
	}
	
	public void update(String command){
		//System.out.println("Controller command is: "+command);
		switch(command){
		//start exploration
		case "Explore": Simulator.m.controlPanel.performExploration(); System.out.println("----Starting Exploration----"); break;
		//start shortest path
		case "ShortestPath": Simulator.m.controlPanel.performShortestPath(); System.out.println("----Starting Shortest Path----"); break;
		//reset the map
		case "Reset": Simulator.reset(); System.out.println("----Reset----"); break;
		//default is sensors
		default: 
			
			//update sensors
			break;
		}
	}

}
