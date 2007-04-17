/*=======================================================
  Genetic Algorithm to solve the n-Queens problem.
  
  To do:
  - add selection, crossover and mutation methods
  - initialize the population
  - implement the fitness evaluation
          
  Search for TODO in the comments!
=========================================================*/
import java.util.*;
import java.text.*;


public class GeneticAlgorithm
{
  //----------------------------------
  // Run the GA
  //----------------------------------
  public void run()
  {
    initialize();    
    fitnessEvaluation();
    
    while (!checkForTermination())
    {
      ++generation;
      
      parentSelection();
      crossover();
      mutation();
      
      fitnessEvaluation();
    }
    
    printResult();
  }
  
  
  //------------------------------------------------------------
  // Select parents
  // - selects the parent individuals to be used for creating
  //   the next generation of solutions
  //
  // TODO: implement selection method(s)
  //       - populate parents with selected solutions!
  //       - each pair of parents will generate two offspring,
  //         so the size of parents should be the same as
  //         population
  //------------------------------------------------------------
  protected void parentSelection()
  {
    // Clear parents list
    parents = new Individual[populationSize];
    
    // Perform selection...
    if (selectionType.equals("tournament"))
    {
      ; // TODO: Insert your selection method here!
  		int seed1;
  		int seed2;
	    	
  		//select better one from the tournament
  		for(int i = 0; i<populationSize;i++)
  		{
  	  		seed1 = random.nextInt(populationSize);
  	  		seed2 = random.nextInt(populationSize);
  			
			while(seed1 == seed2)
  			{
  				seed2 = random.nextInt(populationSize);
  			}		
  			if(population[seed1].fitness > population[seed2].fitness)
  				parents[i] = population[seed1];
  			else
  				parents[i] = population[seed2];
  		}
    }
    else
    {
      throw new RuntimeException("Selection type not supported: " + selectionType);
    }
  }
  
  
  //----------------------------------------------------------
  // Perform crossover
  // - creates a new child based on genetic recombination of
  //   two parents
  //
  // TODO: implement crossover method(s)
  //       - create and return a new solution from the given
  //         parents
  //----------------------------------------------------------
  protected Individual crossover(Individual p1, Individual p2)
  {
    Individual child = new Individual();
    
    // Perform crossover...
    if (crossoverType.equals("single_point"))
    {
      ; // TODO: Insert your crossover method here!
      int crossPoint = random.nextInt(gridSize - 1);
      int startPoint;
      int column2;
      ArrayList<Integer> unchangedQueens = new ArrayList<Integer>();
      for(startPoint=0; startPoint<crossPoint+1; startPoint++)
      {
    	  unchangedQueens.add(p1.chromosome[startPoint]);
      }
      for(startPoint=crossPoint+1; startPoint<gridSize; startPoint++)
      {
    	  for(int j=0; j<gridSize; j++)
    	  {
    		  column2 = (j + startPoint)%gridSize;
    		  boolean validqueen = false;
    		  boolean foundsolution = false;
    		  if(!unchangedQueens.contains(p2.chromosome[column2]))
    			  validqueen = true;
    		  if(validqueen)
    		  {
    			  foundsolution = true;
    			  child.chromosome[startPoint] = p2.chromosome[column2];
    			  unchangedQueens.add(p2.chromosome[column2]);
    		  }
    		  if(foundsolution == true)
    			  break;
    	  }
      }
      for(startPoint=0; startPoint<crossPoint+1; startPoint++)
      {
    	  child.chromosome[startPoint] = p1.chromosome[startPoint];
      }
    }
    else
    {
      throw new RuntimeException("Crossover type not supported: " + crossoverType);
    }
    
    return child;
  }
  
  
  //--------------------------------------------------------
  // Perform mutation
  // - modifies an individuals chromosome
  //
  // TODO: implement mutation method(s)
  //       - mutate the chromosome of the given individual
  //--------------------------------------------------------
  protected void mutate(Individual individual)
  {    
    // Perform mutation...
    if (mutationType.equals("swap_columns"))
    {
      ; // TODO: Insert your mutation method here! 
  		int temp = 0;
  		int column1 = random.nextInt(gridSize);
  		int column2 = random.nextInt(gridSize);
  		temp = individual.chromosome[column1];
  		individual.chromosome[column1] = individual.chromosome[column2];
  		individual.chromosome[column2] = temp;
    }   
    else
    {
      throw new RuntimeException("Mutation type not supported: " + mutationType);
    }    
  }

  
  //------------------------------------------------
  // Initialization
  // - create a randomized population of solutions
  // - initialize the algorithm etc
  //
  // TODO: initialize population
  //       - generate a new randomized population
  //         of solutions
  //------------------------------------------------
  protected void initialize()
  {
    population = new Individual[populationSize];
    parents = new Individual[populationSize];
    
    // TODO: initialize the population here!
    for(int i=0; i<populationSize; i++)
    {
    	population[i] = new Individual(true);
    	parents[i] = new Individual(true);
    }
    
    // Initialize algorithm
    generation = 0;
    startTime = System.currentTimeMillis();
    endTime = System.currentTimeMillis() + (int)(maxTime * 1000);
    terminationReason = "???";
    numFevals = 0;
  }
  
  
  //------------------------------------------------------------
  // Fitness evaluation
  // - Evaluate the fitness of each solution
  // - Counts each check once only for the penalty calculation
  //   (ie. per pair of checked queens instead of per queen)
  //------------------------------------------------------------
  protected void fitnessEvaluation()
  {
    for (Individual i: population)
    {
      i.evaluateFitness();      
      ++numFevals;
    }
    
    // Sort population by descending order of fitness
    // Makes it easy to check for the best solution!
    Arrays.sort(population, new IndividualDescending());
  }
  
  
  //------------------------------------------------------
  // Termination check
  // - Determine if a termination condition has been met
  //------------------------------------------------------
  protected boolean checkForTermination()
  {
    // Max evaluation time
    if (System.currentTimeMillis() > endTime)
    {
      terminationReason = "*** Time expired ***";
      return true;
    }
    
    // Max generation
    if (generation >= maxGenerations)
    {
      terminationReason = "*** Maximum generation reached ***";
      return true;
    }
    
    // Found valid solution!
    // - because the population is sorted we can just check
    //   the first solution
    if (population[0].fitness >= 1)
    {
      terminationReason = "*** Solution found! ***";
      return true;
    }
    
    return false;
  }

  
  //------------------------------------------------------------------
  // Crossover
  // - create new solutions from pairs of parents
  // - each combination of parents has crossover_rate probability of
  //   being used for crossover
  // - if crossover is not performed the first parent is cloned
  //------------------------------------------------------------------
  protected void crossover()
  {
    // Clear the population - completely replace with new individuals (no elitism)
    population = new Individual[populationSize];
    
    // Take two parents at a time to perform crossover
    for (int i = 0; i + 1 < populationSize; ++i)
    {
      Individual parentA = parents[i];
      Individual parentB = parents[i+1];
      
      // New child from parents A & B
      if (random.nextDouble() < crossoverRate)
        population[i] = crossover(parentA, parentB);
      else
        population[i] = parentA.clone();        

      // New child from parents B & A
      if (random.nextDouble() < crossoverRate)
        population[i+1] = crossover(parentB, parentA);
      else
        population[i+1] = parentB.clone();
    }
  }

  
  //------------------------------------------------------------------
  // Mutation  
  // - each individual has mutation_rate chance of being mutated
  //------------------------------------------------------------------
  protected void mutation()
  {
    for (Individual individual: population)
    {
      // Determine whether to mutate...
      if (random.nextDouble() < mutationRate)
      {
        mutate(individual);
      }
    }
  }
  
  
  //-------------------------
  // Output stats & results
  //-------------------------
  protected void printResult()
  {
    System.out.println(terminationReason);
    System.out.println("generations: " + generation);
    System.out.println("function evaluations: " + numFevals);
    DecimalFormat nf = new DecimalFormat("0.000");
    System.out.println(
      "elapsed time: " +
      nf.format((System.currentTimeMillis() - startTime) / 1000.0) + "s");
    if (population[0].fitness >= 1)
    {
      System.out.println("Solution:");
      printSolution(population[0]);
    }
  }
  
  
  //------------------------
  // Output a solution 
  //------------------------
  protected void printSolution(Individual soln)
  {
    if (gridSize <= 32)
    {
      // Use ASCII graphics!
      for (int row = 0; row < gridSize; ++row)
      {
        for (int col = 0; col < gridSize; ++col)
        {
          if (soln.chromosome[col] == row)
            System.out.print("Q");
          else if ((row + col) % 2 == 0)
            System.out.print(".");
          else
            System.out.print("-");
        }
        System.out.println();
      }
    }
    else
    {
      // Just list the row values
      System.out.print("[" + soln.chromosome[0]);
      for (int col = 1; col < gridSize; ++col)
        System.out.print(", " + soln.chromosome[col]);
      System.out.println("]");
    }
  }
  
  
  //------------------------------------------------------
  // Set algorithm parameters
  // - All required values should be set before running!
  //------------------------------------------------------
  public void setAttribute(String key, String value)
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

  
  //=====================
  // Inner classes
  //=====================
  
