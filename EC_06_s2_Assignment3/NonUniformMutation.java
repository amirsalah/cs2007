/* Non-uniform mutation 
 * */
import java.util.Random;
import java.io.IOException;
import java.lang.Math;

public class NonUniformMutation implements Mutation{
	private double b = 3;  //b controls the selection pressure, pressure increases with enlarging b
	private int T;     //Maximum generation number
	private int t;     //Current generation number
	private double y;  // y = right(k) - x or x - left(k)
	private TestCase tc;
	
	private Random ran;
	private double lowerBound, upperBound;
	
	public NonUniformMutation(TestCase tc)throws IOException{
		this.tc = tc;
		this.b = tc.b;
		T = this.tc.GetMaxGenNum();
		ran = new Random();
	}
	
	public void MutationChromosome(double[] chromosome, int varIndex){
		lowerBound = tc.GetLowerBound(chromosome, varIndex);
		upperBound = tc.GetUpperBound(chromosome, varIndex);
		t = tc.GetCurrentGenNum();
		
		/* Increase the variable if a random binary digit is false, decrease otherwise */
		/* NOTE: t/T always == 0 because t and T is integer, rather double */
		if( !ran.nextBoolean() ){
			y = upperBound - chromosome[varIndex];
			chromosome[varIndex] += y*ran.nextDouble()*Math.pow((1-(double)t/(double)T), b);
		}else{
			y = chromosome[varIndex] - lowerBound;
			chromosome[varIndex] -= y*ran.nextDouble()*Math.pow((1-(double)t/(double)T), b);
		}
		return;
	}
}
