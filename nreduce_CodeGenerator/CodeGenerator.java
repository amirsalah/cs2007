import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StreamTokenizer;

/* Generate binding code for ELC and foreign c functions */

public class CodeGenerator {

	//-------------------
	// Main method
	//-------------------
	public static void main(String[] args) {
	    // Check for correct number of args
	    if (args.length < 1 || args.length > 2)
	    {
	        printUsage();
	        return;
	    }
	    
	    // Check if the definition file exist
	    String def_file = args[0];
	    if(!fileExist(def_file)){
	    	System.out.println("File: " + def_file + " does not exist");
	    } else {
	        // Read the file
	        
	    }
		
	}
	
	//---------------------------
	// Output the program usage
	//---------------------------
	static protected void printUsage()
	{
		System.out.println("Usage: java CodeGen [definition_file]");
	    System.out.println("  definition_file - define function prototypes, etc.");
	}
	
	static protected boolean fileExist(String fileName){
		return (new File(fileName)).exists();
	}
	  

}
