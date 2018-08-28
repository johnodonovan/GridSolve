  @file GridSolve.java
  @author John O'Donovan
  @date August 2018

Summary: 
Java class for solving an N*N arrangment of N symbols on a square grid with some constraints.  

Problem description: 
	Each row and column must have a unique symbol from a set of N symbols. 
	Includes a slection of fixed symbols and locations
	Includes a constraint for unique symbols in each nonet (local 9th of board). This means that N needs to be divisible by 3.  To use the code with other sizes, remove the isGridSafe function call.

Approach: 
	Uses a simple random sample consensus (RANSAC) method. [Fischler & Bolles]

 Additional Notes
 	Used to successfully solve the grid puzzle in Haiku Games Ipad version (Midnight Carnival)
   	NOTES:  Can be easily adjusted for solving SUDOKU and other grid-based games
   	NOTES:  Might be useful for teaching use of random sampling and a scoring/fit metric for solving simple problems
   	


