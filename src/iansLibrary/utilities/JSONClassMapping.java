package iansLibrary.utilities;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.management.Query;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import kirkModels.config.Settings;
import kirkModels.fields.SavableField;
import kirkModels.orm.backend.sync.queries.CreateTable;

public final class JSONClassMapping {
		
		public static Object jsonFieldToObject(String key, Object jsonVal) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			Object toReturn = null;
			
			String type = key.split("#")[1];
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
		
		public static Object jsonAnyToObject(Object jsonVal) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			Object toReturn = null;
			
			if (jsonVal == null) {
				toReturn = null;
			} else if (jsonVal.getClass().isPrimitive() || jsonVal instanceof String) {
				toReturn = jsonVal;
			} else if (jsonVal instanceof JSONArray) {
				toReturn = jsonArrayToArray((JSONArray) jsonVal);
			} else if (jsonVal instanceof JSONObject) {
				toReturn = jsonObjectToObject((JSONObject) jsonVal);
			}
			return toReturn;
		}
		
		public static Object jsonObjectToObject(JSONObject jsonVal) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			Object toReturn = null;
			
			//figure out how to see if a class is a subclass of Query
			System.out.println(Query.class.isAssignableFrom(Class.forName((String) jsonVal.get("type"))));
			if (Query.class.isAssignableFrom(Class.forName((String) jsonVal.get("type")))) {
				jsonVal.put("1#java.lang.String#_dbName", Settings.database.schema);
			}
			
			String className = (String) jsonVal.get("type");
			HashMap<Class<?>, Object> vals = new HashMap<Class<?>, Object>();
			Class[] paramTypes = new Class[jsonVal.keySet().size()-1];
			int i = 0;
			for (Object key : jsonVal.keySet()) {
				if (!((String) key).equals("type")) {
					//set value at key to attribute of toReturn.
					Class type = Class.forName(((String) key).split("#")[1]);
					Object value = jsonAnyToObject(jsonVal.get(key));
					paramTypes[i] = type;
					vals.put(type, value);
					i ++;
				}
			}
			Constructor c = Class.forName(className).getConstructor(paramTypes);
			toReturn = c.newInstance(vals.entrySet().toArray());
			
			return toReturn;
		}
		
		public static Object jsonArrayToArray(JSONArray jsonVal) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			Object toReturn = null;
			
			String dataType = jsonVal.get(0).toString();
			Class typeClass = Class.forName(dataType);
			toReturn = Array.newInstance(typeClass, jsonVal.size());
			
			for (int i = 1; i < jsonVal.size(); i ++) {
				Object javaVal = jsonAnyToObject(i);
				Array.set(toReturn, i, javaVal);
			}
			
			return toReturn;
		}

}
