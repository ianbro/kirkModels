package kirkModels.fields;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.config.Settings;
import kirkModels.orm.QuerySet;
import kirkModels.orm.Savable;

public class ManyToManyField<T extends DbObject, R extends DbObject> extends DbObject implements Savable<R> {

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
	
	public void getObjects() {
		QuerySet<R> values = null;
		ArrayList<Integer> ids = new ArrayList<>();
		
		QuerySet<ManyToManyField<T, R>> tempQ = new QuerySet<ManyToManyField<T, R>>(new HashMap<String, Object>(){{
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
		
		values = new QuerySet<R>(args);
		
		this.objects = values;
	}

	@Override
	public QuerySet all() {
		return this.objects.all();
	}

	@Override
	public R create(HashMap<String, Object> kwargs) {
		// TODO Auto-generated method stub
		return this.objects.create(kwargs);
	}

	@Override
	public R get(HashMap<String, Object> kwargs) throws SQLException {
		return this.objects.get(kwargs);
	}

	@Override
	public QuerySet<R> getOrCreate(HashMap<String, Object> kwargs) throws SQLException {
		return this.objects.getOrCreate(kwargs);
	}

	@Override
	public QuerySet<R> filter(HashMap<String, Object> kwargs) {
		return this.objects.filter(kwargs);
	}

	@Override
	public void delete(HashMap<String, Object> kwargs) throws Exception {
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
