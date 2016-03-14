/**
 * 
 */
package iansLibrary.data.structures.tree;

import java.util.ArrayList;

import iansLibrary.data.structures.tree.exceptions.MultiIndexesForValueException;

/**
 * @author Ian
 * @date Sep 25, 2015
 * @project Tree
 * @todo TODO
 */
public class Node<E>{

	private int depth;
	public Tree<E> hostTree;
	protected Integer nodeLimit;
	public Node<E> parent;
	public TreePath pathToThis;
	public E value;
	
	public ArrayList<Node<E>> children = new ArrayList<Node<E>>();
	
	public Node(Node<E> parent, Integer limit, Tree<E> hostTree, E value){
		this.parent = parent;
		this.nodeLimit = limit;
		this.hostTree = hostTree;
		this.value = value;
		this.pathToThis = this.parent.pathToThis.copy();
		this.pathToThis.push(this.parent.children.size());
		this.depth = this.pathToThis.size() - 1;
	}
	
	public Node(Integer limit, Tree<E> hostTree, E value){
		//constructor for anchor node
		this.parent = null;
		this.nodeLimit = limit;
		this.hostTree = hostTree;
		this.value = value;
		this.pathToThis = new TreePath();
		this.pathToThis.push(-1);
		this.depth = 0;
	}
	
	public Node(){
		
	}
	
	public int getDepth(){
		return this.depth;
	}
	
	public void setDepth(int d){
		this.depth = d;
	}
	
	public int indexInChildrenOf(Node<E> val) throws MultiIndexesForValueException{
		int index = -1;
		for(int i = 0; i < this.children.size(); i ++){
			if(this.children.get(i).equals(value)){
				if(index != -1){
					throw new MultiIndexesForValueException(value);
				}
				else{
					index = i;
				}
			}
		}
		return index;
	}
	
	public void addChild(Node<E> val){
		this.children.add(val);
		
		//increment host tree size
		this.hostTree.incrementSize();
		
		//increment host tree allStorage
		if(this.hostTree.allStorage.containsKey(this.depth + 1)){
			this.hostTree.allStorage.get(this.depth + 1).add(val);
		}
		else{
			this.hostTree.allStorage.put(this.depth + 1, new ArrayList<Node<E>>(){{add(val);}});
		}
		
		//increment hostTree bredth
		if(this.hostTree.allStorage.get(this.depth + 1).size() > this.hostTree.getBredth()){
			this.hostTree.incrementBredth();
		}
		
		//incrementing hostTree depth
		if(this.hostTree.allStorage.keySet().size() > this.hostTree.getDepth()){
			this.hostTree.incrementDepth();
		}
	}
	
	public Node<E> followPathFromHere(TreePath path){
		Node<E> temp = null;
		TreePath pathFromHere = this.pathToThis.getDifferenceThisLonger(path);
		while(!pathFromHere.isEmpty()){
			temp = this.children.get(pathFromHere.poll());
		}
		this.pathToThis.reset();
		return temp;
	}
	
	public Node<E> getChild(int index){
		return this.children.get(index);
	}
	
	public void addChild(int index, Node<E> node){
		this.children.add(index, node);
		
		//increment host tree size
		this.hostTree.incrementSize();
		
		//increment host tree allStorage
		if(this.hostTree.allStorage.containsKey(this.depth + 1)){
			this.hostTree.allStorage.get(this.depth + 1).add(node);
		}
		else{
			this.hostTree.allStorage.put(this.depth + 1, new ArrayList<Node<E>>(){{add(node);}});
		}
		
		//increment hostTree bredth
		if(this.hostTree.allStorage.get(this.depth + 1).size() > this.hostTree.getBredth()){
			this.hostTree.incrementBredth();
		}
		
		//incrementing hostTree depth
		if(this.hostTree.allStorage.keySet().size() > this.hostTree.getDepth()){
			this.hostTree.incrementDepth();
		}
	}
	
	public String childrenToString(){
		String str = "[";
		for(Node child : this.children){
			str = str + child.toString() + ", ";
		}
		str = str + "]";
		
		return str;
	}
	
	public String toString(){
		return this.value.toString();
	}
}
