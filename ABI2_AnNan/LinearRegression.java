import java.util.Vector;
import java.util.*;
/**
 * The linear regression method used to make prediction of next day's adj_close
 * hill climing is used to adjust the parameters
 */
public class LinearRegression {
	Vector<Double> rawData = null; // the raw adj. close data
	fileProcess stockData;
	double k1, k2, k3, k4, k5; // the parameters
	int intStart;
	Random random = new Random();
	
	public LinearRegression(Vector<Double> raw){
		this.rawData = raw;
		stockData =  new fileProcess();
		stockData.LoadDate();
	}
	
	//
	public void InitLinearModel(){
		//use historical data to initialize parameters k1 - k5
		double bestDistance = 100000;
		double newDistance = 100000;
		double  newK1 = k1;
		double  newK2 = k2;
		double  newK3 = k3;
		double  newK4 = k4;
		double  newK5 = k5;
		double openOld = stockData.open.get(2529);
		double highOld = stockData.high.get(2529);
		double lowOld = stockData.low.get(2529);
		double closeOld = rawData.get(2529);
		double volumeOld = stockData.volume.get(2529);
		double closeNew = rawData.get(2528);
		
		// Hill climing to optimize the parameters
		for(int i=0; i<100; i++){
			newK1 = k1 * ( 2 * random.nextDouble() - 1);
			newK2 = k2 * ( 2 * random.nextDouble() - 1);
			newK3 = k3 * ( 2 * random.nextDouble() - 1);
			newK4 = k4 * ( 2 * random.nextDouble() - 1);
			newK5 = k5 * ( 2 * random.nextDouble() - 1);
		
			newDistance = Math.abs(closeNew - (newK1*openOld + newK2*highOld + newK3*lowOld + newK4*closeOld + newK5*volumeOld));
			if ( newDistance < bestDistance){
				k1 = newK1;
				k2 = newK2;
				k3 = newK3;
				k4 = newK4;
				k5 = newK5;
				bestDistance = newDistance;
			}
		}
		
	}
	
	/**
	 * predict the adj close value of the day (indicating by the index)
	 */
	public double predictAdjClose(int index){
		double newAdjClose = 0;
		newAdjClose = stockData.high.get(index + 1) * k1 +
						stockData.open.get(index + 1) * k2 +
						stockData.low.get(index + 1) * k3 +
						rawData.get(index + 1) * k4 +
						stockData.volume.get(index + 1) * k5;
		
		return newAdjClose;
	}
	
	/**
	 * Adapt linear regression prediction model when applying the stock data.
	 */
	public void AdaptLRParameters(int index){
		//use historical data to initialize parameters k1 - k5
		double bestDistance = 100000;
		double newDistance = 100000;
		double  newK1 = k1;
		double  newK2 = k2;
		double  newK3 = k3;
		double  newK4 = k4;
		double  newK5 = k5;
		
		double openOld = stockData.open.get(index + 2);
		double highOld = stockData.high.get(index + 2);
		double lowOld = stockData.low.get(index + 2);
		double closeOld = rawData.get(index + 2);
		double volumeOld = stockData.volume.get(index + 2);
		double closeNew = rawData.get(index + 1);
		
		// Hill climing to adjust the parameters
		for(int i=0; i<100; i++){
			newK1 = k1 * ( 2 * random.nextDouble() - 1);
			newK2 = k2 * ( 2 * random.nextDouble() - 1);
			newK3 = k3 * ( 2 * random.nextDouble() - 1);
			newK4 = k4 * ( 2 * random.nextDouble() - 1);
			newK5 = k5 * ( 2 * random.nextDouble() - 1);
		
			newDistance = Math.abs(closeNew - (newK1*openOld + newK2*highOld + newK3*lowOld + newK4*closeOld + newK5*volumeOld));
			if ( newDistance < bestDistance){
				k1 = newK1;
				k2 = newK2;
				k3 = newK3;
				k4 = newK4;
				k5 = newK5;
				bestDistance = newDistance;
			}
		}
	}
	
}
