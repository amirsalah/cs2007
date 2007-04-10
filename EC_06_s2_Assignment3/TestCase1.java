/* The Test case #1 */
/* ********************************************************** */
/* chromosome[0]=x1, [1]=x2, [2]=x3, [4]=y1, [5]=y2 [6]=y3     */
/* [7]=y4, [8]=y5, [9]=y6, [10]=y7, [11]=y8, [12]=y9          */
/* ********************************************************** */
//import java.util.ArrayList;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;


public class TestCase1 extends TestCase {
	double x1, x2, x3, x4, y1, y2, y3, y4, y5, y6, y7, y8, y9;  //The variables for the evaluation func
	 
	public TestCase1() throws IOException{
		super();      //Construct the super class
		numVar = 13;  //There are 13 variables for a chromosome
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
		int i = 0;
		
		VarAssign(chromosome);
		
		//Test 2*x1+2*x2+y6+y7 <=10
		if( (2*x1 + 2*x2 + y6 + y7) > 10 ){
			return false;
		}
		
		if( (2*x1 + 2*x3 + y6 + y8) > 10 ){
			return false;
		}
		
		if( (2*x2 + 2*x3 + y7 + y8) > 10 ){
			return false;
		}
		
		if( ((-8)*x1 + y6) > 0 ){
			return false;
		}
		
		if( ((-8)*x2 +y7) > 0 ){
			return false;
		}
		
		if( ((-8)*x3 + y8) > 0 ){
			return false;
		}
		
		if( ((-2)*x4 - y1 + y6) > 0 ){
			return false;
		}
		
		if( ((-2)*y2 - y3 + y7) > 0 ){
			return false;
		}
		
		if( ((-2)*y4 - y5 + y8) > 0 ){
			return false;
		}
		
		
		//Test (0 <= x[i] <= 1)
		for(i=0; i<4; i++){
			if((chromosome[i] < 0) || (chromosome[i] > 1)){
				return false;
			}
		}
		
		//Test y[i] i=123459
		if( (y1<0) || (y1>1) || (y2<0) || (y2>1) || (y3<0) || (y3>1) || (y4<0) || (y4>1) || 
				(y5<0) || (y5>1) || (y9<0) || (y9>1) ){
			return false;
		}
		
		if( (y6<0) || (y7<0) || (y8<0) ){
			return false;
		}
		
		/* Satisfy all the constraints */
		return true;
	}
	

	/* Generate a chromosome without testing its vadility */
	private double[] GenChromosome(){
		Random ran = new Random();
		double[] chromosome = new double[numVar];
		
		/* Generate random number for x1 - x4 */
		for(int i=0; i<4; i++){
			chromosome[i] = ran.nextDouble();
		}
		
		/* Generate random number for y1 - y5 */
		for(int i=0; i<5; i++){
			chromosome[i+4] = ran.nextDouble();
		}
		
		/* Generate randome number for y9 */
		chromosome[12] = ran.nextDouble();
		
		/* 
		 * Generate randome number for y6 y7 y8
		 * By observation from the constraints, none of these 3 chromosome can exceed 14
		 */
		for(int i=0; i<3; i++){
			chromosome[i+9] = ran.nextDouble()*14;
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
		x4 = chromosome[3];
		y1 = chromosome[4];
		y2 = chromosome[5];
		y3 = chromosome[6];
		y4 = chromosome[7];
		y5 = chromosome[8];
		y6 = chromosome[9];
		y7 = chromosome[10];
		y8 = chromosome[11];
		y9 = chromosome[12];
	}
	
	protected void initBestResult(double best){
		bestResult = best;
	}
	
	/* The evaluation function */
	public double Eval(double[] chromosome){
		double result;
		
		if(chromosome.length != numVar){
			System.out.println("Invalid number of varialbes to be evaluated in Test case 1");
		}
		VarAssign(chromosome);
		
		result = 5*x1 + 5*x2 + 5*x3 + 5*x4 -5*(x1*x1 + x2*x2 + x3*x3 + x4*x4)
				- (y1 + y2 + y3 + y4 + y5 + y6 + y7 + y8 + y9);
		return result;
	}
	
	private double[] GetVarBound(double[] chromosome, int index){
		double[] bound = new double[2]; //bound[0] is the lower bound, while [1] is upper bound
		
		VarAssign(chromosome);
		
		switch(index){
		/* index == 0, indicating variable x1 */
		case 0:
			bound[0] = Math.max(y6/8, 0);
			bound[1] = Math.min( Math.min((10-2*x2-y6-y7)/2, (10-2*x3-y6-y8)/2), 1 );
			break;
		case 1:
			bound[0] = Math.max(y7/8, 0);
			bound[1] = Math.min( Math.min((10-2*x1-y6-y7)/2, (10-2*x3-y7-y8)/2), 1);
			break;
		case 2:
			bound[0] = Math.max(y8/8, 0);
			bound[1] = Math.min( Math.min((10-2*x1-y6-y8)/2, (10-2*x2-y7-y8)/2), 1);
			break;
		case 3:
			bound[0] = Math.max((y6-y1)/2, 0);
			bound[1] = 1;
			break;
		case 4:
			bound[0] = Math.max(y6-2*x4, 0);
			bound[1] = 1;
			break;
		case 5:
			bound[0] = Math.max((y7-y3)/2, 0);
			bound[1] = 1;
			break;
		case 6:
			bound[0] = Math.max(y7-2*y2, 0);
			bound[1] = 1;
			break;
		case 7:
			bound[0] = Math.max((y8-y5)/2, 0);
			bound[1] = 1;
			break;
		case 8:
			bound[0] = Math.max(y8-2*y4, 0);
			bound[1] = 1;
			break;
		case 9:
			bound[0] = 0;
			bound[1] = MyMath.Min(10-2*x1-2*x2-y7, 10-2*x1-2*x3-y8, 8*x1, 2*x4+y1);
			break;
		case 10:
			bound[0] = 0;
			bound[1] = MyMath.Min(10-2*x1-2*x2-y6, 10-2*x1-2*x3-y8, 8*x2, 2*y2+y3);
			break;
		case 11:
			bound[0] = 0;
			bound[1] = MyMath.Min(10-2*x1-2*x3-y6, 10-2*x1-2*x3-y7, 8*x3, 2*y4+y5);
			break;
		case 12:
			bound[0] = 0;
			bound[1] = 1;
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





