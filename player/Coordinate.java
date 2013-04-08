package player;

/**
 * A public class for holding all the fields for a position on the game board. This class is a
 * container for data, not an ADT; hence all the fields are public.
 * The x-coordinate index the horizontal direction (left to right) on the board.
 * The y-coordinate index the vertical direction (top to bottom).
 * x- and y-coordinates start at zero.
 *
 **/

public class Coordinate {
    
  public int x;
  public int y;
    
  public Coordinate(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public String toString() {
    return "("+x+","+y+")";
  }
}
