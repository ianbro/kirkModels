package kirkModels.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.DbObject;

public class QuerySet{
	
	public ResultSet results;
	
	public QuerySet(ResultSet results){
		this.results = results;
	}
	
	public DbObject get(int i) throws SQLException{
		if(this.toRow(i)){
			int id = this.results.getInt("id");
			return DbObject.objects.get(new HashMap<String, Object>(){{put("id", id);}});
		}
		this.toRow(1);
		return null;
	}
	
//	public DbObject getById(int id) throws SQLException{
//		while(this.results.next()){
//			if(this.results.getInt("id") == id){
//				int i = 1;
//				while(true){
//					this.results.getObject(i);
//				}
//			}
//		}
//	}
	
	public boolean toRow(int i) throws SQLException{
		boolean found = false;
		int count = 0;
		while(!found){
			if(this.results.next()){
				count ++;
			}
			else{
				break;
			}
			if(count == i){
				found = true;
			}
		}
		while(this.results.previous()){}
		return found;
	}

	public int size(){
		int count = 0;
		try {
			while(this.results.next()){
				count ++;
			}
			this.toRow(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

	public String toString(){
		String str = "<";
		
		for(int i = 0; i < this.size(); i ++){
			if(i > 0){
				str = str + ", ";
			}
			
			@SuppressWarnings("unchecked")
			DbObject reference = null;
			try {
				reference = this.get(i);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			str = str + reference.toString();
		}
		
		str = str + ">";
		return str;
	}
}
