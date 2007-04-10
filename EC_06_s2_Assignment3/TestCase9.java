/* Test case #9 */
import java.io.IOException;
import java.util.Random;

public class TestCase9 extends TestCase{
	double x1, x2, x3;
	
	public TestCase9()throws IOException{
		super();      //Construct the super class
		numVar = 3;   //There are 3 variables for a chromosome
		max = true;   //Indicate this case is a Maximization problem
		
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
	
	/* Generate a chromosome without testing its vadility */
	private double[] GenChromosome(){
		Random ran = new Random();
		double[] chromosome = new double[numVar];
		
		/* 
		 * Generate random number for x1, x2, x3
		 * Note that, by roughly observation that, any of the 3 variables cannot exceed 5
		 * since 7*x3<=29.1 while x1 and x2 are 0
		 */
		for(int i=0; i<3; i++){
			chromosome[i] = ran.nextDouble()*5;
		}
		return chromosome;
	}
	
	public double[][] GetPop(){
		return pop;
	}
	
	/* Assign each variables with the value in the array */
	private void VarAssign(double[] chromosome){
		x1 = chromosome[0];
		x2 = chromosome[1];
		x3 = chromosome[2];
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
	
		result = (3*x1 + x2 - 2*x3 + 0.8)/(2*x1 - x2 + x3)
				 + (4*x1 - 2*x2 + x3)/(7*x1 + 3*x2 - x3);
		
		return result;
	}
	
	/* Test to see if any constraint is violated for a chromosome */
	public boolean ConstraintsTest(double[] chromosome){
		
		VarAssign(chromosome);
		
		if((x1 + x2 - x3) > 1){
			return false;
		}
		
		if((x2 - x1 - x3) > -1){
			return false;
		}
		
		if((12*x1 + 5*x2 + 12*x3) > 34.8){
			return false;
		}
		
		if((12*x1 + 12*x2 + 7*x3) > 29.1){
			return false;
		}
		
		if(((-6)*x1 + x2 + x3) > -4.1){
			return false;
		}
		
		if((x1 < 0) || (x2 < 0) || (x3 < 0)){
			return false;
		}
		
		return true;
	}
	
	private double[] GetVarBound(double[] chromosome, int index){
		double[] bound = new double[2]; //bound[0] is the lower bound, while [1] is upper bound
		
		VarAssign(chromosome);
		
		switch(index){
		/* index == 0, indicating variable x1 */
		case 0:
			bound[0] = MyMath.Max(x2 - x3 + 1, (4.1 + x2 + x3)/6, 0);
			bound[1] = MyMath.Min((34.8 - 5*x2 - 12*x3)/12, (29.1 - 12*x2 - 7*x3)/12, 1 + x3 - x2);
			break;
		case 1:
			bound[0] = 0;
			bound[1] = MyMath.Min(1 + x3 - x1, x1 + x3 - 1, (34.8 - 12*x1 - 12*x3)/5, (29.1 - 12*x1 - 7*x3)/12, 6*x1 - x3 - 4.1);
			break;
		case 2:
			bound[0] = MyMath.Max(x1 + x2 - 1, x2 - x1 + 1, 0);
			bound[1] = MyMath.Min(6*x1 - x2 - 4.1, (34.8 - 12*x1 - 5*x2)/12, (29.1 - 12*x1 - 12*x2)/7);
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





