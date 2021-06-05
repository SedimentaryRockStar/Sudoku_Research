package Sudoku;

import java.util.*;
import java.io.*;


public class Sudoku3 {
    /* SIZE is the size parameter of the Sudoku puzzle, and N is the square of the size.
     */
    public int SIZE, N;

    /* The grid contains all the numbers in the Sudoku puzzle.  Numbers which have
     * not yet been revealed are stored as 0.
     */
    public int grid[][];


    // Field that stores the same Sudoku puzzle solved in all possible ways
    public HashSet<Sudoku3> solutions = new HashSet<Sudoku3>();


    public void solve(boolean allSolutions) {
        ArrayList<HashSet<Integer>> rows = buildHashAL(N);
        ArrayList<HashSet<Integer>> columns = buildHashAL(N);
        ArrayList<HashSet<Integer>> squares = buildHashAL(N);
        ArrayList<int[]> emptyCells= new ArrayList<>();//Use HashSet to speed up.
        int[] tmp= new int[N* N];// Use a deep copy of the grid to generate the grid to deal with all solution situation
        for(int i= 0; i< N; i++){
            for(int j= 0; j< N; j++){
                int num= grid[i][j];
                tmp[i*N +j]= num;
                if(num!= 0){
                    rows.get(i).add(num);
                    columns.get(j).add(num);
                    int k= SIZE*(i/SIZE)+ j/SIZE;
                    squares.get(SIZE*(i/SIZE)+ j/SIZE).add(num);
                }
            }
        }// Collect all the information needed to perform future tasks

        for(int i= 0; i< N; i+= SIZE){
            for(int j= 0; j< N; j+= SIZE) {
                for(int r= i; r< i+ SIZE; r++){
                    for(int c= j; c< j+ SIZE; c++ ){
                        if(grid[r][c]== 0){
                            int[] a= {r, c, 0};
                            emptyCells.add(a);
                        }
                    }
                }
            }
        }

        fill(rows, columns, squares, emptyCells, tmp, allSolutions);

    }

    private static ArrayList<HashSet<Integer>> buildHashAL(int num){
        ArrayList<HashSet<Integer>> al = new ArrayList<HashSet<Integer>>();
        for(int i= 0; i< num; i++){
            HashSet<Integer> set= new HashSet<Integer>();
            al.add(set);
        }
        return al;
    }// A method to build up an arraylist made up of the HashSet



    // A Set of methods to check for basic rules of Sudoku.
    // Instead of checking for the whole board Do the operation every time to eliminate extra work

    private boolean rowSafe(ArrayList<HashSet<Integer>> rows, int row, int num){
        return !rows.get(row).contains(num);
    }
    private boolean columnSafe(ArrayList<HashSet<Integer>> columns, int column, int num){
        return !columns.get(column).contains(num);
    }
    private boolean squareSafe(ArrayList<HashSet<Integer>> squares, int square, int num){
        return !squares.get(square).contains(num);
    }
    private boolean basicValid(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                               ArrayList<HashSet<Integer>> squares, int row, int column, int num){
        if(!rowSafe(rows, row, num)) return false;
        if(!columnSafe(columns, column, num)) return false;
        int square= SIZE* (row/SIZE)+ column/SIZE;
        return squareSafe(squares, square, num);
    }    //Integrate all the basic checks



    private boolean fill(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                         ArrayList<HashSet<Integer>> squares, ArrayList<int[]> emptyCells, int[] grid,
                         boolean allSolutions){
        if(emptyCells.isEmpty()){
            return true;
        }

        for(int i= 1; i<= N; i++){
            int[] cell= emptyCells.remove(emptyCells.size()- 1);
            int row= cell[0];
            int column= cell[1];
            int weight= cell[2];
            if(basicValid( rows,  columns, squares, row, column, i)){
                grid[row*N+ column]= i;
                rows.get(row).add(i);
                columns.get(column).add(i);
                squares.get(SIZE* (row/SIZE)+ column/SIZE).add(i);// Set up the corresponding scenario for future check
                if(fill(rows, columns, squares, emptyCells, grid, allSolutions)){
                    Sudoku3 s = new Sudoku3(SIZE, grid);
                    this.grid= s.grid;
                    if(!allSolutions) return true;
                    solutions.add(s);
                    clearCell(rows, columns, squares, emptyCells, grid, i, row, column, weight);
                    return false;
                }else{
                    clearCell(rows, columns, squares, emptyCells, grid, i, row, column, weight);
                }
            }else{
                emptyCells.add(cell);
            }
        }
        return false;// Unable to fill the grid,
    }

