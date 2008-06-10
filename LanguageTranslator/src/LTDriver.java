/** 
 * @author: Bo CHEN
 * student ID: 1139520
 */

import lexer.XMLLexer;
import parser.XMLParser;
import semantics.XMLSemantics;
import codegeneration.XMLCodeGen;

public class LTDriver {

    //-------------------
    // Main method
    //-------------------
    public static void main(String[] args) {
        // Check for correct number of args
        if (args.length < 1 | args.length > 2){
            printUsage();
            return;
        }

        // Check the arguments
        if(!isValidOption(args[0])){
            printUsage();
            return;
        }

        boolean manualInput;
        String sourceFile = null;
        // Set source file
        if(args.length == 1){
            manualInput = true;
        }else{
            manualInput = false;
            sourceFile = args[1];
        }
        
        
        // invoke the lexer
        if(args[0].equals("-lexer")){
            lexing(manualInput, sourceFile);
        }
        
        // invoke the parser
        XMLParser parser;
        if(args[0].equals("-parser")){
            parser = new XMLParser(sourceFile);
            parser.parsing();
        }
        
        XMLSemantics semantics;
        if(args[0].equals("-semantics") || args[0].equals("-codegen")){
            boolean noSemanticError = true;
            // Start semantic analysis
            parser = new XMLParser(sourceFile);
            if(parser.parsing()){
                System.out.println("***********************************************");
                System.out.println("Start semantic analysis");
            }
            
            semantics = new XMLSemantics(parser.getASTRoot());
            noSemanticError = semantics.semanticChecking();
            System.out.println("***********************************************");
            System.out.println("Semantic analysis finished");
            
            // Start code generation if no semantic error detected
            if(noSemanticError){
                XMLCodeGen code = new XMLCodeGen(parser.getASTRoot());
                code.genCode();
            }
        }
        

    }
    
    /**
     * Output the program usage
     */
    static protected void printUsage()
    {
      System.out.println("Usage: java LTDriver [OPTION] [SOURCE FILE]");
      System.out.println("  Options:");
      System.out.println("          -lexer        generate output from the lexer");
      System.out.println("          -parser       generate output from the parser");
      System.out.println("          -semantics    semantic analysis");
      System.out.println("          -codegen      code generation");
      System.out.println("  SOURCE FILE           the XML file to be translated");
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
        
        if(option.equals("-semantics")){
            return true;
        }
        
        if(option.equals("-codegen")){
            return true;
        }
        
        return false;
    }

    /**
     * Use the lexer
     * @param manualInput true if user will manually input the source code, false if the source file is provided
     * @param sourceFile the source file name, null if manualInput is true
     */
    static protected void lexing(boolean manualInput, String sourceFile){
        XMLLexer lexer;
        String source_dir = "../source_language/";
        
        try{
            lexer = new XMLLexer();
            if(manualInput){
                lexer.manualLexer();
            } else {
                lexer.autoLexer(source_dir.concat(sourceFile));
            }
        }
        catch(java.io.IOException e){
            System.out.println("Error. File: " + sourceFile + " cannot be solved");
            System.exit(0);
        }
    }
    
}
