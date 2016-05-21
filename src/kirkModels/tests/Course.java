package kirkModels.tests;

import kirkModels.fields.CharField;
import kirkModels.orm.Model;
import kirkModels.orm.QuerySet;

public class Course extends Model {

	public QuerySet<Course> objects;
	
	public CharField name = new CharField("name", false, null, true, 100);
	public CharField designator = new CharField("designator", false, null, true, 100);
	
	
}
