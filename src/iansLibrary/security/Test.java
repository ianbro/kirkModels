package iansLibrary.security;

public class Test {

	public static void main(String[] args) {
		Password password = new Password("HELr LOZ");
		Password encripted = password.EncriptNumber(3);
		Password deciphered = encripted.decipherNumber(3);
		System.out.println("origional password: " + password);
		System.out.println("encripted: " + encripted);
		System.out.println("password: " + deciphered);

	}

}
