/**
 * 
 */
package iansLibrary.data.structures;

import java.util.ArrayList;

/**
 * @author Ian
 * @date Sep 25, 2015
 * @project Tree
 * @todo TODO
 */
public class Queue<E>{

	protected ArrayList<E> storage;
	
	public Queue(){
		this.storage = new ArrayList<E>();
	}
	
	public void push(E val){
		this.storage.add(val);
	}
	
	public E poll(){
		try{
			E val = this.storage.get(0);
			this.storage.remove(0);
			return val;
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
	}
	
	public E peek(){
		try{
			E val = this.storage.get(0);
			return val;
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
	}
	
	public void clear(){
		this.storage.clear();
	}
	
	public boolean isEmpty(){
		if(this.storage.size() == 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public int size(){
		return this.storage.size();
	}
	
	public Queue<E> copy(){
		Queue<E> backup = new Queue<E>();
		for(int i = this.storage.size() - 1; i >= 0; i --){
			backup.push(this.storage.get(i));
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
