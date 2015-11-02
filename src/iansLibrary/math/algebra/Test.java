package iansLibrary.math.algebra;

public class Test {

	public static void main(String[] args) {
		testEquation();

	}
	
	//Equation
	public static void testEquation(){
		Equation e = new Equation("1-5*(8+6*7)+7*(9-6*5)-9");
		System.out.println(e);
		System.out.println(e.toReversePolish());
	}

}
