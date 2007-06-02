package stock_tracker.models;

import stock_tracker.data.*;

/**
 * this model calculates the average value of previous "numWindows" days' stock prices
 * the numWindows can be changed, and 20 is recommanded.
 */
public class MovingAverageModel extends TimeSeriesModel {
	private double[] weights = new double[20];
	private int numWindows;
	
	public MovingAverageModel(StockPointsSet dataSet){
		super(dataSet);
		numWindows = weights.length;
		SetWeights();
	}
	
	/**
	 * Set average weights for each previous close value
	 * Note: each weight is the same
	 */
	protected void SetWeights(){
		for(int i=0; i<numWindows; i++){
			weights[i] = (double)1/(double)numWindows;
		}
	}
	
	public void Predict(){
		// Set the first date to predict its stock price
		double predictedValue;
		
		for(int i=0; i<numPredictionDays; i++){
			predictedValue = 0;
			
			for(int j=0; j<numWindows; j++){
				double privousV = dataSet.GetAdjClose(startIndex + j + 1);
				predictedValue += weights[j] * privousV;
			}
			
			StorePrediction(startIndex, predictedValue);
			startIndex--;
		}
	}
}
