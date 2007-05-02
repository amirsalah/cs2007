package stockpredictor.models;

import stockpredictor.data.StockDate;
import stockpredictor.data.StockPointsSet;
import stockpredictor.InvalidDateException;

/**
 * This model is a improvement of single exponential model.
 * The model take the trend of data into account, in comparison single exponential model doesn't
 * 
 * The formulation for this model can be found at
 * http://www.itl.nist.gov/div898/handbook/pmc/section4/pmc433.htm
 * However, this implementation is slightly different from the one given in the web.
 * 
 * The trend smoothing only consider the past 20 days, rather than all previous day.
 * 
 */
public class DoubleExponentialSmoothingModel extends TimeSeriesModel {
	private double alpha;  // The smoothing factor
	private double gamma;  // The smoothing factor of global trend
	private int numWindows = 20; // The number of previous days to be considered in prediction
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
				// Parameters initialization for predicting next day
				predictingDate = predictingDate.NextValidDate(dataSet);
			}
		}
		catch(InvalidDateException ide){
			return;
		}
	}

	/**
	 * Prediction taking account with global trend.
	 * 
	 * @param predictingDate the day need to predict its adj. close
	 * @param remainingWindows
	 * @return the predicted value
	 * @throws InvalidDateException
	 */
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
	
	/**
	 * Exponential smoothing global trend, by applying single exponential smoothing to
	 * the differences of past days. 
	 * @param predictingDate 
	 * @param remainingWindows
	 * @return b(t)
	 * @throws InvalidDateException not a valid date
	 */
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
