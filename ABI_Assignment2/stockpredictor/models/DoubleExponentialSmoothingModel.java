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
		double predictedValue;
		
		for(int i=0; i<numPredictionDays; i++){
			int windows = numWindows;
			predictedValue = PredictionWithTrend(startIndex, windows);
				
			StorePrediction(startIndex, predictedValue);
			startIndex--;
		}

	}

	/**
	 * Prediction taking account with global trend.
	 * 
	 * @param predictingDate the day need to predict its adj. close
	 * @param remainingWindows
	 * @return the predicted value
	 */
	private double PredictionWithTrend(int startIndex, int remainingWindows){
		int rWindows = remainingWindows - 1;
		
		if(remainingWindows == 1){
			// The initial value for S(t)
			return dataSet.GetAdjClose(startIndex + numWindows);
		}
		else{
			double previousAdjClose = dataSet.GetAdjClose(startIndex + numWindows - rWindows);
			return alpha *  previousAdjClose + 
			(1 - alpha) * (PredictionWithTrend(startIndex, rWindows) + TrendExponentialSmoothing(startIndex, remainingWindows));
		}
	}
	
	/**
	 * Exponential smoothing global trend, by applying single exponential smoothing to
	 * the differences of past days. 
	 * @param predictingDate 
	 * @param remainingWindows
	 * @return b(t)
	 */
	private double TrendExponentialSmoothing(int startIndex, int remainingWindows){
		int rWindows = remainingWindows - 1;
		
		if(remainingWindows == 2){
			double lastAdjClose = dataSet.GetAdjClose(startIndex + numWindows);
			double last2ndAdjClose = dataSet.GetAdjClose(startIndex + numWindows - 1);
			// The initial value of B(t)
			return last2ndAdjClose - lastAdjClose;
		}
		else{
			double previousAdjClose = dataSet.GetAdjClose(startIndex + numWindows - rWindows);
			double previous2ndAdjClose = dataSet.GetAdjClose(startIndex + numWindows - rWindows + 1);
			return gamma * (previousAdjClose - previous2ndAdjClose) + 
				(1 - gamma) * TrendExponentialSmoothing(startIndex, rWindows);
		}
	}
	
	
}
