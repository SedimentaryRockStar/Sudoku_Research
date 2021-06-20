package Sudoku;

import java.util.*;
import java.io.*;
// This is the advanced version of backtracking. Not super efficient in 4x4, though.
public class Sudoku1 {
    //SIZE is the size parameter of the Sudoku puzzle, and N is the square of the size.
    public int SIZE, N;
    //The grid contains all the numbers in the Sudoku puzzle.  Numbers which have not yet been revealed are stored as 0.
    public int grid[][];
    // Field that stores the same Sudoku puzzle solved in all possible ways
    public HashSet<Sudoku1> solutions = new HashSet<Sudoku1>();


    public void solve(boolean allSolutions) {
        ArrayList<HashSet<Integer>> rows = buildHashAL(N);
        ArrayList<HashSet<Integer>> columns = buildHashAL(N);
        ArrayList<HashSet<Integer>> squares = buildHashAL(N);
        //int[] tmp= new int[N* N];// Use a deep copy of the grid to generate the grid to deal with all solution situation
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int num = grid[i][j];
                //tmp[i*N +j]= num;
                if (num != 0) {
                    rows.get(i).add(num);
                    columns.get(j).add(num);
                    squares.get(SIZE * (i / SIZE) + j / SIZE).add(num);
                }
            }
        }

        ArrayList<ArrayList<int[]>> grids= buildAArrayList(N);
        int[][] tmp= new int[N][N];
        for(int i= 0; i< N; i+= SIZE){
            for(int j= 0; j< N; j+= SIZE) {
                int[][] tmp1= new int[SIZE][SIZE];
                ArrayList<int[]> emptySpots= new ArrayList<>();
                int a= 0;
                for(int r= i; r< i+ SIZE; r++){
                    int b= 0;
                    for(int c= j; c< j+ SIZE; c++ ){
                        int num= grid[r][c];
                        tmp[r][c]= num;
                        tmp1[a][b]= num;
                        b++;
                        if(num== 0){
                            int[] q= {r- i, c- j};
                            emptySpots.add(q);
                        }
                    }
                    a++;
                }
                fillGrid(rows, columns, squares, tmp1, grids, i, j, emptySpots);
            }
        }
        boolean[] flags= new boolean[grids.size()];
        for(int i= 0; i< grids.size(); i++){
            flags[i]= grids.get(i).size()== 0;
        }
        fillBoard(rows, columns, squares, grids, tmp, false, flags);
        grid= tmp;

    }







    private boolean fillBoard(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                              ArrayList<HashSet<Integer>> squares, ArrayList<ArrayList<int[]>> grids, int[][] board, boolean allFlagged, boolean[] flags){
        allFlagged= true;
        int idx= -1;
        int mSize= -1;
        for(int i= 0; i< grids.size(); i++){
            if(allFlagged && !flags[i]) {
                idx= i;
                mSize= grids.get(i).size();
                allFlagged= false;
            }else if (!flags[i] && grids.get(i).size() < mSize) {
                idx = i;
                mSize = grids.get(i).size();
            }
        }

        if(allFlagged) return true;
        flags[idx] = true;
        int row= (idx/SIZE)* SIZE;
        int column= (idx% SIZE)* SIZE;
        ArrayList<int[]> Squares= grids.get(idx);
        System.out.println(idx);
        for(int i= 0; i< Squares.size(); i++){
            int[] square= Squares.get(i);
            if(putSquare(rows, columns, squares, board, square, row, column)){
                if(fillBoard(rows, columns, squares, grids, board, allFlagged, flags)) {
                    return true;
                }
            }
                removeSquare(rows, columns, squares, board, square, row, column);
        }
        flags[idx]= false;
        return false;
    }

    private boolean putSquare(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                              ArrayList<HashSet<Integer>> squares, int[][] board, int[] square, int row, int column){
        for(int i= 0; i< SIZE; i++) {
            int curRow = row + i;
            for (int j = 0; j < SIZE; j++) {
                int curCol = column + j;
                if (basicValid(rows, columns, squares, curRow, curCol, square[i* SIZE+ j])) {
                    int num= square[i* SIZE+ j];
                    board[curRow][curCol] = num;
                    rows.get(curRow).add(num);
                    columns.get(curCol).add(num);
                    squares.get(SIZE * (curRow / SIZE) + curCol / SIZE).add(num);

                }else if (grid[curRow][curCol]== 0){
                    return false;
                }
            }
        }
        return true;
    }

    private void removeSquare(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                              ArrayList<HashSet<Integer>> squares, int[][] board, int[] square, int row, int column){
        for(int i= 0; i< SIZE; i++) {
            int curRow = row + i;
            for (int j = 0; j < SIZE; j++) {
                int curCol = column + j;
                int num= board[curRow][curCol];
                board[curRow][curCol] = grid[curRow][curCol];
                if(grid[curRow][curCol]== 0) {
                    rows.get(curRow).remove(num);
                    columns.get(curCol).remove(num);
                    squares.get(SIZE * (curRow / SIZE) + curCol / SIZE).remove(num);
                }
            }
        }

    }

    private void clearCell(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                           ArrayList<HashSet<Integer>> squares, ArrayList<int[]> emptyCells,
                           int[][] grid, int i, int row, int column,int startRow, int startColumn) {
        grid[row- startRow][column- startColumn]= 0;
        rows.get(row).remove(i);
        columns.get(column).remove(i);
        squares.get(SIZE* (row/SIZE)+ column/SIZE).remove(i);// If the grid cannot be filled, return its original status.
        int[] tmp= {row- startRow, column- startColumn};
        emptyCells.add(tmp);
    }
    private boolean fillGrid(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                             ArrayList<HashSet<Integer>> squares,  int[][] Square, ArrayList<ArrayList<int[]>> grids,
                             int startRow, int startColumn, ArrayList<int[]> emptySpots){
        if(emptySpots.isEmpty()){
            return true;
        }

        for(int i= 1; i<= N; i++) {
            int[] cell = emptySpots.remove(emptySpots.size() - 1);
            int row = cell[0];
            int column = cell[1];
            if(basicValid( rows,  columns, squares, row+ startRow, column+ startColumn, i)){
                Square[row][column]= i;
                rows.get(row+ startRow).add(i);
                columns.get(column+ startColumn).add(i);
                squares.get(SIZE* (startRow/SIZE)+ startColumn/SIZE).add(i);
                if(fillGrid(rows, columns, squares, Square, grids, startRow, startColumn, emptySpots)){
                    int[] tmp= new int[SIZE* SIZE];
                    int idx= 0;
                    for(int m= 0; m< SIZE; m++){
                        for(int n= 0; n< SIZE; n++) {
                            tmp[idx]= Square[m][n];
                            idx++;
                        }
                    }
                    grids.get(SIZE* (startRow/SIZE)+ startColumn/SIZE).add(tmp);
                    clearCell(rows, columns, squares, emptySpots, Square, i, row+ startRow, column+ startColumn, startRow, startColumn);
                    return false;
                }else {
                    clearCell(rows, columns, squares, emptySpots, Square, i, row+ startRow, column+ startColumn, startRow, startColumn);
                }
            }else{
                emptySpots.add(cell);
            }
        }
        return false;
    } //Fill the small grids and then integrate
    private static ArrayList<ArrayList<int[]>> buildAArrayList(int num){
        ArrayList<ArrayList<int[]>> aal= new ArrayList<>();
        for(int i= 0; i< num; i++){
            ArrayList<int[]> al= new ArrayList<>();
            aal.add(al);
        }
        return aal;
    }
    private static ArrayList<HashSet<Integer>> buildHashAL(int num){
        ArrayList<HashSet<Integer>> al = new ArrayList<HashSet<Integer>>();
        for(int i= 0; i< num; i++){
            HashSet<Integer> set= new HashSet<Integer>();
            al.add(set);
        }
        return al;
    }// A method to build up an arraylist made up of the HashSet
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
    Sudoku1(int size, int[] board){
        SIZE = size;
        N = size*size;

        grid = new int[N][N];
        for( int i = 0; i < N; i++ )
            System.arraycopy(board, i * N, grid[i], 0, N);
    } //A new constructor for multiple solutions


}
