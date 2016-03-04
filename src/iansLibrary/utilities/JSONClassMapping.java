package iansLibrary.utilities;

import java.lang.reflect.Array;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public final class JSONClassMapping {
		
		public static Object jsonFieldToObject(String key, Object jsonVal) throws ClassNotFoundException {
			Object toReturn = null;
			
			String type = key.split("|")[1];
			Class typeClass = Class.forName(type);
			if (type.substring(0, 2).equals("[L")) {
				toReturn = Array.newInstance(typeClass, ((JSONArray) jsonVal).size());
				
				for (int i = 0; i < ((JSONArray) jsonVal).size(); i ++) {
					Object javaVal = jsonAnyToObject(i);
					Array.set(toReturn, i, javaVal);
				}
			}
			
			return toReturn;
		}
		
		public static Object jsonAnyToObject(Object jsonVal) throws ClassNotFoundException {
			Object toReturn = null;
			
			if (jsonVal instanceof JSONArray) {
				toReturn = jsonArrayToArray(jsonVal);
			}
			
			return toReturn;
		}
		
		public static Object jsonObjectToObject(JSONObject jsonVal) {
			Object toReturn = null;
			
			String className = (String) jsonVal.get("type");
			for (Object key : jsonVal.keySet()) {
				if (!((String) key).equals("type")) {
					//set value at key to attribute of toReturn.
				}
			}
			
			return toReturn;
		}
		
		public static Array jsonArrayToArray(Object jsonVal) throws ClassNotFoundException {
			Array toReturn = null;
			
			String dataType = ((JSONArray) jsonVal).get(0).toString();
			Class typeClass = Class.forName(dataType);
			toReturn = (Array) Array.newInstance(typeClass, ((JSONArray) jsonVal).size());
			
			for (int i = 1; i < ((JSONArray) jsonVal).size(); i ++) {
				Object javaVal = jsonAnyToObject(i);
				Array.set(toReturn, i, javaVal);
			}
			
			return toReturn;
		}

}
