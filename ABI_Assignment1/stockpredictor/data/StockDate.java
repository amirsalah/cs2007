package stockpredictor.data;

import stockpredictor.InvalidDateException;;

public class StockDate {
	private int year;
	private int month;
	private int date;
	
	private int searchTimes = 100;
	
	
	public StockDate(){
	}
	
	public StockDate(int year, int month, int date){
		this.year = year;
		this.month = month;
		this.date = date;
	}
	
	/**
	 * Get next day, Note: it's pretty simple fixed structure that each month has 31 days
	 * @param today 
	 * @return the Date of tomorrow
	 */
	public StockDate NextDate(){
		StockDate tomorrow = new StockDate(year, month, date);
		
		if(date <= 30){
			tomorrow.SetDate(date + 1);
		}else if(month <= 11){
			tomorrow.SetDate(1);
			tomorrow.SetMonth(month + 1);
		}else{
			tomorrow.SetYear(year + 1);
			tomorrow.SetDate(1);
			tomorrow.SetMonth(1);
		}
		
		return tomorrow;
	}
	
	/**
	 * Get next valid date that exists in the stock data set
	 * @param dataSet the stock data set that will be tested 
	 * @return the valid next day
	 * @throws InvalidDateException 
	 */
	public StockDate NextValidDate(StockPointsSet dataSet) throws InvalidDateException{
		StockDate tomorrow = NextDate();
		int counter;
		
		for(counter=0; counter<searchTimes; counter++){
			if(dataSet.ContainsDate(tomorrow)){
				break;
			}
			tomorrow = tomorrow.NextDate();
		}
		
		if(counter == 99){
			throw new InvalidDateException("No valid next date in next " + String.valueOf(searchTimes) + "days ");
		}else{
			return tomorrow;
		}
	}
	
	public StockDate PreviousDate(){
		StockDate yesterday = new StockDate(year, month, date);
		
		if(date >= 2){
			yesterday.SetDate(date - 1);
		}else if(month >=2){
			yesterday.SetDate(31);
			yesterday.SetMonth(month - 1);
		}else{
			yesterday.SetYear(year - 1);
			yesterday.SetDate(31);
			yesterday.SetMonth(12);
		}
		
		return yesterday;
	}
	
	public StockDate PreviousValidDate(StockPointsSet dataSet) throws InvalidDateException{
		StockDate yesterday = PreviousDate();
		int counter;
		
		for(counter=0; counter<searchTimes; counter++){
			if(dataSet.ContainsDate(yesterday)){
				break;
			}
			yesterday = yesterday.PreviousDate();
		}
		
		if(counter == 99){
			throw new InvalidDateException("No valid previous date in next " + String.valueOf(searchTimes) + "days ");
		}else{
			return yesterday;
		}
	}
	
	/**
	 * Set the Date to the specific year
	 * @param year the year to be specified
	 */
	public void SetYear(int year){
		this.year = year;
	}
	
	public void SetMonth(int month){
		this.month = month;
	}
	
	public void SetDate(int date){
		this.date = date;
	}
	
	public void Set(int year, int month, int date){
		this.year = year;
		this.month = month;
		this.date = date;
	}
	
	public StockDate clone(){
		StockDate newDate = new StockDate(year, month, date);
		return newDate;
	}
	
	/**
	 * Output the string structure of the date: yyyyMMdd
	 */
	public String toString(){
		String stdMonth = String.valueOf(month);
		String stdDate = String.valueOf(date);
		
		if(month < 10){
			stdMonth = "0" + stdMonth;
		}
		if(date < 10){
			stdDate = "0" + stdDate;
		}
		return String.valueOf(year) + "/" + stdMonth + "/" + stdDate; 
	}
	
}
