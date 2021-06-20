package Sudoku;


import java.util.*;
import java.io.*;


public class Sudoku2 {
    /* SIZE is the size parameter of the Sudoku puzzle, and N is the square of the size.
     */
    public int SIZE, N;

    /* The grid contains all the numbers in the Sudoku puzzle.  Numbers which have
     * not yet been revealed are stored as 0.
     */
    public int[][] grid;

    public Node[][] m;

    public ColumnNode head;

    // Field that stores the same Sudoku puzzle solved in all possible ways
    public HashSet<Sudoku2> solutions = new HashSet<>();



    /**
     * This method will return the column node with fewest nodes i.e. The smallest size
     * @param head The root node;
     * @return c The ColumnNode with the smallest size;
     */
    public ColumnNode choose_Column(ColumnNode head){
        ColumnNode c= head;
        for(ColumnNode i= (ColumnNode) head.R; i!= head; i= (ColumnNode) i.R){
            if (i.size< c.size) c= i;
        }
        return c;
    }



    public boolean search(int k, ArrayList<int[]> s, boolean allSolutions){
        if (head.R== head) return true;
        ColumnNode c= choose_Column(head);
        if(c.size== 0) return false;// Fail to cover the matrix in this branch;
        c.unlinkR(); //Update the head node of columns if the chosen one is the head

        for(Node r= c.D; r!= c; r= r.D) {
            s.add(r.data);
            Node j = r.R;
            while (j != r){
                j.C.cover();
                j = j.R;
            }
            if (search(k + 1, s, allSolutions)) {
                paintGrid(s, allSolutions);
                return !allSolutions;
            }
            s.remove(r.data);
            j = r.L;
            while (j != r){
                j.C.uncover();
                j = j.L;
            }
        }
        c.relinkR();
        c.uncover();
        return false;
    }


    public void paintGrid(ArrayList<int[]> s, boolean allSolutions){
        for(int[] data: s){
            grid[data[0]][data[1]]= data[2];
        }
        if(allSolutions) solutions.add(new Sudoku2(this));
    }








    public void solve(boolean allSolutions) {
        preprocessM();
        //printM();
        ArrayList<int[]> s= new ArrayList<>();
        search(0, s, allSolutions);
    }
    public void initiateM(){
        for(int idx= 0; idx< N* N; idx++){
            for(int j= N* idx; j<N* idx+ N; j++){
                m[j][idx]= new Node();
            }
        }// Initiate the Cell Constraint

        for(int idx= N* N; idx< 2* N* N; idx+= N){
            int num= idx- N* N;
            for(int i= N* num; i< N* (num+ N); i+= N){
                for(int j= 0; j< N; j++){
                    m[i+ j][idx+ j]= new Node();
                }
            }
        }// Initiate the Row Constraint

        int idx= 2* N* N;
        for(int i= 0; i< N* N* N; i+= N* N){
            for(int j= 0; j< N* N; j++){
                m[i+ j][idx+ j]= new Node();
            }
        }// Initiate the Column Constraint


        int curRow= 0;
        for(int i= 0; i< SIZE; i++){
            int col= 3* N* N+ i* SIZE* N;
            for(int j= 0; j< SIZE; j++ ) {
                for (int k = col; k < col + SIZE * N; k += N) {
                    for (int n = 0; n < SIZE; n++) {
                        for (int l = 0; l < N; l++) {
                            m[curRow][k + l] = new Node();
                            curRow++;
                        }
                    }
                }
            }

        }//Initiate the Box Constraint
    }
    public void linkM(){
        ColumnNode curCol= new ColumnNode();
        ColumnNode firstCol= new ColumnNode();
        for(int j= 0; j< m[1].length; j++) {
            ColumnNode col= new ColumnNode();
            col.name= j+ 1;
            if(j== 0){
                curCol= col;
                firstCol= col;
            }else{
                curCol.linkR(col);
                curCol= col;
            }
            Node first= new Node();
            int i;
            for (i = 0; i < m.length; i++) {
                if(m[i][j]!= null){
                    first= m[i][j];
                    break;
                }
            }
            first.C= col;
            col.linkD(first);
            col.size++;
            Node cur= first;
            for(int n= i+ 1; n< m.length; n++){
                /*Notice: Since the function is used when initializing the m matrix, so, we can assume that first happens somewhere in the middle.
                i.e. There exist some Nodes under first Node
                 */
                if(m[n][j]!= null){
                    cur.linkD(m[n][j]);
                    cur= m[n][j];
                    cur.C= col;
                    col.size++;
                }
            }
            cur.linkD(col);
        }
        head.linkR(firstCol);
        curCol.linkR(head);

        for (Node[] nodes : m) {
            Node first = null;
            int j;
            for (j = 0; j < m[0].length; j++) {
                if (nodes[j] != null) {
                    first = nodes[j];
                    break;
                }
            }
            Node cur = first;
            for (int n = j + 1; n < m[0].length; n++) {
                if (nodes[n] != null) {
                    cur.linkR(nodes[n]);
                    cur = nodes[n];
                }
            }
            if(cur!= null && first!= null)cur.linkR(first);
        }
    }
    /**
     * This are two instance methods to preprocess the m matrix at the beginning of the solve method.
     * Delete a row will perform the action to unlink the Node from its up and down contiguous Nodes.
     * PreprocessM will perform the deletion of certain region where a number is already in the cell of grid.
     */
    public void delARow(int r){
        Arrays.fill(m[r], null);
    }
    public void delARegion(int region, int row){ // Exemption is made at Row row where there exists a number in the grid of the input matrix
        for(int i= region; i< region+ N; i++){
            if(i!= row) delARow(i);
        }
    }
    public void preprocessM(){
        for(int i= 0; i< grid.length; i++){
            for(int j= 0; j< grid[0].length; j++){
                if(grid[i][j]!= 0) {
                    int num = N * (N * i + j); // Use the idx of row to calculate the actual column number. Under this set of exact cover of Sudoku,
                    int row= num+ grid[i][j]- 1;
                    delARegion(num, row);
                }
            }
        }
        linkM();
    }
    public void markM(){
        for(int i= 0; i< m.length; i++ ){
            int row= i/(N* N);
            int col= i/N% N;
            int num= i% N+ 1;
            for(int j= 0; j< m[0].length; j++){
                if(m[i][j] != null) m[i][j].data= new int[]{row, col, num};
            }
        }
    }
    public Sudoku2(int size ) {
        SIZE = size;
        N = size*size;

        grid = new int[N][N];
        for( int i = 0; i < N; i++ )
            for( int j = 0; j < N; j++ )
                grid[i][j] = 0;

        m= new Node[N* N* N][N* N* 4];
        initiateM();
        markM();
        head= new ColumnNode();
        head.size= 81;
    }
    public Sudoku2(Sudoku2 sudoku2){
        SIZE= sudoku2.SIZE;
        N= SIZE* SIZE;
        grid= new int[N][N];
        for(int i= 0; i< N; i++){
            System.arraycopy(sudoku2.grid[i], 0, grid[i], 0, N);
        }
    }
    public void printM(){
        System.out.println();
        for (Node[] nodes : m) {
            for (int j = 0; j < 4 * N * N; j++) {
                if (nodes[j] != null /*&& nodes[j].U.D == nodes[j] && nodes[j].D.U == nodes[j]*/) {
                    System.out.print("1 ");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }


}
