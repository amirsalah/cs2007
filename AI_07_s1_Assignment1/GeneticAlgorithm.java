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
    
    /*** Perform tournament selection ***/
    if (selectionType.equals("tournament"))
    {
      ; // TODO: Insert your selection method here!
      /* Increase the tournamentSize will increase the selection pressure */
      int tournamentSize = 10;  
      int bestSeed = random.nextInt(populationSize);
      int randomSeed = random.nextInt(populationSize);
      Set<Integer> seeds = new HashSet<Integer>();
      
      seeds.add(bestSeed);
      for(int i=0; i<populationSize; i++){
    	  // Choose the best seed from each tournament
    	  for(int j=0; j<tournamentSize; j++){
    		  randomSeed = random.nextInt(populationSize);
    		  // Make sure individuals involved in the tournament are different
    		  while(seeds.contains(randomSeed)){
    			  randomSeed = random.nextInt(populationSize);
    		  }
    		  if(population[randomSeed].fitness > population[bestSeed].fitness){
    			  bestSeed = randomSeed;
    		  }
    		  parents[i] = population[bestSeed];
    	  }
    	  seeds.clear();
    	  bestSeed = random.nextInt(populationSize);
    	  seeds.add(bestSeed);
      }
    }else 
    
    /*** Perfrom roulette wheel selection (fitness-proportionate selection) ***/
    if(selectionType.equals("roulette_wheel")){
    	double fitnessSum = 0.0;
    	double[] accFitness = new double[populationSize]; // Accumulated fitness
    	double randomFitness = 0.0;
    
    	// Sum of all current chromosomes' fitness values
    	for(int i=0; i<populationSize; i++){
    		fitnessSum += population[i].fitness;
    		accFitness[i] = fitnessSum; 
    	}

    	// Select chromosomes according to their fitness values
    	for(int i=0; i<populationSize; i++){
    		randomFitness = random.nextDouble() * fitnessSum;
    		
    		for(int j=0; j<populationSize; j++){
    			if(accFitness[j] >= randomFitness){
    				parents[i] = population[j];
    				break;
    			}
    		}
    	}
    }else if(selectionType.equalsIgnoreCase("ranking_selection")){
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
    
    /*** Perform single point crossover ***/
    if (crossoverType.equals("single_point"))
    {
      ; // TODO: Insert your crossover method here!
      Set<Integer> originalQueens = new HashSet<Integer>();
      int cutPoint = random.nextInt(gridSize - 1);
      int duEleCounter = 0; // The counter of duplicated elements that exist in the original set
      
      // Add elements(in p1) located before the cutPoint to the set
      for(int i=0; i<cutPoint+1; i++){
    	  originalQueens.add(p1.chromosome[i]);
    	  child.chromosome[i] = p1.chromosome[i];
      }
      
      for(int i=cutPoint+1; i<gridSize; i++){
    	  // Check in circle to insert the elements from individual p2 to p1
    	  for(int j=duEleCounter; j<gridSize; j++){
    		  if( !originalQueens.contains(p2.chromosome[(j + i)%gridSize]) ){
    			  originalQueens.add(p2.chromosome[(j + i )%gridSize]);
    			  child.chromosome[i] = p2.chromosome[(j + i)%gridSize];
    			  break;
    		  }
    		  duEleCounter++;
    	  }
      }
    }else
    	
    	/*** Perform partially matched crossover (double points crossover) ***/
    if(crossoverType.equals("partially_matched")){
    	Map<Integer, Integer> swapQueens = new HashMap<Integer, Integer>();
    	ArrayList<Integer> keyQueens = new ArrayList<Integer>(); // The partial queens that being swapped
    	ArrayList<Integer> valueQueens = new ArrayList<Integer>();
    	int cutPoint1 = random.nextInt(gridSize - 1);
    	int cutPoint2 = random.nextInt(gridSize - 1);
    	
    	// Ensure the two cutting points are different
    	while(cutPoint2 == cutPoint1){
    		cutPoint2 = random.nextInt(gridSize - 1);
    	}
    	
    	// Swap cut point 1 and 2 if point2 is larger than point1
    	if(cutPoint1 > cutPoint2){
    		int temp = cutPoint1;
    		cutPoint1 = cutPoint2;
    		cutPoint2 = temp;
    	}
    	
    	// Add the matched queens into the hash map and the child
    	for(int i=cutPoint1+1; i<cutPoint2+1; i++){ 		
    		child.chromosome[i] = p2.chromosome[i];
    		keyQueens.add(p2.chromosome[i]);
    		valueQueens.add(p1.chromosome[i]);
    	}
    	
    	for(int i=cutPoint1+1; i<cutPoint2+1; i++){
    		int matchedQueen = p1.chromosome[i];
    		while(keyQueens.contains(matchedQueen)){
    			// Make sure p2[i] != p1[i], or the loop will be infinite
    			if(matchedQueen != p2.chromosome[i]){
    				matchedQueen = valueQueens.get(keyQueens.indexOf(matchedQueen));
    			}else{
    				break;
    			}
    		}
    		
    		swapQueens.put(p2.chromosome[i], matchedQueen);
    	}
    	
    	// Add remaining elements to the child and replace the duplicated elements    	
    	for(int i=0; i<cutPoint1+1; i++){
    		if(swapQueens.containsKey(p1.chromosome[i])){
    			child.chromosome[i] = swapQueens.get(p1.chromosome[i]);
    		}else{
    			child.chromosome[i] = p1.chromosome[i];
    		}
    	}
    	
    	for(int i=cutPoint2+1; i<gridSize; i++){
    		if(swapQueens.containsKey(p1.chromosome[i])){
    			child.chromosome[i] = swapQueens.get(p1.chromosome[i]);
    		}else{
    			child.chromosome[i] = p1.chromosome[i];
    		}
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
      int position1 = random.nextInt(gridSize);
      int position2 = random.nextInt(gridSize);
      int tmp;
      
      // Ensure the enerated two columns are different
      while(position1 == position2){
    	  position2 = random.nextInt(gridSize);
      }
      
      // Swap the two columns
      tmp = individual.chromosome[position1];
      individual.chromosome[position1] = individual.chromosome[position2];
      individual.chromosome[position2] = tmp;

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
    for(int i=0; i<populationSize; i++){
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
    	Set<Integer> positionSet = new HashSet<Integer>();
    	int position = random.nextInt(gridSize);
          
    	for(int i=0; i<gridSize; i++){
    		while(positionSet.contains(position)){
    			position = random.nextInt(gridSize);
    		}
    		positionSet.add(position);
    		chromosome[i] = position;
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
    	int numCollisions = 0;
    	int selPressure = 1;  // Control selection pressure, higher value, higher selection pressure
    	int offset = 1;       // offset used to calculate the diagnal attack between queens
    	
    	// Calculate the total number of collisions
    	for(int i=0; i<(gridSize - 1); i++){
    		offset = 1;
    		
    		for(int j=i+1; j<gridSize; j++){
    			if( (chromosome[j] == (chromosome[i] + offset)) || (chromosome[j] == (chromosome[i] - offset))){
    				numCollisions++;
    			}
    			offset++;
    		}
    	}
    	
    	// Calculate the fitness based on the number of collisions
    	if(numCollisions == 0){
    		fitness = 1.0;
    	}else{
    		// Note: "+ 1" is used to deal with the situation when numCollisions == 1; the fitness will be 1
    		fitness = (double)1.0/(double)(numCollisions * selPressure + 1);
    	}
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
