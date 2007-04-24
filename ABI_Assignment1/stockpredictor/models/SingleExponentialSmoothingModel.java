package stockpredictor.models;

import stockpredictor.data.*;
import stockpredictor.*;


public class SingleExponentialSmoothingModel extends TimeSeriesModel{
	private double alpha;
	private int numWindows = 20;
	private int numExistingPrices = 20;
	private boolean noWindows = false;
	
	
	public SingleExponentialSmoothingModel(StockPointsSet dataSet, double alpha){
		super(dataSet);
		// check if alpha is in (0, 1]
		if((alpha <=0) || (alpha >1)){
			System.out.println("Error: invalid alpha.");
			System.out.println("Alpha should be between (0, 1]");
			return;
		}
		
		this.alpha = alpha;
		
	}
	
	public void Predict(){
		// Set the first date to predict its stock price
		StockDate predictingDate = startDate.clone();
		double predictedValue;
		
		try{
			if(noWindows){
				UnlimitedPrediction();
			}else{
				for(int i=0; i<(dataSet.Length() - numExistingPrices); i++){
					int windows = numWindows;
					predictedValue = LimitedPrediction(predictingDate, windows);
					
					RecordPrediction(predictingDate, predictedValue);
					//System.out.println("Predicted Value: " + predictedValue);
					// Parameters initialization for predicting next day
					predictingDate = predictingDate.NextValidDate(dataSet);
				}
			}
		}
		catch(InvalidDateException ide){
			return;
		}
	}
	
	
	private void UnlimitedPrediction(){
		
	}
	
	/**
	 * This is a recursive function that perform prediction for a fixed size of windows, which is n previous dates
	 * The principle of this method is shown below:
	 * S(t) = alpha * Price(t-1) + (1 - alpha) * S(t - 1)
	 * S(2) = Price(1);
	 * 
	 * @param predictingDate the date whose stock price to be predicted
	 * @param remainingWindows the number of remaining windows
	 * @return the predicted value
	 * @throws InvalidDateException
	 */
	private double LimitedPrediction(StockDate predictingDate, int remainingWindows) throws InvalidDateException{
		int rWindows = remainingWindows - 1;
		
		if(remainingWindows == 1){
			return dataSet.GetAdjClose(predictingDate.PreviousNthValidDate(dataSet, numWindows));
		}else{
			double previousAdjPrice = dataSet.GetAdjClose(predictingDate.PreviousNthValidDate(dataSet, numWindows - remainingWindows + 1));
			return alpha *  previousAdjPrice + (1 - alpha) * LimitedPrediction(predictingDate, rWindows);
		}
	}
	
}



