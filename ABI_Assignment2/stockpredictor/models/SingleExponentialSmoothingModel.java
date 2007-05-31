package stockpredictor.models;

import stockpredictor.data.*;
import stockpredictor.*;

/**
 * This is one of the time series prediction method,
 * in which the weights of previous stock prices are assigned exponentially.
 * Basically, recent days are given more significant weights.
 * 
 * The parameter alpha controls the smoothness of the algorithm, 
 * alpha should be between 0.0 and 1.0
 * 
 */
public class SingleExponentialSmoothingModel extends TimeSeriesModel{
	private double alpha; // The smoothing factor
	private int numWindows = 20; // the number of previous days to be considered
	private int numExistingPrices = 20;
	private boolean noWindows = false; // true if all the previous days are used for prediction
	
	
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
					// Parameters initialization for predicting next day
					predictingDate = predictingDate.NextValidDate(dataSet);
//					if(predictingDate.GetYear() == 2007 && predictingDate.GetMonth() == 1 && predictingDate.GetDate() == 24){
	//					System.out.println();
		//			}
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
	 * S(2) = adjClose(1);
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
			double previousAdjClose = dataSet.GetAdjClose(predictingDate.PreviousNthValidDate(dataSet, numWindows - remainingWindows + 1));
			return alpha *  previousAdjClose + (1 - alpha) * LimitedPrediction(predictingDate, rWindows);
		}
	}
	
}

