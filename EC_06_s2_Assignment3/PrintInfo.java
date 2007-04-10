import java.io.IOException;

public class PrintInfo{
	static ConfigParser configHandler= null;
	
	public PrintInfo()throws IOException{
		configHandler = new ConfigParser("config.txt");
	}
	
	public void PrintSettings(TestCase testCase){
		System.out.println("Test case #" + configHandler.GetCaseNumber());
		System.out.println(testCase.mutationMethod);
		if(testCase.mutationMethod == "NonUniformMutation"){	
			System.out.println("Selection pressure: " + testCase.b);
		}
		System.out.println("Corssover rate: " + testCase.crossoverRate);
		System.out.println("Mutation rate: " + testCase.mutationRate);
		if(testCase.NLP){
			System.out.println("Number of iterations: " + testCase.numIterations);
		}
		System.out.println();
	}
	
	public void PrintBestChromosome(TestCase testCase){
		System.out.println("Test best solution: " + "{");
		for(int i=0; i<testCase.numVar; i++){
			System.out.println(testCase.bestChromosome[i] + ", ");
		}
		System.out.println("}");
		if(testCase.NLP == false){
			System.out.println("The optimum: " + testCase.bestResult);
			System.out.println("found in the generation: " + testCase.bestGenerationNum);
		}else{
			System.out.println("The optimum: " + ((NLTestCase)testCase).EvalFunc(testCase.bestChromosome));
			System.out.println("found in the iteration: " + ((NLTestCase)testCase).bestIteration
					 + "  generation: " + testCase.bestGenerationNum);
		}
		
	}
	
	public static void PrintIterations(NLTestCase testCase){
		System.out.println("In iteration: " + testCase.iterationNum);
		System.out.print("Active constraints: ");
		activeConstraints(testCase);
	}
	
	public static void PrintFinalConstraints(NLTestCase testCase){
		System.out.println("Active constraints for the best solution:");
		activeConstraints(testCase);
	}
	
	public static void activeConstraints(NLTestCase testCase){
		if(configHandler.GetCaseNumber() == 2){
 			if(((TestCase2)testCase).c1 == 1)
				System.out.print("c1  ");
			if(((TestCase2)testCase).c2 == 1)
				System.out.print("c2  ");
			if(((TestCase2)testCase).c3 == 1)
				System.out.print("c3  ");
		}
		
		if(configHandler.GetCaseNumber() == 3){
 			if(((TestCase3)testCase).c1 == 1)
				System.out.print("c1  ");
			if(((TestCase3)testCase).c2 == 1)
				System.out.print("c2  ");
			if(((TestCase3)testCase).c3 == 1)
				System.out.print("c3  ");
			if(((TestCase3)testCase).c4 == 1)
				System.out.print("c4  ");
		}
		
		if(configHandler.GetCaseNumber() == 4){
 			if(((TestCase4)testCase).e1 == 1)
				System.out.print("e1  ");
			if(((TestCase4)testCase).e2 == 1)
				System.out.print("e2  ");
			if(((TestCase4)testCase).e3 == 1)
				System.out.print("e3  ");
		}
		System.out.println();
	}
	
}
