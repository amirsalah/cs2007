package stockpredictor.data;

import java.util.ArrayList;
import java.util.HashMap;

public class StockPointsSet
{
	private HashMap<StockDate, ArrayList<Double>> stockPoints = new HashMap<StockDate, ArrayList<Double>>();
	private int adjCloseIndex = 5;
	
	/**
	 * Add a new stock data into the database storing stock prices
	 * @param point the stock data in a specific day
	 * @return true if add successfully
	 */
	public boolean AddPoint(StockPoint point)
	{
		if( stockPoints.put(point.GetCalendar(), point.GetPrices()) != null )
		{
			System.out.println("Error: duplicated mapping");
			return false;
		}
		else
//			System.out.println("Add successfully");
			return true;
	}

	/**
	 * Get stock prices in a specific day
	 * @param date the specific day
	 * @return the prices of that day
	 */
	public ArrayList<Double> GetStockPrices(StockDate date)
	{
		return stockPoints.get(date);
	}
	
	/**
	 * Get the number of available stock points
	 * @return the number
	 */
	public int Length(){
		return stockPoints.size();
	}
	
	/**
	 * Test if the specific date is a valid date
	 * @param date The date to be tested
	 * @return true if the date exists in the data set
	 */
	public boolean ContainsDate(StockDate date){
		boolean contain = false;
		contain = stockPoints.containsKey(date);
		return contain;
	}
	
	public double GetAdjClose(StockDate date){
		return stockPoints.get(date).get(adjCloseIndex);
	}
	
}
