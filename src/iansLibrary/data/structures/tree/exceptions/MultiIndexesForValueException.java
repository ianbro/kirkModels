/**
 * 
 */
package iansLibrary.data.structures.tree.exceptions;

/**
 * @author Ian
 * @date Sep 27, 2015
 * @project Tree
 * @todo TODO
 */
public class MultiIndexesForValueException extends Exception {

	public MultiIndexesForValueException(){
		super("Multiple indexes were found for node with this value.");
	}
	
	public MultiIndexesForValueException(Object val){
		super("Multiple indexes were found for node with value: " + val.toString() +"");
	}
}
