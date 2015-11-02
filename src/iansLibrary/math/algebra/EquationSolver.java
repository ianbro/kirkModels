package iansLibrary.math.algebra;

import iansLibrary.data.structures.Stack;

public abstract class EquationSolver {

	public static double answer;
	public static Equation equation;
	public static Stack<String> toEvaluate = new Stack<String>();
	
	public static double solveEquation(Equation e){
		equation = e;
		while(!equation.isEmpty()){
			String target = (String)equation.poll();
			if(isSign(target)){
				String e2 = (String)toEvaluate.pop();
				String e1 = (String)toEvaluate.pop();
				answer = performOperation(e1, target, e2);
				toEvaluate.insert(String.valueOf(answer));
			}
			else{
				toEvaluate.insert(target);
			}
		}
		
		return Double.valueOf((String)toEvaluate.pop());
	}
	
	private static double performOperation(String e1, String operation, String e2){
		switch(operation){
		case "+":
			return Double.valueOf(e1) + Double.valueOf(e2);
		case "-":
			return Double.valueOf(e1) - Double.valueOf(e2);
		case "*":
			return Double.valueOf(e1) * Double.valueOf(e2);
		case "/":
			return Double.valueOf(e1) / Double.valueOf(e2);
		}
		return 0.0;
	}
	
	public static boolean isSign(String e){
		if(e.equals("+") || e.equals("-") || e.equals("*") || e.equals("/")){
			return true;
		}
		else{
			return false;
		}
	}
}
