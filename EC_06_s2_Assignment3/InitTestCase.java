import java.io.IOException;


public class InitTestCase {
	ConfigParser configHandler= null;
	
	public InitTestCase() throws IOException{
		configHandler = new ConfigParser("config.txt");
	}
	
	public TestCase ProduceTestCase() throws IOException{
		TestCase testCase = null;
		
		/* Produce test case object */
		switch(configHandler.GetCaseNumber()){
		case 1:
			testCase = new TestCase1();
			break;
		case 2:
			testCase = new TestCase2();
			testCase.NLP = true;	
			break;
		case 3:
			testCase = new TestCase3();
			testCase.NLP = true;
			break;
		case 4:
			testCase = new TestCase4();
			testCase.NLP = true;
			break;
		case 5:
		case 6:
			System.out.println("Test case 5, 6, 8, 10, 11 are not implemented");
			System.out.println("Please input 1, 2, 3, 4, 7, 9 or 12 as the case number");
			System.exit(-1);
			break;
		case 7:
			testCase = new TestCase7();
			break;
		case 8:
			System.out.println("Test case 5, 6, 8, 10, 11 are not implemented");
			System.out.println("Please input 1, 2, 3, 4, 7, 9 or 12 as the case number");
			System.exit(-1);
			break;
		case 9:
			testCase = new TestCase9();
			break;
		case 10:
		case 11:
			System.out.println("Test case 5, 6, 8, 10, 11 are not implemented");
			System.out.println("Please input 1, 2, 3, 4, 7, 9 or 12 as the case number");
			System.exit(-1);
			break;
		case 12:
			testCase = new TestCase12();
			break;
		}
		return testCase;
	}
	
	public void InitParameters(TestCase testCase) throws IOException{

		/* Parameters setting */
		if(configHandler.DefaultParameters()){
			/* Use default parameters for this test case */
			switch(configHandler.GetCaseNumber()){
			case 1:
				testCase.popSize = 90;
				testCase.maxGenNum = 1000;
				testCase.crossoverRate = 0.02;
				testCase.mutationRate = 0.002;
				testCase.b = 0.1;
				testCase.mutationMethod = "BoundaryMutation";
				break;
			case 2:
				testCase.popSize = 90;
				testCase.maxGenNum = 1000;
				testCase.crossoverRate = 0.2;
				testCase.mutationRate = 0.09;
				testCase.b = 6;
				testCase.numIterations = 4;
				testCase.mutationMethod = "NonUniformMutation";
				break;
			case 3:
				testCase.popSize = 90;
				testCase.maxGenNum = 1000;
				testCase.crossoverRate = 0.2;
				testCase.mutationRate = 0.08;
				testCase.b = 1.8;
				testCase.numIterations = 2;
				testCase.mutationMethod = "NonUniformMutation";
				break;
			case 4:
				testCase.popSize = 90;
				testCase.maxGenNum = 1000;
				testCase.crossoverRate = 0.1;
				testCase.mutationRate = 0.09;
				testCase.b = 0.5;
				testCase.numIterations = 3;
				testCase.mutationMethod = "NonUniformMutation";
				break;
			case 7:
				testCase.popSize = 90;
				testCase.maxGenNum = 1000;
				testCase.crossoverRate = 0.02;
				testCase.mutationRate = 0.02;
				testCase.b = 0.2;
				testCase.mutationMethod = "BoundaryMutation";
				break;
			case 9:
				testCase.popSize = 90;
				testCase.maxGenNum = 1000;
				testCase.crossoverRate = 0.02;
				testCase.mutationRate = 0.09;
				testCase.b = 1;
				testCase.mutationMethod = "NonUniformMutation";
				break;
			case 12:
				testCase.popSize = 90;
				testCase.maxGenNum = 1000;
				testCase.crossoverRate = 0.2;
				testCase.mutationRate = 0.08;
				testCase.b = 2;
				testCase.mutationMethod = "NonUniformMutation";
				break;
			}
		}
	}
}
