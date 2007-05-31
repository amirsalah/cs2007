package stockpredictor.data;

import java.util.ArrayList;

public class StockPoint extends java.lang.Object{
	private ArrayList<Double> dayStockPrices = new ArrayList<Double>(6);
	private StockDate date = null;
	private int adjCloseIndex = 5;
	private double open, high, low, close, volume, adj_close;
	
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
	
}
