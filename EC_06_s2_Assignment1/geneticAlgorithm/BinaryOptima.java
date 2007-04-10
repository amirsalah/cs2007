/* Author: Bo CHEN 		Student ID: 1139520
 * 
 */
package geneticAlgorithm;


import java.util.ArrayList;
import java.util.Stack;
import java.lang.Math;
import java.util.Random;
 

public class BinaryOptima {
	public BinaryOptima(Stack evalFunc, Stack<String> dataIndicator, ArrayList<Double> varLowBound, ArrayList<Double> varUpBound, ArrayList<String> funcVar, int numVar, int pop_size, int num_generations, double prob_crossover, double prob_mutation, int dec_precision, boolean evalMax ){
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
		this.dec_precision = dec_precision;
		
		chromosomesLength = 0;
		chromosomes = new String[pop_size];
		evalValue = new double[pop_size];
//		fitness = new double[pop_size];
		funcValuables = new double[numVar];

	}
	
	/*
	 * Evolutionary procoess: create population(Create), individual competition(Selection), 
	 * spwan new population, chronosomes crossover and mutation.
	 * the population would be improved by this evolutionary loop. 
	 */
	private void evolutionProcess(){
		int chroLength = chromosomesFormat();
		double negativeMax = 0; // To rectify the negative evaluation value.
		
		creatPopulation( chroLength, pop_size );
		
		/* initialize the bestSoFar individual. N. B. it can not be arbitrarily initilized to be any number */
		bestSoFar = evalChromosome(chromosomes[0]);
		generationOfBest = 0;
		bestChromosome = chromosomes[0];
		for(int i = 0; i < num_generations; i++){
			int[] selectedIndividuals = new int[pop_size];
			String[] tempChromosomes = new String[pop_size];
			
			/* Evaluate each chromosome in this generation */
			for (int j = 0; j < pop_size; j++){
				/* Change the Min problem to Max problem */
				if ( evalMax == false ){
					evalValue[j] = -evalChromosome(chromosomes[j]);
					if ( -evalValue[j] < bestSoFar ){	// in case the best individual occurs in the previous generation
						bestSoFar = -evalValue[j];
						generationOfBest = i;
						bestChromosome = chromosomes[j];
					}
				}
				else{
					evalValue[j] = evalChromosome(chromosomes[j]);
					if ( evalValue[j] > bestSoFar ){
						bestSoFar = evalValue[j];
						generationOfBest = i;
						bestChromosome = chromosomes[j];
					}
				}
				
				/* Rectify the negative value in case of minus evaluation value occuring  */
				if ( evalValue[j] < negativeMax )
					negativeMax = evalValue[j];
				
//				System.out.println("Original data: " + evalValue[j]);
			}
			
			if ( negativeMax < 0){
				for ( int j = 0; j < pop_size; j++){
					evalValue[j] = evalValue[j] + Math.abs(negativeMax) * 1.01;
//					System.out.println(evalValue[j]);
				}
			}
			
			/* Select pop_size individuals according to their probability */
			selectedIndividuals = selectProcess(evalValue);
/*			
			for( int j = 0; j < pop_size; j++){
				System.out.println("Original Chro: " + chromosomes[j]);
				System.out.println("Selected Individuals: " + selectedIndividuals[j]);
			}
*/			
			/* Spawn new population after selection */
			for( int j = 0; j < pop_size; j++){
				int indexOfSelection = selectedIndividuals[j];
				tempChromosomes[j] = chromosomes[ indexOfSelection ];
			}
			chromosomes = tempChromosomes.clone();
/*		
			for( int j = 0; j < pop_size; j++){
				System.out.println("After selection Chro " + j + ": " + chromosomes[j]);
			}
*/			
			chroRecombination( chromosomes );
/*			
			for( int j = 0; j < pop_size; j++){
				System.out.println("After Recombination Chro " + j + ": " + chromosomes[j]);
			}
*/
			chroMutation( chromosomes );
/*		
			for( int j = 0; j < pop_size; j++){
				System.out.println("After Mutation Chro " + j + ": " + chromosomes[j]);
			}
*/			
		}
		
	}

	public Double getOptima(){
		evolutionProcess();
		return bestSoFar;
	}
	
	public int getBestGeneration(){
		return generationOfBest;
	}
	
	public String getBestChromosome(){
		return bestChromosome;
	}
	/*
	 * Initializing the length of chromosome. 
	 * The legth of each variable can be read from the varLength arraylist.
	 * the bounds for the first variable are in ArrayList(0), second -> ArrayList(1) and so forth
	 */
	private int chromosomesFormat(){
		chromosomesLength = 0;
		
		for(int i = 0; i < numVar; i++){
			varLength.add(deterChroLength(varLowBound.get(i), varUpBound.get(i), dec_precision));
			chromosomesLength += varLength.get(i);
//			System.out.println(varLength.get(i));
		}
		return chromosomesLength;
	}
	
