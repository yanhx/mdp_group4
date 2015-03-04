package map_simulator;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.ImageIcon;

public class Map {
	
	private Scanner m;
	private int Map[][] = new int[15][20];
	private int simulationMap[][] = new int[15][20];
	private boolean isVisited[][] = new boolean[15][20];
	private Image unexplored, explored, obstacle, shortestpath;
	
	public Map(){
		ImageIcon img = new ImageIcon("images/unexplored.jpg");
		unexplored = img.getImage();
		img = new ImageIcon("images/explored.jpg");
		explored = img.getImage();
		img = new ImageIcon("images/obstacle.jpg");
		obstacle = img.getImage();
		img = new ImageIcon("images/shortestpath.jpg");
		shortestpath = img.getImage();

		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 15; x++) {
				Map[x][y] = 0;
			}
		}
		
		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 15; x++) {
				isVisited[x][y] = false;
			}
		}
		
		if (Config.SIMULATOR) {
			readSimulatedMap();		
		} 
		else {
			if (Config.SHORTESTPATH_SIMULATOR) {
				readSimulatedMap();
				convertUnexploredToClearForSimulator();
			}
		}
	}
	
	public int[][] getSimulationMap() {
		return simulationMap;
	}

	public void setSimulationMap(int[][] simulationMap) {
		this.simulationMap = simulationMap;
	}

	public Image getShortestpath() {
		return shortestpath;
	}

	public void setShortestpath(Image shortestpath) {
		this.shortestpath = shortestpath;
	}

	public Image getObstacle(){
		return obstacle;
	}
	
	public Image getExplored(){
		return explored;
	}
	
	public int[][] getMap() {
		return Map;
	}

	public void setMap(int[][] map) {
		Map = map;
	}

	public void copyMap (int[][] map) {
		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 15; x++) {
				 Map[x][y] = map[x][y];
			}
		}
	}
	
	public Image getUnexplored(){
		return unexplored;
	}
	
	public int getLocInfo(int x, int y){
		if (x<0 || y<0 || x>14 || y>19)
			return 2;
		return Map[x][y];
	}
	
	public void setLocInfo(int x, int y, int type){
		if (x<0 || y<0 || x>14 || y>19)
			return;
		Map[x][y] = type;
	}

	public void setSimulatedLocInfo(int x, int y, int type){
		if (x<0 || y<0 || x>14 || y>19)
			return;
		simulationMap[x][y] = type;
	}
	
	public void printMap() {
		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 15; x++) {
				System.out.print(Map[x][y]);
			}
			System.out.println();
		}
	}
	
	public void printSimulationMap() {
		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 15; x++) {
				System.out.print(simulationMap[x][y]);
			}
			System.out.println();
		}
	}
	
	public void resetMap() {
		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 15; x++) {
				Map[x][y] = 0;
			}
		}
	}
	
	public void readSimulatedMap(){
		try{
			m = new Scanner(new File(ControlPanel.getFilename()));	
		}catch(Exception e){
			System.out.println("Error loading map");
		}
		
		int y = 0;
		while(m.hasNext()){
			String s = m.nextLine();
			String[] one = s.split("");
			for(int x = 0; x < 15; x++){
				simulationMap[x][y] = Integer.parseInt(one[x]);
				if (Integer.parseInt(one[x]) == 2) {
					Map[x][y] = 3;
				}
			}
			y++;
		}	
		m.close();
	}
	
	public void convertUnexploredToClearForSimulator(){
		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 15; x++) {
				if (Map[x][y] == 0) {
					Map[x][y] = 1;
				}
				if (Map[x][y] == 3) {
					Map[x][y] = 2;
				}
			}
		}
	}
	
	public int getSimulatedData(int x, int y){
		if (x==-1 || y==-1)
			return 2;
		return simulationMap[x][y];
	}
	
	public boolean finishExplored() {
		 for (int[] temp : Map) {
			 for (int value : temp) {
		        if (value == 0) //unexplored
		            return false;
		    }
		 }
		    return true;
	}
	
	public void setExplored(Robot r) {
		for(int i=-1; i <= 1; i++) {
			int x = r.getPos().getX() + i;
			for(int j=-1; j <= 1; j++) {
				int y = r.getPos().getY() + j;
				Map[x][y] = 1;
			}
		}
	}
	
	public void setVisited(Robot r) {
		for(int i=-1; i <= 1; i++) {
			int x = r.getPos().getX() + i;
			for(int j=-1; j <= 1; j++) {
				int y = r.getPos().getY() + j;
				Map[x][y] = 1;
				isVisited[x][y] = true;
			}
		}
	}

	public int countObstacle() {
		int noOfObstacle = 0;
		
		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 15; x++) {
				if (Map[x][y] == 2) { //2 = obstacle
					noOfObstacle += 1;
				}
			}
		}
		
		return noOfObstacle;
	}
	
	public int countUnexplored() {
		int noOfUnexplored = 0;
		
		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 15; x++) {
				if (Map[x][y] == 0 || Map[x][y] == 3) {
					noOfUnexplored += 1;
				}
			}
		}
		
		return noOfUnexplored;
	}
	
	public void convertUnexploredToClear() {
		int temp = 0;
		
		if (countObstacle() >= 23) {
			temp = countObstacle()-23;
			
				for (int y = 0; y < 20; y++) {
					for (int x = 0; x < 15; x++) {
						if (Map[x][y] == 0 || Map[x][y] == 3) {
							Map[x][y] = 1;
						}
					}
				}
				return;
		}
		
		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 15; x++) {
				if (Map[x][y] == 0 || Map[x][y] == 3) {
					Map[x][y] = 2;
				}
			}
		}
	}
	
	public boolean isVisited(int x, int y){
		return isVisited[x][y];
	}
	
	public void setVisited(int x, int y) {
		if (x<0 || y<0 || x>14 || y>19)
			return;		
		isVisited[x][y] = true;
	}

	public double checkExplorationState() {
		double noOfUnexplored = 0;
		
		for (int[] temp : Map) {
			for (int value : temp) {
				if (value == 0)
					noOfUnexplored++;
			}
		}
		
		return Math.round((((300.0-noOfUnexplored)/300.0)*100.0) * 100.0) / 100.0;
	}
	
	public int[] getOneUnexploredBlock() {
		int[] position = new int[2];
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 20; j++) {
				if (Map[i][j] == 0) {
					position[0] = i;
				    position[1] = j;
				    return position;
				}
			}
		}
		return null;
	}
	
//	public Position getUnvisitedPosition() {
//		for (int y = 0; y < 20; y++) {
//			for (int x = 0; x < 15; x++) {
//				System.out.print(simulationMap[x][y]);
//			}
//			System.out.println();
//		}
//	}
	
//	public void saveExploredMap() {
//		try {
//			PrintWriter out = new PrintWriter("map layout/map 1.txt");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		for (int y = 0; y < 20; y++) {
//			for (int x = 0; x < 15; x++) {
//				System.out.print(Map[x][y]);
//			}
//			System.out.println();
//		}
//	}
}
