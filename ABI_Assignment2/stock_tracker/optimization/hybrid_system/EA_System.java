package stock_tracker.optimization.hybrid_system;

import java.util.ArrayList;
import java.util.Random;

import stock_tracker.data.StockPoint;
import stock_tracker.data.StockPointsSet;
import stock_tracker.optimization.Optimization;

/**
 * Evolutionary algorithm system, used to evolve fuzzy trading rules.
 * The best chromosome will be return in evolve() method.
 * @author Bo CHEN
 */
public class EA_System extends Optimization{
	private int numIndividuals = 100;
	// each individual in the population is a fuzzy trading rule set
	
	private ArrayList<FuzzyLogicRules> population = new ArrayList<FuzzyLogicRules>(numIndividuals);
	private ArrayList<Double> fitnessValues = new ArrayList<Double>(numIndividuals);
	private ArrayList<FuzzyLogicRules> children = new ArrayList<FuzzyLogicRules>(numIndividuals);
	private ArrayList<Double> childrenFitnessValues = new ArrayList<Double>(numIndividuals);
	private StockPointsSet dataSet; // the stock prices data
	private int numGenerations = 200;
	private Random random = new Random();
	
	private double mutationRate = 0.3;

	
	public EA_System(StockPointsSet dataSet, StockPointsSet actualSet){
		super(actualSet);
		this.dataSet = dataSet;
	}
	
	public FuzzyLogicRules evolve(){
		InitPopulation();
		
		//Start evolutionary process,
		for(int i=0; i<numGenerations; i++){
			fitnessValues.clear();
			//Evaluate each individual in the population
			for(int index = 0; index < population.size(); index++){
				double fitness;
				fitness = Evaluate_FLRulesSet(population.get(i));
				fitnessValues.add(fitness); //New fitness values for the new population
			}
			
			// new children by tournament selection
			for(int index=0; index<population.size(); index++){
				int index1 = random.nextInt(population.size());
				int index2 = random.nextInt(population.size());
				children.add(TournamentSelection(index1, index2));
			}
			
			// Mutation
			for(int index=0; index<children.size(); index++){
				if(random.nextDouble()<mutationRate){
					children.get(index).Mutation();
				}
			}
			
			//Evaluate each individual in the chilren
			for(int index = 0; index < children.size(); index++){
				double fitness;
				fitness = Evaluate_FLRulesSet(children.get(i));
				childrenFitnessValues.add(fitness); //New fitness values for the new population
			}
			
			population.clear();
			// Form new population, half from children, half from parent
			for(int index=0; index<children.size(); index++){
				population.add(children.get(i));
			}
			
		}
		
		double highestFitness = fitnessValues.get(0);
		int bestIndex = 0;
		//Select best rule set
		for(int i=0; i<population.size(); i++){
			
			if(fitnessValues.get(i)> highestFitness){
				highestFitness = fitnessValues.get(i);
				bestIndex = i;
			}
		}
		return population.get(bestIndex);
	}
	
	/**
	 * Initialize the population
	 */
	private void InitPopulation(){
		for(int i=0; i<population.size(); i++){
			FuzzyLogicRules newRulesSet = new FuzzyLogicRules(dataSet);
			newRulesSet.RandamizeRules();
			population.add(newRulesSet);
		}
	}
	
	/**
	 * Evaluation function for the specific rules set
	 * @param rulesSet
	 * @return
	 */
	private double Evaluate_FLRulesSet(FuzzyLogicRules rulesSet){
		for(int i=0; i<numPredictionDays; i++){
			// initialize the current account by yesterday's values.
			StockPoint yesterday = dataSet.GetPoint(startIndex - i + 1);
			StockPoint today = dataSet.GetPoint(startIndex - i);
			today.BankingInit(yesterday);			
//			double buyRate = rulesSet.BuyRecommend(today);
			
			if(rulesSet.BuyRecommend(today) >= 0.8){
				today.BuyMax();
				System.out.println("Buy:" + today.GetCalendar());
			}
			
			if(rulesSet.BuyRecommend(today) <= 0.2){
				today.SellMax();
				System.out.println("Sell:" + today.GetCalendar());
			}
			
		}
		
		StockPoint lastDay = dataSet.GetPoint(startIndex - numPredictionDays + 1);
		lastDay.SellMax();
		return lastDay.GetBalance();
		
	}
	
	/**
	 * Tournament selection, select better fuzzy rules set,
	 * the better rules set has higher fitness value
	 * @param index1
	 * @param index2
	 * @return
	 */
	private FuzzyLogicRules TournamentSelection(int index1, int index2){
		if(fitnessValues.get(index1) > fitnessValues.get(index2)){
			return population.get(index1);
		}else{
			return population.get(index2);
		}
	}
	
	public double optimize(){
		return 0;
	}
}
