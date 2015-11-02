package iansLibrary.security.auth;

import java.util.ArrayList;

import iansLibrary.utilities.ModdedDate;

public class Person {

	protected String firstName;
	protected String lastName;
	protected boolean gender; //if true: male, if false: female
	protected String phoneNumber;
	protected Object[] address; //{number, street, city, state/province, country, zip}
	protected ModdedDate birthDate;
	protected String email;
	
	protected ArrayList<Account> accounts;
	protected ArrayList<Object> info; //info memory starts with first name, then last name
	
	public Person(String firstName, String lastName, boolean gender, String phoneNumber,
			int addressNum, String street, String city, String state, String country, int zip,
			int[] birthDate, String email){
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.phoneNumber = phoneNumber;
		{
		this.address[0] = addressNum;
		this.address[1] = street;
		this.address[2] = city;
		this.address[3] = state;
		this.address[4] = country;
		this.address[5] = zip;
		}
		this.birthDate = new ModdedDate(birthDate[0], birthDate[1], birthDate[2]);
		this.email = email;
		
		info = new ArrayList<Object>();
		this.info.add(firstName);
		this.info.add(lastName);
	}
	
	public Person(){}

	//basic info
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getFullName(){
		return this.firstName + "_" + this.lastName;
	}
	public boolean getGender(){
		return this.gender;
	}
	public void setPhoneNumber(String number){
		this.phoneNumber = number;
	}
	public String getPhoneNumber(){
		return this.phoneNumber;
	}
	public ModdedDate getBirthDate(){
		return this.birthDate;
	}
	public int getAge(){
		ModdedDate now = new ModdedDate();
		if(now.date() >= this.birthDate.date() && now.month() >= this.birthDate.month()){
			return now.year() - this.birthDate.year();
		}
		else{
			return now.year() - this.birthDate.year() - 1;
		}
	}
	public void setEmail(String email){
		this.email = email;
	}
	public String getEmail(){
		return this.email;
	}
	
	
	

	//dynamic info
	public ArrayList<Account> getAccounts() {
		return accounts;
	}
	public void initializeAccounts(){
		accounts = new ArrayList<Account>();
	}
	public void addInfo(Object[] info){
		this.info.add(info);
	}
	public Object getInfo(int index){
		return this.info.get(index);
	}
}
