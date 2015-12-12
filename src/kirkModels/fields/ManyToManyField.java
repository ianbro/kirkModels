package kirkModels.fields;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.config.Settings;
import kirkModels.orm.QuerySet;
import kirkModels.orm.Savable;

public class ManyToManyField<T extends DbObject, R extends DbObject> extends DbObject implements Savable {

	public ForeignKey<T> reference1;
	public String hostModel;
	
	public ForeignKey<R> reference2;
	public String refModel;
	
	public Integer hostId;
	public String tableLabel;
	
	public QuerySet<R> objects;
	
	public ManyToManyField(DbObject host, Class<R> refModel){
		this.hostModel = host.getClass().getName();
		this.refModel = refModel.getName();
		this.reference1 = new ForeignKey<T>(host.getClass().getSimpleName().toLowerCase() + "_id", (Class<T>) host.getClass(), false, null, false, "NO ACTION");
		this.reference2 = new ForeignKey<R>(refModel.getSimpleName().toLowerCase() + "_id", refModel, false, null, false, "NO ACTION");
		this.hostId = host.id.val();
	}
	
	public ArrayList<Integer> getObjects() {
		ArrayList<R> values = new ArrayList<R>();
		
		QuerySet<ManyToManyField<T, R>> tempQ = Settings.database.dbHandler.selectFrom(this.getClass(), new HashMap<String, Object>(){{
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
				args.put("id", tempQ.results.getInt("id"));
				QuerySet<R> ref = Settings.database.dbHandler.selectFrom((Class<R>) Class.forName(this.refModel), args);
//				Class.forName(refModel).
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.objects = objects;
		return null;
	}

	@Override
	public QuerySet all() {
		String hostModel = this.hostModel;
		Integer id = this.hostId;
		try {
			QuerySet refs = this.objects.filter(new HashMap<String, Object>(){{
				put(Class.forName(hostModel).getSimpleName().toLowerCase() + "_id", id);
			}});
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void create(HashMap<String, Object> kwargs) {
		// TODO Auto-generated method stub
		this.objects.create(kwargs);
	}

	@Override
	public DbObject get(HashMap<String, Object> kwargs) {
		Integer id = this.hostId;
		try {
			kwargs.put(Class.forName(this.hostModel).getSimpleName().toLowerCase() + "_id", id);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return this.objects.get(kwargs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public QuerySet getOrCreate(HashMap<String, Object> kwargs) {
		Integer id = this.hostId;
		try {
			kwargs.put(Class.forName(this.hostModel).getSimpleName().toLowerCase() + "_id", id);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return this.objects.getOrCreate(kwargs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public QuerySet filter(HashMap<String, Object> kwargs) {
		Integer id = this.hostId;
		try {
			kwargs.put(Class.forName(this.hostModel).getSimpleName().toLowerCase() + "_id", id);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.objects.filter(kwargs);
	}

	@Override
	public void delete(HashMap<String, Object> kwargs) {
		Integer id = this.hostId;
		try {
			kwargs.put(Class.forName(this.hostModel).getSimpleName().toLowerCase() + "_id", id);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.objects.delete(kwargs);
	}
	
	public void setHostId(int id){
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
