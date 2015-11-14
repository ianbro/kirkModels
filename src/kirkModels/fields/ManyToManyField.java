package kirkModels.fields;

import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.orm.QuerySet;
import kirkModels.orm.Savable;

public class ManyToManyField<T extends DbObject, R extends DbObject> extends DbObject implements Savable {

	public ForeignKey<T> reference1;
	public String hostModel;
	
	public ForeignKey<R> reference2;
	public String refModel;
	
	public Integer hostId;
	
	public ManyToManyField(Class<T> hostModel, Class<R> refModel){
		this.reference1 = new ForeignKey<T>(hostModel.getSimpleName().toLowerCase() + "_id", hostModel, false, null, false, "NO ACTION");
		this.reference2 = new ForeignKey<R>(refModel.getSimpleName().toLowerCase() + "_id", refModel, false, null, false, "NO ACTION");
		this.hostModel = hostModel.getName();
		this.refModel = refModel.getName();
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
	public void create(HashMap<String, Object> kwargs, Class<T> type) {
		// TODO Auto-generated method stub
		this.objects.create(kwargs, null);
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
		return this.objects.get(kwargs);
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
		return this.objects.getOrCreate(kwargs);
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
}
