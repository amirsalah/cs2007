import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConfigParser {
	private BufferedReader configFile= null;
	private int testCase = 1;
	private int popSize = 70;
	private int generation  = 1000;
	private double crossoverRate = 0.2;
	private double mutationRate = 0.002;
	private double selectionPressure = 2;
	private boolean defaultParameters = true;
	private String mutationMethod = "NonUniformMutation";
	private int numIterations = 3;
	
	public ConfigParser(String file) throws IOException{
		/* Read config.txt file from the directory */
		try{
	        configFile = new BufferedReader(new FileReader(file));
	    }
	    catch (java.io.FileNotFoundException e) {
	        System.out.println("File: '" + file + "' could not be found");
	        System.out.println("Please ensure there is a config.txt in the directory");
	        System.exit(-1);
	    }
	    
	    /* Initialize the parameters */
	    fileReader(file);
	}
	
	/* Analyse the config file, and record relevant parameters */
	private void fileReader(String file) throws IOException{
		String newLine;
		int parameters = 0;
		
		/* Read the parameters */
		for(int i=0; i<=200; i++){
			newLine = configFile.readLine();
			if(newLine.contains("Test case")){
				newLine = configFile.readLine();
				testCase = Integer.parseInt(newLine);
				if((testCase < 1) || (testCase > 12)){
					System.out.println("Wrong test case number: " + testCase);
					System.out.println("The test case should be between 1 - 12");
					System.exit(-1);
				}else{
					parameters++;
				}
			}
			
			if(newLine.contains("parameters")){
				newLine = configFile.readLine();

				if(newLine.equalsIgnoreCase("Yes")){
					defaultParameters = true;
				}else{
					if(newLine.equalsIgnoreCase("No")){
						defaultParameters = false;
					}else{
						System.out.println("Error: please input 'Yes' or 'No' to use the default parameters");
						System.out.println("Default paramenters are used now");
					}
				}		
				parameters++;
			}
			
			if(newLine.contains("Mutation method")){
				newLine = configFile.readLine();
				int mut = Integer.parseInt(newLine);
				if( mut == 1){
					mutationMethod = "NonUniformMutation";
				}else{
					if(mut == 2){
						mutationMethod = "BoundaryMutation";
					}else{
						System.out.println("Error mutation method");
						System.out.println("please input 'NonUniformMutation' or 'BoundaryMutation'");
						System.out.println("Default NonUniformMutation is used now");
					}
				}
				parameters++;
			}
			
			if(newLine.contains("population")){
				newLine = configFile.readLine();
				popSize = Integer.parseInt(newLine);
				if(popSize <= 0){
					System.out.println("Wrong population size: " + popSize);
					System.out.println("The population size should be larger than 0");
					System.exit(-1);
				}else{
					parameters++;
				}
			}
			
			if(newLine.contains("generation")){
				newLine = configFile.readLine();
				generation = Integer.parseInt(newLine);
				if(generation <= 0){
					System.out.println("Wrong number of generations: " + generation);
					System.out.println("The number of generations should be larger than 0");
					System.exit(-1);
				}else{
					parameters++;
				}
			}
			
			if(newLine.contains("crossover")){
				newLine = configFile.readLine();
				crossoverRate = Double.parseDouble(newLine);
				if((crossoverRate < 0) || (crossoverRate > 1)){
					System.out.println("Wrong cross over rate: " + crossoverRate);
					System.out.println("The cross over rate should be between 0 - 1");
					System.exit(-1);
				}else{
					parameters++;
				}
			}
			
			if(newLine.contains("mutation")){
				newLine = configFile.readLine();
				mutationRate = Double.parseDouble(newLine);
				if((mutationRate < 0) || (mutationRate > 1)){
					System.out.println("Wrong mutation rate: " + mutationRate);
					System.out.println("The mutation rate should be between 0 - 1");
					System.exit(-1);
				}else{
					parameters++;
					
				}
			}
			
			if(newLine.contains("pressure")){
				newLine = configFile.readLine();
				selectionPressure = Double.parseDouble(newLine);
				if(selectionPressure < 0){
					System.out.println("Wrong selection pressure: " + mutationRate);
					System.out.println("The selection pressure b should be larger than 0");
					System.exit(-1);
				}else{
					parameters++;
				}
			}
			
			if(newLine.contains("iterations")){
				newLine = configFile.readLine();
				numIterations = Integer.parseInt(newLine);
				if((numIterations < 1) || (numIterations > 50)){
					System.out.println("Wrong num of iterations: " + numIterations);
					System.out.println("The number should be between 1 - 50");
					System.exit(-1);
				}else{
					parameters++;
				}
				/* There are 9 parameter to be read, or the program exits */
				if (parameters == 9){
					return;
				}else{
					System.out.println("Error: lack of parameters in the config file");
					System.exit(-1);
				}
			}
			
		}
	}
	
	public int GetCaseNumber(){
		return testCase;
	}
	
	public int GetPopSize(){
		return popSize;
	}
	
	public int GetGeneration(){
		return generation;
	}
	
	public double GetCrossoverRate(){
		return crossoverRate;
	}
	
	public double GetMutationRate(){
		return mutationRate;
	}
	
	public double GetSelectionPressure(){
		return selectionPressure;
	}
	
	public boolean DefaultParameters(){
		return defaultParameters;
	}
	
	public String GetMutationMethod(){
		return mutationMethod;
	}
	
	public int GetNumIterations(){
		return numIterations;
	}
	
}
	
	
	
