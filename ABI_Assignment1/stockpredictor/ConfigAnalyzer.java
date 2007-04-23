package stockpredictor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Hashtable;
import java.io.IOException;


public class ConfigAnalyzer {
	/**
	 * Analyze a config file, save pairs of parameters and values to a hashtable
	 * @param configFile the config file to be analyized
	 */
	protected ConfigAnalyzer(String configFile)
	{
		try
	    {
			Reader input = new BufferedReader(new FileReader(configFile));
	    
			// Parse each line
			StreamTokenizer tok = new StreamTokenizer(input);
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
	        		throw new RuntimeException("Error: key without value. \n" + line);
	        	case 2:
	        		parameters.put(values[0], values[1]);
	        		break;
	        	default:
	        		throw new RuntimeException("Error: too many tokens. \n" + line);
	    		}
	    	}
	    }
	    catch (IOException e)
	    {
	      System.out.println(e);
	      throw new RuntimeException("Error: read config file: " + configFile);
	    }
	  }
	
	/**
	 * Set algorithm parameters
	 * @param key parameters
	 * @param value value of the corresponding parameter
	 */
/*	public void setAttribute(String key, String value)
	{
		try
	    {
	      if (key.equals("grid_size"))
	        gridSize = Integer.parseInt(value);
	      else if (key.equals("max_time"))
	        maxTime = Integer.parseInt(value);
	      else if (key.equals("population_size"))
	        populationSize = Integer.parseInt(value);
	      else if (key.equals("max_generations"))
	        maxGenerations = Integer.parseInt(value);
	      else if (key.equals("selection_type"))
	        selectionType = value;
	      else if (key.equals("crossover_type"))
	        crossoverType = value;
	      else if (key.equals("crossover_rate"))
	        crossoverRate = Double.parseDouble(value);
	      else if (key.equals("mutation_type"))
	        mutationType = value;
	      else if (key.equals("mutation_rate"))
	        mutationRate = Double.parseDouble(value);
	      else
	        throw new RuntimeException("Unknown tag: " + key);
	    }
	    catch (Exception e)
	    {
	        System.out.println(e);
	        throw new RuntimeException("Error setting GA attribute: [" + key + ", " + value + "]");
	    }
	}
*/
	private Hashtable<String, String> parameters = new Hashtable<String, String>();
}
