package stock_tracker.optimization;

import stock_tracker.models.DoubleExponentialSmoothingModel;
import stock_tracker.data.*;

/**
 * This approach is based on MACD stock indicator, in which buy action will be performed
 * when both short term prediction and long term prediction is increasing, and the short-term
 * increasing rate is larger than long-term increasing rate. 
 * 
 * @author Bo CHEN
 */
public class MACDBasedOptimization extends Optimization{

	public MACDBasedOptimization(StockPointsSet dataSet){
		super(dataSet);
	}
	
	public double optimize(){
		DoubleExponentialSmoothingModel predictionModel = new DoubleExponentialSmoothingModel(dataSet, 0.4, 0.4);
		predictionModel.Predict();
		
		for(int i=0; i<numPredictionDays; i++){
			// initialize the current account by yesterday's values.
			StockPoint yesterday = dataSet.GetPoint(startIndex - i + 1);
			StockPoint today = dataSet.GetPoint(startIndex - i);
			today.BankingInit(yesterday);			
			
			// MACD implementation
			if(today.isIncreasing()){
				if(today.ShortIncreasingRate()/(double)20 > today.LongIncreasingRate()){
					// buy shares with all the available bank balance
					today.BuyMax();
				}
			}
			
			if(today.isDecreasing()){
				if(today.ShortDecreasingRate()/(double)19 > today.LongDecreasingRate()){
					// Sell all the holding shares
					today.SellMax();
				}
			}
		}
		
		return CashOut();
	}
}
