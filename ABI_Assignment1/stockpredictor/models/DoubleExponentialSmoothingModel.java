package stockpredictor.models;

import stockpredictor.data.StockDate;
import stockpredictor.data.StockPointsSet;
import stockpredictor.InvalidDateException;

public class DoubleExponentialSmoothingModel extends TimeSeriesModel {
	private double alpha;
	private double gamma;
	private int numWindows = 20;
	private int numExistingPrices = 20;
	
	public DoubleExponentialSmoothingModel(StockPointsSet dataSet, double alpha, double gamma){
		super(dataSet);
		
		// Check the vadility of alpha and gamma
		if(alpha > 1.0 || alpha <0){
			System.out.println("Error: invalid alpha.");
			System.out.println("Alpha should be between [0, 1]");
			return;
		}
		if(gamma > 1.0 || gamma <0){
			System.out.println("Error: invalid gamma.");
			System.out.println("Alpha should be between [0, 1]");
			return;
		}
		
		this.alpha = alpha;
		this.gamma = gamma;
	}
	
	public void Predict(){
		// Set the first date to predict its stock price
		StockDate predictingDate = startDate.clone();
		double predictedValue;
		
		try{
			for(int i=0; i<(dataSet.Length() - numExistingPrices); i++){
				int windows = numWindows;
				predictedValue = PredictionWithTrend(predictingDate, windows);
				
				RecordPrediction(predictingDate, predictedValue);
				//System.out.println("Predicted Value: " + predictedValue);
				// Parameters initialization for predicting next day
				predictingDate = predictingDate.NextValidDate(dataSet);
			}
		}
		catch(InvalidDateException ide){
			return;
		}
	}
	
	private double PredictionWithTrend(StockDate predictingDate, int remainingWindows) throws InvalidDateException {
		int rWindows = remainingWindows - 1;
		
		if(remainingWindows == 1){
			// The initial value for S(t)
			return dataSet.GetAdjClose(predictingDate.PreviousNthValidDate(dataSet, numWindows));
		}
		else{
			double previousAdjClose = dataSet.GetAdjClose(predictingDate.PreviousNthValidDate(dataSet, numWindows - remainingWindows + 1));
			return alpha *  previousAdjClose + 
			(1 - alpha) * (PredictionWithTrend(predictingDate, rWindows) + TrendExponentialSmoothing(predictingDate, remainingWindows));
		}
	}
	
	private double TrendExponentialSmoothing(StockDate predictingDate, int remainingWindows) throws InvalidDateException {
		int rWindows = remainingWindows - 1;
		
		if(remainingWindows == 2){
			double lastAdjClose = dataSet.GetAdjClose(predictingDate.PreviousNthValidDate(dataSet, numWindows));
			double last2ndAdjClose = dataSet.GetAdjClose(predictingDate.PreviousNthValidDate(dataSet, numWindows - 1));
			// The initial value of B(t)
			return last2ndAdjClose - lastAdjClose;
		}
		else{
			double previousAdjClose = dataSet.GetAdjClose(predictingDate.PreviousNthValidDate(dataSet, numWindows - remainingWindows + 1));
			double previous2ndAdjClose = dataSet.GetAdjClose(predictingDate.PreviousNthValidDate(dataSet, numWindows - remainingWindows + 2));
			return gamma * (previousAdjClose - previous2ndAdjClose) + 
				(1 - gamma) * TrendExponentialSmoothing(predictingDate, rWindows);
		}
	}
	
	
}
