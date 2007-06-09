package stock_tracker.models;

import java.util.Vector;

import stock_tracker.data.StockPointsSet;

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
	private int numWindows_Long = 25; // The number of previous days to be considered in prediction
	private int numWindows_Short = 5;
	private int currentNumWindows;
	private Vector<Integer> shortNthDaysPrediction = new Vector<Integer>();
	private Vector<Integer> longNthDaysPrediction = new Vector<Integer>();
	
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
	
	private void ModelConfig(){
		numWindows_Long = 25;
		longNthDaysPrediction.add(5);
		longNthDaysPrediction.add(10);
		longNthDaysPrediction.add(20);
		longNthDaysPrediction.add(35);
			
		numWindows_Short = 5;
		shortNthDaysPrediction.add(1);
		shortNthDaysPrediction.add(2);
		shortNthDaysPrediction.add(5);
		shortNthDaysPrediction.add(7);
	}
	
	public void Predict(){
		double predictedValue;
		ModelConfig();
		
		for(int i=0; i<numPredictionDays; i++){
			// Long term prediction
			int windows = numWindows_Long;
			currentNumWindows = numWindows_Long;
			for(int j=0; j<longNthDaysPrediction.size(); j++){
				predictedValue = PredictionWithTrend(startIndex, windows, longNthDaysPrediction.get(j));
				dataSet.GetPoint(startIndex).SetLongTermPredictions(predictedValue);
			}
			
			// Short term prediction
			windows = numWindows_Short;
			currentNumWindows = numWindows_Short;
			for(int j=0; j<longNthDaysPrediction.size(); j++){
				predictedValue = PredictionWithTrend(startIndex, windows, shortNthDaysPrediction.get(j));
				dataSet.GetPoint(startIndex).SetShortTermPredictions(predictedValue);
			}
			
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
	private double PredictionWithTrend(int startIndex, int remainingWindows, int NthDay){
		int rWindows = remainingWindows - 1;
		
		if(remainingWindows == 1){
			// The initial value for S(t)
			return dataSet.GetAdjClose(startIndex + currentNumWindows);
		}
		else{
			double previousAdjClose = dataSet.GetAdjClose(startIndex + currentNumWindows - rWindows);
			return alpha *  previousAdjClose + 
			(1 - alpha) * (PredictionWithTrend(startIndex, rWindows, NthDay) + NthDay * TrendExponentialSmoothing(startIndex, remainingWindows));
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
			double lastAdjClose = dataSet.GetAdjClose(startIndex + currentNumWindows);
			double last2ndAdjClose = dataSet.GetAdjClose(startIndex + currentNumWindows - 1);
			// The initial value of B(t)
			return last2ndAdjClose - lastAdjClose;
		}
		else{
			double previousAdjClose = dataSet.GetAdjClose(startIndex + currentNumWindows - rWindows);
			double previous2ndAdjClose = dataSet.GetAdjClose(startIndex + currentNumWindows - rWindows + 1);
			return gamma * (previousAdjClose - previous2ndAdjClose) + 
				(1 - gamma) * TrendExponentialSmoothing(startIndex, rWindows);
		}
	}
	
	
}
