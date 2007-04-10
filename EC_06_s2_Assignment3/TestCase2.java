/* Test case #2 */
/* The non-linear constraints will be c1, c2, c3, c4, .... (for non-linear inequation)
 * e1, e2, e3, e4.....(for non-linear equation constraints).
 */
import java.io.IOException;

public class TestCase2 extends NLTestCase {
	double x1, x2, x3, x4, x5, x6, x7, x8; //Variables
	int c1 = 0, c2 = 0, c3 = 0;            //Nonlinear constraints
	
	public TestCase2() throws IOException {
		super();      //Construct the super class
		numVar = 8;   //There are 8 variables for a chromosome
		max = false;  //Indicate this case is a minimization
		iterationNum = 0;
		penaltyPressure = 1.0;
		
		NLConstraints = 3;   //The number of non-linear inequation constraints
		NLEConstraints = 0;  //The number of non-linear equation constraints
		
		pop = new double[popSize][numVar];
		offsprings = new double[popSize][numVar];
		bestChromosome = new double[numVar];
	}
	
	/* Generate initial population, each chromosome has the same values initially */
	public double[][] GenPop(){
		double[] chromosome = new double[numVar];
		
		/* Specify a starting point */
		chromosome[0] = 400;     //x1
		chromosome[1] = 1200;    //x2
		chromosome[2] = 3000;    //x3
		chromosome[3] = 100;     //x4
		chromosome[4] = 100;     //x5
		chromosome[5] = 100;     //x6
		chromosome[6] = 100;     //x7
		chromosome[7] = 100;     //x8
		
		for(int i=0; i<popSize; i++){
			pop[i] = chromosome;
		}
		
		/* Initial the active constraints */
		if(ViolationCalculate(pop[0])[0] != 0){
			c1 = 1;
		}
		if(ViolationCalculate(pop[0])[1] != 0){
			c2 = 1;
		}
		if(ViolationCalculate(pop[0])[2] != 0){
			c3 = 1;
		}
		
		if(!ConstraintsTest(chromosome)){
			System.out.println("Invalid initial population");
		}
		
		/* Initialize the best result */
		bestResultPenalty = Eval(chromosome);
		System.arraycopy(chromosome, 0, bestChromosome, 0, numVar);
		bestGenerationNum = 1;
		bestIteration = iterationNum;
		
		return pop;
	}
	
	/* Test to see if any constraint is violated for a chromosome
	 * Note: for case #2, which is a nonliear problem,
	 * the constraint test is just the test of its linear constraints */
	public boolean ConstraintsTest(double[] chromosome){
		int i;
		VarAssign(chromosome);
		
		if((x1 < 100) || (x1 > 10000)){
			return false;
		}
		
		if((x2 < 1000) || (x2 > 10000) || (x3 < 1000) || (x3 > 10000)){
			return false;
		}
		
		for(i=3; i<8; i++){
			if( (chromosome[i] < 10) || (chromosome[i] > 1000) ){
				return false;
			}
		}
		
		if( (1 - 0.0025*(x4 + x6)) < 0){
			return false;
		}
		
		if( (1 - 0.0025*(x5 + x7 - x4)) < 0 ){
			return false;
		}
		
		if( (1 - 0.01*(x8 - x5)) < 0 ){
			return false;
		}
		
		return true;
	}
	
	public double[][] GetPop(){
		return pop;
	}
	
	/* Assign each variables with the value in the array */
	private void VarAssign(double[] chromosome){
		x1= chromosome[0];
		x2= chromosome[1];
		x3= chromosome[2];
		x4= chromosome[3];
		x5= chromosome[4];
		x6= chromosome[5];
		x7= chromosome[6];
		x8= chromosome[7];
	}
	
	protected void initBestResult(double best){
		bestResult = best;
	}
	
