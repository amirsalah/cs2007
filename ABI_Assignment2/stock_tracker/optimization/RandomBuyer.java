package stock_tracker.optimization;

import stock_tracker.data.StockPoint;
import stock_tracker.data.StockPointsSet;
import java.util.Random;

public class RandomBuyer extends Optimization{
	
	public RandomBuyer(StockPointsSet dataSet){
		super(dataSet);
	}
	
	public double optimize(){
		Random random = new Random();
		
		for(int i=0; i<numPredictionDays; i++){
			// initialize the current account by yesterday's values.
			StockPoint yesterday = dataSet.GetPoint(startIndex - i + 1);
			StockPoint today = dataSet.GetPoint(startIndex - i);
			today.BankingInit(yesterday);
			
			// 0: buy, 1: sell, 2: hold
			switch(random.nextInt(3)){
			case 0:
				today.BuyMax();
				break;
			case 1:
				today.SellMax();
				break;
			case 2:
				break;
			}
		}
		
		return CashOut();
	}
}
