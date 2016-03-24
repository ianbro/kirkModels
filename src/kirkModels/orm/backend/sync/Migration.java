package kirkModels.orm.backend.sync;

import java.lang.reflect.Constructor;

import iansLibrary.utilities.JSONMappable;
import kirkModels.config.Settings;
import kirkModels.orm.DbObject;
import kirkModels.orm.backend.sync.queries.CreateTable;
import kirkModels.queries.Query;
import kirkModels.tests.Person;

public class Migration implements JSONMappable{
	
	public String dependsOn = null;
	public Query[] operations;

	public Migration(String _dependsOn, Query[] _operations) {
		// TODO Auto-generated constructor stub
		if (_operations != null && _operations.equals("null-value")) {
			this.dependsOn = null;
		} else {
			this.dependsOn = _dependsOn;
		}
		this.operations = _operations;
	}
	
	public Migration(Class<? extends DbObject> type) {
		this.dependsOn = null;
		try {
			this.operations = new Query[]{
					new CreateTable(Settings.database.name, type.newInstance()),
			};
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.operations = new Query[0];
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.operations = new Query[0];
		}
	}

	@Override
	public Constructor getJsonConstructor() {
		// TODO Auto-generated method stub
		try {
			return this.getClass().getConstructor(new Class[]{
					String.class,
					Query[].class,
			});
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String[] getConstructorFieldOrder() {
		// TODO Auto-generated method stub
		return new String[]{
				"dependsOn",
				"operations"
		};
	}

}
