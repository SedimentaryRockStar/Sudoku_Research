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
        R.L= n;
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
        this.size= -1;
    }


    // The following are the implementation of the cover & uncover method from the paper
    public void cover(ColumnNode c){
        super.unlinkR();
        Node i= c.D;
        while(i!= c){
            Node j= i.R;
            while(j!= i){
                j.unlinkD();
                j.C.size--;
                j= j.R;
            }
            i= i.D;
        }
    }

    public void uncover(ColumnNode c){
        Node i= c.U;
        while(i!= c){
            Node j= i.L;
            while(j!= i){
                j.C.size++;
                j.relinkD();
                j= j.L;
            }
            i= i.U;
        }
        c.relinkR();
    }
}
