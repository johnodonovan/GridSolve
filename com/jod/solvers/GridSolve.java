/*
*   Java class for solving an N*N arrangment of N symbols on a grid where no row or column contains the same symbol
*   Uses a simple random sample consensus (RANSAC) method. [Fischler & Bolles]
*   Includes a slection of fixed symbols and locations
*   Includes a constraint for unique symbols in each nonet (local 9th of board)
*
*   Used to solve the grid puzzle in Haiku Games Ipad version (Carnival)
*   NOTES:  Can be easily adjusted for solving SUDOKU and other grid-based games
*   NOTES:  Might be useful for teaching use of random sampling and a scoring/fit metric for solving simple problems
*   @author jod
*   @date August 2018
*/


package com.jod.solvers;


public class GridSolve {



	//size of the grid (N*N). Note that this number should be divisible by 3.  To make it work with other numbers, remove the isGridSafe function call
	private static final int N = 9;
	//number of attempts to place symbols per run (will reach local max so don't set this too high)
	private static final int MAX_ITERATIONS = 15000;
	//x, y and symbol type for any fixed constraints 
	private static final int [] FIXED_X_POSITIONS = new int[] {0, 0, 0, 0, 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 7, 7, 8, 8, 8, 8};
	private static final int [] FIXED_Y_POSITIONS = new int[] {0, 3, 6, 8, 4, 5, 0, 2, 6, 1, 3, 5, 8, 1, 4, 7, 0, 3, 5, 7, 2, 6, 8, 3, 4, 0, 2, 5, 8};
	private static final int [] FIXED_TOKEN_IDS = new int[] {1, 8, 0, 4, 3, 4, 6, 8, 1, 0, 6, 5, 3, 8, 7, 4, 7, 3, 0, 6, 0, 7, 2, 4, 0, 5, 2, 3, 8};
    
    //set of labels for printing the final tokens
    private static final String [] TOKEN_NAMES = new String[] {"W", "p", "f", "r", "v", "*", "O", "4", "N", "-"};


	private int[][] grid;

	private static String [] tokenNames;
	private static int [] fixedD;
	private static int [] tokenIds;
	private static int [] fixedx;
	private static int [] fixedy;

	private int best = 2 * N;

