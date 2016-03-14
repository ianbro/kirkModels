/**
 * 
 */
package iansLibrary.data.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ian
 * @date Sep 25, 2015
 * @project Tree
 * @todo TODO
 */
public class Stack<E>{

	protected ArrayList<E> storage;
	
	public Stack(){
		this.storage = new ArrayList<E>();
	}
	
	public void insert(E val){
		this.storage.add(0, val);
	}
	
	public E pop(){
		try{
			E val = this.storage.get(0);
			this.storage.remove(0);
			return val;
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
	}
	
	public E top(){
		try{
			E val = this.storage.get(0);
			return val;
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
	}
	
	public boolean isEmpty(){
		if(this.storage.size() == 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void clear(){
		this.storage.clear();
	}
	
	public int size(){
		return this.storage.size();
	}
	
	public Stack<E> copy(){
		Stack<E> backup = new Stack<E>();
		for(int i = 0; i < this.storage.size(); i ++){
			backup.insert(this.storage.get(i));
		}
		return backup;
	}
	
	public String toString(){
		String next = " --> ";
		String str = "";
		
		for(int i = 0; i < this.storage.size(); i ++){
			str = str + next + this.storage.get(i).toString();
		}
		
		return str;
	}
}
