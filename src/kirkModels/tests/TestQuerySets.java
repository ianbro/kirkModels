package kirkModels.tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import kirkModels.orm.QuerySet;
import kirkModels.queries.scripts.WhereCondition;
import kirkModels.utils.exceptions.ObjectAlreadyExistsException;
import kirkModels.utils.exceptions.ObjectNotFoundException;

public abstract class TestQuerySets {
	
	public static HashSet<String> fails = new HashSet<String>();

	public static void run() {
		
		testCreateSingle();
		testGet();
		createExtraPeople();
		testFilter();
		testEditExisting();
		testCreateForeignKeyObject();
		testGetForeignKeyObject();
		testAddManyToManyObject();
		testGetManyToManyRelationship();
		testDelete();
		
	}
	
	public static void testCreateSingle() {
		
		System.out.println("Test Create Single...");
		
		System.out.println("\tCreating Person Ian Kirpatrick...");
		
		ArrayList<WhereCondition> conditions = new ArrayList<WhereCondition>();
		WhereCondition name = new WhereCondition("name", WhereCondition.EQUALS, "Ian Kirkpatrick");
		WhereCondition age = new WhereCondition("age", WhereCondition.EQUALS, 19);
		conditions.add(name);
		conditions.add(age);

		System.out.println("\tPerson filtered by 'Ian Kikrpatrick' and 19: " + Person.objects.filter(conditions));
		
		if (Person.objects.filter(conditions).count() > 0) {
			System.out.println("\tSorry, Ian Kirkpatrick already exists in the database.");
			return;
		}
		
		try {
			Person ian = Person.objects.create(conditions);
		} catch (ObjectAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fails.add("TestQuerySets.testCreateSingle");
		}
		
		if (Person.objects.filter(conditions).count() != 1) {
			System.out.println("\tSorry, Ian Kirkpatrick was not successfully created.");
			fails.add("TestQuerySets.testCreateSingle");
			return;
		} else {
			System.out.println("\tPerson filtered by 'Ian Kikrpatrick': " + Person.objects.filter(conditions));
			System.out.println("\tCreated Person Ian Kirkpatrick");
		}
		
	}
	
	public static void testGet() {
		
		System.out.println("Test Get Person...");
		
		System.out.println("\tGetting Ian Kirkpatrick...");

		ArrayList<WhereCondition> conditions = new ArrayList<WhereCondition>();
		WhereCondition name = new WhereCondition("name", WhereCondition.EQUALS, "Ian Kirkpatrick");
		conditions.add(name);
		
		Person ian = null;
		try {
			ian = Person.objects.get(conditions);
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("\tSorry, Ian Kirkpatrick does not exist.");
			fails.add("TestQuerySets.testGet");
		}
		
		System.out.println("\tGot Ian Kirkpatrick: " + ian);
		
	}
	
	public static void createExtraPeople() {
		System.out.println("\tCreating Person...");
		
		Person wynton = new Person();
		wynton.name.set("Wynton Kirkpatrick");
		wynton.age.set(19);
		
		Person josh = new Person();
		josh.name.set("Josh Jackson");
		josh.age.set(20);
		
		System.out.println("\t Wynton Kirkpatrick");
		wynton.save();
		System.out.println("\t Josh Jackson");
		josh.save();
	}
	
	public static void testFilter() {
		
		System.out.println("Test Filter...");
		
		ArrayList<WhereCondition> conditions = new ArrayList<WhereCondition>();
		WhereCondition name = new WhereCondition("age", WhereCondition.EQUALS, 19);
		conditions.add(name);
		
		QuerySet<Person> people = Person.objects.filter(conditions);

		ArrayList<WhereCondition> conditions1 = new ArrayList<WhereCondition>();
		WhereCondition name1 = new WhereCondition("name", WhereCondition.EQUALS, "Ian Kirkpatrick");
		conditions1.add(name1);
		
		ArrayList<WhereCondition> conditions2 = new ArrayList<WhereCondition>();
		WhereCondition name2 = new WhereCondition("name", WhereCondition.EQUALS, "Wynton Kirkpatrick");
		conditions2.add(name2);

		ArrayList<WhereCondition> conditions3 = new ArrayList<WhereCondition>();
		WhereCondition name3 = new WhereCondition("name", WhereCondition.EQUALS, "Josh Jackson");
		conditions3.add(name3);
		
		if (people.filter(conditions1).count() == 1 && people.filter(conditions2).count() == 1 && people.filter(conditions3).count() == 0) {
			System.out.println("\tFiltered people by age=19: " + people);
		} else {
			System.out.println("\tSorry, filter did not work properly. It returned: " + people);
			fails.add("TestQuerySets.testFilter");
		}
		
	}
	
	public static void testEditExisting() {
		
		System.out.println("Test Edit...");
		
		Person ian = null;
		
		ArrayList<WhereCondition> conditions = new ArrayList<WhereCondition>();
		WhereCondition name = new WhereCondition("name", WhereCondition.EQUALS, "Ian Kirkpatrick");
		conditions.add(name);
		
		try {
			ian = Person.objects.get(conditions);
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fails.add("TestQuerySets.testEditExisting");
		}
		
		ian.age.set(20);
		ian.save();
		
		ian = null;
		
		try {
			ian = Person.objects.get(conditions);
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fails.add("TestQuerySets.testEditExisting");
		}
		
		if (ian == null || ian.age.val() != 20) {
			System.out.println("\tSorry, ian's age did not change correctly.");
			fails.add("TestQuerySets.testEditExisting");
		} else {
			System.out.println("\tChanged Ian Kirkpatrick's age: " + ian.age.val());
		}
		
	}
	