	/*
	 *  Return the length of a chromosome.
	 */
	private int deterChroLength(Double lowBound, Double upBound, int decPrecision){
		if ( (upBound - lowBound) < 0){
			System.out.println("Wrong variable domain!");
			System.exit(0);
		}
		
		double figure = (upBound - lowBound) * Math.pow(10, decPrecision);
		int power = 0;
		
		while ( figure > Math.pow(2, power)){
			power++;
		}
//		System.out.println("L:" + power + " " + decPrecision);
		return power;
	}

	/*
	 * Create the population with binary representation.
	 */
	private void creatPopulation(int chroLength, int popSize){
		Random generator = new Random();
		
		for( int i = 0; i < popSize; i++){
			chromosomes[i] = "";  // initializing each chromosome.
			for ( int j = 0; j < chroLength; j++){
				if ( generator.nextBoolean())
					chromosomes[i] += "1";
				else
					chromosomes[i] += "0";
			}
//			System.out.println("chromosome" + i + ": " + chromosomes[i] );
		}
	}
	
	/*
	 * Evaluate a chromosome based on the function in the Stack evalFunc 
	 * The parameter should be a string with char only containing "1" and "0"
	 */
	private double evalChromosome(String chromosome){
		for (int i = 0; i < numVar; i++){
			if (i == 0){
				funcValuables[i] = Integer.parseInt( chromosome.substring(0, varLength.get(i)), 2);
//				System.out.println(chromosome.substring(0, varLength.get(i)));
				funcValuables[i] = varLowBound.get(i) + funcValuables[i] * (varUpBound.get(i) - varLowBound.get(i)) / (Math.pow(2, varLength.get(i)) - 1);
//				System.out.println("LowerBound: " + varLength.get(i));
//				System.out.println(funcValuables[i]);
			}
			else{
				// N.B. the elements in the varLength stack are not accumulative.
				// It is the length of each individual variable.
				funcValuables[i] = Integer.parseInt( chromosome.substring(varLength.get(i-1), varLength.get(i-1) + varLength.get(i)), 2);
				funcValuables[i] = varLowBound.get(i) + funcValuables[i] * (varUpBound.get(i) - varLowBound.get(i)) / (Math.pow(2, varLength.get(i)) - 1);
//				System.out.println(funcValuables[i]);
//				System.out.println(chromosome.substring(varLength.get(i-1), varLength.get(i-1) + varLength.get(i)));
			}
		}
		
		return computePostfix(funcValuables);
		
	}
	
	/*
	 * The computation process of postfix notation can be found at the en.wikipedia.org
	 */
	public double computePostfix(double[] funcValuables){
		Stack<Double> tempStack = new Stack<Double>();
		String dataType;
		Stack<String> copyDataIndicator = new Stack<String>(); 
		Stack copyEvalFunc = new Stack();
		double lowerDigit, upperDigit;
		int	indexOfVariable;
/*		
		System.out.println("Inside ComputePostfix");
		int size_reOutPut = dataIndicator.size();
		for( int i = 0; i < size_reOutPut; i++){
			System.out.println(dataIndicator.pop());
		}
*/		
		copyDataIndicator = (Stack<String>)dataIndicator.clone();
		copyEvalFunc = (Stack) evalFunc.clone();
//		System.out.println("copy size: " + copyDataIndicator.size());

		while ( !copyDataIndicator.empty() ){
			dataType = copyDataIndicator.pop();
			
			switch(dataType.charAt(0))
			{
			case 'D':
				tempStack.push((Double)copyEvalFunc.pop());
				break;
			case 'O':
				
				switch(copyEvalFunc.pop().toString().charAt(0))
				{
				case '+':
					upperDigit = tempStack.pop();
					lowerDigit = tempStack.pop();
					tempStack.push( lowerDigit + upperDigit );
					break;
				case '-':
					upperDigit = tempStack.pop();
					lowerDigit = tempStack.pop();
					tempStack.push( lowerDigit - upperDigit );
					break;
				case '*':
					upperDigit = tempStack.pop();
					lowerDigit = tempStack.pop();					
					tempStack.push( lowerDigit * upperDigit );
					break;
				case '/':
					upperDigit = tempStack.pop();
					lowerDigit = tempStack.pop();
					tempStack.push( lowerDigit / upperDigit );
					break;
				case '^':
					upperDigit = tempStack.pop();
					lowerDigit = tempStack.pop();
					tempStack.push(Math.pow(lowerDigit, upperDigit));
					break;
				default:
					System.out.println("Invalid operator!");
					System.exit(0);
				}
				break; // N.B. Don't forget this break ;-)
				
			case 'V':
				// look up the funcVar stack to find which variable it is 
				// and push the correct variable to tempStack
//				System.out.println(evalFunc.peek());
				indexOfVariable = funcVar.indexOf((String)copyEvalFunc.pop());
//				System.out.println("index: " + indexOfVariable);
				tempStack.push((Double)funcValuables[indexOfVariable]);
				break;
				
			default:
				System.out.println("Wrong parameter in the dataIndicator stack");
				System.exit(0);
			}
		}

//		System.out.println("result " + tempStack.size());
		return tempStack.peek();
	}
	
	
	public int[] selectProcess(double[] evalValue){
		double sumOfValues = 0, temp = 0;
		double[] probability = new double[pop_size];
		double[] cumProbability = new double[pop_size];	
		int[] selectedIndividuals = new int[pop_size]; 
		Random generator = new Random();
		
		
		cumProbability[0] = 0; 
		/* Sum the fitness values of all the individuals in this generation */
		for (int i = 0; i < pop_size; i++){
			sumOfValues += evalValue[i];
		}

		/* Compute the individual probability and accumulative probability */
		for (int i = 0; i < pop_size; i++){
			
			probability[i] = evalValue[i] / sumOfValues;
			temp += probability[i]; 
			cumProbability[i] = temp;
//			System.out.println("p" + i + ":" + probability[i]);
		}
//		System.out.println("Sum: " + sumOfValues + " Probability: " + cumProbability[pop_size - 2]);
		
		for (int i = 0; i < pop_size; i++){
			temp = generator.nextDouble();
			int j = 0;
			while( temp > cumProbability[j]){
				j++;
			}
			selectedIndividuals[i] = j;	
		}
		
		return selectedIndividuals;
	}
	
