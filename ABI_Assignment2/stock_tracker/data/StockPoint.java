/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 27th, May 2007
=========================================================*/
package stock_tracker.data;

import java.util.ArrayList;

public class StockPoint {
	private ArrayList<Double> dayStockPrices = new ArrayList<Double>(6);
	private StockDate date = null;
	private int adjCloseIndex = 5;
	private double open, high, low, close, volume, adj_close;
	private ArrayList<Double> predictedValues = new ArrayList<Double>(3);
	
	//Variables for optimization
	private boolean weekend = false;
	private double balance;
	private double shares;
	private double sharesBuy = 0;
	private boolean buy = false;
	private boolean sell = false;
	private boolean hold = true;
	
	private ArrayList<Double> shortTermPredictions = new ArrayList<Double>();
	private ArrayList<Double> longTermPredictions = new ArrayList<Double>();
	
	
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
	
	public void SetWeekend(boolean isWeekend){
		weekend = isWeekend;
	}
	
	public double GetBalance(){
		return balance;
	}
	
	public double GetShares(){
		return shares;
	}
	
	public void SetBalance(double newBalance){
		balance = newBalance;
	}
	
	public void SetShares(double newShares){
		shares = newShares;
	}
	
	public void BankingInit(StockPoint yesterday){
		balance = yesterday.GetBalance() + 1; // Got 1 dollar each day
		shares = yesterday.shares;
	}
	
	public void BuyMax(){
		double availableBalance = balance * 0.997; // 0.3% transaction lost
		shares += availableBalance/adj_close;
		balance = 0;
		
		hold = false;
		buy = true;
		sharesBuy = availableBalance/adj_close;
	}
	
	public void SellMax(){
		balance += shares * adj_close * 0.997; // 0.3% transaction lost
		sharesBuy = -shares;
		shares = 0;
		
		hold = false;
		sell = true;
		
	}
	
	public boolean isWeekend(){
		return weekend;
	}
	
	public double GetSharesBuy(){
		return sharesBuy;
	}
	
	
	public void SetLongTermPredictions(double predictionValue){
		longTermPredictions.add(predictionValue);
	}
	
	public void SetShortTermPredictions(double predictionValue){
		shortTermPredictions.add(predictionValue);
	}
	
	public boolean isIncreasing(){
		if( (shortTermPredictions.get(1) - adj_close) > 0 ){ // Set the threshold value
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isDecreasing(){
		if( (shortTermPredictions.get(1) - adj_close) < -20 ){
			return true;
		}else{
			return false;
		}
	}
	
	public double ShortIncreasingRate(){
		return (shortTermPredictions.get(2) - adj_close) / (double)5.0; // 5 days further
	}
	
	public double LongIncreasingRate(){
		return (longTermPredictions.get(2) - adj_close) / (double)5.0; // 20 days further
	}
	
	public double ShortDecreasingRate(){
		return (adj_close - shortTermPredictions.get(2)) / (double)5.0;
	}
	
	public double LongDecreasingRate(){
		return (adj_close - longTermPredictions.get(2)) / (double)5.0;
	}
}
