/**
 * 
 */
package iansLibrary.data.structures.tree.tests;

import iansLibrary.data.structures.Queue;
import iansLibrary.data.structures.Stack;
import iansLibrary.data.structures.tree.Node;
import iansLibrary.data.structures.tree.Tree;
import iansLibrary.data.structures.tree.TreePath;

/**
 * @author Ian
 * @date Sep 25, 2015
 * @project Tree
 * @todo TODO
 */
public class TestMain {

	public static void test(){
		
//		System.out.println("STACK");
//		Stack<Integer> stack = new Stack<Integer>();
//		stack.insert(1);
//		stack.insert(2);
//		stack.insert(3);
//		stack.insert(4);
//		stack.insert(5);
//		
//		System.out.println(stack.top());
//		System.out.println(stack.toString());
//		
//
//		System.out.println("QUEUE");
//		Queue<Integer> queue = new Queue<Integer>();
//		queue.push(1);
//		queue.push(2);
//		queue.push(3);
//		queue.push(4);
//		queue.push(5);
//		
//		System.out.println(queue.peek());
//		System.out.println(queue.toString());
		

		System.out.println("TREE PATH");
		TreePath path = new TreePath();
		path.push(0);
		path.push(3);
		path.push(2);
		path.push(6);
		System.out.println(path.toString());
		
		TreePath path2 = path.copy();
		path2.push(9);
		System.out.println(path2);
		
		path.poll();
		System.out.println(path.toString());
		
		path.reset();
		System.out.println(path.toString());
		

		System.out.println("TREE");
		Tree<Integer> tree = new Tree<Integer>();
		tree.setAnchor(null, tree, 0);
		tree.anchor.addChild(new Node<Integer>(tree.anchor, null, tree, 1));
		System.out.println("===============================creating third level");
		tree.anchor.getChild(0).addChild(new Node<Integer>(tree.anchor.getChild(0), null, tree, 10));
		System.out.println("===============================done crating third level");
		tree.anchor.addChild(new Node<Integer>(tree.anchor, null, tree, 2));
		tree.anchor.addChild(new Node<Integer>(tree.anchor, null, tree, 3));
		tree.anchor.addChild(new Node<Integer>(tree.anchor, null, tree, 4));
		tree.anchor.addChild(new Node<Integer>(tree.anchor, null, tree, 5));
		
		
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println(tree.anchor.toString());
		System.out.println(tree.anchor.getChild(0).pathToThis);
		System.out.println(tree.anchor.childrenToString());
		System.out.println(tree.getDepth());
		System.out.println(tree.getBredth());
		System.out.println(tree.getSize());
	}
}
