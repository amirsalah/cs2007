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
	
	/**
	 * Get the value in the current date
	 * @return year
	 */
	public int GetYear(){
		return year;
	}
	
	public int GetMonth(){
		return month;
	}
	
	public int GetDate(){
		return date;
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
	
	public boolean equals(StockDate givenDate){
		if(givenDate.GetDate() == date 
				&& givenDate.GetMonth() == month 
				&& givenDate.GetYear() == year){
			return true;
		}
		
		return false;
	}
	
}
