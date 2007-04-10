/* Test case #12 */
import java.io.IOException;
import java.util.Random;
import java.lang.Math;

public class TestCase12 extends TestCase{
	double x1, x2;
	
	public TestCase12() throws IOException {
		super();      //Construct the super class
		numVar = 2;   //There are 2 variables for a chromosome
		max = false;  //Indicate this case is a minimization
		
		pop = new double[popSize][numVar];
		offsprings = new double[popSize][numVar];
		bestChromosome = new double[numVar];
	}
	
	/* Generate initial population */
	public double[][] GenPop(){
		for(int i=0; i<popSize; i++){
			double[] chromosome = new double[numVar];
			chromosome = GenChromosome();
			if(ConstraintsTest(chromosome)){
				pop[i] = chromosome;
				/* Init best result */
				if(i==0){
					initBestResult(Eval(chromosome));
				}
			}else{
				/* The chromosme is not feasible */
				i--;
			}
		}
		return pop;
	}
	
	/* Test to see if any constraint is violated for a chromosome */
	public boolean ConstraintsTest(double[] chromosome){
		
		VarAssign(chromosome);
		
		if((x1/Math.sqrt(3) - x2) < 0){
			return false;
		}
		
		if( (6 - x1 - Math.sqrt(3)* x2) < 0 ){
			return false;
		}
		
		if( (x1 < 0) || (x1 > 6) || (x2 < 0)){
			return false;
		}
		
		return true;
		
	}
	
	
	/* Generate a chromosome without testing its vadility */
	private double[] GenChromosome(){
		Random ran = new Random();
		double[] chromosome = new double[numVar];
		
		/* Generate x1, x2.
		 * by observation: x2 <= x1/sqrt(3) */
		chromosome[0] = ran.nextDouble()*6;
		chromosome[1] = ran.nextDouble()*4;
		
		return chromosome;
	}
	
	public double[][] GetPop(){
		return pop;
	}
	
	/* Assign each variables with the value in the array */
	private void VarAssign(double[] chromosome){
		x1= chromosome[0];
		x2= chromosome[1];
	}
	
	protected void initBestResult(double best){
		bestResult = best;
	}
	
	public double Eval(double[] chromosome){
		double result;
	
		if(chromosome.length != numVar){
			System.out.println("Invalid number of varialbes to be evaluated in Test case 1");
		}
		VarAssign(chromosome);
		
		if( (x1 >= 0) && (x1 < 2) ){
			result = x2 + Math.pow(10, -5)*(x2 - x1)*(x2 - x1) - 1.0;
			return result;
		}
		
		if( (x1 >= 2) && (x1 < 4) ){
			result = (1.0/(27.0*Math.sqrt(3))) * ( (x1 - 3)*(x1 - 3) - 9)*x2*x2*x2;
			return result;
		}
		
		if( (x1 >= 4) && (x1 <= 6) ){
			result = (1.0/3.0) * (x1 - 2)*(x1 - 2)*(x1 - 2) + x2 - 11.0/3.0;
			return result;
		}
		
		/* Otherwise, the variables are incorrect */
		return 1000;
	}
	
	private double[] GetVarBound(double[] chromosome, int index){
		 /* bound[0] is the lower bound, while [1] is upper bound */
		double[] bound = new double[2];
		
		VarAssign(chromosome);
		
		switch(index){
		/* index == 0, indicating variable x1 */
		case 0:
			bound[0] = Math.max((Math.sqrt(3))*x2, 0);
			bound[1] = Math.min(6 - (Math.sqrt(3))*x2, 6);
			break;
		case 1:
			bound[0] = 0;
			bound[1] = Math.min(x1/(Math.sqrt(3)), (6 - x1)/Math.sqrt(3));
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