  //--------------------------------------------
  // Individual
  // - potential solution to n-Queens problem
  //--------------------------------------------
  protected class Individual implements Cloneable
  {
    public Individual()
    {
      chromosome = new int[gridSize];      
      fitness = 0;
    }
    
    public Individual(boolean initialize)
    {
      this();
      
      if (initialize == true)
        initialize();
    }
    
    //------------------------------------------------------------
    // Initialize
    // - initialize this individual to be a random solution
    //
    // TODO: implement!
    //       - set the chromosome of the individual to a random
    //         solution
    //       - each value in the chromosome is the row number
    //         for each column... each row number from 0 to n-1
    //         should appear exactly once per solution.
    //       - For example, a solution
    //           {5, 7, 2, 1, 4, 0, 6, 3}
    //         has a queen in row 5 of column 0, row 7 of
    //         column 1 etc.
    //------------------------------------------------------------
    protected void initialize()
    {
    	ArrayList<Integer> numSet = new ArrayList<Integer>();
    	for(int i=0; i<gridSize; i++){
    		numSet.add(i);
    	}
    	
    	// randomly select number from the number set, which includes numbers from 0 to gridSize-1
    	for(int i=0; i<gridSize; i++){
    		chromosome[i] = numSet.remove(random.nextInt(numSet.size()));
    	}
    	
    }
    
