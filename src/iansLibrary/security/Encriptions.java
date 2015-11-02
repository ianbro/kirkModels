package iansLibrary.security;

import java.util.ArrayList;

public abstract class Encriptions {

	public static String toShiftedNumberLine(String toEncript, int shiftVal){
		if(shiftVal > 73){
			return "cannot shift over 73 spaces";
		}
		String retVal = "";
		for(int i = 0; i < toEncript.length(); i ++){
			char c = toEncript.charAt(i);
			int ascii;
			String asciiString;
			if(c == ' '){
				ascii = 0 + shiftVal;
			} else {
				ascii = ((int) c) - 64 + shiftVal;
			}
			if(ascii < 10){
				asciiString = "0" + ascii;
			} else {
				asciiString = String.valueOf(ascii);
			}
			retVal = retVal + asciiString;
		}
		return retVal;
	}
	
	public static String toShiftedNumberLine(Password password, int shiftVal){
		if(shiftVal > 73){
			return "cannot shift over 73 spaces";
		}
		String toEncript = password.toString();
		String retVal = "";
		for(int i = 0; i < toEncript.length(); i ++){
			char c = toEncript.charAt(i);
			int ascii;
			String asciiString;
			if(c == ' '){
				ascii = 0 + shiftVal;
			} else {
				ascii = ((int) c) - 64 + shiftVal;
			}
			if(ascii < 10){
				asciiString = "0" + ascii;
			} else {
				asciiString = String.valueOf(ascii);
			}
			retVal = retVal + asciiString;
		}
		return retVal;
	}
	
	public static String fromShiftedNumberLine(String toDecipher, int shiftVal){
		ArrayList<String> string = new ArrayList<String>();
		//shifting back down to normal
		for(int i = 0; i < toDecipher.length(); i +=2){
			string.add(toDecipher.substring(i, i+2));
		}
		ArrayList<String> string2 = new ArrayList<String>();
		for(int i = 0; i < string.size(); i ++){
			int newVal = Integer.valueOf(string.get(i)) - shiftVal;
			string2.add(String.valueOf(newVal));
		}
		string = string2;
		
		string2 = new ArrayList<String>();
		for(int i = 0; i < string.size(); i ++){
			if(string.get(i).equals("0")){
				string2.add(" ");
			}
			else {
				int ascii = (int) Integer.valueOf(string.get(i));
				String c = Character.toString((char) (ascii + 64));
				string2.add(c);
			}
		}
		
		String retVal = "";
		for(int i = 0; i < string2.size(); i ++){
			retVal = retVal + string2.get(i);
		}
		
		return retVal;
	}
	
	public static Password fromShiftedNumberLine(Password toDecipher, int shiftVal){
		String stringToDecipher = toDecipher.value;
		ArrayList<String> string = new ArrayList<String>();
		//shifting back down to normal
		for(int i = 0; i < stringToDecipher.length(); i +=2){
			string.add(stringToDecipher.substring(i, i+2));
		}
		for(int i = 0; i < string.size(); i ++){
			System.out.print(string.get(i));
		} System.out.println("");
		ArrayList<String> string2 = new ArrayList<String>();
		for(int i = 0; i < string.size(); i ++){
			int newVal = Integer.valueOf(string.get(i)) - shiftVal;
			string2.add(String.valueOf(newVal));
		}
		string = string2;
		
		string2 = new ArrayList<String>();
		for(int i = 0; i < string.size(); i ++){
			if(string.get(i).equals("0")){
				string2.add(" ");
			}
			else {
				int ascii = (int) Integer.valueOf(string.get(i));
				String c = Character.toString((char) (ascii + 64));
				string2.add(c);
			}
		}
		
		String retVal = "";
		for(int i = 0; i < string2.size(); i ++){
			retVal = retVal + string2.get(i);
		}
		
		return new Password(retVal);
	}
}
