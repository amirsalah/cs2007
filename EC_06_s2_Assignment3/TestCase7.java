/* The test case #7 */

import java.io.IOException;
import java.util.Random;
import java.lang.Math;

public class TestCase7 extends TestCase{
	double x1, x2, x3, x4, x5, y;
	
	public TestCase7() throws IOException {
		super();      //Construct the super class
		numVar = 6;   //There are 6 variables for a chromosome
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
//				System.out.println("Valid chromosome:" + i);
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
		int i = 0;
		
		VarAssign(chromosome);
		
		if( (6*x1 + 3*x2 + 3*x3 + 2*x4 + x5) > 6.5 ){
			return false;
		}
		
		if( (10*x1 + 10*x3 + y) > 20 ){
			return false;
		}
		
		for(i=0; i<5; i++){
			if( (chromosome[i] > 1) || (chromosome[i] < 0) ){
				return false;
			}
		}
		
		if( y < 0){
			return false;
		}
		
		return true;
	}
	
	/* Generate a chromosome without testing its vadility */
	private double[] GenChromosome(){
		Random ran = new Random();
		double[] chromosome = new double[numVar];
		
		/* Generate x1, x2, x3, x4, x5 */
		for(int i=0; i<5; i++){
			chromosome[i] = ran.nextDouble();
		}
		
		/* Generate y
		 * By observation, 0 <= y <= 20 - 10*x1 - 10*x3 ==> y <= 70
		 */
		chromosome[numVar-1] = ran.nextInt(70);
		
		return chromosome;
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
		y = chromosome[5];
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
		
		result = (-10.5)*x1 -7.5*x2 - 3.5*x3 - 2.5*x4 - 1.5*x5 - 10*y
				 - 0.5*(x1*x1 + x2*x2 + x3*x3 + x4*x4 + x5*x5);
		
		return result;
	}
	
	private double[] GetVarBound(double[] chromosome, int index){
		 /* bound[0] is the lower bound, while [1] is upper bound */
		double[] bound = new double[2];
		
		VarAssign(chromosome);
		
		switch(index){
		/* index == 0, indicating variable x1 */
		case 0:
			bound[0] = 0;
			bound[1] = MyMath.Min((6.5 - 3*x2 - 3*x3 - 2*x4 - x5)/6, (20 - 10*x3 - y)/10, 1);
			break;
		
			/* x2 */
		case 1:
			bound[0] = 0;
			bound[1] = Math.min((6.5 - 6*x1 - 3*x3 - 2*x4 - x5)/3, 1);
			break;
			
		case 2:
			bound[0] = 0;
			bound[1] = MyMath.Min((6.5 - 6*x1 - 3*x2 - 2*x4 - x5)/3, (20 - 10*x1 - y)/10, 1);
			break;
			
		case 3:
			bound[0] = 0;
			bound[1] = Math.min((6.5 - 6*x1 - 3*x2 - 3*x3 - x5)/2, 1);
			break;
			
		case 4:
			bound[0] = 0;
			bound[1] = Math.min(6.5 - 6*x1 - 3*x2 - 3*x3 - 2*x4, 1);
			break;
			
		case 5:
			bound[0] = 0;
			bound[1] = 20 - 10*x1 - 10*x3;
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



