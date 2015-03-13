package map_simulator;

import java.io.BufferedReader;
import java.io.IOException;

public class InitProgram {

	public static MainFrame maze;
	public static Simulator simulator;
	public static Client client;
	public static BluetoothListener bluetoothListener;
	public static void main(String[] args) throws IOException {
		maze = new MainFrame();
		simulator = new Simulator(maze);
		
		if(!Config.SIMULATOR) {
//			client = new Client("127.0.0.1",9111);
//			client = new Client("172.21.149.89",7777);
//			BluetoothServer server = new BluetoothServer();
//			server.start();
			
//			Thread thread = new Thread(){public void run() {
//                try {
//					Thread.sleep(5000);
//					System.out.println("%%%");
//					client.pauseThreadListening();
//					System.out.println("%%%");
//					client.send("test123");
//					client.resumeThreadListening();
//					
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//            }
//			};
//			thread.start();
			client = new Client("192.168.4.4",8080);
			client.connect();

		}
	}
}
