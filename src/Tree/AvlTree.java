package Tree;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class AvlTree <T> implements Serializable {

    /***********************************************************************************************************
     Done for Java 8
     Note that accessors are treated like properties : Use .root() instead of getRoot()
     This is done for convenience only
     Feel free to refactor methods if you want to stick to standardized getters and setters
     Constructors & most methods tend to be overloaded. Delve into the code to see what's available.
     IDs are handled safely & automatically.
     You can still edit them or declare them. See AvlNode class for more details.
     ***********************************************************************************************************/

    private AvlNode <T> root;

    private final Random rand = new Random();

    private int size = 0;

    private boolean areDuplicatesAllowed = true;
    //If this is true, data with hashcode already in the tree can't be inserted.

    private String savePath = "C:/Users/" + System.getProperty("user.name") + "/Documents/Ares/avl/saved_root.ser";
    //Base save path can be changed when calling load or save method

    /***************************
     Constructors
     ***************************/

    public AvlTree(){
        this.root = null;
    }

    public AvlTree(boolean areDuplicatesAllowed){
        this.areDuplicatesAllowed = areDuplicatesAllowed;
        this.root = null;
    }

    public AvlTree(T data){
        this.root = new AvlNode<>(data);
        size++;
    }

    public AvlTree(T data, boolean areDuplicatesAllowed){
        this.areDuplicatesAllowed = areDuplicatesAllowed;
        this.root = new AvlNode<>(data);
        size++;
    }

    public AvlTree(AvlNode <T> root){
        this.root = root;
        size++;
    }

    public AvlTree(AvlNode <T> root, boolean areDuplicatesAllowed){
        this.areDuplicatesAllowed = areDuplicatesAllowed;
        this.root = root;
        size++;
    }

    /***************************
     Getters & Setters
     ***************************/

    public AvlNode <T> root() {
        return root;
    }

    public void root(AvlNode <T> root) {
        //No need to set the root manually to initialize the tree. Use add() instead.
        if(size == 0){
            size++;
        }
        this.root = root;
    }

    public boolean areDuplicatesAllowed(){
        return areDuplicatesAllowed;
    }

    public int size(){
        return size;
    }

    public String savePath(){
        return savePath;
    }

    public void savePath(String savePath){
        this.savePath = savePath;
    }

    /***************************
     Private methods
     ***************************/

    private AvlNode <T> leftRotation(AvlNode <T> node){
        //Keeps the tree balanced. Don't modify it unless you know what you're doing.
        AvlNode <T> temp = node.right();
        node.right(temp.left());
        temp.left(node);
        return temp;
    }

    private AvlNode <T> rightRotation(AvlNode <T> node) {
        //Keeps the tree balanced. Don't modify it unless you know what you're doing.
        AvlNode<T> temp = node.left();
        node.left(temp.right());
        temp.right(node);
        return temp;
    }

    private int getBalance(AvlNode <T> node){
        //Keeps the tree balanced. Don't modify it unless you know what you're doing.
        return height(node.left()) - height(node.right());
    }

    private AvlNode <T> uniqueId(AvlNode <T> node){
        //Everytime a node is inserted into the tree, this method is called.
        //It will change current node ID if another node has the same ID.
        //This method becomes slow if your node count approaches 2^31-1.
        //If you reach that point, consider reworking this tree ID system to String IDs.
        if(contains(node.id())){
            return uniqueId(randomId(node));
        }
        return node;
    }

    private AvlNode <T> randomId(AvlNode <T> node){
        //Changes a node ID to another random ID.
        node.id((rand.nextInt(Integer.MAX_VALUE)));
        return node;
    }

    private int height(AvlNode <T> node){
        //Returns tree height
        if(node == null)
            return 0;
        else
            return 1 + Math.max(height(node.left()), height(node.right()));
    }

    private AvlNode <T> balancedInsertion(AvlNode <T> root, AvlNode <T> insertedNode){

        //This method takes care of adding nodes while keeping the tree balanced.

        //Adding part
        if (root == null && (areDuplicatesAllowed || !contains(insertedNode.data().hashCode()))){
            size++;
            root = insertedNode;
        }else if(root != null){

            if (insertedNode.id() < root.id()){
                root.left(balancedInsertion(root.left(), insertedNode));
            }else{
                root.right(balancedInsertion(root.right(), insertedNode));
            }
        }

        if(root == null){
            return null;
        }

        //Balancing part
        root = balanceAfterInsertion(root, insertedNode);
        return root;
    }

    private AvlNode<T> balanceAfterInsertion(AvlNode<T> root, AvlNode<T> insertedNode) {
        if (getBalance(root) > 1) {
            if (insertedNode.id() < root.left().id()) {
                // Unbalanced tree, case left-left
                root = rightRotation(root);
            } else {
                // Unbalanced tree, case left-right
                root.left(leftRotation(root.left()));
                root = rightRotation(root);
            }
        } else if (getBalance(root) < -1) {
            if (insertedNode.id() > root.right().id()) {
                // Unbalanced tree, case right-right
                root = leftRotation(root);
            } else {
                // Unbalanced tree, case right-left
                root.right(rightRotation(root.right()));
                root = leftRotation(root);
            }
        }
        return root;
    }

    private AvlNode <T> getById(AvlNode <T> root, int id){
        //Returns the node with the given ID or null
        if (root == null) {
            return null;
        } else if (root.id() == id) {
            return root;
        } else if (id < root.id()) {
            return getById(root.left(), id);
        } else {
            return getById(root.right(), id);
        }
    }

    private AvlNode <T> getMinValue(AvlNode <T> node) {
        //Required for the remove() method below.
        while (node.left() != null) {
            node = node.left();
        }
        return node;
    }

    private AvlNode <T> remove(AvlNode <T> root, AvlNode <T> target){

        //Takes care of removing the target node while keeping the tree balanced.

        if (root == null) {
            return null;
        }

        //Deletion part
        if (target.id() < root.id()) {
            root.left(remove(root.left(), target));
        } else if (target.id() > root.id()) {
            root.right(remove(root.right(), target));
        } else {
            if ((root.left() == null) || (root.right() == null)) {
                AvlNode <T> temp;
                size--;
                if (root.left() == null) {
                    temp = root.right();
                } else {
                    temp = root.left();
                }
                root = temp;
            } else {
                AvlNode <T> temp = getMinValue(root.right());
                root.id(temp.id());
                root.right(remove(root.right(), target));
            }
        }

        if (root == null) {
            return null;
        }

        //Balancing part
        AvlNode<T> newRoot = balanceAfterDeletion(root);
        if (newRoot != null) return newRoot;
        return root;
    }

    private AvlNode<T> balanceAfterDeletion(AvlNode<T> root) {
        int balance = getBalance(root);

        if (balance > 1 && getBalance(root.left()) >= 0) {
            return rightRotation(root);
        }

        if (balance > 1 && getBalance(root.left()) < 0) {
            root.left(leftRotation(root.left()));
            return rightRotation(root);
        }

        if (balance < -1 && getBalance(root.right()) <= 0) {
            return leftRotation(root);
        }

        if (balance < -1 && getBalance(root.right()) > 0) {
            root.right(rightRotation(root.right()));
            return leftRotation(root);
        }
        return null;
    }

    private void print(AvlNode <T> node, int level){
        //Prints the tree in the console using IDs. Level must be zero, node can be any node.
        if(node==null){
            return;
        }

        print(node.right(), level+1);

        if(level!=0){
            for(int i=0;i<level-1;i++){
                System.out.print("|\t");
            }
            System.out.println("|-------"+node.id());
        }else{
            System.out.println(node.id());
        }
        print(node.left(), level+1);
    }

    private void printData(AvlNode <T> root, int level){
        //Prints the tree in the console using data instead of IDs. Level must be zero, node can be any node.
        if(root==null){
            return;
        }

        printData(root.right(), level+1);

        if(level != 0){
            for(int i = 0; i < level - 1 ; i++){
                System.out.print("|\t");
            }
            System.out.println("|-------"+root.data());
        }else{
            System.out.println(root.data());
        }
        printData(root.left(), level+1);
    }

    /***************************
     Public methods
     ***************************/

    public boolean isEmpty(){
        return root == null;
    }

    public void clear(){
        root(null);
    }

    public int height(){
        return height(root());
    }

    public void add(AvlNode <T> node){
        //Wrapper method. See balancedInsertion() for details.
        if(this.root == null){
            this.root = node;
            size++;
            return;
        }

        if(areDuplicatesAllowed){
            node = uniqueId(node);
        }

        this.root = balancedInsertion(this.root, node);
    }

    public void add(T data){
        //Wrapper method. See balancedInsertion() for details.
        if(this.root == null){
            this.root = new AvlNode <>(data);
            size++;
            return;
        }

        AvlNode <T> node = new AvlNode <>(data);

        if(areDuplicatesAllowed){
            node = uniqueId(node);
        }

        this.root = balancedInsertion(this.root, node);
    }

    public void addAll(Collection <? extends AvlNode <T>> c){
        for(Object node : c.toArray()){
            add((AvlNode<T>) node);
        }
    }

    public void addAllData(Collection <? extends T> c){
        for(Object data : c.toArray()){
            add((T) data);
        }
    }

    public void remove(int id){
        //Wrapper method. See private remove() for details.
        if(contains(id)){
            this.root = remove(this.root, get(id));
        }
    }

    public void remove(AvlNode <T> target){
        //Wrapper method. See private remove() for details.
        if(target == null){
            return;
        }
        if(contains(target)){
            this.root = remove(this.root, target);
        }
    }

    public void remove(T data){
        //Tries to remove data object using its hashcode. Requires data to have a proper hashcode method.
        //Will NOT remove a duplicate. If you have "John" stored twice in your tree, only one will have a hashcode based ID (here 2314539).
        //The other one will have a random ID and cannot be removed this way.
        //Use remove(get(data)) instead.
        //Use allowDuplicate() to forbid duplicates if needed.
        remove(data.hashCode());
    }

    public void removeAll(Collection <? extends AvlNode <T>> c){
        for(Object node : c.toArray()){
            if(contains((AvlNode<T>) node)){
                remove((AvlNode<T>)node);
            }
        }
    }

    public void removeAllData(Collection <? extends T> c){
        for(Object data : c.toArray()){
            if(containsData((T) data)){
                remove((T) data);
            }
        }
    }

    public boolean contains(AvlNode <T> node){
        return get(node.id()) != null;
    }

    public boolean contains(int id){
        return get(id) != null;
    }

    public boolean containsData(T data){
        return get(data.hashCode()) != null;
    }

    public boolean containsAll(Collection <? extends AvlNode <T>> c){
        for(Object node : c.toArray()){
            if(!contains((AvlNode<T>) node)){
                return false;
            }
        }
        return true;
    }

    public boolean containsAllData(Collection <? extends T> c){
        for(Object data : c.toArray()){
            if(!containsData((T) data)){
                return false;
            }
        }
        return true;
    }

    public void allowDuplicates(boolean doWeAllowThem){
        areDuplicatesAllowed = doWeAllowThem;
    }

    public AvlNode <T> get(int id){
        return getById(this.root, id);
    }

    public AvlNode <T> get(AvlNode <T> node){
        return getById(this.root, node.id());
    }

    public AvlNode <T> get(T data){
        return get(data.hashCode());
    }

    public List<Integer> getIterators(){
        //returns all IDs in the tree for later iteration.
        //Iterators are returned from top to bottom, left to right.
        //Due to tree rotations, it isn't possible to predict exactly the order IDs will have.
        List <AvlNode<T>> stack = new LinkedList<>();
        List <AvlNode<T>> temporaryList = new ArrayList<>();
        List <Integer> result = new ArrayList<>();
        stack.add(this.root);
        fillLists(stack, temporaryList);
        for(AvlNode <T> node : temporaryList){
            result.add(node.id());
        }
        return result;
    }

    public void replace(AvlNode<T> target, AvlNode<T> newNode){
        //Doesn't keep original position
        remove(target);
        add(newNode);
    }

    public List <AvlNode <T>> toArrayList(){
        //Turns the tree into an array from top to bottom and left to right.
        LinkedList <AvlNode <T>> stack = new LinkedList<>();
        ArrayList <AvlNode <T>> list = new ArrayList<>();
        stack.add(this.root);
        fillLists(stack, list);
        return list;
    }

    public List <AvlNode <T>> toLinkedList(){
        //Turns the tree into an array from top to bottom and left to right.
        LinkedList <AvlNode <T>> stack = new LinkedList<>();
        LinkedList <AvlNode <T>> list = new LinkedList<>();
        stack.add(this.root);
        fillLists(stack, list);
        return list;
    }

    private void fillLists(List<AvlNode<T>> stack, List<AvlNode<T>> list) {
        while(!stack.isEmpty()){
            list.add(stack.get(0));
            if(stack.get(0).left() != null){
                stack.add(stack.get(0).left());
            }

            if(stack.get(0).right() != null){
                stack.add(stack.get(0).right());
            }

            stack.remove(stack.get(0));
        }
    }


    /***************************
     Tree printers
     ***************************/

    public void print(){
        //Basic print-to-console method
        print(this.root, 0);
    }

    public void print(AvlNode<T> node){
        //Prints node children
        print(node, 0);
    }

    public void printData(){
        //Basic print-to-console method but for data instead of IDs
        printData(this.root, 0);
    }

    public void printData(AvlNode<T> node){
        //Prints node & children data
        printData(node, 0);
    }

    public boolean save() {
        //Careful : This method always overrides and always uses the same default path.
        //To save multiple trees, use save(Path) with a different path for each tree.
        File f = new File(savePath);
        if(!f.exists()){
            try {
                if(!Files.exists(Paths.get(savePath).getParent())){
                    Files.createDirectories(Paths.get(savePath).getParent());
                }

                if(f.createNewFile()){
                    System.out.println("File Created");
                }

            }catch (Exception e){
                e.printStackTrace();
                System.out.println("Error while creating the save file");
                return false;
            }
        }

        try{
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.root);
            oos.close();
            fos.close();
            System.out.println("Tree saved properly");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error when saving to the file");
            return false;
        }
    }

    public boolean load(){
        //Loads a pre-existing serialized tree stored into a file into current tree.
        try {
            FileInputStream fis = new FileInputStream(savePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.root = (AvlNode<T>) ois.readObject();
            fis.close();
            ois.close();
            System.out.println("Tree loaded properly");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while loading the save : File not found");
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error while loading the save : Save cannot be loaded into memory (incompatible object)");
            return false;
        }
    }

    public boolean save(String savePath){
        this.savePath = savePath;
        return save();
    }

    public boolean load(String savePath){
        this.savePath = savePath;
        return load();
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }

        if(!(o instanceof AvlTree)){
            return false;
        }
        AvlTree <T> otherTree = (AvlTree<T>)o;
        return this.hashCode() == otherTree.hashCode();
    }

    @Override
    public int hashCode(){
        int hash = 18;
        if(root != null ){
            hash += root.hashCode();
        }
        return hash;
    }

    @Override
    public String toString(){
        //Returns a String formatted like this : [id1] [id2] [id3]
        if(this.root == null){
            return "";
        }

        StringBuilder str = new StringBuilder();
        LinkedList <AvlNode <T>> stack = new LinkedList<>();
        stack.add(this.root);
        while(!stack.isEmpty()){
            //Change .id() to .data() in the line below to print data instead. Requires a proper toString() method on data object.
            str.append("[").append(stack.get(0).id()).append("] ");
            if(stack.get(0).left() != null){
                stack.add(stack.get(0).left());
            }

            if(stack.get(0).right() != null){
                stack.add(stack.get(0).right());
            }

            stack.remove(stack.get(0));
        }
        return str.toString();
    }
}