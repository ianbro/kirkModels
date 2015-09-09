package tests;

import java.util.HashMap;

import kirkModels.objects.CharField;
import kirkModels.objects.IntegerField;
import kirkModels.objects.Model;
import kirkModels.objects.SQLField;

public class Profile extends Model<Profile> {
	
	public String firstName;
	public String lastName;
	public String username;
	public String password;
	public Integer age;

	public Profile(String firstName, String lastName, String username, String password, int age) {
		super(new HashMap<String, SQLField>(){{
			put("firstName", new CharField("name", false, null, false, 20));
			put("lastName", new CharField("lastName", false, null, false, 20));
			put("username", new CharField("username", false, null, true, 15));
			put("password", new CharField("password", false, null, false, 15));
			put("age", new IntegerField("age", false, null, false, 150));
		}});
		
		this.sqlFields.get("firstName").set(firstName);
		this.sqlFields.get("lastName").set(lastName);
		this.sqlFields.get("userName").set(username);
		this.sqlFields.get("password").set(password);
		this.sqlFields.get("age").set(age);
	}
	
	public Profile() {
		super(new HashMap<String, SQLField>(){{
			put("firstName", new CharField("name", false, null, false, 20));
			put("lastName", new CharField("lastName", false, null, false, 20));
			put("username", new CharField("username", false, null, true, 15));
			put("password", new CharField("password", false, null, false, 15));
			put("age", new IntegerField("age", false, null, false, 150));
		}});
	}

}
