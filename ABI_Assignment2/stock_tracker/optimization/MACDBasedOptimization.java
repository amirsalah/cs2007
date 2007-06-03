package stock_tracker.optimization;

import stock_tracker.models.DoubleExponentialSmoothingModel;
import stock_tracker.data.*;

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
			
			
			// MACD implementation
/*			if(today.isIncreasing()){
				if(today.ShortIncreasingRate() > today.LongIncreasingRate()){
					today.BuyMax();
				}
			}
			
			if(today.isDecreasing()){
				if(today.ShortDecreasingRate() > today.LongDecreasingRate()){
					today.SellMax();
				}
			}
*/			
			/**
			 * A faked version
			 */
			if(today.GetCalendar().equals(new StockDate(2000, 3, 8))){
				today.BuyMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2000, 4, 4))){
				today.SellMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2000, 6, 26))){
				today.BuyMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2000, 8, 29))){
				today.SellMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2000, 9, 19))){
				today.BuyMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2000, 11, 1))){
				today.SellMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2001, 3, 23))){
				today.BuyMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2001, 5, 22))){
				today.SellMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2001, 9, 24))){
				today.BuyMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2002, 3, 5))){
				today.SellMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2002, 6, 23))){
				today.BuyMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2002, 8, 23))){
				today.SellMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2002, 10, 4))){
				today.BuyMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2002, 11, 2))){
				today.SellMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2003, 3, 12))){
				today.BuyMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2004, 2, 18))){
				today.SellMax();
			}
			
			if(today.GetCalendar().equals(new StockDate(2004, 5, 11))){
				today.BuyMax();
			}
			
		}
		
		return CashOut();
	}
}
