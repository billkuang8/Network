package cs61bjp;

import player.Move;

/* GameState.java */
//holds information about the current state of the board, including evaluation functions and the board with positions

public class GameState {

  final static int BLACK = -1;
  final static int EMPTY = 0;
  final static int WHITE = 1;

  final static int BOARDLENGTH = 8;

  final static double EVALMIN = -100;
  final static double EVALMAX = 100;
  final static int MAXLENGTH = 0;     //index constant for return double array of features method
  final static int EXPANDED = 1;      //index constant
  final static int DX = 2;            //index constant
  final static int DY = 3;            //index constant

  protected Board gameBoard;
  protected int blackCount;
  protected int whiteCount;

  public GameState() {
    gameBoard = new Board();
    blackCount = 0;
    whiteCount = 0;
  }

  /**
   * evaluation() returns a double that is a representation of how good the
   * state is for the player indicated by side
   * 
   * @return double of calculated evaluation function of the game state
   **/
  public double evaluation(int side) {
    double[] constants = {1.0, 1.0, -1.0, -1.0}; //constant multipliers for features
    //MachinePlayer.debugPrint("evaluation start:");
    double[] myEval1 = features(side, startingNodes(side), true);
    double[] myEval2 = features(side, endingNodes(side), false);
    double[] enemyEval1 = features(opponent(side),
				      startingNodes(opponent(side)), true);
    double[] enemyEval2 = features(opponent(side),
				      endingNodes(opponent(side)), false);
    if (myEval1 == null) {
      return EVALMAX;
    }
    if (enemyEval1 == null) {
      return EVALMIN;
    }

    double ret = 0;

    for(int i = 0; i<constants.length; i++) {
      ret+= (myEval1[i]+myEval2[i]-enemyEval1[i]-enemyEval2[i])*constants[i];
    }

    //MachinePlayer.debugPrint("MYEVAL1 => expanded: "+myEval1[EXPANDED]+"  dx: "+myEval1[DX]+"   dy: "+myEval1[DY]);
    //MachinePlayer.debugPrint("MYEVAL2 => expanded: "+myEval2[EXPANDED]+"  dx: "+myEval2[DX]+"   dy: "+myEval2[DY]);

    if(ret<=EVALMIN) {
      ret = EVALMIN+1;
    } else if(ret>=EVALMAX) {
      ret = EVALMAX-1;
    }

    return ret;
  }
  
  /**
   * opponent() returns the integer representation for the opponent
   * @param side is the integer representation of the player or machine
   * @return integer representation of the opponent side
   **/
  protected static int opponent(int side) {
    if (side == BLACK) {
      return WHITE;
    } else {
      return BLACK;
    }
  }


  /** move() changes the state of the board by adding or stepmoving
    * @param m is the Move
    * @param side is the int representation of the side for which the move is made (BLACK or WHITE)
    * @return returns a boolean, true if the move is made, and false if the move is invalid or the move is a quit move
    **/
  protected boolean move(Move m, int side) {
    if (m.moveKind == Move.QUIT || !isValidMove(m, side)) {
      return false;
    }
    if (m.moveKind == Move.STEP) {
      gameBoard.set(m.x2, m.y2, EMPTY);
    }
    if (m.moveKind == Move.ADD) {
      if (side == BLACK) {
	blackCount++;
      } else {
	whiteCount++;
      }
    }
    gameBoard.set(m.x1, m.y1, side);
    return true;
  }


  /**
    * unmove() takes a Move and undoes it (takes it off the board, or if it is a step function, unsteps the chip)
    * @param m is the Move to be undone
    * @param side is the int representation of the side for which the move needs to be undone. (BLACK or WHITE)
    * @return returns a boolean, true if the unmove was succesful and walse otherwise
    **/
  protected boolean unmove(Move m, int side) {
    if (m.moveKind == Move.QUIT) {
      return false;
    } else if (m.moveKind == Move.ADD) {
      if (gameBoard.squareContents(m.x1, m.y1) != side) {
	System.out.println("false");

	return false;
      } else {
	if (side == BLACK) {
	  blackCount--;
	} else {
	  whiteCount--;
	}
	gameBoard.set(m.x1, m.y1, EMPTY);
	return true;
      }
    } else { // STEP
      return move(new Move(m.x2, m.y2, m.x1, m.y1), side);
    }
  }

