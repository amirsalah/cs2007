package stock_tracker.data;

import java.util.ArrayList;

public class StockPoint {
	private ArrayList<Double> dayStockPrices = new ArrayList<Double>(6);
	private StockDate date = null;
	private int adjCloseIndex = 5;
	private double open, high, low, close, volume, adj_close;
	private ArrayList<Double> predictedValues = new ArrayList<Double>(3);
	
	public StockPoint(StockDate date,
			double open,
			double high,
			double low,
			double close,
			double volume,
			double adj_close)
	{
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.adj_close = adj_close;
		dayStockPrices.add(open);
		dayStockPrices.add(high);
		dayStockPrices.add(low);
		dayStockPrices.add(close);
		dayStockPrices.add(volume);
		dayStockPrices.add(adj_close);
	}
	
	/**
	 * Get the date of this stock market data
	 * @return calendar type of date
	 */
	public StockDate GetCalendar()
	{
		return date;
	}
	
	/**
	 * Get the prices list of this stock point
	 * @return prices: open, high, low and close
	 */
	public ArrayList<Double> GetPrices(){
		return dayStockPrices;
	}
	
	/**
	 * Get the adj* close from the current day
	 * @return the adj close value
	 */
	public double GetAdjClose(){
		return dayStockPrices.get(adjCloseIndex);
	}
	
	public StockPoint clone(){
		return new StockPoint(date, open, high, low, close, volume, adj_close);
	}
	
	public void SetDate(StockDate newDate){
		date = newDate.clone();
	}
	
	/**
	 * Test to see if the given stock point is the same as current point
	 * @param point the given stock point
	 * @return true if the two points are the same, false otherwise
	 */
	public boolean equals(StockPoint point){
		if(!date.equals(point.GetCalendar())){
			return false;
		}
		
		for(int i=0; i<dayStockPrices.size(); i++){
			if(dayStockPrices.get(i) != point.GetPrices().get(i)){
				return false;
			}
		}
		
		return true;
	}
	
	public void SetPredictionValue(double predictedValue, double absError, double lmsError){
		predictedValues.add(predictedValue);
		predictedValues.add(absError);
		predictedValues.add(lmsError);
	}
	
	public double GetPredictedValue(){
		return predictedValues.get(0);
	}
	
	public double GetAbsError(){
		return predictedValues.get(1);
	}
	
	public double GetLmsError(){
		return predictedValues.get(2);
	}
	
}
