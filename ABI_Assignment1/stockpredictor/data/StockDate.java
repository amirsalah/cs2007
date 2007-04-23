package stockpredictor.data;

public class StockDate {
	private int year;
	private int month;
	private int date;
	
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
	 */
	public StockDate NextValidDate(StockPointsSet dataSet){
		StockDate tomorrow = NextDate();
		
		while( !dataSet.ContainsDate(tomorrow)){
			tomorrow = tomorrow.NextDate();
		}
		
		return tomorrow;
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
	
	public StockDate PreviousValidDate(StockPointsSet dataSet){
		StockDate yesterday = PreviousDate();
		
		while( !dataSet.ContainsDate(yesterday) ){
			yesterday = yesterday.PreviousDate();
		}
		
		return yesterday;
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
		
		return String.valueOf(year) + stdMonth + stdDate;
	}
	
}
