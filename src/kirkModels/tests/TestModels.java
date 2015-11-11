package kirkModels.tests;

import kirkModels.orm.backend.scripts.PSqlScript;

public abstract class TestModels {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Person p = new Person();
		p.initializeManyToManyFields();
		p.age.set(19);
		p.name.set("Ian Kirkpatrick");
		PSqlScript a = new PSqlScript("person");
		System.out.println(a.getSaveNewInstanceString(p));
		System.out.println(a.getUpdateInstanceString(p));
	}

}
