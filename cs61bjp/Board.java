package cs61bjp;

/* Board.java */

public class Board {
    
    private int[][] gameBoard;
    
    public Board() {
   	 gameBoard = new int[8][8];
    }
    
    public int squareContents(int x, int y) {
   	 return gameBoard[x][y];
    }
    
    public int squareContents(Coordinate c) {
   	 return gameBoard[c.x][c.y];
    }

   public void set(int x, int y, int content) {
	gameBoard[x][y] = content;
   }

   public void set(Coordinate c, int content) {
	gameBoard[c.x][c.y] = content;
   }

  public String toString() {
    String ret = "";
    for(int j=0; j<8; j++) {
      ret+="|";
      for(int i=0; i<8; i++) {
	if(gameBoard[i][j]==GameState.BLACK) {
	  ret+=" B ";
	} else if(gameBoard[i][j]==GameState.WHITE) {
	  ret+=" W ";
	} else {
	  ret+="   ";
	}
      }
      ret+="|\n\n";
    }
    return ret;
  }
}

