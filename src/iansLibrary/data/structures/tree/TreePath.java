/**
 * 
 */
package iansLibrary.data.structures.tree;

import java.util.ArrayList;

import iansLibrary.data.structures.Queue;

/**
 * @author Ian
 * @date Sep 25, 2015
 * @project Tree
 * @todo TODO
 */
public class TreePath extends Queue<Integer>{
	
	protected Queue<Integer> backup;
	
	public TreePath(){
		super();
		this.backup = new Queue<Integer>();
	}
	
	public TreePath(boolean backup){
		super();
	}
	
	public void reset(){
		this.clear();
		for(int i = 0; i < this.backup.size(); i ++){
			Integer index = this.backup.poll();
			super.push(index);
			this.backup.push(index);
		}
	}
	
	public Integer poll(){
		Integer val = super.poll();
		if(this.isEmpty()){
			this.reset();
		}
		return val;
	}
	
	public void push(Integer val){
		super.push(val);
		this.backup.push(val);
	}
	
	public TreePath copy(){
		TreePath val = new TreePath();
		for(int i = 0; i < this.size(); i ++){
			val.push(this.storage.get(i));
		}
		return val;
	}
	
	public int size(){
		try{
			return this.backup.size();
		}
		catch(NullPointerException e){
			return super.size();
		}
	}
	
	public TreePath getDifferenceThisLonger(TreePath path){
		TreePath val = new TreePath();
		while(!path.isEmpty()){
			Integer temp = path.poll();
			if(temp == this.poll()){
				val.push(temp);
			}
			else{
				return null;
			}
		}
		path.reset();
		this.reset();
		return val;
	}
	
	public TreePath getDifferenceThisShorter(TreePath path){
		TreePath val = new TreePath();
		while(!this.isEmpty()){
			Integer temp = this.poll();
			if(temp == path.poll()){
				val.push(temp);
			}
			else{
				return null;
			}
		}
		path.reset();
		this.reset();
		return val;
	}
	
	public boolean equals(TreePath o){
		if(this.storage.size() != o.size()){
			return false;
		}
		for(int i = 0; i < this.storage.size(); i ++){
			Integer step = this.storage.get(i);
			Integer otherStep = o.storage.get(i);
			if(step.intValue() != otherStep.intValue()){
				return false;
			}
		}
		return true;
	}
}
