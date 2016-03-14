package iansLibrary.security;

public class Password {

	String value;
	
	public Password(String password){
		this.value = password;
	}
	
	public String toString(){
		return this.value;
	}
	
	public Password EncriptNumber(int shiftVal){
		String retVal = Encriptions.toShiftedNumberLine(this.value, shiftVal);
		return new Password(retVal);
	}
	
	public Password decipherNumber(int shiftVal){
		String retVal = Encriptions.fromShiftedNumberLine(this.value, shiftVal);
		return new Password(retVal);
	}
}