	public static void testDelete() {
		
		System.out.println("Test Delete...");
		
		System.out.println("\tDeleting Person Ian Kirkpatrick...");
		
		ArrayList<WhereCondition> conditions = new ArrayList<WhereCondition>();
		WhereCondition name = new WhereCondition("name", WhereCondition.EQUALS, "Ian Kirkpatrick");
		conditions.add(name);
		
		System.out.println("\tPerson filtered by 'Ian Kikrpatrick': " + Person.objects.filter(conditions));
		
		if (Person.objects.filter(conditions).count() == 0) {
			System.out.println("\tSorry, Ian Kirkpatrick does not exists in the database.");
			fails.add("TestQuerySets.testDelete");
			return;
		} else if (Person.objects.filter(conditions).count() > 1) {
			System.out.println("\tSorry, Ian Kirkpatrick exists several times in the database.");
			fails.add("TestQuerySets.testDelete");
			return;
		}
		
		try {
			Person.objects.delete(conditions);
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fails.add("TestQuerySets.testDelete");
		}
		
		if (Person.objects.filter(conditions).count() != 0) {
			System.out.println("\tSorry, Ian Kirkpatrick was not successfully deleated.");
			fails.add("TestQuerySets.testDelete");
			return;
		} else {
			System.out.println("\tPerson filtered by 'Ian Kikrpatrick': " + Person.objects.filter(conditions));
			System.out.println("\tDeleted Person Ian Kirkpatrick");
		}
		
	}
	
	public static void testGetForeignKeyObject() {
		
		System.out.println("Test Get ForeignKey realtionship...");
		
		Person ian = null;
		
		ArrayList<WhereCondition> conditions = new ArrayList<WhereCondition>();
		WhereCondition name = new WhereCondition("name", WhereCondition.EQUALS, "Ian Kirkpatrick");
		conditions.add(name);
		
		try {
			ian = Person.objects.get(conditions);
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fails.add("TestQuerySets.testGetForeignKeyObject");
		}
		
		Person father = null;
		try {
			father = ian.father.getRef();
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (father.name.val().equals("Daniel Kirkpatrick")) {
			System.out.println("\tGot Ian Kirkpatrick's father: " + father);
		} else {
			System.out.println("\tSorry, Ian Kirkpatrick's father was not retreived correctly: " + father);
			fails.add("TestQuerySets.testGetForeignKeyObject");
		}
		
	}
	
	public static void testCreateForeignKeyObject() {
		
		System.out.println("Test Get ForeignKey realtionship...");
		
		Person ian = null;
		
		ArrayList<WhereCondition> conditions = new ArrayList<WhereCondition>();
		WhereCondition name = new WhereCondition("name", WhereCondition.EQUALS, "Ian Kirkpatrick");
		conditions.add(name);
		
		try {
			ian = Person.objects.get(conditions);
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fails.add("TestQuerySets.testGetForeignKeyObject");
		}
		
		Person father = new Person();
		father.age.set(50);
		father.name.set("Daniel Kirkpatrick");
		father.save();
		
		ian.father.setObject(father);
		ian.save();
		
		ian = null;
		
		try {
			ian = Person.objects.get(conditions);
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fails.add("TestQuerySets.testCreateForeignKeyObject");
		}
		
		Person fRef = null;
		try {
			fRef = ian.father.getRef();
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (fRef.name.val().equals("Daniel Kirkpatrick")) {
			System.out.println("\tSet Ian Kirkpatrick's father: " + father);
		} else {
			System.out.println("\tSorry, Ian Kirkpatrick's father was not saved correctly: " + father);
			fails.add("TestQuerySets.testCreateForeignKeyObject");
		}
		
	}
	
	public static void testAddManyToManyObject() {
		
		System.out.println("Test add ManyToManyField...");
		
		Person ian = null;
		
		ArrayList<WhereCondition> conditions = new ArrayList<WhereCondition>();
		WhereCondition name = new WhereCondition("name", WhereCondition.EQUALS, "Ian Kirkpatrick");
		conditions.add(name);
		
		try {
			ian = Person.objects.get(conditions);
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Person dan = null;
		
		ArrayList<WhereCondition> conditions1 = new ArrayList<WhereCondition>();
		WhereCondition name1 = new WhereCondition("name", WhereCondition.EQUALS, "Ian Kirkpatrick");
		conditions1.add(name1);
		
		try {
			dan = Person.objects.get(conditions1);
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ian.friends.add(dan);
		} catch (ObjectAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ian = Person.objects.get(conditions);
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (ian.friends.count() != 1) {
			System.out.println("\tSorry, Ian Kirkpatrick's friends did not save correctly: " + ian.friends.all());
			fails.add("TestQuerySets.testAddManyToManyObject");
		} else {
			System.out.println("\tAdded Daniel Kirkpatrick to Ian Kirkpatrick's friends: " + ian.friends.all());
		}
		
	}
	
	public static void testGetManyToManyRelationship() {
		
		System.out.println("Test Getting Many To Many Relationship...");
		
		Person ian = null;
		
		ArrayList<WhereCondition> conditions = new ArrayList<WhereCondition>();
		WhereCondition name = new WhereCondition("name", WhereCondition.EQUALS, "Ian Kirkpatrick");
		conditions.add(name);
		
		try {
			ian = Person.objects.get(conditions);
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Person dan = null;
		
		dan = ian.friends.all().getRow(0);
		
		System.out.println("\tGot Ian Kirkpatrick's friends: " + ian.friends.all());
		
	}
}
