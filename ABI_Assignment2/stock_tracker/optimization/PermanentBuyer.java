package stock_tracker.optimization;

import stock_tracker.data.StockPoint;
import stock_tracker.data.StockPointsSet;

public class PermanentBuyer extends Optimization{

	public PermanentBuyer(StockPointsSet dataSet){
		super(dataSet);
	}
	
	public double optimize(){
		for(int i=0; i<numPredictionDays; i++){
			// initialize the current account by yesterday's values.
			StockPoint yesterday = dataSet.GetPoint(startIndex - i + 1);
			StockPoint today = dataSet.GetPoint(startIndex - i);
			today.BankingInit(yesterday);
			
			today.BuyMax();
		}
		
		return CashOut();
	}
}
