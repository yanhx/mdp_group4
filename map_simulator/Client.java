package map_simulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;

public class Client { 
//extends Observable implements Runnable{
	private static Socket skt;
    private static boolean connected;
    private static String hostname;
    private static int port;
    private static BufferedReader in;
    private static PrintWriter ps;
    private static ClientController clientReceiver;
//    private static ClientSender clientSender;
    private static Controller control;
    
	public Client(String hostname, int port){
		this.hostname = hostname;
		this.port = port;
		this.connected = false;
		this.control = new Controller();
	}

	public static void connect(){
		do{
			try{
				skt = new Socket(hostname,port);
				in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
		        connected = true;
		        if(connected == true){
			        System.out.println("Connected to " + hostname + "!");
					clientReceiver = new ClientController(skt);
					clientReceiver.registerObserver(control);
					clientReceiver.start();
		        }       
			} catch (UnknownHostException e) {
	            System.err.println("Don't know about host: hostname");
	            connected = false;
	        } catch (IOException e) {
	            System.err.println("Couldn't get I/O for the connection to: hostname");
	            connected = false;
	        }
		}while(!connected);
	}
	
	public static void pauseThreadListening() {
		clientReceiver.pauseThreadListening();
	}
	
	public static void resumeThreadListening() {
		clientReceiver.resumeThreadListening();
	}
	
	public static BufferedReader getBufferedReader() {
		return clientReceiver.getBufferedReader();
	}
	
	public static void send(String robotCommand){
		try{
		if(skt != null){
			clientReceiver.send(robotCommand);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
