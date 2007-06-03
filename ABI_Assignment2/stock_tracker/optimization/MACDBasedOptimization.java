package stock_tracker.optimization;

import stock_tracker.data.StockPoint;
import stock_tracker.data.StockPointsSet;
import stock_tracker.models.DoubleExponentialSmoothingModel;
import stock_tracker.models.Model;


public class MACDBasedOptimization extends Optimization{

	public MACDBasedOptimization(StockPointsSet dataSet){
		super(dataSet);
	}
	
	public double optimize(){
		DoubleExponentialSmoothingModel predictionModel = new DoubleExponentialSmoothingModel(dataSet, 0.60, 0.60);
		predictionModel.Predict();
		
		for(int i=0; i<numPredictionDays; i++){
			// initialize the current account by yesterday's values.
			StockPoint yesterday = dataSet.GetPoint(startIndex - i + 1);
			StockPoint today = dataSet.GetPoint(startIndex - i);
			today.BankingInit(yesterday);
			
			// Never buy or sell shares on weekends
			if(today.isWeekend()){
				continue;
			}
			
			
		}
		
		return CashOut();
	}
}
