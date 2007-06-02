package stock_tracker.optimization;

import stock_tracker.data.StockPoint;
import stock_tracker.data.StockPointsSet;

public class PermanentHolder extends Optimization{
	
	public PermanentHolder(StockPointsSet dataSet){
		super(dataSet);
	}
	
	public double optimize(){
		for(int i=0; i<numPredictionDays; i++){
			// initialize the current account by yesterday's values.
			StockPoint yesterday = dataSet.GetPoint(startIndex - i + 1);
			dataSet.GetPoint(startIndex - i).BankingInit(yesterday);
		}
		return CashOut();
	}
	
}
