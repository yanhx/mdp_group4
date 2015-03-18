Hi Nicholas, to finalize the format of the string that we going to send and receive

Over bluetooth connection: 

For orientation and map coordination: Android would receive a string in the format 
	RowCol_Orientation_Obstacles
where
RowCol: example row 12, col 12 => “1212”
Orientation: 0: North, 1 East, 2 South, 3 West
Obstacles: example if robot detect an obstacles at row 10, col 12 and an obstacle at row 10, col 11 =>  “1012 1011”  (a space between) 


Orientation: 
switch(robotOrientation) {
		case Constants.EAST: // facing east
			robotOrientation = 1;
			break;
		case Constants.SOUTH: // facing south
			robotOrientation = 2;
			break;
		case Constants.WEST: // facing west
			robotOrientation = 3;
			break;
		case Constants.NORTH: // facing north
			robotOrientation = 0;
			break;
		}

