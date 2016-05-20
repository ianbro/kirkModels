package kirkModels.orm.backend.sync;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import iansLibrary.utilities.JSONClassMapping;
import iansLibrary.utilities.JSONMappable;
import kirkModels.config.Settings;
import kirkModels.orm.DbObject;
import kirkModels.orm.backend.sync.migrationTracking.MigrationFile;
import kirkModels.orm.backend.sync.migrationTracking.MigrationTracking;
import kirkModels.orm.backend.sync.queries.AddForeignKey;
import kirkModels.orm.backend.sync.queries.AlterTable;
import kirkModels.orm.backend.sync.queries.CreateTable;
import kirkModels.orm.backend.sync.queries.DropTable;
import kirkModels.orm.backend.sync.queries.Operation;
import kirkModels.orm.queries.InsertQuery;
import kirkModels.orm.queries.Query;
import kirkModels.orm.queries.scripts.WhereCondition;
import kirkModels.tests.Person;
import kirkModels.utils.exceptions.ObjectAlreadyExistsException;
import kirkModels.utils.exceptions.ObjectNotFoundException;

public class Migration implements JSONMappable{
	
	public String dependsOn = null;
	public Query[] operations;
	public Query[] foreignKeyOperations;

	public Migration(String _dependsOn, Query[] _operations, Query[] _foreignKeyOperations) {
		// TODO Auto-generated constructor stub
		if (_operations != null && _operations.equals("null-value")) {
			this.dependsOn = null;
		} else {
			this.dependsOn = _dependsOn;
		}
		this.operations = _operations;
		this.foreignKeyOperations = _foreignKeyOperations;
		this.moveForeignKeyOperationsFromOperations();
	}
	
	/**
	 * creates the initial migration for {@code type}.
	 * @param type
	 */
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
		this.moveForeignKeyOperationsFromOperations();
	}
	
	public Migration(String dependsOn, boolean blank) {
		this.dependsOn = dependsOn;
		this.operations = new Query[0];
		this.foreignKeyOperations = new Query[0];
	}
	
	/**
	 * takes any foreign key operations from the array operations and moves it to the field foreignKeyOperations
	 */
	public void moveForeignKeyOperationsFromOperations(){
		ArrayList<AlterTable> foreignKeyOperationsTemp = new ArrayList<AlterTable>();
		ArrayList<Query> operationsTemp = this.getOpertations();
		for (int i = 0; i < operationsTemp.size(); i ++) {
			Query query = operationsTemp.get(i);
			if (query instanceof AlterTable) {
				for (Operation op : ((AlterTable) query).operations) {
					if (op instanceof AddForeignKey) {
						foreignKeyOperationsTemp.add((AlterTable) operationsTemp.get(i));
						operationsTemp.remove(i);
					}
				}
			} else if (query instanceof CreateTable) {
				AddForeignKey[] afk = new AddForeignKey[((CreateTable) query).foreignKeys.length];
				for (int j = 0; j < afk.length; j++) {
					afk[j] = new AddForeignKey(((CreateTable) query).foreignKeys[j]);
				}
				if (afk.length > 0){
					foreignKeyOperationsTemp.add(new AlterTable(query.dbName, query.tableName, afk));
				}
			}
		}
		this.foreignKeyOperations = new Query[foreignKeyOperationsTemp.size()];
		this.foreignKeyOperations = foreignKeyOperationsTemp.toArray(this.foreignKeyOperations);
		this.operations = operationsTemp.toArray(this.operations);
	}
	
	public void run() throws SQLException {
		for (Query query : this.operations) {
			System.out.println("\t" + query.shortDescription);
			query.run();
		}
	}

	@Override
	public Constructor getJsonConstructor() {
		// TODO Auto-generated method stub
		try {
			return this.getClass().getConstructor(new Class[]{
					String.class,
					Query[].class,
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
				"operations",
				"foreignKeyOperations",
		};
	}
	
	public static Migration getMigrationFromFile(File source) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FileNotFoundException {
		Scanner scnr = new Scanner(source).useDelimiter("\\Z");
		String jsonVal = scnr.next();
		scnr.close();
		JSONObject json;
		try {
			json = (JSONObject) new JSONParser().parse(jsonVal);
			return (Migration) JSONClassMapping.jsonAnyToObject(json);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<Query> getOpertations() {
		ArrayList<Query> list = new ArrayList<Query>();
		for (int i = 0; i < operations.length; i++) {
			list.add(operations[i]);
		}
		return list;
	}
	
	public ArrayList<AlterTable> getForeignKeyOpertations() {
		ArrayList<AlterTable> list = new ArrayList<AlterTable>();
		for (int i = 0; i < foreignKeyOperations.length; i++) {
			list.add((AlterTable) foreignKeyOperations[i]);
		}
		return list;
	}
	
	public String toString() {
		String str = "Depends on: " + this.dependsOn + "\n";
		for (int i = 0; i < this.operations.length; i++) {
			Query op = this.operations[i];
			str = str + "\n" + op.toString();
		}
		str = str + "\n====================================================================";
		return str;
	}

}
