package stockpredictor.models;

import stockpredictor.*;
import stockpredictor.data.*;

public class MovingAverageModel extends TimeSeriesModel{
	private double[] weights = new double[20];
	private double predictedValue;
	private int numWindows;
	
	public MovingAverageModel(StockPointsSet dataSet){
		super(dataSet);
		SetWeights();
		numWindows = weights.length;
	}
	
	/**
	 * Set average weights for each previous close value
	 */
	protected void SetWeights(){
		for(int i=0; i<numWindows; i++){
			weights[i] = (double)1/(double)numWindows;
		}
	}
	
	public void Predict(){
		// Set the first date to predict its stock price
		StockDate predictingDate = startDate.clone();
		StockDate previousDate = predictingDate.PreviousValidDate(dataSet);
		
		for(int i=0; i<(dataSet.Length() - numWindows); i++){
			predictedValue = 0;
			for(int j=0; j<numWindows; j++){
				predictedValue += weights[j] * dataSet.GetAdjClose(previousDate);
				previousDate = previousDate.PreviousValidDate(dataSet);
			}
			
			// Parameters initialization for predicting next day
			predictingDate = predictingDate.NextValidDate(dataSet);
			previousDate = predictingDate.PreviousValidDate(dataSet);

		}
	}
	
	
}
