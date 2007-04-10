/* The arithmetical crossover */
import java.util.Random;

public class ArithmeticalCrossover implements Crossover {
	private double a;     //The ratio of crossover
	private Random ran;   //Used to generate random number of a between [0,1]
	private int numVar;   //Number of variables for current test case
	private TestCase tc;

	public ArithmeticalCrossover(TestCase tc){
		ran = new Random();
		this.tc = tc;
		numVar = this.tc.GetNumVar();
		a = 0.5;
	}
	
	public void CrossoverChromosomes(double[] chromosome1, double[] chromosome2){
		a = ran.nextDouble();
		
//		System.out.println("numVar:" + chromosome1.length);
		for(int i=0; i<numVar; i++){
			chromosome1[i] = a*chromosome1[i] + (1-a)*chromosome2[i];
			chromosome2[i] = a*chromosome2[i] + (1-a)*chromosome1[i];
		}
		
		return;
	}
}
