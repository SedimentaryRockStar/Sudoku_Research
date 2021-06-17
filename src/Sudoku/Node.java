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
        if(this== C. D) C.D= this.D;
        if(this== C. U) C.U= this.U;// Deal with the special case where the node is the head or tail of the column
    }

    public void unlinkR(){
        R. L= L;
        L. R= R;
    }
    /* L[R[x]] ← L[x], R[L[x]] ← R[x]  */

    public void relinkD(){
        U. D= this;
        D. U= this;
        if(this== C.D.U) C.D= this;
        if(this== C.U.D) C.U= this;
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
    public void cover(){
        super.unlinkR();//Unlink the column from other columns
        Node i= this.D;
       do{
            Node j= i;
            do{
                j.unlinkD();
                j.C.size--;
                j= j.R;
            }while(j!= i);
            i= i.D;
        }while(i!= this.D);
    }

    public void uncover(){
        Node i= this.U;
        do{
            i= i.U;
            Node j= i;
            do{
                j.C.size++;
                j.relinkD();
                j= j.L;
            }while(j!= i);
        }while (i!= this.U);
        relinkR();
    }
}