    //--------------------------------------------------------------
    // Evaluate fitness
    // - evaluate the fitness of this solution
    //
    // TODO: implement!
    //       - set the fitness of this solution, normalized
    //         between 0 and 1
    //       - 1 indicates a valid solution (no checked queens) for
    //         any size board
    //       - the greater the number of checked queens, the lower
    //         the fitness should be
    //--------------------------------------------------------------
    public void evaluateFitness()
    {
    	double checkedQueens = 0;
    	
    	for(int i=0; i<gridSize; i++){
    		int column = 1;
    		for(int j=i+1; j<gridSize; j++){
    			if(chromosome[j] == (chromosome[i] + column))
    				checkedQueens = checkedQueens + 1;
    			if(chromosome[j] == (chromosome[i] - column))
    				checkedQueens = checkedQueens + 1;
    			column = column + 1;
    		}
    	}
    	
    	if(checkedQueens == 0)
    		fitness = 1;
    	else
    		fitness = 1.0 / (checkedQueens * 2);
    }
    
    public Individual clone()
    {
      Individual i = new Individual();
      i.chromosome = this.chromosome.clone();
      i.fitness = this.fitness;
      return i;
    }
    
    // Data
    int[] chromosome;  // each "gene" in the chromosome represents a column, with the gene
                       // value representing the row number of the queen in that column
                       // - the row values are zero indexed (ie. from 0 to n-1)
    double fitness;
  }
  
  
  //---------------------------------------------
  // Comparator for Individuals
  // - to sort by descsending order of fitness
  //---------------------------------------------
  protected class IndividualDescending implements Comparator<Individual>
  {
    public int compare(Individual i1, Individual i2)
    {
      if (i1.fitness > i2.fitness)
        return -1;
      else if (i1.fitness < i2.fitness)
        return 1;
      return 0;
    }
  }

  
  //===================
  // Data
  //===================
  protected int gridSize; 
  protected double maxTime = 0;
  
  protected int populationSize = 0;
  protected int maxGenerations = -1;
  protected String selectionType = "undefined";
  protected String crossoverType = "undefined";
  protected double crossoverRate = 0;
  protected String mutationType = "undefined";
  protected double mutationRate = 0;
  
  protected int generation;
  protected long endTime;
  protected long startTime;
  protected Individual[] population;
  protected Individual[] parents;
  
  protected Random random = new Random();
  
  protected String terminationReason = "???";
  protected long numFevals;  
}
