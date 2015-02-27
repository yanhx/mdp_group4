package map_simulator;

public class Sensor {

	private Position position;
	private Direction direction;
	private SensorType sensorType;
	private Map m;
	private static int[][] simulationMap;
	public static String data;
	
//	public Sensor(SensorType sensorType) {
//		this(sensorType, new Position(), null);
//	}
	
	public Sensor(SensorType sensorType, Position position, Direction direction, Map m) {
		this.position = position;
		this.direction = direction;
		this.sensorType = sensorType;
		this.m = m;
		this.simulationMap = m.getSimulationMap();
	}
	
	public Sensor(SensorType sensorType, Position position, Direction direction) {
		this.position = position;
		this.direction = direction;
		this.sensorType = sensorType;
	}

	public Position[] getPositionfLongRangeSensor() {
		Position[] rangeList = new Position[3];
		int[] rangeX = new int[3];
		int[] rangeY = new int[3];
		
		for(int i = 0; i < 3; i++){
			rangeX[i] = -1;
			rangeY[i] = -1;
		}

		switch(direction) {
		case NORTH:
			rangeX[0] = position.getX();
			rangeY[0] = position.getY() - 1;
			rangeX[1] = position.getX();
			rangeY[1] = position.getY() - 2;
			rangeX[2] = position.getX();
			rangeY[2] = position.getY() - 3;
			break;
		case SOUTH:
			rangeX[0] = position.getX();
			rangeY[0] = position.getY() + 1;
			rangeX[1] = position.getX();
			rangeY[1] = position.getY() + 2;
			rangeX[2] = position.getX();
			rangeY[2] = position.getY() + 3;
			break;
		case EAST:
			rangeX[0] = position.getX() + 1;
			rangeY[0] = position.getY();
			rangeX[1] = position.getX() + 2;
			rangeY[1] = position.getY();
			rangeX[2] = position.getX() + 3;
			rangeY[2] = position.getY();
			break;
		case WEST:
			rangeX[0] = position.getX() - 1;
			rangeY[0] = position.getY();
			rangeX[1] = position.getX() - 2;
			rangeY[1] = position.getY();
			rangeX[2] = position.getX() - 3;
			rangeY[2] = position.getY();
			break;
		}
		
		for(int i = 0; i < 3; i++){
			if(rangeX[i] < simulationMap.length && rangeY[i] < simulationMap[0].length && rangeX[i] >= 0 && rangeY[i] >= 0) {
				rangeList[i] = new Position(rangeX[i], rangeY[i], direction);
			}
			else{
				rangeList[i] = new Position(-1, -1, null);
			}
		}
		return rangeList; 
	}
	
	public Position getPosition() {
		return position;
	}

	public Position getPositionNextToSensor() {
		int x = -1;
		int y = -1;

		switch(direction) {
		case NORTH:
			x = position.getX();
			y = position.getY() - 1;
			break;
		case SOUTH:
			x = position.getX();
			y = position.getY() + 1;
			break;
		case EAST:
			x = position.getX() + 1;
			y = position.getY();
			break;
		case WEST:
			x = position.getX() - 1;
			y = position.getY();
			break;
		}

		if(x < simulationMap.length && y < simulationMap[0].length && x >= 0 && y >= 0) {
			return new Position(x, y, direction);
		}

		return new Position(-1, -1, null);
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/*
	 * 
	 * Calculate the row and the col of the block next to the sensor.
	 * Then get the data from the simulation map.
	 * return -1 if the sensor cannot pick up
	 * 
	 */
	public int getData() {	//one block in front
		switch(sensorType) {
		case SENSOR_F1:
			return m.getSimulatedData(this.getPositionNextToSensor().getX(), this.getPositionNextToSensor().getY());
		case SENSOR_F2:
			return m.getSimulatedData(this.getPositionNextToSensor().getX(), this.getPositionNextToSensor().getY());
		case SENSOR_F3:
			return m.getSimulatedData(this.getPositionNextToSensor().getX(), this.getPositionNextToSensor().getY());
		case SENSOR_R:
			return m.getSimulatedData(this.getPositionNextToSensor().getX(), this.getPositionNextToSensor().getY());
		case SENSOR_L:
			return m.getSimulatedData(this.getPositionNextToSensor().getX(), this.getPositionNextToSensor().getY());
		}
		return -1;
	}
	
	public int[] getRangeData(){
		int[] values = new int[5];
		Position[] list = new Position[5];
		list = getPositionfLongRangeSensor();
		for(int i = 0; i < list.length; i++){
			values[i] = m.getSimulatedData(list[i].getX(), list[i].getY());
		}
		return values;
	}
	
//	public static void updateSensorData(String d) {
//		data = d;
//	}
}