    private void clearCell(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                           ArrayList<HashSet<Integer>> squares, ArrayList<int[]> emptyCells,
                           int[] grid, int i, int row, int column, int weight) {
        grid[row*N+ column]= 0;
        rows.get(row).remove(i);
        columns.get(column).remove(i);
        squares.get(SIZE* (row/SIZE)+ column/SIZE).remove(i);// If the grid cannot be filled, return its original status.
        int[] tmp= {row, column, weight};
        emptyCells.add(tmp);
    }

    private Sudoku3(int size, int[] board){
        SIZE = size;
        N = size*size;

        grid = new int[N][N];
        for( int i = 0; i < N; i++ )
            System.arraycopy(board, i * N, grid[i], 0, N);
    } //A new constructor for multiple solutions



    public Sudoku3(int size ) {
        SIZE = size;
        N = size*size;

        grid = new int[N][N];
        for( int i = 0; i < N; i++ )
            for( int j = 0; j < N; j++ )
                grid[i][j] = 0;
    }
    static int readInteger( InputStream in ) throws Exception {
        int result = 0;
        boolean success = false;

        while( !success ) {
            String word = readWord( in );

            try {
                result = Integer.parseInt( word );
                success = true;
            } catch( Exception e ) {
                // Convert 'x' words into 0's
                if( word.compareTo("x") == 0 ) {
                    result = 0;
                    success = true;
                }
                // Ignore all other words that are not integers
            }
        }

        return result;
    }
    static String readWord( InputStream in ) throws Exception {
        StringBuffer result = new StringBuffer();
        int currentChar = in.read();
        String whiteSpace = " \t\r\n";
        // Ignore any leading white space
        while( whiteSpace.indexOf(currentChar) > -1 ) {
            currentChar = in.read();
        }

        // Read all characters until you reach white space
        while( whiteSpace.indexOf(currentChar) == -1 ) {
            result.append( (char) currentChar );
            currentChar = in.read();
        }
        return result.toString();
    }
    public void read( InputStream in ) throws Exception {
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                grid[i][j] = readInteger( in );
            }
        }
    }
    void printFixedWidth( String text, int width ) {
        for( int i = 0; i < width - text.length(); i++ )
            System.out.print( " " );
        System.out.print( text );
    }
    public void print() {
        // Compute the number of digits necessary to print out each number in the Sudoku puzzle
        int digits = (int) Math.floor(Math.log(N) / Math.log(10)) + 1;

        // Create a dashed line to separate the boxes
        int lineLength = (digits + 1) * N + 2 * SIZE - 3;
        StringBuffer line = new StringBuffer();
        for( int lineInit = 0; lineInit < lineLength; lineInit++ )
            line.append('-');

        // Go through the grid, printing out its values separated by spaces
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                printFixedWidth( String.valueOf( grid[i][j] ), digits );
                // Print the vertical lines between boxes
                if( (j < N-1) && ((j+1) % SIZE == 0) )
                    System.out.print( " |" );
                System.out.print( " " );
            }
            System.out.println();

            // Print the horizontal line between boxes
            if( (i < N-1) && ((i+1) % SIZE == 0) )
                System.out.println( line.toString() );
        }
    }

    public static void main( String args[] ) throws Exception {
        InputStream in = new FileInputStream("medium3x3.txt");

        // The first number in all Sudoku files must represent the size of the puzzle.  See
        // the example files for the file format.
        int puzzleSize = readInteger( in );
        if( puzzleSize > 100 || puzzleSize < 1 ) {
            System.out.println("Error: The Sudoku puzzle size must be between 1 and 100.");
            System.exit(-1);
        }

        Sudoku3 s = new Sudoku3( puzzleSize );

        s.read( in );


        System.out.println("Before the solve:");
        s.print();
        System.out.println();

        s.solve(false);

        // Print out the (hopefully completed!) puzzle
        System.out.println("After the solve:");
        s.print();
        System.out.println(s.solutions.size());

    }
}