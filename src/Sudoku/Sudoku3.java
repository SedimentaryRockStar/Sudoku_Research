package Sudoku;

import java.util.*;
import java.io.*;
// This is the advanced version of backtracking. Not super efficient in 4x4, though.
public class Sudoku3 {
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

        ArrayList<ArrayList<int[]>> Rows = buildAArrayList(N);
        int[][] tmp = new int[N][N];
        for (int i = 0; i < N; i++) {
            int[] tmp1 = new int[N];
            ArrayList<int[]> emptySpots = new ArrayList<>();
            for (int j = 0; j < N; j++) {
                int num = grid[i][j];
                tmp[i][j] = num;
                tmp1[j] = num;
                if (num == 0) {
                    emptySpots.add(new int[]{i, j});
                }
            }
            fillRow(rows, columns, squares, tmp1, Rows, i, emptySpots);
        }
        boolean[] flags = new boolean[Rows.size()];
        for (int i = 0; i < Rows.size(); i++) {
            flags[i] = Rows.get(i).size() == 0;
        }
        fillBoard(rows, columns, squares, Rows, tmp, false, flags);
        grid = tmp;

    }


    private boolean fillBoard(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                              ArrayList<HashSet<Integer>> squares, ArrayList<ArrayList<int[]>> aRows, int[][] board, boolean allFlagged, boolean[] flags) {
        allFlagged = true;
        int idx = -1;
        int mSize = -1;
        for (int i = 0; i < aRows.size(); i++) {
            if (allFlagged && !flags[i]) {
                idx = i;
                mSize = aRows.get(i).size();
                allFlagged = false;
            } else if (!flags[i] && aRows.get(i).size() < mSize) {
                idx = i;
                mSize = aRows.get(i).size();
            }
        }

        if (allFlagged) return true;
        flags[idx] = true;
        ArrayList<int[]> Rows = aRows.get(idx);
        System.out.println(idx);
        for (int i = 0; i < Rows.size(); i++) {
            int[] aRow = Rows.get(i);
            if (putRow(rows, columns, squares, board, aRow, idx)) {
                if (fillBoard(rows, columns, squares, aRows, board, allFlagged, flags)) {
                    return true;
                }
            }
            removeRow(rows, columns, squares, board, aRow, idx);
        }
        flags[idx] = false;
        return false;
    }

    private boolean putRow(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                           ArrayList<HashSet<Integer>> squares, int[][] board, int[] Row, int row) {
        for (int i = 0; i < Row.length; i++) {
            if (basicValid(rows, columns, squares, row, i, Row[i])) {
                int num = Row[i];
                board[row][i] = num;
                rows.get(row).add(num);
                columns.get(i).add(num);
                squares.get(SIZE * (row / SIZE) + i / SIZE).add(num);
            } else if (grid[row][i] == 0) {
                return false;
            }
        }
        return true;
    }

    private void removeRow(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                           ArrayList<HashSet<Integer>> squares, int[][] board, int[] Row, int row) {
        for (int i = 0; i < Row.length; i++) {
            int num = board[row][i];
            board[row][i] = grid[row][i];
            if (grid[row][i] == 0) {
                rows.get(row).remove(num);
                columns.get(i).remove(num);
                squares.get(SIZE * (row / SIZE) + i / SIZE).remove(num);
            }
        }
    }

    private void clearCell(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                           ArrayList<HashSet<Integer>> squares, ArrayList<int[]> emptyCells,
                           int[] Row, int i, int row, int column) {
        Row[column] = 0;
        rows.get(row).remove(i);
        columns.get(column).remove(i);
        squares.get(SIZE * (row / SIZE) + column / SIZE).remove(i);// If the grid cannot be filled, return its original status.
        emptyCells.add(new int[]{row, column});
    }

    private boolean fillRow(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                            ArrayList<HashSet<Integer>> squares, int[] Row, ArrayList<ArrayList<int[]>> Rows,
                            int row, ArrayList<int[]> emptySpots) {
        if (emptySpots.isEmpty()) {
            return true;
        }

        for (int i = 1; i <= N; i++) {
            int[] cell = emptySpots.remove(emptySpots.size() - 1);
            int column = cell[1];
            if (basicValid(rows, columns, squares, row, column, i)) {
                Row[column] = i;
                rows.get(row).add(i);
                columns.get(column).add(i);
                squares.get(SIZE * (row / SIZE) + column / SIZE).add(i);
                if (fillRow(rows, columns, squares, Row, Rows, row, emptySpots)) {
                    Rows.get(row).add(Arrays.copyOf(Row, N));
                    clearCell(rows, columns, squares, emptySpots, Row, i, row, column);
                    return false;
                } else {
                    clearCell(rows, columns, squares, emptySpots, Row, i, row, column);
                }
            } else {
                emptySpots.add(cell);
            }
        }
        return false;
    } //Fill the small Rows and then integrate

    private static ArrayList<ArrayList<int[]>> buildAArrayList(int num) {
        ArrayList<ArrayList<int[]>> aal = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            ArrayList<int[]> al = new ArrayList<>();
            aal.add(al);
        }
        return aal;
    }

    private static ArrayList<HashSet<Integer>> buildHashAL(int num) {
        ArrayList<HashSet<Integer>> al = new ArrayList<HashSet<Integer>>();
        for (int i = 0; i < num; i++) {
            HashSet<Integer> set = new HashSet<Integer>();
            al.add(set);
        }
        return al;
    }// A method to build up an arraylist made up of the HashSet

    private boolean rowSafe(ArrayList<HashSet<Integer>> rows, int row, int num) {
        return !rows.get(row).contains(num);
    }

    private boolean columnSafe(ArrayList<HashSet<Integer>> columns, int column, int num) {
        return !columns.get(column).contains(num);
    }

    private boolean squareSafe(ArrayList<HashSet<Integer>> squares, int square, int num) {
        return !squares.get(square).contains(num);
    }

    private boolean basicValid(ArrayList<HashSet<Integer>> rows, ArrayList<HashSet<Integer>> columns,
                               ArrayList<HashSet<Integer>> squares, int row, int column, int num) {
        if (!rowSafe(rows, row, num)) return false;
        if (!columnSafe(columns, column, num)) return false;
        int square = SIZE * (row / SIZE) + column / SIZE;
        return squareSafe(squares, square, num);
    }    //Integrate all the basic checks

    Sudoku3(int size, int[] board) {
        SIZE = size;
        N = size * size;

        grid = new int[N][N];
        for (int i = 0; i < N; i++)
            System.arraycopy(board, i * N, grid[i], 0, N);
    } //A new constructor for multiple solutions


    public Sudoku3(int size) {
        SIZE = size;
        N = size * size;

        grid = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                grid[i][j] = 0;
    }

}