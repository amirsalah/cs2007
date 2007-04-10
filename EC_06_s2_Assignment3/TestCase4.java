import java.io.IOException;

/* Test case #4 */

public class TestCase4 extends NLTestCase{
	double x1, x2, x3, x4, x5;
	int e1=0, e2=0, e3=0;
	
	public TestCase4() throws IOException {
		super();      //Construct the super class
		numVar = 5;   //There are 5 variables for a chromosome
		max = false;  //Indicate this case is a minimization
		iterationNum = 0;
		penaltyPressure = 1.0;
		
		NLConstraints = 0;   //The number of non-linear inequation constraints
		NLEConstraints = 3;  //The number of non-linear equation constraints
		
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
		
		for(int i=0; i<popSize; i++){
			pop[i] = chromosome;
		}
		
		/* Initial the active constraints */
		if(ViolationCalculate(pop[0])[0] != 0){
			e1 = 1;
		}
		if(ViolationCalculate(pop[0])[1] != 0){
			e2 = 1;
		}
		if(ViolationCalculate(pop[0])[2] != 0){
			e3 = 1;
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
		VarAssign(chromosome);
		
		if( (Math.abs(x1) > 2.3) || (Math.abs(x2) > 2.3)){
			return false;
		}
		
		if( (Math.abs(x3) > 3.2) || (Math.abs(x4) > 3.2) || (Math.abs(x5) > 3.2)){
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
	}
	
	protected void initBestResult(double best){
		bestResult = best;
	}
	
	/* Note: this Evaluate function includes the penalty part */
	public double Eval(double[] chromosome){
		double result;
		double e1ViolateDegree, e2ViolateDegree, e3ViolateDegree;
	
		if(chromosome.length != numVar){
			System.out.println("Invalid number of varialbes to be evaluated in Test case 4");
		}
		VarAssign(chromosome);
		
		e1ViolateDegree = ViolationCalculate(chromosome)[0];
		e2ViolateDegree = ViolationCalculate(chromosome)[1];
		e3ViolateDegree = ViolationCalculate(chromosome)[2];
	
		result = Math.pow(Math.E, x1*x2*x3*x4*x5) + (1.0/(2.0*penaltyPressure))
				*(e1*e1ViolateDegree + e2*e2ViolateDegree + e3*e3ViolateDegree);
		
		return result;
	}
	
	/* Evaluation exlcude the penalty value */
	public double EvalFunc(double[] chromosome){
		double result;
		
		VarAssign(chromosome);
		
		result = Math.pow(Math.E, x1*x2*x3*x4*x5);
		return result;
	}
	
	public double[] ViolationCalculate(double[] chromosome){
		double[] violateDegree = new double[4];
		
		VarAssign(chromosome);
		
		if(x1*x1 + x2*x2 + x3*x3 + x4*x4 + x5*x5 != 10){
			violateDegree[0] = Math.abs(x1*x1 + x2*x2 + x3*x3 + x4*x4 + x5*x5 - 10);
		}else{
			violateDegree[0] = 0;
		}
		
		if((x2*x3 - 5*x4*x5) != 0){
			violateDegree[1] = Math.abs(x2*x3 - 5*x4*x5);
		}else{
			violateDegree[1] = 0;
		}
		
		if((Math.pow(x1, 3) + Math.pow(x2, 3)) != -1){
			violateDegree[2] = Math.abs( Math.pow(x1, 3) + Math.pow(x2, 3) + 1);
		}else{
			violateDegree[2] = 0;
		}
	
		return violateDegree;
	}
	
	public void ActivateConstraint(int constraintNum){
		
		switch(constraintNum){
		case 0:
			e1 = 1;
			break;
		case 1:
			e2 = 1;
			break;
		case 2:
			e3 = 1;
			break;

		}
	}
	
	public void DisableConstraint(int constraintNum){
		
		switch(constraintNum){
		case 0:
			e1 = 0;
			break;
		case 1:
			e2 = 0;
			break;
		case 2:
			e3 = 0;
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
			bound[0] = -2.3;
			bound[1] = 2.3;
			break;
		case 2:
		case 3:
		case 4:
			bound[0] = -3.2;
			bound[1] = 3.2;
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



