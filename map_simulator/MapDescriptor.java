package map_simulator;

public class MapDescriptor {

	public final int UNEXPLORED = 0;
	public final int WALKABLE = 1;
	public final int OBSTACLE = 2;
	
	public MapDescriptor(){
		
	}
	
	public String encodeExploredUnexplored(int[][] map) {

		String encodedMapInBinary = "";
		String encodedMap = "";

		encodedMapInBinary += "11";

		for (int y = 19; y >= 0; y--) {
			for (int x = 0; x < 15; x++) {
				switch(map[x][y]) {
					case UNEXPLORED:
						encodedMapInBinary += "0";
						break;
					default:
						encodedMapInBinary += "1";
				}
			}
		}
		encodedMapInBinary += "11";

		encodedMap = stringToHex(encodedMapInBinary);
		
		return encodedMap;
	}

	public String encodeObstacle(int[][] map) {

		String encodedMapInBinary = "";
		String encodedMap = "";

		for (int y = 19; y >= 0; y--) {
			for (int x = 0; x < 15; x++) {
				switch(map[x][y]) {
					case OBSTACLE:
						encodedMapInBinary += "1";
						break;
					case UNEXPLORED:
						continue;
					default:
						encodedMapInBinary += "0";
				}
			}
		}

		while (encodedMapInBinary.length() % 8 != 0) {
			encodedMapInBinary += "0";
		}
		
		encodedMap = stringToHex(encodedMapInBinary);
		
		return encodedMap;
	}

	public String stringToHex(String input) {
		String hex = "";
		String checkStr = "";
		int i;
		
		for (i = 0; i < input.length(); i = i + 4) {
			if ((i + 4) <= input.length())
				checkStr = input.substring(i, i + 4);

			switch (checkStr) {
			case "1111":
				hex += "F";
				break;
			case "1110":
				hex += "E";
				break;
			case "1101":
				hex += "D";
				break;
			case "1100":
				hex += "C";
				break;
			case "1011":
				hex += "B";
				break;
			case "1010":
				hex += "A";
				break;
			case "1001":
				hex += "9";
				break;
			case "1000":
				hex += "8";
				break;
			case "0111":
				hex += "7";
				break;
			case "0110":
				hex += "6";
				break;
			case "0101":
				hex += "5";
				break;
			case "0100":
				hex += "4";
				break;
			case "0011":
				hex += "3";
				break;
			case "0010":
				hex += "2";
				break;
			case "0001":
				hex += "1";
				break;
			case "0000":
				hex += "0";
				break;
			}
		}
		
		return hex;
	}
}