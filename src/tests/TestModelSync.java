package tests;

import java.sql.Connection;
import java.util.ArrayList;

import kirkModels.db.sync.AddField;
import kirkModels.db.sync.AlterFieldName;
import kirkModels.db.sync.DBSynchronization;
import kirkModels.db.sync.DropField;
import kirkModels.db.sync.SQLOperation;
import kirkModels.objects.CharField;

public class TestModelSync extends DBSynchronization {

	public TestModelSync(Connection conn){
		super(conn);
		this.modelName = "testmodel";
		this.operations = new ArrayList<SQLOperation>(){{
			add(new AddField("lastName", new CharField("lastName", false, null, false, 20)));
		}};
	}
}