	/*
	 * Return a boolean array indicating which chromosomes should performance crossover
	 */
	private void chroRecombination( String[] chromosomes){
		Random generator = new Random();
		Stack<Integer> chroCrossover = new Stack<Integer>();
		int numberCounter = 0, cutPoint = 0;
		int[] indexOfChro = new int[2];
		String tempSubstring = "";
		String tempString = "";
		
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
		
		/* Performance crossover on each pair of chromosomes */
		while ( !chroCrossover.empty() ){
			indexOfChro[0] = chroCrossover.pop();
			indexOfChro[1] = chroCrossover.pop();
			cutPoint = generator.nextInt(chromosomesLength);
//			System.out.println("Cut Point:" + cutPoint);
			tempSubstring = chromosomes[ indexOfChro[0] ].substring(cutPoint, chromosomesLength);
			tempString = chromosomes[ indexOfChro[1] ].substring(0, cutPoint) + tempSubstring;
			tempSubstring = chromosomes[ indexOfChro[1] ].substring(cutPoint, chromosomesLength);
			chromosomes[ indexOfChro[1] ] = tempString;
			tempString = chromosomes[ indexOfChro[0] ].substring(0, cutPoint) + tempSubstring;
			chromosomes[ indexOfChro[0] ] = tempString;
		}	
	}
	
	/*
	 * Mutating the give chromosomes according to the relative mutation rate
	 */
	private void chroMutation(String[] chromosomes){
		Stack<Integer> chroMutation = new Stack<Integer>();
		Random generator = new Random();
		int totalBits, bitNumber, chroNumber;
		String tempString = "";
		
		totalBits = chromosomesLength * pop_size;
		
		// Generate random bits in the range of (0, totalBits) 
		for( int i = 0; i < totalBits; i++ ){
			if ( generator.nextDouble() < prob_mutation ){
				chroMutation.push(i);
//				System.out.println("Mutation bit: " + i);
			}
		}
		
		// Performance mutation
		while ( !chroMutation.empty() ){
			bitNumber = chroMutation.pop();
			chroNumber = ( bitNumber / chromosomesLength );
			bitNumber = ( bitNumber % chromosomesLength );
//			System.out.println("Mutation chro: " + chroNumber + "  Mutation bit: " + bitNumber);
			if ( chromosomes[ chroNumber ].charAt(bitNumber) == '1'){
				tempString = chromosomes[ chroNumber ].substring(0, bitNumber) + "0" + chromosomes[ chroNumber ].substring(bitNumber + 1, chromosomesLength);
			}else{
				tempString = chromosomes[ chroNumber ].substring(0, bitNumber) + "1" + chromosomes[ chroNumber ].substring(bitNumber + 1, chromosomesLength);
			}
			
			chromosomes[ chroNumber ] = tempString;
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
	private	int dec_precision = 1;
	
	int chromosomesLength;
	private String[] chromosomes;
	private double[] evalValue;
//	private double[] fitness;
	private double[] funcValuables;
	double bestSoFar;
	int generationOfBest;
	String bestChromosome;
	private ArrayList<Integer> varLength = new ArrayList<Integer>();
}
