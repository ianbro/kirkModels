package kirkModels.tests;

import kirkModels.fields.CharField;
import kirkModels.orm.DbObject;
import kirkModels.orm.QuerySet;

public class Course extends DbObject {

	public QuerySet<Course> objects;
	
	public CharField name = new CharField("name", false, null, true, 100);
	public CharField designator = new CharField("designator", false, null, true, 100);
	
	
}
