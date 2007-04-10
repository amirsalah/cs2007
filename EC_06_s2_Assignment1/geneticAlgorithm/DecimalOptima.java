/* Author: Bo CHEN 		Student ID: 1139520
 * 
 */
package geneticAlgorithm;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class DecimalOptima {

	public DecimalOptima(Stack evalFunc, Stack<String> dataIndicator, ArrayList<Double> varLowBound, ArrayList<Double> varUpBound, ArrayList<String> funcVar, int numVar, int pop_size, int num_generations, double prob_crossover, double prob_mutation, boolean evalMax ){
		this.evalFunc = evalFunc;
		this.dataIndicator = dataIndicator;
		this.varLowBound = varLowBound;
		this.varUpBound = varUpBound;
		this.funcVar = funcVar;
		this.numVar = numVar;
		this.pop_size = pop_size;
		this.num_generations = num_generations;
		this.prob_crossover = prob_crossover;
		this.prob_mutation = prob_mutation;
		this.evalMax = evalMax;
		
		generator = new Random();		
		chromosomesLength = 0;
		chromosomes = new double[pop_size][numVar];
		evalValue = new double[pop_size];
//		fitness = new double[pop_size];
		bestSoFar = 0;

	}
	
	/*
	 * Evolutionary procoess: create population(Create), individual competition(Selection), 
	 * spwan new population, chronosomes crossover and mutation.
	 * the population would be improved by this evolutionary loop. 
	 */
	private void evolutionProcess(){
		BinaryOptima instanceOfBinary = new BinaryOptima(evalFunc, dataIndicator, varLowBound, varUpBound, funcVar, numVar, pop_size, num_generations, prob_crossover, prob_mutation, 4, evalMax );
		double negativeMax = 0; // To rectify the negative evaluation value.
		
		creatPopulation();
		
		bestSoFar = evalChromosome(chromosomes[0], instanceOfBinary);
		generationOfBest = 0;
		bestChromosome = chromosomes[0];
		for(int i = 0; i < num_generations; i++){
			int[] selectedIndividuals = new int[pop_size];
			double[][] tempChromosomes = new double[pop_size][numVar];

			/* Evaluate each chromosome in this generation */
			for (int j = 0; j < pop_size; j++){
				/* Change the Min problem to Max problem */
				if ( evalMax == false ){
					evalValue[j] = -evalChromosome(chromosomes[j], instanceOfBinary);
					if ( -evalValue[j] < bestSoFar ){ // in case the best individual occurs in the previous generation
						bestSoFar = -evalValue[j];
						generationOfBest = i;
						bestChromosome = chromosomes[j];
					}
				}
				else{
					evalValue[j] = evalChromosome(chromosomes[j], instanceOfBinary);
					if ( evalValue[j] > bestSoFar ){
						bestSoFar = evalValue[j];
						generationOfBest = i;
						bestChromosome = chromosomes[j];
					}
				}

				/* Rectify the negative value in case of minus evaluation value occuring  */
				if ( evalValue[j] < negativeMax )
					negativeMax = evalValue[j];
				
//				System.out.println("Original data[" + j + "]: "+ evalValue[j]);
			}
			
			if ( negativeMax < 0){
				for ( int j = 0; j < pop_size; j++){
					evalValue[j] = evalValue[j] + Math.abs(negativeMax) * 1.01;
//					System.out.println(evalValue[j]);
				}
			}
			
			/* Select pop_size individuals according to their probability */
			selectedIndividuals = selectProcess(evalValue, instanceOfBinary);
/*	
            //the Debug code here is different from binaryOptima class		
			for( int j = 0; j < pop_size; j++){
				System.out.println("Original Chro: " + chromosomes[j][0] + " " + chromosomes[j][1] + " " + chromosomes[j][2]);
				System.out.println("Selected Individuals: " + selectedIndividuals[j]);
			}
*/
			
			/* Spawn new population after selection */
			for( int j = 0; j < pop_size; j++){
				int indexOfSelection = selectedIndividuals[j];
				tempChromosomes[j] = chromosomes[ indexOfSelection ];
			}
			chromosomes = tempChromosomes.clone();
			
			chroRecombination( chromosomes );
			
			chroMutation( chromosomes );
	}

}

	/* Return the best chromosome that emerge during the evolutionary process */
	public Double getOptima(){
		evolutionProcess();
		return bestSoFar;
	}
	
	public int getBestGeneration(){
		return generationOfBest;
	}
	
	public double[] getBestChromosome(){
		return bestChromosome;
	}
	
	/* Set the chromosomes to be decimal genes */
	private void creatPopulation(){
	
		for( int i = 0; i < pop_size; i++){
			for ( int j = 0; j < numVar; j++){
				chromosomes[i][j] = varLowBound.get(j) +   generator.nextDouble() * ( varUpBound.get(j) - varLowBound.get(j)); 
//				System.out.println("Chromosomes[" + i + "][" + j + "]: " + chromosomes[i][j]);
			}
		}
		
	}
	
	/* Evaluate a chromosome, by invoking the postfix colculation function in the BinaryOptima class */
	private double evalChromosome(double[] chromosome, BinaryOptima instanceOfBinary){
		return 	instanceOfBinary.computePostfix(chromosome);
	}
	
	/* Select some chromosomes based on their probability, by invoking method in BinaryOptima class */
	private int[] selectProcess(double[] evalValue, BinaryOptima instanceOfBinary){
		return instanceOfBinary.selectProcess(evalValue);
	}
	
	
	private void chroRecombination(double[][] chromosomes){
		Stack<Integer> chroCrossover = new Stack<Integer>();
		int numberCounter = 0, cutPoint = 0;
		int[] indexOfChro = new int[2];		
		
		for ( int i = 0; i < pop_size; i++){
			if ( generator.nextDouble() <= prob_crossover){
				chroCrossover.push(i);
				numberCounter++;
//				System.out.println("Chromosomes need crossover: " + i);
			}		
		}
		
		/* deal with the case o odd number of individuals that need crossover */
		if ( ( numberCounter % 2 ) != 0 ){
			int randomInt = generator.nextInt(pop_size);
			chroCrossover.push(randomInt);
		}
		
		/* Performance crossover on each pair of chromosomes, swaping corresponding variables */
		while ( !chroCrossover.empty() ){
			double[] tempSubChro = new double[numVar];
			indexOfChro[0] = chroCrossover.pop();
			indexOfChro[1] = chroCrossover.pop();
			cutPoint = generator.nextInt(numVar);
//			System.out.println("Cut Point:" + cutPoint);
			for( int i = 0; i < cutPoint; i++){
				tempSubChro[i] = chromosomes[indexOfChro[0]][i];
				chromosomes[indexOfChro[0]][i] = chromosomes[indexOfChro[1]][i];
				chromosomes[indexOfChro[1]][i] = tempSubChro[i];
			}	
		}
	}
	
	/* The mutation of decimal is distinct with binary representation, but similar conceptually
	 * randomly product the new variables, rather than just 1 bit.
	 */
	private void chroMutation(double[][] chromosomes ){
		Stack<Integer> chroMutation = new Stack<Integer>();
		int totalNumber = pop_size * numVar;
		int indexOfChro = 0, indexOfVar = 0, index = 0;
		
		for( int i = 0; i < totalNumber; i++){
			if ( generator.nextDouble() <= prob_mutation ){
				chroMutation.push(i);
			}
		}
		
		while( !chroMutation.empty() ){
			index = chroMutation.pop();
			indexOfChro = index / numVar;
			indexOfVar = index % numVar;			
			chromosomes[indexOfChro][indexOfVar] = varLowBound.get(indexOfVar) +   generator.nextDouble() * ( varUpBound.get(indexOfVar) - varLowBound.get(indexOfVar));
		}
	}
	
	
	
	private Stack evalFunc = new Stack();
	private Stack<String> dataIndicator = new Stack<String>();
	private	boolean evalMax = true;
	private	int numVar = 0;
	private	ArrayList<Double> varLowBound = new ArrayList<Double>();
	private	ArrayList<Double> varUpBound = new ArrayList<Double>();
	private	ArrayList<String> funcVar = new ArrayList<String>();
	private	int pop_size = 20;
	private	int num_generations = 2000;
	private	double prob_crossover = 0.30;
	private	double prob_mutation = 0.01;
	private Random generator;
	
	int chromosomesLength;
	private double[][] chromosomes;
	private double[] evalValue;
//	private double[] fitness;
	double bestSoFar;
	int generationOfBest;
	double[] bestChromosome;
//	private ArrayList<Integer> varLength = new ArrayList<Integer>();
}
