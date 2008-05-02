import java.io.FileReader;
import lexer.XMLLexer;
import lexer.Yytoken;
import lexer.symbol.sym;

public class LTDriver {

    //-------------------
    // Main method
    //-------------------
    public static void main(String[] args) {
        // Check for correct number of args
        if (args.length != 2){
            printUsage();
            return;
         }

         // Set source file
         String sourceFile = args[1];
         
         // Check the arguments
         if(!isValidOption(args[0])){
             printUsage();
             return;
         }
         
         // use the lexer
         XMLLexer lexer;
         String source_dir = "source_language/";
         if(args[0].equals("-lexer")){
             try{
                 lexer = new XMLLexer(source_dir.concat(sourceFile));
                 lexer.generateOutput();
             }
             catch(java.io.IOException e){
                 System.out.println("Error. File: " + sourceFile + " cannot be solved");
                 System.exit(0);
             }
         }


    }
    
    //---------------------------
    // Output the program usage
    //---------------------------
    static protected void printUsage()
    {
      System.out.println("Usage: java LTDriver [OPTION] [SOURCE FILE]");
      System.out.println("  Options:");
      System.out.println("          -lexer        generate output from the lexer");
      System.out.println("          -parser       generate output from the parser");
      System.out.println("          SOURCE FILE   the XML file to be translated");
    }
    
    /**
     * Check if the option input by user is valid
     * @param option
     * @return true if the option is valid, false otherwise
     */
    static protected boolean isValidOption(String option){
        if(option.equals("-lexer")){
            return true;
        }
        
        if(option.equals("-parser")){
            return true;
        }
        
        return false;
    }


}
