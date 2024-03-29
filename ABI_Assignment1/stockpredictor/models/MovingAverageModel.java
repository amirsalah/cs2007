package stockpredictor.models;

import stockpredictor.*;
import stockpredictor.data.*;

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
		StockDate predictingDate = startDate.clone();
		StockDate previousDate;
		double predictedValue;
		
		for(int i=0; i<(dataSet.Length() - numWindows); i++){
			predictedValue = 0;
			previousDate = predictingDate.clone();
			
			try{
				for(int j=0; j<numWindows; j++){
					previousDate = previousDate.PreviousValidDate(dataSet);
					predictedValue += weights[j] * dataSet.GetAdjClose(previousDate);
				}
				
				RecordPrediction(predictingDate, predictedValue);
				// Parameters initialization for predicting next day
				predictingDate = predictingDate.NextValidDate(dataSet);
				previousDate = predictingDate.PreviousValidDate(dataSet);
			}
			catch(InvalidDateException ide){
				System.out.println(ide.getMessage());
				break;
			}

		}
	}
	
}
