package map_simulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

public class BluetoothListener extends Thread{
	private StreamConnection mConnection;
	private OutputStream mmOutStream;
	public BluetoothListener(StreamConnection connection)
	{
		mConnection = connection;
		try {
			mmOutStream = mConnection.openOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run() {
		try {
			// prepare to receive data
			InputStream inputStream = mConnection.openInputStream();

			System.out.println("waiting for input");
			byte[] buffer = new byte[1024];  // buffer store for the stream
	        int bytes;
			while (true) {
				bytes = inputStream.read(buffer);
				processCommand(bytes,buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void processCommand(int bytes, byte[] buffer) {
		String string = new String(buffer,0,bytes);
		String code;
		switch(string){
		case "Explore" : 		Simulator.m.controlPanel.performExploration(); 
								System.out.println("Bluetooth say start explore");
								break;
							
		case "ShortestPath" : 	Simulator.m.controlPanel.performShortestPath();
								System.out.println("Bluetooth say start shortestPath");
								break;
								
		case "Reset" : 			Simulator.reset();
								System.out.println("Bluetooth say reset");
								break;
		case "Forward" : 		code = Config.MODE_EXPLORE + Config.MOVEMENT_GO_STRAIGHT + Config.SPEED_ONE_QUARTER + RobotDescriptor.convertDistance(1);
								Client.send(RobotDescriptor.convert(code));	
								break;
								
		case "Left":			code = Config.MODE_EXPLORE + Config.MOVEMENT_TURN_LEFT + Config.SPEED_ONE_QUARTER + RobotDescriptor.convertDistance(0);
								Client.send(RobotDescriptor.convert(code));	
								break;
								
		case "Right":			code = Config.MODE_EXPLORE + Config.MOVEMENT_TURN_RIGHT + Config.SPEED_ONE_QUARTER + RobotDescriptor.convertDistance(0);
								Client.send(RobotDescriptor.convert(code));	
								break;
								
		case "Down" :			code = Config.MODE_EXPLORE + Config.MOVEMENT_TURN_BACK + Config.SPEED_ONE_QUARTER + RobotDescriptor.convertDistance(0);
								Client.send(RobotDescriptor.convert(code));	
								break;
								
		default : System.out.println(string);
					break;
		}
	}
	public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
}