  /**
   * isValidMove() determines whether the given �move� is valid for
   * player �side�.
   * @param m is a specific move the player wants to make,
   * which will be a position on the GameBoard.
   * @param side is the GameState.BLACK or  
   * GameState.WHITE
   * @return true if the move is valid for player �side�,
   * otherwise false.
   **/
  protected boolean isValidMove(Move m, int side) {
    int x = m.x1;
    int y = m.y1;

    if (m.moveKind == Move.QUIT) {
      return false;
    }
    if (m.moveKind == Move.STEP) {
      if (gameBoard.squareContents(m.x2, m.y2) != side) {
	return false;
      }
    }
    if (wrongGoal(x, y, side)) {
      return false;
    }
    if (gameBoard.squareContents(x, y) != EMPTY) {
      return false;
    }
    if (m.moveKind == Move.STEP) {
      gameBoard.set(m.x2, m.y2, EMPTY);
    }
    if (hasCluster(x, y, side)) {
      if (m.moveKind == Move.STEP) {
	gameBoard.set(m.x2, m.y2, side);
      }
      return false;
    } else {
      if (m.moveKind == Move.STEP) {
	gameBoard.set(m.x2, m.y2, side);
      }
      return true;
    }
  }
  
  /**
   * wrongGoal() checks if the chip color matches with the physical side it is supposed to be in (white chips can only be east/west edges and black chips can only be on the north/south edges)
   * @param x is the x coordinate integer on the board
   * @param y is the y coordinate integer on the board
   * @param side is the color side to be checked 
   * @return true if the side is in the wrong goal, or in the corner, and false otherwise
   **/
  protected boolean wrongGoal(int x, int y, int side) {
    if (side == BLACK) {
      if (x == 0) {
	return true;
      }
      if (x == BOARDLENGTH - 1) {
	return true;
      }
    } else if (side == WHITE) {
      if (y == 0) {
	return true;
      }
      if (y == BOARDLENGTH - 1) {
	return true;
      }
    }
    return false;
  }

  /** 
   * hasCluster() checks if the given chip coordinate is adjacent to two other chips of that color (adjacent in this case means touching, or touching a chip that touches a different chip of that color, also forming a cluster)
   * @param x is the x coordinate integer on board
   * @param y is the y coordinate integer on board
   * @param side is the side of the chip to be checked (BLACK or WHITE)
   * @return returns a boolean, true if there is a cluster of three at that point and false otehrwise
   **/
  protected boolean hasCluster(int x, int y, int side) {
    SList adjacents = findAdjacents(x, y, side);
    if (adjacents.length() >= 2) {
      return true;
    } else if (adjacents.length() == 1) {
      int x1 = ((Coordinate) adjacents.nth(1)).x;
      int y1 = ((Coordinate) adjacents.nth(1)).y;

      if (hasAdjacents(x1, y1, side)) {
	return true;
      } else {
	return false;
      }
    } else {
      return false;
    }
  }

  /**
   * findAdjacents() returns an SList (singularly linked list) of Coordinate objects surrounding and touching the given point on board, of the given side
   * @param x is the x coordinate integer on board 
   * @param y is the y coordinate integer on board
   * @param side is the side of the chip to be checked (BLACK or WHITE)
   * @return returns an SList of adjacent and matching to �side� Coordinate objects
   **/
  protected SList findAdjacents(int x, int y, int side) {
    SList adjacents = new SList();
    for (int i = x - 1; i <= x + 1; i++) {
      for (int j = y - 1; j <= y + 1; j++) {
	if (i >= 0 && j >= 0 && i < BOARDLENGTH && j < BOARDLENGTH) {
	  if (i == x && j == y) {
	    continue;
	  } else if (gameBoard.squareContents(i, j) == side) {
	    Coordinate c = new Coordinate(i, j);
	    adjacents.insertFront(c);
	  }
	}
      }
    }
    return adjacents;
  }

