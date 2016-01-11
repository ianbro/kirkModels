package kirkModels.fields;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.DateFormat.Field;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.config.Settings;
import kirkModels.orm.DbObject;
import kirkModels.orm.QuerySet;
import kirkModels.orm.Savable;
import kirkModels.queries.DeleteQuery;
import kirkModels.queries.InsertQuery;
import kirkModels.queries.SelectQuery;
import kirkModels.queries.UpdateQuery;
import kirkModels.queries.scripts.WhereCondition;
import kirkModels.utils.exceptions.ObjectAlreadyExistsException;
import kirkModels.utils.exceptions.ObjectNotFoundException;

public class ManyToManyField<T extends DbObject, R extends DbObject> extends DbObject implements Savable<R> {

	public ForeignKey<T> reference1; // label: "host_<T>_id"
	public String hostModel;
	
	public ForeignKey<R> reference2; // label: "reference_<R>_id"
	public String refModel;
	
	public QuerySet<R> objectSet;
	public QuerySet<ManyToManyField<T, R>> objects;
	
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
		this.tableName = label + "__" + firstTable + "___" + refTable;
		
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
		ArrayList<Integer> ids = new ArrayList<Integer>();
		Class<R> refClass = null;
		try {
			refClass = (Class<R>) Class.forName(this.refModel);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Class m2mClass = this.getClass();
		
		QuerySet<ManyToManyField<T, R>> tempQ = new QuerySet<ManyToManyField<T, R>>(m2mClass, this.tableName, new ArrayList<WhereCondition>(){{
			add(new WhereCondition(reference1.label, WhereCondition.EQUALS, reference1.val()));
		}});
		
		objects = tempQ;
		
		ArrayList<WhereCondition> conditions = new ArrayList<WhereCondition>();
		
		for (ManyToManyField<T, R> relationship : tempQ) {
			int relId = relationship.reference2.val();
			ids.add(relId);
		}
		
		if(ids.size() > 1){
			
			WhereCondition c = new WhereCondition("id", WhereCondition.CONTAINED_IN, ids);
			conditions.add(c);
			
		} else if (ids.size() == 1) {
			
			WhereCondition c = new WhereCondition("id", WhereCondition.EQUALS, ids.get(0));
			conditions.add(c);
			
		} else {
			
			WhereCondition c = new WhereCondition("id", WhereCondition.EQUALS, 0);
			conditions.add(c);
			
		}
		
		values = DbObject.getObjectsForGenericType(refClass).filter(conditions);
		values.conditions = new ArrayList<WhereCondition>();
		
		this.objectSet = values;
	}
	
	public SavableField getField(String fieldName){
		
		SavableField field = null;
		
		if (fieldName.contains("reference_")) {
			field =  this.reference2;
		} else if (fieldName.contains("host_")){
			field =  this.reference1;
		} else if (fieldName.equals("id")){
			field =  this.id;
		} else {
			field =  super.getField(fieldName);
		}
		
		return field;
	}
	
