/*=======================================================
  NQueens main class.

  Solves the n-Queen problem using a Genetic Algorithm.
  Example solution for n=8:
    .-.-Q-.-
    -.Q.-.-.
    Q-.-.-.-
    -.-.-.Q.
    .Q.-.-.-
    -.-.-.-Q
    .-.-.Q.-
    -.-Q-.-.
    
=========================================================*/
import java.io.*;

public class NQueens
{
  //-------------------
  // Main method
  //-------------------
  static public void main(String[] args)
  { 
    // Check for correct number of args
    if (args.length < 2 || args.length > 3)
    {
      printUsage();
      return;
    }
    
    // Set config file
    String configFile = "config.txt";
    if (args.length == 3)
      configFile = args[2];
    
    // Create & configure GA
    GeneticAlgorithm ga = new GeneticAlgorithm();
    try
    {
      ga.setAttribute("grid_size", args[0]);
      ga.setAttribute("max_time", args[1]);
      configureGA(ga, configFile);
    }
    catch (Exception e)
    {
      printUsage();
      return;
    }
    
    // Evolve away...
    ga.run();
  }
  

  //---------------------------
  // Output the program usage
  //---------------------------
  static protected void printUsage()
  {
    System.out.println("Usage: java NQueens <n> <max_time> [config_file]");
    System.out.println("  n           - grid size, eg 8 for 8x8 grid.");
    System.out.println("  max_time    - max time to evolve solution, in seconds.");
    System.out.println("  config_file - GA config file (optional, defaults to config.txt).");
  }
  
  
  //-----------------------------------
  // Configure the GA
  // - based on the given config file
  //-----------------------------------
  static protected void configureGA(GeneticAlgorithm ga, String configFile)
  {
    try
    {
      Reader in = new BufferedReader(new FileReader(configFile));
      
      // Parse each line
      StreamTokenizer tok = new StreamTokenizer(in);
      tok.wordChars(0x20, 0x7E); 
      tok.lowerCaseMode(true);
      tok.commentChar('#');
      tok.eolIsSignificant(true);
      for (int ttype = tok.nextToken();
           ttype != StreamTokenizer.TT_EOF;
           ttype = tok.nextToken())
      {
        if (ttype == StreamTokenizer.TT_EOL)
          continue;
        
        String rawLine = tok.sval;
        
        String line = rawLine.replaceAll("\\s", "");
        String[] values = line.split("=");
        switch (values.length)
        {
          case 0:
            continue;
          case 1:
            throw new RuntimeException("Error parsing line (key without value):\n" + line);
          case 2:
            ga.setAttribute(values[0], values[1]);
            break;
          default:
            throw new RuntimeException("Error parsing line (too many tokens):\n" + line);
        }
      }
    }
    catch (Exception e)
    {
      System.out.println(e);
      throw new RuntimeException("Could not read config file: " + configFile);
    }
  }

}