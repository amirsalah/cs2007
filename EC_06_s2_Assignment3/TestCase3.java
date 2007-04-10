/* Test case #3 */
import java.io.IOException;

public class TestCase3 extends NLTestCase{
	double x1, x2, x3, x4, x5, x6, x7;
	int c1=0, c2=0, c3=0, c4=0;
	
	public TestCase3() throws IOException {
		super();      //Construct the super class
		numVar = 7;   //There are 7 variables for a chromosome
		max = false;  //Indicate this case is a minimization
		iterationNum = 0;
		penaltyPressure = 1.0;
		
		NLConstraints = 4;   //The number of non-linear inequation constraints
		NLEConstraints = 0;  //The number of non-linear equation constraints
		
		pop = new double[popSize][numVar];
		offsprings = new double[popSize][numVar];
		bestChromosome = new double[numVar];
	}
	
	/* Generate initial population, each chromosome has the same values initially */
	public double[][] GenPop(){
		double[] chromosome = new double[numVar];
		
		/* Specify a starting point */
		chromosome[0] = 0;     //x1
		chromosome[1] = 0;     //x2
		chromosome[2] = 0;     //x3
		chromosome[3] = 0;     //x4
		chromosome[4] = 0;     //x5
		chromosome[5] = 0;     //x6
		chromosome[6] = 0;     //x7
		
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
		if(ViolationCalculate(pop[0])[3] != 0){
			c4 = 1;
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
	
	/* There is no linear constraints, just the domains of varialbes */
	public boolean ConstraintsTest(double[] chromosome){
		int i;
		VarAssign(chromosome);
	
		for(i=0; i<7; i++){
			if( (chromosome[i] < -10.0) || (chromosome[i] > 10.0) ){
				return false;
			}
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
	}
	
	protected void initBestResult(double best){
		bestResult = best;
	}
	
	/* Note: this Evaluate function includes the penalty part */
	public double Eval(double[] chromosome){
		double result;
		double c1ViolateDegree, c2ViolateDegree, c3ViolateDegree, c4ViolateDegree;
	
		if(chromosome.length != numVar){
			System.out.println("Invalid number of varialbes to be evaluated in Test case 3");
		}
		VarAssign(chromosome);
		
		c1ViolateDegree = ViolationCalculate(chromosome)[0];
		c2ViolateDegree = ViolationCalculate(chromosome)[1];
		c3ViolateDegree = ViolationCalculate(chromosome)[2];
		c4ViolateDegree = ViolationCalculate(chromosome)[3];
		
		result = (x1 - 10.0)*(x1 - 10.0) + 5*(x2 - 12)*(x2 - 12) + Math.pow(x3, 4)
				 + 3*(x4 - 11)*(x4 -11) + 10*Math.pow(x5, 6) + 7*Math.pow(x6, 2) 
				 + Math.pow(x7, 4) - 4*x6*x7 - 10*x6 - 8*x7
				 + (1.0/(2.0*penaltyPressure))*(c1*c1ViolateDegree + c2*c2ViolateDegree 
						 + c3*c3ViolateDegree + c4*c4ViolateDegree);
		
		return result;
	}
	
	/* Evaluation exlcude the penalty value */
	public double EvalFunc(double[] chromosome){
		double result;
		
		VarAssign(chromosome);
		
		result = (x1 - 10.0)*(x1 - 10.0) + 5*(x2 - 12)*(x2 - 12) + Math.pow(x3, 4)
		 + 3*(x4 - 11)*(x4 -11) + 10*Math.pow(x5, 6) + 7*Math.pow(x6, 2) 
		 + Math.pow(x7, 4) - 4*x6*x7 - 10*x6 - 8*x7;
		
		return result;
	}
	
	public double[] ViolationCalculate(double[] chromosome){
		double[] violateDegree = new double[4];
		
		VarAssign(chromosome);
		
		if( (127 - 2*x1*x1 - 3*Math.pow(x2, 4) - x3 - 4*x4*x4 - 5*x5) < 0){
			violateDegree[0] = Math.pow( 127 - 2*x1*x1 - 3*Math.pow(x2, 4) - x3 - 4*x4*x4 - 5*x5, 2);
		}else{
			violateDegree[0] = 0;
		}
		
		if( (282 - 7*x1 - 3*x2 - 10*x3*x3 - x4 + x5) < 0){
			violateDegree[1] = Math.pow( 282 - 7*x1 - 3*x2 - 10*x3*x3 - x4 + x5, 2);
		}else{
			violateDegree[1] = 0;
		}
		
		if( (196 - 23*x1 - x2*x2 - 6*x6*x6 + 8*x7) < 0){
			violateDegree[2] = Math.pow(196 - 23*x1 - x2*x2 - 6*x6*x6 + 8*x7, 2);
		}else{
			violateDegree[2] = 0;
		}
		
		if( ((-4)*x1*x1 - x2*x2 + 3*x1*x2 - 2*x3*x3 - 5*x6 + 11*x7) < 0){
			violateDegree[3] = Math.pow((-4)*x1*x1 - x2*x2 + 3*x1*x2 - 2*x3*x3 - 5*x6 + 11*x7, 2);
		}else{
			violateDegree[3] = 0;
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
		case 3:
			c4 = 1;
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
		case 3:
			c4 = 0;
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
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			bound[0] = -10.0;
			bound[1] = 10.0;
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
