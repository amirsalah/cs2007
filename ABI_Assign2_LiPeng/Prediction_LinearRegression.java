import java.util.Vector;
import java.util.*;

/**
 * The linear regression prediction model, predicting the next day's adj close
 * the initialization and adaption are done by hill climbing
 */
public class Prediction_LinearRegression {
	int intStart;
	Random rn = new Random();
	
	public Prediction_LinearRegression(Vector<Double> raw){
		this.adj_close = raw;
	}
	
	double k1, k2, k3, k4, k5; 
	Vector<Double> adj_close = null; 
	CsvFileImport dowJones = Driver.dowJones;

	/**
	 * Initiaize the prediction model by the past data
	 */
	public void initialPredictionModel(){
		int oldindex = 2529;
		double bestDistance = 50000;
		double newDistance = 50000;
		double openOld = dowJones.open.get(oldindex);
		double highOld = dowJones.high.get(oldindex);
		double lowOld = dowJones.low.get(oldindex);
		double closeOld = adj_close.get(oldindex);
		double volumeOld = dowJones.volume.get(oldindex);
		double closeNew = adj_close.get(2528);
		double  nk1 = k1, nk2 = k2, nk3 = k3, nk4 = k4, nk5 = k5;

		
		/* Using hill climbing technique to choose the best (local maxima) */
		for(int i=0; i<50; i++){
			// generate new k values
			nk1 = k1 * ( 2 * rn.nextGaussian());
			nk2 = k2 * ( 2 * rn.nextGaussian());
			nk3 = k3 * ( 2 * rn.nextGaussian());
			nk4 = k4 * ( 2 * rn.nextGaussian());
			nk5 = k5 * ( 2 * rn.nextGaussian());
		
			double oldValue = nk1*openOld + nk2*highOld + nk3*lowOld + nk4*closeOld + nk5*volumeOld;
			
			newDistance = Math.abs(closeNew - oldValue);
			
			if ( newDistance < bestDistance){
				bestDistance = newDistance;
				k1 = nk1;k2 = nk2;k3 = nk3;k4 = nk4;k5 = nk5;
			}
		}
		
	}
	
	
	/* Making prediction */
	public double predictAdjClose(int index){
		return dowJones.high.get(index + 1) * k1 + dowJones.open.get(index + 1) * k2 +
						dowJones.low.get(index + 1) * k3 + adj_close.get(index + 1) * k4 +
						dowJones.volume.get(index + 1) * k5;
	}
	
	
	/* The adaption procedure duing the prediction on actual data */
	public void AdaptLRParameters(int index){
		double bestDistance = 50000;
		double newDistance = 50000;
		double openOld = dowJones.open.get(index + 2);
		double highOld = dowJones.high.get(index + 2);
		double lowOld = dowJones.low.get(index + 2);
		double closeOld = adj_close.get(index + 2);
		double volumeOld = dowJones.volume.get(index + 2);
		double closeNew = adj_close.get(index + 1);
		double  nk1 = k1, nk2 = k2,nk3 = k3,nk4 = k4, nk5 = k5;
		
		// ajust parameters
		for(int i=0; i<100; i++){
			nk1 = k1 * (2 * rn.nextGaussian());
			nk2 = k2 * ( 2 * rn.nextGaussian());
			nk3 = k3 * ( 2 * rn.nextGaussian());
			nk4 = k4 * ( 2 * rn.nextGaussian());
			nk5 = k5 * ( 2 * rn.nextGaussian());
			double oldValue = nk1*openOld + nk2*highOld + nk3*lowOld + nk4*closeOld + nk5*volumeOld;
			newDistance = Math.abs(closeNew - oldValue);
			if ( newDistance < bestDistance){
				bestDistance = newDistance;
				k1 = nk1;k2 = nk2;k3 = nk3;k4 = nk4;k5 = nk5;
			}
		}
	}
	
}
