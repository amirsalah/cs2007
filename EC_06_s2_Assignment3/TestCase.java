import java.io.IOException;
import java.util.Random;
import java.util.Stack;

/* The interfaces for the test cases */

abstract class TestCase {
	protected ConfigParser configHandler;
	protected int numVar;
	protected boolean max;
	protected int popSize;
	protected double[][] pop, offsprings;
	protected double crossoverRate;
	protected double mutationRate;
	protected int currentGenerationNum;
	
	protected int maxGenNum; //Maximal generation number
	protected double[] bestChromosome;
	protected double bestResult;
	protected int bestGenerationNum;
	
	protected double b;
	/* Used in the non linear test cases */
	protected int iterationNum;
	protected int numIterations; // The total number of iterations
	protected double penaltyPressure;
	
	/* Indicate if it is a Non-linear TestCase */
	protected boolean NLP = false;
	protected double bestResultPenalty;
	protected String mutationMethod;

	
	public TestCase()throws IOException{
		configHandler = new ConfigParser("config.txt");
		if( !configHandler.DefaultParameters() ){
			popSize = configHandler.GetPopSize();
			maxGenNum = configHandler.GetGeneration();
			crossoverRate = configHandler.GetCrossoverRate();
			mutationRate = configHandler.GetMutationRate();
			b = configHandler.GetSelectionPressure();
			numIterations = configHandler.GetNumIterations();
			mutationMethod = configHandler.GetMutationMethod();
		}else{
			/* Using default parameters */
			InitTestCase initial = new InitTestCase();
			initial.InitParameters(this);
		}
	}
	
	/* Genrate population */
	public abstract double[][] GenPop();
	
	/* Test if violate the constraints */
	public abstract boolean ConstraintsTest(double[] chromosome);
	
	/* Returing true indicates it is a maximization func, min otherwise */
	public boolean Max(){
		return max;
	}
	
	/* Return number of varibles for current chromosome */
	public int GetNumVar(){
		return numVar;
	}
	
	public int GetMaxGenNum(){
		return maxGenNum;
	}
	
	/* Initialize the best result */
	protected abstract void initBestResult(double best);
	
	public abstract double Eval(double[] chromosome);
	
	public abstract double GetLowerBound(double[] chromosome, int index);
	
	public abstract double GetUpperBound(double[] chromosome, int index);
	
	/* Crossover the specific population */
	public void Crossover(String coMethod, String species){
		Crossover co = null; 
		Random ran = new Random();
		Stack<Integer> selectedChromosomes = new Stack<Integer>(); //The chromosomes to be crossovered
		int numSelectedChro = 0, chroIndex1, chroIndex2;
		
		if( coMethod.equalsIgnoreCase("ArithmeticalCrossover") ){
			co = new ArithmeticalCrossover(this);
		}
		
		/* Select chromosomes to be crossovered */
		for(int i=0; i<popSize; i++){
			if(ran.nextDouble() <= crossoverRate){
				selectedChromosomes.push(i);
			}
		}
		
		/* the number of selected chromosomes should be even */
		if((selectedChromosomes.size()%2) != 0){
			selectedChromosomes.pop();
		}
		
		numSelectedChro = selectedChromosomes.size();
		for(int i=0; i<(numSelectedChro/2); i++){
			chroIndex1 = selectedChromosomes.pop();
			chroIndex2 = selectedChromosomes.pop();
			
			/* Crossover parents population */
			if(species.equalsIgnoreCase("Parents")){
				co.CrossoverChromosomes(pop[chroIndex1], pop[chroIndex2]);		
			}
			/* Crossover offprings */
			if(species.equalsIgnoreCase("Offsprings")){
				co.CrossoverChromosomes(offsprings[chroIndex1], offsprings[chroIndex2]);
			}
		}
		
	}
	
	public void Selection(String selMethod, String species){
		Selection sel = null;
		if(selMethod.equalsIgnoreCase("BiTournamentSelection") && species.equalsIgnoreCase("Offsprings")){
			sel = new BiTournamentSelection(this);
			offsprings = sel.select(pop, popSize);
			
			for(int i=0; i<popSize; i++){
				if( ConstraintsTest(offsprings[i]) == false ){
					System.arraycopy(bestChromosome, 0, offsprings[i], 0, numVar); 
				}
			}
			
		}
		
		if(selMethod.equalsIgnoreCase("RankingSelection") && species.equalsIgnoreCase("ParentsAndOffsprings")){
			sel = new RankingSelection(this);
			double[][] allPop = new double[2*popSize][numVar];
			
			/* Produce an array containing parents and offsprings */
			for(int i=0; i<popSize; i++){
				for(int j=0; j<numVar; j++){
					allPop[i][j] = pop[i][j];
				}      
			}
			for(int i=popSize; i<2*popSize; i++){
				for(int j=0; j<numVar; j++){
					allPop[i][j] = offsprings[i-popSize][j];
				}
			}
			/* Replace the parent population with the best from both parents and offsprings */
			pop = sel.select(allPop, popSize);
		}
	
		return;
	}
	
	public void Mutation(String mutMethod, String species) throws IOException{
		Mutation mut = null;
		Random ran = new Random();
		
		if(mutMethod.equalsIgnoreCase("NonUniformMutation")){
			mut = new NonUniformMutation(this);
		}
		
		if(mutMethod.equalsIgnoreCase("BoundaryMutation")){
			mut = new BoundaryMutation(this);
		}
		
		/* Mutate offsprings */
		if(species.equalsIgnoreCase("Offsprings")){
			for(int i=0; i<popSize; i++){
				for(int j=0; j<numVar; j++){
					if(ran.nextDouble() <= mutationRate){
						mut.MutationChromosome(offsprings[i], j);
					}
				}
			}

		}

	}
	
	public void SetCurrentGenNum(int generationNum){
		currentGenerationNum = generationNum;
	}
	
	public int GetCurrentGenNum(){
		return currentGenerationNum;
	}
	
}