  /**
   * hasAdjacents() checks if there are adjacents of given color at given point
   * @param x is the x coordinate integer on board
   * @param y is the y coordinate integer on board
   * @param side is the side of chip to be checked (BLACK or WHITE)
   * @return true if there is at least one touching adjacent chip of same color, and false otherwise
   **/
  protected boolean hasAdjacents(int x, int y, int side) {
    return !findAdjacents(x, y, side).isEmpty();
  }

  /**
   * validMoves() generates a SList of valid moves for
   * the player �side�. 
   * special case: When generating stepmoves, there may be duplicates (not a problem, still a correct move, but you may have more than one of it)
   * @param side is side for which the moves need to be generated (BLACK or WHITE)
   * @return a SList that contains Coordinate objects of
   * such valid moves, null if no valid moves are found.
   **/
  protected SList validMoves(int side) {
    boolean isStep = false;
    if ((side == BLACK && blackCount == 10)
	|| (side == WHITE && whiteCount == 10)) {
      isStep = true;
    }
    SList current = new SList();
    if (isStep) {
      for (int i = 0; i < BOARDLENGTH; i++) {
	for (int j = 0; j < BOARDLENGTH; j++) {
	  if (gameBoard.squareContents(i, j) == side) {
	    current.insertFront(new Coordinate(i, j));
	  }
	}
      }
    }
    SList moves = new SList();
    for (int i = 0; i < BOARDLENGTH; i++) {
      for (int j = 0; j < BOARDLENGTH; j++) {
	Move m = new Move(i, j);
	if (isValidMove(m, side)) {
	  if (!isStep) {
	    moves.insertFront(m);
	  } else {
	    SListNode currentNode = current.front();
	    for (int k = 0; k < 10; k++) {
	      Coordinate c = (Coordinate) currentNode.item;
	      moves.insertFront(new Move(i, j, c.x, c.y));
	      currentNode = currentNode.next;
	    }
	  }
	}
      }
    }
    // check 24 spaces around each current position
    if (isStep) {
      SListNode currentNode = current.front();
      for (int k = 0; k < 10; k++) {
	Coordinate c = (Coordinate) currentNode.item;
	moves = validStepMoves(c, side, moves);
	currentNode = currentNode.next;
      }
    }
    return moves;
  }

  /** 
   * validStepMoves() returns an SList of step moves for the player �side�. They are the extra possibilities of step moves that arise when the piece at the given coordinate has been removed (not removed for sure, but theoretically)
   * @param c is the coordinate that has been possibly moved 
   * @param side is the side of the coordinate c
   * @param moves is the list of all possible moves before checking the step move at the given coordinate
   * @return SList moves, with the other move possibilities after the step added in
   **/
  protected SList validStepMoves(Coordinate c, int side, SList moves) {
    for (int i = c.x - 2; i <= c.x + 2; i++) {
      for (int j = c.y - 2; j <= c.y + 2; j++) {
	if (i >= 0 && j >= 0 && i < BOARDLENGTH && j < BOARDLENGTH) {
	  Move m = new Move(i, j, c.x, c.y);
	  if (isValidMove(m, side)) {
	    moves.insertFront(m);
	  }
	}
      }
    }
    return moves;
  }

  /**
   * networkNeighbors() returns an SList of NetworkNode Objects that are in a
   * network with the given Coordinate. Checks what color is at the parent. If
   * there is no piece there, behavior of method is undefined.
   * 
   * @param parent
   *            is NetworkNode on board
   **/
  protected SList networkNeighbors(NetworkNode parent) {
    SList sl = new SList();
    Coordinate c = parent.coord;
    int xOfC = c.x;
    int yOfC = c.y;
    int side = gameBoard.squareContents(c);
    int oppSide = opponent(side);

    // start going directly north, then go counter-clockwise
    int[] dx = { 0, -1, -1, -1, 0, 1, 1, 1 };
    int[] dy = { -1, -1, 0, 1, 1, 1, 0, -1 };

    for (int dir = 0; dir < 8; dir++) {
      int x = xOfC, y = yOfC;

      x += dx[dir];
      y += dy[dir];

      while (x < BOARDLENGTH && x >= 0 && y < BOARDLENGTH && y >= 0
	     && gameBoard.squareContents(x, y) != oppSide) {

	if (gameBoard.squareContents(x, y) == side) {
	  NetworkNode newN = new NetworkNode(new Coordinate(x, y),
					     parent);
	  sl.insertEnd(newN);
	  break;
	}

	x += dx[dir];
	y += dy[dir];

      }
    }
    return sl;
  }

