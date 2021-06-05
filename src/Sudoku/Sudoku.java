package Sudoku;

import java.util.HashSet;

public class Sudoku {

    /* SIZE is the size parameter of the Sudoku puzzle, and N is the square of the size.
     */
    public int SIZE, N;

    /* The grid contains all the numbers in the Sudoku puzzle.  Numbers which have
     * not yet been revealed are stored as 0.
     */
    public int grid[][];

    // Field that stores the same Sudoku puzzle solved in all possible ways
    public HashSet<Sudoku> solutions = new HashSet<Sudoku>();

    public void solve(boolean allSolutions){}
}
