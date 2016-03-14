/**
 * 
 */
package iansLibrary.data.structures.tree;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Ian
 * @date Sep 25, 2015
 * @project Tree
 * @todo TODO
 */
public class Tree<E> {

	public Node<E> anchor;
	private int depth = 0;
	private int bredth = 0;
	private int size = 0;
	private Tree<E> bakcUp;
	public HashMap<Integer, ArrayList<Node<E>>> allStorage;
	
	public Tree(){
		this.allStorage = new HashMap<Integer, ArrayList<Node<E>>>();
		this.allStorage.put(0, new ArrayList<Node<E>>());
		this.bakcUp = new Tree<E>(true);
	}
	
	private Tree(boolean backup){
		this.allStorage = new HashMap<Integer, ArrayList<Node<E>>>();
		this.allStorage.put(0, new ArrayList<Node<E>>());
	}
	
	public void setAnchor(Node<E> val){
		this.anchor = val;
		this.allStorage.get(0).add(val);
	}
	
	public void setAnchor(Integer limit, Tree<E> hostTree, E value){
		this.anchor = new Node<E>(limit, hostTree, value);
		this.incrementSize();
	}
	
	public void incrementSize(){
		this.size ++;
	}
	
	public int getSize(){
		return this.size;
	}
	
	public void incrementBredth(){
		this.bredth ++;
	}
	
	public int getBredth(){
		return this.bredth;
	}
	
	public void incrementDepth(){
		this.depth ++;
	}
	
	public int getDepth(){
		return this.depth;
	}
	
	public Node<E> getNodeByPath(TreePath path){
		Node<E> node = this.anchor.followPathFromHere(path);
		return node;
	}
	
	public void pullNodeUp(TreePath path){
		Node<E> node = this.anchor.followPathFromHere(path);
		this.anchor = node;
	}
	
	public void pushNodeDown(TreePath path){
		Node<E> node = this.bakcUp.getNodeByPath(path);
		this.anchor = node;
	}
	
	public void pullUpChild(int index){
		Node<E> node = this.anchor.getChild(index);
		this.anchor = node;
	}
	
	public void backUpOne(){
		Node<E> node = this.anchor.parent;
		this.anchor = node;
	}
	
	public void reset(){
		this.anchor = this.bakcUp.anchor;
	}
}
