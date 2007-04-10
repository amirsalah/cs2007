/***********************************/
/* Author: Bo CHEN                 */
/* Student ID: 1139520             */
/* Date: 2nd, Nov. 2006            */
/***********************************/

import java.io.IOException;

public class Assignment3 {

	public static void main(String[] args) throws IOException{
		TestCase testCase = null;
		InitTestCase initial = new InitTestCase();
		PrintInfo printInfo = new PrintInfo();
		
		/* Initialize the parameters */		
		testCase = initial.ProduceTestCase();
		
		if(testCase.NLP == false){
			GENOCOP_I(testCase);   //Linear test cases
		}else{
			GENOCOP_II(testCase);  //Non linear test cases
		}
		
		printInfo.PrintSettings(testCase);
		printInfo.PrintBestChromosome(testCase);
	}
	
	private static void GENOCOP_I(TestCase testCase)throws IOException{
		/* Creat the initial population */
		testCase.GenPop();
		for(int i=0; i<testCase.maxGenNum; i++){
			testCase.SetCurrentGenNum(i+1);
			testCase.Selection("BiTournamentSelection", "Offsprings");
			testCase.Crossover("ArithmeticalCrossover", "Offsprings");
			testCase.Mutation(testCase.mutationMethod, "Offsprings");
			testCase.Selection("RankingSelection", "ParentsAndOffsprings");
		}
	}
	
	private static void GENOCOP_II(TestCase testCase)throws IOException{
		/* Creat the initial population: containing identical chromosomes */
		testCase.GenPop();
		for(int i=0; i<testCase.numIterations; i++){
			testCase.iterationNum++;
			/* the penalty pressure will be 1, 0.1, 0.01, 0.001 ... */
			testCase.penaltyPressure = Math.pow(10, -(testCase.iterationNum - 1));
			
			/* NOTE: Recompute the bestResultPenalty */
			testCase.bestResultPenalty = ((NLTestCase)testCase).Eval(testCase.bestChromosome);  
		
			AdjustActiveConstraints(((NLTestCase)testCase));
			PrintInfo.PrintIterations(((NLTestCase)testCase));
			
			/*Performance the GENOCOP I system */
			GENOCOP_I(testCase);
			
			/* Set the start point for next iteration: the best chromosome from previous iteration */
			for(int j=0; j<testCase.popSize; j++){
				System.arraycopy(testCase.bestChromosome, 0, testCase.pop[j], 0, testCase.numVar);
			}
		}
		AdjustActiveConstraints(((NLTestCase)testCase));
		PrintInfo.PrintFinalConstraints(((NLTestCase)testCase));
		
	}
	
	public static void AdjustActiveConstraints(NLTestCase testCase){
		// Record all the non linear constraints
		double[] NLConstraints = new double[((NLTestCase)testCase).NLConstraints + ((NLTestCase)testCase).NLEConstraints];
	
		/* Update A: active constraints
		 * Explicit cast is needed for using this function */
		NLConstraints = ((NLTestCase)testCase).ViolationCalculate(testCase.bestChromosome);
		
		for(int j=0; j<NLConstraints.length; j++){
			if(NLConstraints[j] != 0){
				((NLTestCase)testCase).ActivateConstraint(j);
			}else{
				((NLTestCase)testCase).DisableConstraint(j);
			}
		}
	}
}
