package map_simulator;

public class RobotDescriptor {

	public static String convertSensors(int sensors){
		String s = Integer.toBinaryString(sensors);
		while (s.length() != 6) {
			s = "0" + s;
		}
		return s;
	}
	
	public static String convert(String code){
		String asciiCode = "";
		char nextChar;
		
		//padding
		//code = "0" + code;
		//System.out.println(code);
		
		return code;
				
//		for (int i = 0; i < code.length(); i += 7){
////			System.out.println(code.substring(i, i+7));
//			nextChar = (char)Integer.parseInt(code.substring(i, i+7), 2);
//			asciiCode += nextChar;
//		}
//		return asciiCode;
	}
	
	public static String convertDistance(int distance){
		String dist = Integer.toBinaryString(distance);
		
		while(dist.length() != 6){
			dist = "0" + dist;
		}
		
		//dist = "0" + dist;
		return dist;
	}
	
	public static String shortestPathStartIndicator(){
		String code1 = "11000010000000";
		String temp="";
		for (int i = 0; i < code1.length(); i += 7){
			temp += (char)Integer.parseInt(code1.substring(i, i+7), 2);
		}
		return temp;
	}
	
	public static String shortestPathEndIndicator(){
		String temp="";
		String code2 = "11111111111111";
		for (int i = 0; i < code2.length(); i += 7){
			temp += (char)Integer.parseInt(code2.substring(i, i+7), 2);
		}
		return temp;
	}
}