	public QuerySet<R> refClassObjects(){
		try {
			return (QuerySet<R>) DbObject.getObjectsForGenericType((Class<T>) Class.forName(this.refModel));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public int getNewId(ManyToManyField instance){
		int newId = this.count() + 1;
		boolean idWorks = false;
		
		while (!idWorks){
			newId ++;
			
			ManyToManyField<T, R> rel = null;
			
			WhereCondition c = new WhereCondition("id", WhereCondition.EQUALS, newId);
			
			try {
				rel = this.objects.get(new ArrayList<WhereCondition>(){{
					add(c);
				}});
			} catch (ObjectNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (rel == null) {
				// not found so id is unique
				idWorks = true;
				break;
			}
		}
		
		return newId;
	}
	
	protected ManyToManyField<T, R> saveRelationship(R instance){
		ManyToManyField<T, R> newRelationship = new ManyToManyField<>();
		
		// need to find total number of relationships in the m2m table and use that for new id
		SelectQuery allRelsResults = new SelectQuery(this.tableName, new ArrayList<WhereCondition>(){{}});
		try {
			allRelsResults.run();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		int newId = this.getNewId(newRelationship);
		
		newRelationship.id.set(newId);
		
		newRelationship.tableName = this.tableName;
		
		newRelationship.reference1.set(this.reference1.val());
		newRelationship.reference1.label = this.reference1.label;
		
		newRelationship.reference2.set(instance.id.val());
		newRelationship.reference2.label = this.reference2.label;
		
		InsertQuery query = new InsertQuery(newRelationship);
		try {
			query.run();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newRelationship;
	}
	
	protected ManyToManyField<T, R> getRelationship(ManyToManyField<T, R> parentFieldDef, R instance){
		ManyToManyField<T, R> rel = null;
		
		try {
			rel = (ManyToManyField<T, R>) this.objects.get(new ArrayList<WhereCondition>(){{
				add(new WhereCondition(parentFieldDef.reference2.label, WhereCondition.EQUALS, instance.id.val()));
			}});
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rel;
	}
	
	protected void deleteRelationship(R instance){
		ManyToManyField<T, R> relToDelete = this.getRelationship(this, instance);
		
		DeleteQuery query = new DeleteQuery(this.tableName, new ArrayList<WhereCondition>(){{
			add(new WhereCondition("id", WhereCondition.EQUALS, relToDelete.id.val()));
		}});
		
		try {
			query.run();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected QuerySet<ManyToManyField<T, R>> getAllRelationships(){
		int id = this.reference1.val();
		
		//get all relationships from this table including other host relationships
		QuerySet<ManyToManyField<T, R>> allRelationships = new QuerySet<ManyToManyField<T, R>>((Class<ManyToManyField<T, R>>) this.getClass());
		//filter down to just the ones related to this reference1
		allRelationships = allRelationships.filter(new ArrayList<WhereCondition>(){{ add(new WhereCondition(reference1.label, WhereCondition.EQUALS, id)); }});
		
		return allRelationships;
	}

	@Override
	public QuerySet<R> all() {
		return this.objectSet.all();
	}

	@Override
	public R create(ArrayList<WhereCondition> conditions) throws ObjectAlreadyExistsException {
		R newInstance = this.objectSet.create(conditions);
		
		this.add(newInstance);
		
		return newInstance;
	}
	
	public R add(R instance) throws ObjectAlreadyExistsException{
		
		if(this.objectSet.filter(new ArrayList<WhereCondition>(){{
			add(new WhereCondition("id", WhereCondition.EQUALS, instance.id.val()));
		}}).count() == 0) {
		
			this.saveRelationship(instance);
			
			if (!this.objectSet.storage.contains(instance)) {
				
				this.objectSet.storage.add(instance);
				
			}
			
			return instance;
			
		} else {
			
			try {
				throw new ObjectAlreadyExistsException(Class.forName(this.refModel).getSimpleName() + " instance with id " + instance.id.val() + " already is related to this object.");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return instance;
		}
		
	}

	@Override
	public R get(ArrayList<WhereCondition> conditions) throws ObjectNotFoundException {
		return this.objectSet.get(conditions);
	}

	@Override
	public QuerySet<R> getOrCreate(ArrayList<WhereCondition> conditions) {
		QuerySet<R> set = this.filter(conditions);
		
		if (set.count() == 0) {
			
			try {
				this.create(conditions);
			} catch (ObjectAlreadyExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return this.filter(conditions);
			
		} else {
			return set;
		}
	}
	
	public QuerySet<R> getOrAdd(ArrayList<WhereCondition> conditions) throws SQLException {
		QuerySet<R> set = this.filter(conditions);
		
		if (set.count() == 0) {
			
			for (R instance : this.refClassObjects().filter(conditions)) {
				try {
					this.add(instance);
				} catch (ObjectAlreadyExistsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return this.filter(conditions);
			
		} else {
			return set;
		}
	}

	@Override
	public QuerySet<R> filter(ArrayList<WhereCondition> conditions) {
		return this.objectSet.filter(conditions);
	}

	@Override
	public void delete(ArrayList<WhereCondition> conditions) throws ObjectNotFoundException {
		
		QuerySet<R> instances = this.filter(conditions);
		
		for(R instance : instances){
			this.remove(instance);
			instance.delete();
		}
		
		if (instances.count() == 0) {
			try {
				throw new ObjectNotFoundException("Sorry, " + Class.forName(this.refModel).getSimpleName()
						+ " intance with conditions: " + conditions + " does not exist in this relationship.");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void remove(R instance) throws ObjectNotFoundException{
		R tempInstance = null;
		
		tempInstance = this.get(new ArrayList<WhereCondition>(){{
			add(new WhereCondition("id", WhereCondition.EQUALS, instance.id.val()));
		}});
		
		if(tempInstance != null){
			this.deleteRelationship(instance);
		} else {
			throw new ObjectNotFoundException("Sorry, " + instance.getClass().getSimpleName() + " instance with id: " + instance.id.val()
								+ " does not exists.");
		}
		
		this.objectSet.storage.remove(instance);
	}
	
	public void setHostId(int id){
		this.reference1.set(id);
	}

	@Override
	public void initializeManyToManyFields() {}

	@Override
	public int count() {
		// TODO Auto-generated method stub
		return this.objectSet.count();
	}
}
