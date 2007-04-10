/* Global rank based selection 
 * chromosomes with higher fitness will survive
 */

import java.util.ArrayList;


public class RankingSelection implements Selection{
	private int numVar;
	private TestCase tc;
	private double[][] newChromosomes;  //New population 
	private ArrayList<Double> fitness;  //Fitness of each chromosome	
	public RankingSelection(TestCase tc){
		this.tc = tc;
		numVar = this.tc.GetNumVar();
	}
	
	public double[][] select(double[][] chromosomes, int num){
		newChromosomes = new double[num][numVar];
		fitness = new ArrayList<Double>(chromosomes.length);
		double evalFitness;
		
		/* Calculate the evaluated values of chromosomes from the function */
		for(int i=0; i<chromosomes.length; i++){
			fitness.add(tc.Eval(chromosomes[i]));
		}
		
		/* Differentiate maximization and minimization problems */
		if(tc.Max()){
			/* Select 'num' best chromosomes from the population (parents+offsprings) */
			for(int i=0; i<num; i++){
				/* 
				 * Travel the fitness array list to find the best chromosome,
				 * Note the length of array list varies with EVERY loop 
				 */
				evalFitness = fitness.get(i);
				for(int j=i; j<fitness.size(); j++){
					/* Find better chromosome, swap it with the first chro i(Note: not 0) */
					if(fitness.get(j) > evalFitness){
						double[] tempChro1 = new double[numVar];						
						tempChro1 = chromosomes[i];
						chromosomes[i] = chromosomes[j];
						chromosomes[j] = tempChro1;
						
						evalFitness = fitness.get(j);
						fitness.add(i, fitness.remove(j)); // swap the fitness values
					}
				}
				/* Record the global best chromosome */
				if(tc.NLP == false){
					if(fitness.get(0) > tc.bestResult){
						tc.bestResult = fitness.get(0);
						tc.bestChromosome = chromosomes[0];
						tc.bestGenerationNum = tc.currentGenerationNum;
					}
				}else{
					/* For non-linear func, it's the best result taking the penalty value into account */
					if(fitness.get(0) > tc.bestResultPenalty){
						tc.bestResultPenalty = fitness.get(0);
						tc.bestChromosome = chromosomes[0];
						tc.bestGenerationNum = tc.currentGenerationNum;
						((NLTestCase)tc).bestIteration = tc.iterationNum;
					}
				}
			}
		}else{
			/* For minimization problem */
			for(int i=0; i<num; i++){
				evalFitness = fitness.get(i);
				for(int j=i; j<fitness.size(); j++){
					/* Find better chromosome, swap it with the first chro i(Note: not 0) */
					if(fitness.get(j) < evalFitness){
						double[] tempChro1 = new double[numVar];
						
						tempChro1 = chromosomes[i];
						chromosomes[i] = chromosomes[j];
						chromosomes[j] = tempChro1;
						
						evalFitness = fitness.get(j);
						fitness.add(i, fitness.remove(j)); // adjust the ith fitness values
					}
				}
			}
			if(tc.NLP == false){
				if(fitness.get(0) < tc.bestResult){
					tc.bestResult = fitness.get(0);
					tc.bestChromosome = chromosomes[0];
					tc.bestGenerationNum = tc.currentGenerationNum;
				}
			}else{ 
				/* For non-linear func, it's the best result taking the penalty value into account */
				if(fitness.get(0) < tc.bestResultPenalty){
					tc.bestResultPenalty = fitness.get(0);
					tc.bestChromosome = chromosomes[0];
					tc.bestGenerationNum = tc.currentGenerationNum;
					((NLTestCase)tc).bestIteration = tc.iterationNum;
				}
			}
		}
		
		for(int i=0; i<num; i++){
			newChromosomes[i] = chromosomes[i];
		}
		
		return newChromosomes;
	}
	
	public double[] GetBestChromosome(){
		return newChromosomes[0];
	}
}
