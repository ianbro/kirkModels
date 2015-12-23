package kirkModels.fields;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.config.Settings;
import kirkModels.orm.QuerySet;
import kirkModels.orm.Savable;

public class ManyToManyField<T extends DbObject, R extends DbObject> extends DbObject implements Savable<R> {

	public ForeignKey<T> reference1; // label: "host_<T>_id"
	public String hostModel;
	
	public ForeignKey<R> reference2; // label: "reference_<R>_id"
	public String refModel;
	
	public Integer hostId;
	public String tableLabel;
	
	public QuerySet<R> objectSet;
	
	/**
	 * This is the constructor for the table and class definition. This does not cantain any actual relationships. to instantiate a relationship, use the constructor with a parent many2many field and and instance passed to it.
	 * @param host
	 * @param refModel
	 */
	public ManyToManyField(String label, DbObject host, Class<R> refModel){
		this.hostModel = host.getClass().getName();
		this.refModel = refModel.getName();
		
		String firstTable = host.getClass().getSimpleName().toLowerCase();
		String refTable = refModel.getSimpleName().toLowerCase();
		this.tableLabel = label + "__" + firstTable + "___" + refTable;
		
		this.reference1 = new ForeignKey<T>("host_" + firstTable + "_id", (Class<T>) host.getClass(), false, null, false, "NO ACTION");
		this.reference2 = new ForeignKey<R>("reference_" + refTable + "_id", refModel, false, null, false, "NO ACTION");
	}
	
	/**
	 * This is an instance for an actual relationship. to asave it, call this.saveRelationship. don't use the following methods:
	 * <br>
	 * <br>
	 * * all
	 * <br>
	 * * filter
	 * <br>
	 * * create
	 * <br>
	 * * get
	 * <br>
	 * * getOrCreate
	 * <br>
	 * <br>
	 * These methods should not be used because it is a specific relationship... not a single one.
	 * <br>
	 * <br>
	 * <br>
	 * <br>
	 * @param parent
	 * @param instance
	 */
	public ManyToManyField(){
		this.reference1 = new ForeignKey<T>();
		this.reference2 = new ForeignKey<R>();
	}
	
	public void getObjects() {
		QuerySet<R> values = null;
		ArrayList<Integer> ids = new ArrayList<>();
		Class<R> refClass = null;
		try {
			refClass = (Class<R>) Class.forName(this.refModel);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Class m2mClass = this.getClass();
		
		QuerySet<ManyToManyField<T, R>> tempQ = new QuerySet<ManyToManyField<T, R>>(m2mClass, new HashMap<String, Object>(){{
			put(reference1.label + "::=", hostId);
		}});
		
		HashMap<String, Object> args = new HashMap<String, Object>();
		
		try {
			args.put("table_label", Class.forName(refModel).getSimpleName().toLowerCase());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			while(tempQ.results.next()){
				ids.add(tempQ.results.getInt("id"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		args.put("id::in", ids);
		
		values = new QuerySet<R>(refClass, args);
		
		this.objectSet = values;
	}
	
	protected ManyToManyField<T, R> saveRelationship(R instance){
		ManyToManyField<T, R> newRelationship = new ManyToManyField<>();
		newRelationship.reference1.set(this.hostId);
		newRelationship.reference2.set(instance.id.val());
		Settings.database.dbHandler.insertInto(newRelationship);
		return newRelationship;
	}
	
	protected ManyToManyField<T, R> getRelationship(R instance){
		ManyToManyField<T, R> rel = null;
		
		try {
			rel = (ManyToManyField<T, R>) this.objects.get(new HashMap<String, Object>(){{}});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rel;
	}
	
	protected void deleteReleationship(R instance){
		ManyToManyField<T, R> relToDelete = this.getRelationship(instance);
		Settings.database.dbHandler.deleteFrom(instance);
	}
	
	protected QuerySet<ManyToManyField<T, R>> getAllRelationships(){
		int id = this.reference1.val();
		
		//get all relationships from this table including other host relationships
		QuerySet<ManyToManyField<T, R>> allRelationships = new QuerySet<ManyToManyField<T, R>>((Class<ManyToManyField<T, R>>) this.getClass());
		//filter down to just the ones related to this reference1
		allRelationships = allRelationships.filter(new HashMap<String, Object>(){{ put("reference1::=", id); }});
		
		return allRelationships;
	}

	@Override
	public QuerySet<R> all() {
		return this.objectSet.all();
	}

	@Override
	public R create(HashMap<String, Object> kwargs) {
		return this.objectSet.create(kwargs);
	}

	@Override
	public R get(HashMap<String, Object> kwargs) throws SQLException {
		return this.objectSet.get(kwargs);
	}

	@Override
	public QuerySet<R> getOrCreate(HashMap<String, Object> kwargs) throws SQLException {
		return this.objectSet.getOrCreate(kwargs);
	}

	@Override
	public QuerySet<R> filter(HashMap<String, Object> kwargs) {
		return this.objectSet.filter(kwargs);
	}

	@Override
	public void delete(HashMap<String, Object> kwargs) throws Exception {
		this.objectSet.delete(kwargs);
	}
	
	public void setHostId(int id){
		this.reference1.set(id);
		this.hostId = id;
	}

	@Override
	public void initializeManyToManyFields() {}

	@Override
	public int count() {
		// TODO Auto-generated method stub
		return 0;
	}
}
