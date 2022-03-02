package Tree;

import java.io.Serializable;

/***********************************************************************************************************
 Done for Java 8
 Note that accessors are treated like properties : Use .left() instead of getLeft()
 This is done for convenience only
 Feel free to refactor methods if you want to stick to standardized getters and setters
 Max ID is max integer
 Constructors & methods tend to be overloaded. Delve into the code to see what's available.
 IDs are handled safely & automatically.
 You can still edit them or declare them. Note that new AvlNode(5) will set node data to 5, not node ID.
 To create a node with no data and a given ID, use new AvlNode(null, 5)
 Data should implement Serializable if you plan to use the save and load functions.
 ***********************************************************************************************************/

public class AvlNode <T> implements Serializable {

    private AvlNode <T> left;
    private AvlNode <T> right;
    private int id;
    private T data;
    //Data must be serializable if you intend to save the tree

    /***************************
     Constructors
     ***************************/

    public AvlNode (T data, int id, AvlNode <T> left, AvlNode <T> right){
        //Full package constructor because reasons
        this.data = data;
        this.id = id;
        this.left = left;
        this.right = right;
    }

    public AvlNode (T data, AvlNode <T> left, AvlNode <T> right){
        //Full package constructor without the id because reasons
        this.data = data;
        this.id = data.hashCode();
        this.left = left;
        this.right = right;
    }

    public AvlNode (T data, int id){
        //Default constructor if you care about controlling your IDs
        this.data = data;
        this.id = id;
        this.left = null;
        this.right = null;
    }

    public AvlNode (T data){
        //Default constructor if you don't care about controlling your IDs
        this.data = data;
        this.id = data.hashCode();
        this.left = null;
        this.right = null;
    }

    public AvlNode (){
        //Base empty constructor
        this.data = null;
        this.id = 0;
        this.left = null;
        this.right = null;
    }

    /***************************
     Getters & Setters
     ***************************/

    public AvlNode <T> left() {
        return left;
    }

    public void left(AvlNode <T> left) {
        this.left = left;
    }

    public AvlNode <T> right() {
        return right;
    }

    public void right(AvlNode <T> right) {
        this.right = right;
    }

    public int id() {
        return id;
    }

    public void id(int id) {
        this.id = id;
    }

    public T data() {
        return data;
    }

    public void data(T data) {
        this.data = data;
    }

    /***************************
     Public methods
     ***************************/

    @Override
    public boolean equals(Object o){
        //Note that if data is of Integer type, a tree that contains 5 10 and 15 will be equal to a tree that contains 3 7 20, as we're just comparing data values.

        if(o == this){
            return true;
        }

        if(!(o instanceof AvlNode)){
            return false;
        }
        AvlNode <T> node = (AvlNode<T>)o;
        return this.hashCode() == node.hashCode();
    }

    @Override
    public int hashCode(){
        int hash = 17 + id;

        if(data != null){
            hash += data.hashCode();
        }

        if(left != null){
            hash += left.hashCode();
        }

        if(right != null){
            hash += right.hashCode();
        }

        return hash;
    }

    @Override
    public String toString(){
        //Works only if your data Object has a proper toString method
        //Required to use the tree printData() method
        return data.toString();
    }
}