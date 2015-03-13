package map_simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ClientController extends Observable{
	private BufferedReader reader; 
	private Socket skt;
	private Controller control;
	private String command;
	private PrintWriter ps;
	private boolean pauseThreadListening;
	
	public ClientController(Socket skt) throws IOException{	
		this.reader = new BufferedReader(new InputStreamReader(skt.getInputStream()));
		this.command = "";
		this.ps = new PrintWriter(skt.getOutputStream(),true);
	//	this.reader.ready();
	}
	
	public BufferedReader getBufferedReader() {
		return reader;
	}
	
	public synchronized void pauseThreadListening() {
		pauseThreadListening = true;
	}
	
	public synchronized void resumeThreadListening() {
		pauseThreadListening = false;
	}
	
	public void registerObserver(Controller control){
		this.control = control;
	}
	
	public void notfityRobot(String command){
		control.update(command);
	}
	
	public void send(String robotCommand){
		try{
		ps.write(robotCommand);
		System.out.println("ROBOT COMMAND: "+robotCommand+" SPACE ");
		//System.out.println("send method");
		ps.flush();
//		try{
//			Thread.sleep(10);
//		}catch(InterruptedException evt){}
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	//@Override
	public void start() {
		String newMessage = new String();
		// TODO Auto-generated method stub
		System.out.println("test exploration!");
		notfityRobot("Explore");
		
		while(true) {
			while(!pauseThreadListening){
				try{
					if(reader.ready()){
						newMessage = this.reader.readLine();
						System.out.println(newMessage);
						notfityRobot(newMessage);

					
					}
				} catch (Exception e){
					System.err.println("This is the error: "+e);
				}			
			} 
		}
		
	}
	
}
