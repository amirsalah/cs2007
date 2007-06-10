/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 27th, May 2007
=========================================================*/
package stock_tracker.optimization;

import stock_tracker.data.*;
import java.util.*;

/**
 * Base class for all the optimization systems. 
 * The bank account should be initialized when the system is constructed
 * @author Bo CHEN
 */
public abstract class Optimization {
	protected StockPointsSet dataSet;
	protected StockDate startDate;
	protected int numPredictionDays;
	protected int startIndex;
	protected boolean simulationStage = false;
	protected double SLIncrease_rate = 20.0;
	protected double SLDecrease_rate = 19.0;
	
	public Optimization(StockPointsSet dataSet){
		this.dataSet = dataSet;
		startDate = new StockDate(2000, 2, 24);
		numPredictionDays = dataSet.Length() - 29; // There are 29 days before 2000/2/24
		startIndex = dataSet.GetIndex(startDate);

		// Initialize the bank account for the starting date
		dataSet.GetPoint(startIndex).SetBalance(1);
		dataSet.GetPoint(startIndex).SetShares(0);
		
		dataSet.GetPoint(startIndex + 1).SetBalance(0);
		dataSet.GetPoint(startIndex + 1).SetShares(0);
	}

	/**
	 * Cash out at the last day, when is 2007/1/25
	 * @return the final bank balance
	 */
	protected double CashOut(){
		StockPoint lastDay = dataSet.GetPoint(0);
		return lastDay.GetBalance() + lastDay.GetAdjClose() * lastDay.GetShares() * 0.997;
	}
	
	public abstract double optimize();
	
	// Adjust increasing rate
	protected double GetSLIncreaseRate(){
		double rate = 17.5;
		Random random = new Random();
		return rate + (random.nextDouble() - 0.5);
	}
	
	protected double GetSLDecreaseRate(){
		double rate = 17.5;
		Random random = new Random();
		return rate + (random.nextDouble() - 0.5);
	}
}