  /**
   * hasValidNetwork() determines if Board has a valid network. if side !=
   * MachinePlayer.COMPUTER || MachinePlayer.OPPONENT return false.
   * 
   * @param side
   *            is MachinePlayer.COMPUTER || MachinePlayer.OPPONENT
   * @return true if player side has winning network in this board, and false
   *         otherwise.
   **/
  public boolean hasValidNetwork(int side) {
    //MachinePlayer.debugPrint("hasValidNetwork start:");
    SList fringe = startingNodes(side);
    //MachinePlayer.debugPrint("fringe: "+fringe);
    while (!fringe.isEmpty()) {
      NetworkNode parentNode = (NetworkNode) fringe.front().item;
      fringe.removeFront();
      //MachinePlayer.debugPrint("parentNode path: "+parentNode.toStringPath());
      SList neighbors = networkNeighbors(parentNode);
      //MachinePlayer.debugPrint("neighbors: "+neighbors);
      SListNode currentNode = neighbors.front();
      for (int i = 0; i < neighbors.length(); i++) {
	NetworkNode currentNetNode = (NetworkNode) currentNode.item;
	if (currentNetNode.getDirection() == currentNetNode.parent.getDirection() ||
	    alreadyVisited(currentNetNode)) {
	  currentNode = currentNode.next;
	  continue;
	} else if (currentNetNode.getSize() >= 6 && isInGoal(currentNetNode)) {
	  //MachinePlayer.debugPrint("hasValidNetwork found a valid network for "+side);
	  return true;
	} else if (!onEdge(currentNetNode)){
	  fringe.insertFront(currentNetNode);
	}
	currentNode = currentNode.next;
      }
      //MachinePlayer.debugPrint("fringe: "+fringe);
    }
    //MachinePlayer.debugPrint("hasValidNetwork found no valid networks for "+side);
    return false;
  }

  protected boolean onEdge(NetworkNode n) {
    return n.coord.x==0 || n.coord.x==(BOARDLENGTH - 1) || n.coord.y==0 || n.coord.y==(BOARDLENGTH - 1);
  }

  /** 
   * alreadyVisited() checks if the last coordinate in the given NetworkNode has already been visited earlier in the NetworkNode
   * @param nn is the NetworkNode to be checked
   * @return true if the last coordinate has been visited, and false otherwise
   **/
  protected boolean alreadyVisited(NetworkNode nn) {
    Coordinate c = nn.coord;
    while (nn.parent != null) {
      nn = nn.parent;
      Coordinate testC = nn.coord;
      if (c.x == testC.x && c.y == testC.y) {
	return true;
      }
    }
    return false;
  }

  /** 
   * isInGoal() checks if the last piece in the NetworkNode is in the goal area
   * @param nn is the NetworkNode to be checked
   * @return true if coordinate of nn is in right or bottom side of board
   **/
  protected boolean isInGoal(NetworkNode nn) {
    Coordinate c = nn.coord;

    if (c.x == (BOARDLENGTH - 1) || c.y == (BOARDLENGTH - 1)) {
      return true;
    } else {
      return false;
    }
  }

