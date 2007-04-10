/* Binary ranking selection
 * chromosomes is the parents, num is the number of chromosomes to be selected
 */
import java.util.Random;

public class BiTournamentSelection implements Selection{
	private int numVar;
	private TestCase tc;
	private double[][] newChromosomes;
	
	public BiTournamentSelection(TestCase tc){
		this.tc = tc;
		numVar = this.tc.GetNumVar();
	}
	
	public double[][] select(double[][] chromosomes, int num){
		newChromosomes = new double[num][numVar];
		Random ran = new Random();
		int chroIndex1, chroIndex2;
		
		/* Select 'num' new chromosomes */
		for(int i=0; i<num; i++){
			
			/* Choose 2 different chromosomes */
			while(true){
				chroIndex1 = ran.nextInt(num);
				chroIndex2 = ran.nextInt(num);
				if(chroIndex1 != chroIndex2){
					break;
				}
			}
			
			/* Tournament between the 2 chromosomes */	
			if(tc.Max() == true){
				/* For maximization func */
				if(tc.Eval(chromosomes[chroIndex1]) >= tc.Eval(chromosomes[chroIndex2])){
					newChromosomes[i] = chromosomes[chroIndex1];
				}else{
					newChromosomes[i] = chromosomes[chroIndex2];
				}
			}else{
				/* For minimization function */
				if(tc.Eval(chromosomes[chroIndex1]) >= tc.Eval(chromosomes[chroIndex2])){
					newChromosomes[i] = chromosomes[chroIndex2];
				}else{
					newChromosomes[i] = chromosomes[chroIndex1];
				}
			}
		}
		return newChromosomes;
		
	}
}
