package kirkModels.tests;

import kirkModels.DbObject;
import kirkModels.fields.CharField;
import kirkModels.fields.IntegerField;
import kirkModels.fields.ManyToManyField;

public class Person extends DbObject{

	public CharField name = new CharField("name", false, null, false, 10);
	public IntegerField age = new IntegerField("age", false, null, false, 150);
	public ManyToManyField<Person, Person> friends = new ManyToManyField<>(Person.class, Person.class);
	public ManyToManyField<Person, Person> enemies = new ManyToManyField<>(Person.class, Person.class);
	public int fjslkfsjflasfjslfs;
	
	@Override
	public void initializeManyToManyFields() {
		friends.setHostId(this.id.val());
		enemies.setHostId(this.id.val());
	}
}