  /** 
   * features() returns an int array of length 4 with features for the eval function or null if validNetwork. Calculates numbers that evaluation uses to grade each board state, using certain features of the board like:
   * max network length squared
   * number of network nodes expanded in search
   * x distance to middle of goal (the closer the better)
   * y distance to middle of goal (closer is better)
   * @param side is side (BLACK of WHITE)
   * @param fringe is the SList of NetworkNode objects on the edge
   * @param starting is true if the fringe is in the start (upper or left side) and false if the fringe is in end (bottom or right side)
   * @return an array of doubles with corresponding values of certain characteristics
   **/
  protected double[] features(int side, SList fringe, boolean starting) {
    double[] ret = new double[4];
    ret[DX] = BOARDLENGTH;
    ret[DY] = BOARDLENGTH;
    double gx, gy;
    if(side == WHITE) {
      if(starting) {
	gx = BOARDLENGTH-1;
      } else {//ending
	gx = 0;
      }
      gy = ((double) BOARDLENGTH-1.0)/2;
    } else {//WHITE
      if(starting) {
	gy = BOARDLENGTH-1;
      } else {//ending
	gy = 0;
      }
      gx = ((double) BOARDLENGTH-1.0)/2;
    }
    while (!fringe.isEmpty()) {
      NetworkNode parentNode = (NetworkNode) fringe.front().item;
      fringe.removeFront();
      //MachinePlayer.debugPrint("parentNode: "+parentNode.toString());

      SList neighbors = networkNeighbors(parentNode);
      SListNode currentNode = neighbors.front();


      if (ret[MAXLENGTH] < parentNode.getSize()) {
	ret[MAXLENGTH] = parentNode.getSize();
      }
      ret[EXPANDED]++;
      if (ret[DX] > Math.abs(gx-parentNode.coord.x)) {
	ret[DX] = Math.abs(gx-parentNode.coord.x);
      }
      if (ret[DY] > Math.abs(gy-parentNode.coord.y)) {
	ret[DY] = Math.abs(gy-parentNode.coord.y);
      }
      for (int i = 0; i < neighbors.length(); i++) {
	NetworkNode currentNetNode = (NetworkNode) currentNode.item;
	if ( currentNetNode.getDirection() == currentNetNode.parent.getDirection() ||
	    alreadyVisited(currentNetNode)) {
	  currentNode = currentNode.next;
	  continue;
	} else if (currentNetNode.getSize() >= 6 && starting
	    && isInGoal(currentNetNode)) {
	  return null;
	} else if (!onEdge(currentNetNode)) {
	  fringe.insertFront(currentNetNode);
	}
	currentNode = currentNode.next;
      }
    }

    ret[MAXLENGTH] = ret[MAXLENGTH] * ret[MAXLENGTH];
    return ret;
  }

  /**
   * startingNodes() generates an SList of NetworkNodes that are in the north or west goal area depending on the color of the �side� given. 
   * @param side is the side (BLACK or WHITE)
   * @return SList of NetworkNodes
   **/
  protected SList startingNodes(int side) {
    int[] x = new int[BOARDLENGTH];
    int[] y = new int[BOARDLENGTH];
    SList ret = new SList();
    if (side == BLACK) {
      for (int i = 0; i < BOARDLENGTH; i++) {
	x[i] = i;
	y[i] = 0;
      }
    } else {// side==WHITE
      for (int i = 0; i < BOARDLENGTH; i++) {
	x[i] = 0;
	y[i] = i;
      }
    }
    for (int i = 0; i < BOARDLENGTH; i++) {
      if (gameBoard.squareContents(x[i], y[i]) == side) {
	NetworkNode temp = new NetworkNode(new Coordinate(x[i], y[i]));
	ret.insertFront(temp);
      }
    }
    return ret;
  }

  /**
   * endingNodes() generates an SList of NetworkNodes that are in the south or east goal depending on the color of �side� given
   * @param side is side (BLACK or WHITE) 
   * @return SList of NetworkNodes
   **/
  protected SList endingNodes(int side) {
    int[] x = new int[BOARDLENGTH];
    int[] y = new int[BOARDLENGTH];
    SList ret = new SList();
    if(side == BLACK) {
      for(int i=0;i<BOARDLENGTH;i++) {
	x[i]=i;
	y[i]=BOARDLENGTH-1;
      }
    } else {//side==WHITE
      for(int i=0;i<BOARDLENGTH;i++) {
	x[i]=BOARDLENGTH-1;
	y[i]=i;
      }
    }
    for(int i=0;i<BOARDLENGTH;i++) {
      if(gameBoard.squareContents(x[i],y[i])==side) {
	NetworkNode temp = new NetworkNode(new Coordinate(x[i],y[i]));
	ret.insertFront(temp);
      }
    }
    return ret;
  }

  /**
   * toString() prints the text representation of the board, as well as the white and black chip count
   * @return String representation of said board.
   **/
  public String toString(){
    return gameBoard.toString()+"blackCount: "+blackCount+"   whiteCount: "+whiteCount;
  }

}
