import java.util.Random;
import java.util.Vector;

/**
 * the parameters for the Moving averages is adapative during the optimization
 * The adjustion is done by evolutiaonry algorithm.
 * a solution consists of 3 parameters with real number representation.
 */
public class Adaption_EA {
	
	class Solution{
		int shortDays = 0;
		int longDays = 0;
		double SOverL = 0;
		
		Solution(int sr, int lr, double SOverL){
			shortDays = sr;
			longDays = lr;
			this.SOverL = SOverL;
		}
	}
	
	Optimization optima;
	Vector<Double> adj_close; // the raw adj. close data
	Vector<Double> bankBalance;
	Vector<Double> sharesBuy;
	Vector<Double> holdingShares;
	Random rn = new Random();
	
	// Adjustable parameters
	int shortDays;
	int longDays;
	double SOverL;
	
	// parameters of evolutionary algorithm
	int NumGenerations = 100;
	int NumOffspring = 20;
	double MutationRate = 0.4;
	double CrossoverRate = 0.2;
	
	Vector<Solution> solutions = new Vector<Solution>(NumOffspring);
	int index;
	
	double originalShares;
	double originalBalance;
	
	public Adaption_EA(Optimization optima){
		this.optima = optima;
		
		adj_close = optima.adj_close; // the raw adj. close data
		bankBalance = optima.balance;
		sharesBuy = optima.gotShares;
		holdingShares = optima.existingShares;
		
		// Adjustable parameters
		shortDays = optima.shortDays;
		longDays = optima.longDays;
		SOverL = optima.SOverL;

		originalShares = holdingShares.get(holdingShares.size() - 21); // get data in 20 days ago
		originalBalance = bankBalance.get(holdingShares.size() - 21);
	}
	
	
	public void EvolveParameters(int index){
		this.index = index;
		
		Initialize(); // initialization
		
		Solution bestSolution;
		double highestFitness = 0;
		Vector<Solution> Offsprings = new Vector<Solution>(NumOffspring);
		
		for(int i=0; i<NumGenerations; i++){
			//selection
			for(int j=0; j<NumOffspring; j++){
				Offsprings.add(tournament(solutions.get(rn.nextInt(NumOffspring)), solutions.get(rn.nextInt(NumOffspring))));
			}
			
			if(rn.nextDouble() < CrossoverRate){
				int index_s1 = rn.nextInt(NumOffspring);
				int index_s2 = rn.nextInt(NumOffspring);
				
				Crossover(Offsprings.elementAt(index_s1), Offsprings.elementAt(index_s2));
			}
			
			if(rn.nextDouble() < MutationRate){
				int index_s1 = rn.nextInt(NumOffspring);
				
				Mutation(Offsprings.get(index_s1));
			}
			
			solutions.clear();
			solutions = (Vector<Solution>) Offsprings.clone();
			Offsprings.clear();
		}
		
		bestSolution = solutions.get(0); 
		
		for(int i=0; i<NumOffspring; i++){
			double newFitness = FitnessEvaluation(solutions.get(i));
			if( newFitness > highestFitness){
				highestFitness = newFitness;
				bestSolution = solutions.get(i);
			}
		}
		
		optima.shortDays = bestSolution.shortDays;
		optima.longDays = bestSolution.longDays;
		optima.SOverL = bestSolution.SOverL;
		
	}
	
	
	// initialize the population
	private void Initialize(){
		solutions.clear();
		
		for(int i=0; i<NumOffspring; i++){
			int NewnumSR = shortDays + rn.nextInt(1);
			int NewnumLR = longDays + rn.nextInt(1);
			double Newtimes = 15 + rn.nextGaussian();
			
			if(NewnumSR > 6){
				NewnumSR = 2;
			}
			
			if(NewnumLR > 22){
				NewnumLR = 12;
			}
			
			Solution newSolution = new Solution(NewnumSR, NewnumLR, Newtimes);
			solutions.add(newSolution);
		}
	}
	
	
	// tournament selection
	private Solution tournament(Solution s1, Solution s2){
		if(FitnessEvaluation(s1) > FitnessEvaluation(s2)){
			return s1;
		}else{
			return s2;
		}
	}
	
	
	// evaluate the fitness value of a given solution
	private double FitnessEvaluation(Solution newSolution){
		double gotMoney = 0;
		int NewnumSR = newSolution.shortDays;
		int NewnumLR = newSolution.longDays;
		double Newtimes = newSolution.SOverL;
		double initShares = originalShares; 
		double initBalance = originalBalance;
			
			for(int j=0; j<20; j++){
				double srdays = 0;
				double lrdays = 0;
				initBalance++;
				
				for(int isr=0; isr<NewnumSR; isr++){
					srdays += adj_close.get(index + 1 + isr) - adj_close.get(index + 2 + isr);
				}
				srdays = srdays/(double)NewnumSR;
				for(int ilr=0; ilr<NewnumLR; ilr++){
					lrdays += adj_close.get(index + 1 + ilr) - adj_close.get(index + 2 + ilr);
				}
				lrdays = lrdays/(double)NewnumLR;
				if( srdays >0 && lrdays >0 && srdays > (double)Newtimes * lrdays){
					initBalance = initBalance + initShares * adj_close.get(index + j) * 0.997;
					initShares = 0;
				}
				if( srdays < 0 && lrdays < 0 && srdays < lrdays *(double)Newtimes){
					initShares = initShares + initBalance * 0.997 / adj_close.get(index + j);
					initBalance = 0;
				}
				if(j == 19){
					gotMoney = initBalance + initShares * adj_close.get(index + j) * 0.997;
				}
			}
			
			return gotMoney;
	}
	
	
	private void Crossover(Solution s1, Solution s2){
		int CuttingPoint = rn.nextInt(2);
		
		if(CuttingPoint == 0){
			int tmp = s1.shortDays;
			s1.shortDays = s2.shortDays;
			s2.shortDays = tmp;
		}
		
		if(CuttingPoint == 1){
			double tmp = s1.SOverL;
			
			s1.SOverL = s2.SOverL;
			s2.SOverL = tmp;
		}
	}
	
	
	// single point mutation
	private void Mutation(Solution s1){
		int mutatePoint = rn.nextInt(3);
		
		if(mutatePoint == 0	){
			s1.shortDays = s1.shortDays + rn.nextInt(1);
		}
		
		if(mutatePoint == 1	){
			s1.longDays = s1.longDays + rn.nextInt(1);
		}
		
		if(s1.shortDays > 6 || s1.shortDays < 2){
			s1.shortDays = 2;
		}
		
		if(s1.longDays > 22 || s1.longDays < 8){
			s1.longDays = 12;
		}
		
		if(mutatePoint == 2	){
			s1.SOverL = 15 + rn.nextGaussian();;
		}
	}
}
