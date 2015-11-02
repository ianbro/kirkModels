package iansLibrary.math.algebra;

import java.util.LinkedList;

import iansLibrary.data.structures.Queue;
import iansLibrary.math.conversions.PolishNotation;

public class Equation extends Queue<String>{
	
	public Equation(String equation){
		boolean done = false;
		int i = 0;
		while(done == false){
			String toInsert = getNextElement(equation, i);
			i += toInsert.length();
			this.push(toInsert);
			if(i+1 > equation.length()){
				done = true;
			}
		}
	}
	
	public Equation(){}
	
	private String getNextElement(String equation, int i){
		if(isSign(String.valueOf(equation.charAt(i)))){
			return String.valueOf(equation.charAt(i));
		}
		for(int j = i; j < equation.length(); j ++){
			if(isSign(equation.substring(j,j+1))){
				return equation.substring(i, j);
			}
			if(j+1 >= equation.length()){
				return equation.substring(i, j+1);
			}
		}
		return "Error";
	}
	
	static boolean isSign(String e){
		if(e.equals("+") || e.equals("-") || e.equals("*") || e.equals("/") || e.equals("(") || e.equals(")")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public Equation toReversePolish(){
		Equation retVal = PolishNotation.convertToPolishNotation(this);
		return retVal;
	}
	
	public double solve(){
		Equation newEquation = this.toReversePolish();
		return EquationSolver.solveEquation(newEquation);
	}
}
