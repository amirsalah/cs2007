package stock_tracker.data;

import java.util.ArrayList;

public class StockPointsSet
{
	private ArrayList<StockPoint> allStockPoints = new ArrayList<StockPoint>();
	
	/**
	 * Add a new stock data into the database storing stock prices
	 * @param point the stock data in a specific day
	 * @return true if add successfully
	 */
	public boolean AddPoint(StockPoint point){
		return allStockPoints.add(point);
	}

	/**
	 * Get stock prices in a specific day, by the index of that day
	 * @param point the specific day
	 * @return the prices of that day
	 */
	public ArrayList<Double> GetStockPrices(int pointIndex)
	{
		return allStockPoints.get(pointIndex).GetPrices();
	}
	
	/**
	 * Get the number of available stock points
	 * @return the number
	 */
	public int Length(){
		return allStockPoints.size();
	}
	
	public boolean Contains(StockPoint point){
		for(int i=0; i<allStockPoints.size(); i++){
			if(allStockPoints.get(i).equals(point)){
				return true;
			}
		}
		
		return false;
	}
	
	public double GetAdjClose(int pointIndex){
		return allStockPoints.get(pointIndex).GetAdjClose();
	}
	
	/**
	 * Get the next point
	 * @param point
	 * @return the found next point, null if no next point
	 */
	public StockPoint NextPoint(StockPoint point){
		for(int pointIndex=0; pointIndex<allStockPoints.size(); pointIndex++){
			if(allStockPoints.get(pointIndex).equals(point)){
				// Test if the point is the last one
				if(pointIndex != allStockPoints.size() - 1){
					return allStockPoints.get(pointIndex+1).clone();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Get the index of specific date in the stock points set.
	 * @param date 
	 * @return the index, -1 if the date is not valid
	 */
	public int GetIndex(StockDate date){
		int index = -1;
		
		for(int i=0; i<allStockPoints.size(); i++){
			if(allStockPoints.get(i).GetCalendar().equals(date)){
				return i;
			}
		}
		
		return index;
	}
	
	public StockPoint GetPoint(int pointIndex){
		return allStockPoints.get(pointIndex);
	}
	
	/**
	 * Get the previous stock point
	 * @param point
	 * @return the valid previous stock point, null otherwise
	 */
	public StockPoint PreviousPoint(StockPoint point){
		for(int pointIndex=0; pointIndex<allStockPoints.size(); pointIndex++){
			if(allStockPoints.get(pointIndex).equals(point)){
				// Test if the point is the last one
				if(pointIndex != 0){
					return allStockPoints.get(pointIndex-1).clone();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Get the stock date in the specific position (by index)
	 * @param pointIndex
	 * @return stock date, null if the index is not valid
	 */
	public StockDate GetDate(int pointIndex){
		if(pointIndex >= 0 && pointIndex < allStockPoints.size()){
			return allStockPoints.get(pointIndex).GetCalendar().clone();
		}
		return null;
	}
	
	public int NumberOfPoints(){
		return allStockPoints.size();
	}
}