	public static void main(String[] args) {

		GridSolve g = new GridSolve();
		g.grid = new int[N][N];

		g.tokenNames = g.TOKEN_NAMES;
		g.tokenIds = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8};
		int score = N * N;
		int range = N;
		int randomtoken = 0;
		int randomi = 0;
		int randomj = 0;
		fixedx = g.FIXED_X_POSITIONS;
		fixedy = g.FIXED_Y_POSITIONS;
		fixedD = g.FIXED_TOKEN_IDS;
		g.grid = g.initialize(g.grid);
		g.grid = g.initializeFixed(g.grid);
		g.assignToken(g, score, range, randomtoken, randomi, randomj);
		System.out.print("\n Run complete \n");
	}

	/*
	*  recursive function to assign tokens
	*
	*/


	private void assignToken(GridSolve gs2, int sc, int range, int randomtoken, int randomi, int randomj) {

		int iter = 0;
		int placed = 0;
		int attempted = 0;
//while not solved
		while (getError(gs2.grid) > 0) {
			placed = 0;
			gs2.grid = new int[N][N];
			gs2.grid = gs2.initialize(gs2.grid);
			gs2.grid = gs2.initializeFixed(gs2.grid);
			int[][]g = gs2.grid;
			//assign a random token to a random position*
			attempted = 0;
			for (int i = 0; i < MAX_ITERATIONS; i++) {
				attempted++;
				range = N;
				randomtoken = (int)(Math.random() * range);
				randomi = (int)(Math.random() * range);
				randomj = (int)(Math.random() * range);

				//debug("\n checking random assignments..." );
				//debug("token: " + randomtoken);
				//debug("i position: " + randomj);
				//debug("j position: " + randomj);

				//check if the assigment is legal
				//(optional) compute a score

				//if score is better than previous, keep the assignment
				if (isLegal(g, randomtoken, randomi, randomj) && !isFixed(randomi, randomj) && isGridSafe(g, randomi, randomj, randomtoken)) {
					g[randomi][randomj] = tokenIds[randomtoken];
					placed++;
					//print the current board and score

					//System.gc();
				}



			}

			iter++;
			printGrid(g, sc, attempted, placed, iter);
		}

	}



	private boolean isFixed(int p, int q) {
		for (int i = 0; i < fixedx.length; i++ ) {
			if (fixedx[i] == p && fixedy[i] == q)
				return true;
		}

		return false;
	}


	private boolean isLegal(int [][] g, int t, int i, int j) {
//debug("\n checking if legal..." );
//debug("token: " + t);
//debug("i position: " + i);
//debug("j position: " + j);

//check current row
		for (int p = 0; p < g.length; p++) {
			//debug("\n g[i][p] is " + g[i][p]);
			//debug("\n g[p][j] is " + g[i][p]);
			if (g[i][p] == t)
				return false;
			if (g[p][j] == t)  //also column check
				return false;
		}

		debug("Found a legal entry!");
		return true;

	}


	private void printGrid(int[][] g, int s, int attempted, int placed, int iter) {
//debug("g length is " + g.length);
//debug("g[0] length is " + g[0].length);
//debug("g[0] value is " + g[2][0]);
//debug("g[0] value is " + g[0][3]);


//System.out.println("Score is:  " + s);
		System.out.println("\n\nIterations:  " + iter + ".");
//System.out.println("Symbols attempted in this iteration:  " + attempted + ".");
//System.out.println("Symbols placed in this iteration:  " + placed + ".");
		System.out.println("Best solution so far has error of: " + best);
		System.out.println("Error is:  " + getError(g));
		for (int i = 0; i < g.length; i++ ) {
			System.out.println("\n");
			for (int j = 0; j < N; j++ ) {
				System.out.print(this.tokenNames[g[i][j]] + ", ");

			}
			//System.out.println();
		}

	}


	private int[][] initialize(int [][] gr) {

		for (int i = 0; i < gr.length; i++) {
			for (int j = 0; j < gr.length; j++) {
				gr[i][j] = N;    //nth token is reserved
			}
		}
		return gr;
	}



	private int[][] initializeFixed(int [][] gr) {

		for (int i = 0; i < fixedx.length; i++) {
			gr[fixedx[i]][fixedy[i]] = fixedD[i];
		}
		return gr;
	}


	/* total error should be 2N or less  plus error from the
	*  remaining unmodified symbols
	*/

	public int getError(int[][] b) {
		int err = 0;

//get horizontal error (N or less)
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b.length; j++) {
				if (b[i][j] == N) {
					err++;
					debug("b[" + i + "][" + j + "] is " + b[i][j]);
					break;
				}
				for (int k = j + 1; k < b.length; k++) {
					if (b[i][j] == b[i][k] || b[i][j] == N) {
						err++;
						break;
					}

				}
				break;
			}
		}
		//get vertical error (N or less)
		for (int q = 0; q < b.length; q++) {
			for (int j = 0; j < b.length; j++) {

				for (int k = j + 1; k < b.length; k++) {
					if (b[j][q] == b[k][q]) {
						err++;
						break;
					}

				}
				break;
			}
		}

		//now check for unmodified symbols
		for (int c = 0; c < b.length; c++) {
			for (int d = 0; d < b.length; d++) {
				if (b[c][d] == N) {
					err++;
					//debug("b["+c+ "][" + d +"] is " + b[c][d]);
				}
			}
		}

		if (err < best)
			best = err;

		return err;
	}


	private boolean isGridSafe(int [][] b, int i, int j, int t) {

//find out what ninth the attempt is in and set the check bounds
		int lowx = 0;
		int highx = 0;
		int lowy = 0;
		int highy = 0;


		if ((i < N) && (i >= 2 * (N / 3))) {
			lowx = 2 * (N / 3);
			highx = N;
		} else if (i < 2 * (N / 3) && i >= N / 3) {
			lowx = N / 3;
			highx = 2 * (N / 3);
		} else {
			lowx = 0;
			highx = N / 3;
		}


		if (j < N && j >= 2 * (N / 3)) {
			lowy = 2 * (N / 3);
			highy = N;
		} else if (j < 2 * (N / 3) && j >= N / 3) {
			lowy = N / 3;
			highy = 2 * (N / 3);
		} else {
			lowy = 0;
			highy = N / 3;
		}


		for (int x = lowx; x < highx; x++) {
			for (int y = lowy; y < highy; y++) {
				if (b[x][y] == t) {
					//debug("b["+x+"]["+y+"] is "+ b[x][y] + "and t is: " + t + ".  Returning false");
					return false;
				}
			}

		}
		debug("board safe, returning true");
		return true;

	}


	private void debug(String s) {
		//System.out.println("Debug:  " + s);
	}


}
