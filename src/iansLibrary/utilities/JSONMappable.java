package iansLibrary.utilities;

import java.lang.reflect.Constructor;

public interface JSONMappable {
	
	public Constructor getJsonConstructor();
	
	public String[] getConstructorFieldOrder();

}
