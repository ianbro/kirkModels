package iansLibrary.math.conversions;

import java.io.PrintWriter;
import java.util.LinkedList;

import iansLibrary.data.structures.Stack;
import iansLibrary.math.algebra.Equation;

public abstract class PolishNotation {

	private static Equation sourceEquation;
	private static Equation tempEquation;
	private static Stack<String> signStack;

	private static int getOrder(String e){
		int order = 0;
		switch(e){
		case "+":
			order = 1;
			break;
		case "-":
			order = 1;
			break;
		case "*":
			order = 2;
			break;
		case "/":
			order = 2;
			break;
		case "(":
			order = -1;
			break;
		case ")":
			order = 3;
			break;
		default:
			order = 0;
			break;
		}
		return order;
	}
	
	private static void moveNextElement(){
		int order = getOrder((String)sourceEquation.peek());
		switch(order){
		case -1:
			signStack.insert(sourceEquation.poll());
			break;
		case 0:
			tempEquation.push(sourceEquation.poll());
			break;
		case 1:
			if (!signStack.isEmpty() && getOrder((String)signStack.top()) == -1){
				signStack.insert(sourceEquation.poll());
			}
			else {
				dumpSignStack();
				signStack.insert(sourceEquation.poll());
			}
			break;
		case 2:
			if(signStack.isEmpty()){
				signStack.insert(sourceEquation.poll());
			}
			else{
				if (getOrder((String)signStack.top()) == -1){
					signStack.insert(sourceEquation.poll());
				}
				else if(getOrder((String)signStack.top()) == 1){
					signStack.insert(sourceEquation.poll());
				}
				else if(getOrder((String)signStack.top()) >= 2){
					dumpSignStack();
					signStack.insert(sourceEquation.poll());
				}
			}
			break;
		case 3:
			dumpSignStack();
			sourceEquation.poll();
		}
		if(sourceEquation.isEmpty() == true){
			dumpSignStack();
		}
	}
	
	private static void dumpSignStack(){
		while(signStack.isEmpty() == false){
			if(signStack.top().equals("(")){
				signStack.pop();
				break;
			} else{
				tempEquation.push(signStack.pop());
			}
		}
	}
	
	private static void printTestInfo(){
			System.out.println("tempEquation: " + tempEquation.toString());
			System.out.println("signStack: " + signStack.toString());
			System.out.println("sourceEquation: " + sourceEquation.toString());
	}
	
	public static Equation convertToPolishNotation(Equation equation){
		sourceEquation = equation;
		tempEquation = new Equation();
		signStack = new Stack();
		
		while(sourceEquation.isEmpty() == false){
//			System.out.println("start");
//			printTestInfo();
//			System.out.println("");
			moveNextElement();
//			System.out.println("end");
//			printTestInfo();
//			System.out.println("\n\n\n\n\n");
		}
		Equation retVal = tempEquation;
		
		sourceEquation = null;
		tempEquation = null;
		signStack = null;
		
		return retVal;
	}
}
