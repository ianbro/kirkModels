package iansLibrary.utilities;

import java.io.File;
import java.nio.file.Path;

public abstract class Errors {

	public static String FileNotFoundMessage(Path p){
		return "Sorry, could not find file: " + p.toString();
	}
	
	public static String FileAlreadyExistsMessage(Path p){
		return "Sorry, file " + p.toString() + " already exists.";
	}
}
