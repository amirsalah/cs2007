/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 27th, May 2007
=========================================================*/
package stock_tracker.optimization;

import stock_tracker.data.*;

public abstract class Optimization {
	protected StockPointsSet dataSet;
	protected StockDate startDate;
	protected StockDate endDate;
	protected int numPredictionDays;
	protected int startIndex;
	
	public Optimization(StockPointsSet dataSet){
		this.dataSet = dataSet;
		startDate = new StockDate(2000, 2, 24);
		endDate = new StockDate(2007, 1, 25);
		numPredictionDays = dataSet.Length() - 29; // There are 29 days before 2000/2/24
		startIndex = dataSet.GetIndex(startDate);
		// Initialize the bank account for the starting date
		dataSet.GetPoint(startIndex).SetBalance(1);
		dataSet.GetPoint(startIndex).SetShares(0);
		
		dataSet.GetPoint(startIndex + 1).SetBalance(0);
		dataSet.GetPoint(startIndex + 1).SetShares(0);
	}
	
	protected double CashOut(){
		StockPoint lastDay = dataSet.GetPoint(0);
		return lastDay.GetBalance() + lastDay.GetAdjClose() * lastDay.GetShares() * 0.997;
	}
	
	public abstract double optimize();
	
}
