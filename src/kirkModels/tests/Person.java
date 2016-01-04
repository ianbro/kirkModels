package kirkModels.tests;

import kirkModels.DbObject;
import kirkModels.fields.CharField;
import kirkModels.fields.ForeignKey;
import kirkModels.fields.IntegerField;
import kirkModels.fields.ManyToManyField;
import kirkModels.orm.QuerySet;

public class Person extends DbObject{
	
	public static QuerySet<Person> objects;

	public CharField name = new CharField("name", false, null, false, 45);
	public IntegerField age = new IntegerField("age", false, null, false, 150);
	public ForeignKey<Person> mother = new ForeignKey<Person>("mother", Person.class, true, null, false, "NO ACTION");
	public ForeignKey<Person> father = new ForeignKey<Person>("father", Person.class, true, null, false, "NO ACTION");
	public ManyToManyField<Person, Person> friends = new ManyToManyField<Person, Person>("friends", this, Person.class);
	public ManyToManyField<Person, Person> enemies = new ManyToManyField<Person, Person>("enemies", this, Person.class);
	public int fjslkfsjflasfjslfs;
	
	public String toString(){
		return "Person '" + this.name + "': " + this.age + " years old";
	}
}
