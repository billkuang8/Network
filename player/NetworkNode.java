package player;

/**
 *  A public class for holding the current coordinate, as well as the parent coordinate
 *  which is linked to the current coordinate. The parent is a pointer to another NetworkNode. Acts like a reverse SList
 *  where the pointers point backwards, not forwards, and the only type of object NetworkNode can hold is a Coordinate object.
 *  lastDirection is the direction from parent to current coordinate
 */

public class NetworkNode {
  protected Coordinate coord;
  protected NetworkNode parent;
  private int lastDirection;  //0 is up/down, 1 is left/right, 2 is upright downleft, 3 is upleft downright
  private int size = 0;
	
  //constructor for when there is no parent. The beginning of a NetworkNode
  public NetworkNode(Coordinate c) {
    parent = null;
    coord = c;
    size = 1;
    lastDirection = -1;
  }
	
  //creates a new NetworkNode and links it with the parent
  public NetworkNode(Coordinate c, NetworkNode parent) {
    this.parent = parent;
    coord = c;
    lastDirection = getDirection(parent.coord, c);
    size = parent.getSize() + 1;
  }
	
  //returns the length of the current NetworkNode
  protected int getSize() {
    return size;
  }

  //returns the direction from parent to coordinate
  public int getDirection() {
    return lastDirection;
  }
	
  //c1 is parent, c2 is child, getDirection() returns an integer direction representation from parent to child
  protected int getDirection(Coordinate c1, Coordinate c2) {
    int dir;
    int x1 = c1.x, y1 = c1.y, x2 = c2.x, y2 = c2.y;
    int xdiff = x2 - x1;
    int ydiff = y2 - y1;
		
    if (xdiff == 0) {
      dir = 0;
    } else if (ydiff == 0) {
      dir = 1;
    } else if ((ydiff < 0 && xdiff > 0) || (ydiff > 0 && xdiff < 0)) {
      dir = 2;
    } else {
      //MachinePlayer.debugPrint("getDirection else: "+c1+" , "+c2+"..."+xdiff+" , "+ydiff);
      dir = 3;
    }
		
    return dir;
  }

  //converts the coordinate of the NetworkNode to a string, and returns
  public String toString() {
    return coord.toString();
  }

  //returns the entire path of the NetworkNode, including direction
  public String toStringPath() {
    if(parent==null) {
      return toString();
    } else {
      return parent.toStringPath()+"-"+lastDirection+"->"+toString();
    }
  }
	
}
