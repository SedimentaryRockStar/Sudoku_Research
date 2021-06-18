package Sudoku;

public class Node{
    Node L, R, U, D;
    ColumnNode C;
    int[] data;

    public Node(){
        L= R= U= D= this;
    }


    public void linkD(Node n){
        D= n;
        D.U= this;
    }

    public void linkR(Node n){
        R= n;
        R.L= this;
    }
    //The upper two methods are used to initialize the Column Node

    public void unlinkD(){
        U. D= D;
        D. U= U;
    }

    public void unlinkR(){
        R. L= L;
        L. R= R;
    }
    /* L[R[x]] ← L[x], R[L[x]] ← R[x]  */

    public void relinkD(){
        U. D= this;
        D. U= this;
    }

    public void relinkR(){
        R. L= this;
        L. R= this;
    }
    /* L[R[x]] ← x, R[L[x]] ← x */

    // The upper four methods are to perform the Dance of the Nodes
}



class ColumnNode extends Node{
    public int size;
    public int name;

    public ColumnNode(){
        super();
        this.size= 0;
    }


    // The following are the implementation of the cover & uncover method from the paper
    public void cover() {
        unlinkR();//Unlink the column from other columns
        for (Node i = this.D; i != this; i = i.D) {
            for (Node j = i.R; j != i; j = j.R) {
                j.unlinkD();
                j.C.size--;
            }
        }
    }

    public void uncover(){
        relinkR();
        for(Node i= this.U; i!= this; i= i.U){
            for(Node j= i.L; j!= i; j= j.L) {
                j.C.size++;
                j.relinkD();
            }
        }
    }
}