	/* Note: this Evaluate function includes the penalty part */
	public double Eval(double[] chromosome){
		double result;
		double c1ViolateDegree, c2ViolateDegree, c3ViolateDegree;
	
		VarAssign(chromosome);
		
		c1ViolateDegree = ViolationCalculate(chromosome)[0];
		c2ViolateDegree = ViolationCalculate(chromosome)[1];
		c3ViolateDegree = ViolationCalculate(chromosome)[2];
		
		/* add the penalty value for the Minimization func */
		result = x1 + x2 + x3 + (1.0/(2.0*penaltyPressure))*(c1*c1ViolateDegree + c2*c2ViolateDegree + c3*c3ViolateDegree);
		return result;
	}
	
	/* Evaluation exlcude the penalty value */
	public double EvalFunc(double[] chromosome){
		double result;
		
		VarAssign(chromosome);
		
		result = x1 + x2 + x3;
		return result;
	}
	
	public double[] ViolationCalculate(double[] chromosome){
		double[] violateDegree = new double[3];
		
		VarAssign(chromosome);
		
		if((x1*x6 - 833.33252*x4 - 100*x1 + 83333.333) < 0){
			violateDegree[0] = Math.pow( (x1*x6 - 833.33252*x4 - 100*x1 + 83333.333), 2);
		}else{
			violateDegree[0] = 0;
		}
		
		if((x2*x7 - 1250*x5 - x2*x4 + 1250*x4) < 0){
			violateDegree[1] = Math.pow((x2*x7 - 1250*x5 - x2*x4 + 1250*x4), 2);
		}else{
			violateDegree[1] = 0;
		}
		
		if((x3*x8 - 1250000 - x3*x5 + 2500*x5) < 0){
			violateDegree[2] = Math.pow( (x3*x8 - 1250000 - x3*x5 + 2500*x5), 2);
		}else{
			violateDegree[2] = 0;
		}
		
		return violateDegree;
	}
	
	public void ActivateConstraint(int constraintNum){
		
		switch(constraintNum){
		case 0:
			c1 = 1;
			break;
		case 1:
			c2 = 1;
			break;
		case 2:
			c3 = 1;
			break;
		}
	}
	
	public void DisableConstraint(int constraintNum){
		
		switch(constraintNum){
		case 0:
			c1 = 0;
			break;
		case 1:
			c2 = 0;
			break;
		case 2:
			c3 = 0;
			break;
		}
	}
	
	
	
	private double[] GetVarBound(double[] chromosome, int index){
		 /* bound[0] is the lower bound, while [1] is upper bound */
		double[] bound = new double[2];
		
		VarAssign(chromosome);
		
		switch(index){
		/* index == 0, indicating variable x1 */
		case 0:
			bound[0] = 100;
			bound[1] = 10000;
			break;
		case 1:
		case 2:
			bound[0] = 1000;
			bound[1] = 10000;
			break;
		// x4
		case 3:
			bound[0] = Math.max(x5 + x7 - 1.0/0.0025, 10);
			bound[1] = Math.min(1.0/0.0025 - x6, 1000);
			break;
		case 4:
			bound[0] = Math.max(x8 - 1.0/0.01, 10);
			bound[1] = MyMath.Min(1.0/0.0025 - x7 + x4, 1000);
			break;
		// x6
		case 5:
			bound[0] = 10;
			bound[1] = Math.min(1.0/0.0025 - x4, 1000);
			break;
		case 6:
			bound[0] = 10;
			bound[1] = Math.min(1.0/0.0025 + x4 - x5, 1000);
			break;
		case 7:
			bound[0] = 10;
			bound[1] = Math.min(1.0/0.01 + x5, 1000);
			break;
		}
		
		return bound;
	}
	
	/* Return the lower bound of the variable with index provided */
	public double GetLowerBound(double[] chromosome, int index){
		return GetVarBound(chromosome, index)[0];
	}
	
	public double GetUpperBound(double[] chromosome, int index){
		return GetVarBound(chromosome, index)[1];
	}
	
}
