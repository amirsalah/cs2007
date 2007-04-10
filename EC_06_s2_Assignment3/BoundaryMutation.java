/* Boundary mutation:
 * the varialbe to be mutated will change to the value that is its either left or right bound.
 * each bound with equal probability.
 */
import java.util.Random;

public class BoundaryMutation implements Mutation{
	private TestCase tc;
	private Random ran;
	private double lowerBound, upperBound;
	
	public BoundaryMutation(TestCase tc){
		ran = new Random();
		this.tc = tc;
	}
	
	public void MutationChromosome(double[] chromosome, int varIndex){
		lowerBound = tc.GetLowerBound(chromosome, varIndex);
		upperBound = tc.GetUpperBound(chromosome, varIndex);
		
		if( !ran.nextBoolean() ){
			chromosome[varIndex] = lowerBound;
		}else{
			chromosome[varIndex] = upperBound;
		}
	}
	
	
}
