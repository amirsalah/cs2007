/*=======================================================
  File reader to read stock market data from a file
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 19th, April 2007
=========================================================*/
package stockpredictor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import stockpredictor.data.StockDate;
import stockpredictor.data.StockPoint;
import stockpredictor.data.StockPointsSet;

/**
 * Read data from the specific file, which contains the stock data.
 * and then transfer the string data into appropriate data, containing open, close, etc.
 */
public class DataReader {
	private BufferedReader fileReader;
	private StockPointsSet dowJonesStock = new StockPointsSet();
	
	public DataReader(String fileName){
		try{
			fileReader = new BufferedReader(new FileReader(fileName));
		}catch(IOException ioe){
			System.out.println("Could not find the file: " + fileName);
		}
		
		StoreData();
	}
	
	private void StoreData(){
		String dayDataStr = null;
		String[] dayDataList = null;
		String[] dateStr = null;
		int month = 0;
		int year = 0;
		int date = 0;
		Double open = 0.0;
		Double high = 0.0;
		Double low = 0.0;
		Double close = 0.0;
		Double volume = 0.0;
		Double adj_close = 0.0;
		
		StockPoint dayPoint;
		StockPoint nextDayPoint = null;
		
		try{
			dayDataStr = fileReader.readLine();
			while(dayDataStr != null){
				dayDataList = dayDataStr.split(",");
				// Process next line, if the current string line is not valid data
				if (dayDataList.length < 7){
					dayDataStr = fileReader.readLine();
					continue;
				}
				
				// Get the date (first strings of lines)
				dateStr = dayDataList[0].split("-");
				try
				{
					open = Double.valueOf(dayDataList[1]);
					high = Double.valueOf(dayDataList[2]);
					low = Double.valueOf(dayDataList[3]);
					close = Double.valueOf(dayDataList[4]);
					volume = Double.valueOf(dayDataList[5]);
					adj_close = Double.valueOf(dayDataList[6]);
					
					year = 2000 + Integer.valueOf(dateStr[2]);
					month = getMonthInt(dateStr[1]);
					date = Integer.valueOf(dateStr[0]);
				}
				catch(NumberFormatException nfe){
					dayDataStr = fileReader.readLine();
					continue;
				}
				// Save date into StockDate
				StockDate stockDate = new StockDate(year, month, date);
				
				dayPoint = new StockPoint(stockDate, open, high, low, close, volume, adj_close);
				
				// Add any previous missing day in the .cvs file
				if((year != 2007) || (month != 1) || (date != 25)){
					AddMissedDays(nextDayPoint.GetCalendar(), dayPoint);
				}
				
				dowJonesStock.AddPoint(dayPoint);
				nextDayPoint = dayPoint.clone();
				
				dayDataStr = fileReader.readLine();
				
//				System.out.print(year);
//				System.out.print("/");
//				System.out.print(month);
//				System.out.print("/");
//				System.out.println(date);
				
			}
		}
		catch(IOException ioe){
			System.out.println("Error in reading file");
		}
		for(int i=0; i<dowJonesStock.NumberOfPoints(); i++){
			System.out.print(dowJonesStock.GetDate(i).GetYear());
			System.out.print("/");
			System.out.print(dowJonesStock.GetDate(i).GetMonth());
			System.out.print("/");
			System.out.println(dowJonesStock.GetDate(i).GetDate());
		}
		System.out.println("Reading finished");
	}
	
	private enum Months{
		Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec
	}
	
	private int getMonthInt(String monthStr){
		switch(Months.valueOf(monthStr)){
		case Jan:
			return 1;
		case Feb:
			return 2;
		case Mar:
			return 3;
		case Apr:
			return 4;
		case May:
			return 5;
		case Jun:
			return 6;
		case Jul:
			return 7;
		case Aug:
			return 8;
		case Sep:
			return 9;
		case Oct:
			return 10;
		case Nov:
			return 11;
		case Dec:
			return 12;
		default:
			return 0;
		}
	}
	
	public StockPointsSet GetPointsSet(){
		return dowJonesStock;
	}
	
	/**
	 * Add missing days, such as weekends.
	 * The sotck prices on these days are the same as its previous valid day.
	 * @param currentDay 
	 * @param previousPoint the last valid stock point
	 */
	public void AddMissedDays(StockDate currentDay, StockPoint previousPoint){
		StockDate previousDay = previousPoint.GetCalendar().clone();
		int numMissedDays = 0;
		// The initial added days are reversed since the data in cvs file is reversed.
		Vector<StockPoint> reversedMissedDays = new Vector<StockPoint>();
		
		//The previous day is in the last year
		if(previousDay.GetMonth() > currentDay.GetMonth()){
			int endDay = 31;
			
			numMissedDays = currentDay.GetDate() + 30 - previousDay.GetDate();
			
			while(numMissedDays > 0){
				StockPoint duplicatedPoint;
				if(previousDay.GetDate() != endDay){
					previousDay.SetDate(previousDay.GetDate() + 1);
				}else{
					previousDay.SetMonth(currentDay.GetMonth());
					previousDay.SetDate(currentDay.GetDate() - numMissedDays);
				}
				
				duplicatedPoint = previousPoint.clone();
				duplicatedPoint.SetDate(previousDay);
				reversedMissedDays.add(duplicatedPoint);
				numMissedDays--;
			}
		}
		
		// The 2 days in different months
		if(previousDay.GetMonth() < currentDay.GetMonth()){
			int endDay = 31;
			// when the previous month is February, which has 28 days in total
			if(previousDay.GetMonth() == 2){
				endDay = 28;
				numMissedDays = currentDay.GetDate() + 27 - previousDay.GetDate();
				if(previousDay.GetYear() == 2000){
					endDay = 29;
					numMissedDays = currentDay.GetDate() + 29 - previousDay.GetDate();
				}
			}
			
			// When the previous month has 30 days in total
			if( (previousDay.GetMonth() == 4) || (previousDay.GetMonth() == 6) ||
					(previousDay.GetMonth() == 9) || (previousDay.GetMonth() == 11) ){
				endDay = 30;
				numMissedDays = currentDay.GetDate() + 29 - previousDay.GetDate();
			}
			
			if( (previousDay.GetMonth() == 1) || (previousDay.GetMonth() == 3) ||
					(previousDay.GetMonth() == 5) || (previousDay.GetMonth() == 7) ||
					(previousDay.GetMonth() == 8) || (previousDay.GetMonth() == 10) ||
					(previousDay.GetMonth() == 12) ){
//				 When the previous month has 31 days in total
				endDay = 31;
				numMissedDays = currentDay.GetDate() + 30 - previousDay.GetDate();
			}
			
			while(numMissedDays > 0){
				StockPoint duplicatedPoint;
				if(previousDay.GetDate() != endDay){
					previousDay.SetDate(previousDay.GetDate() + 1);
				}else{
					previousDay.SetMonth(currentDay.GetMonth());
					previousDay.SetDate(currentDay.GetDate() - numMissedDays);
				}
				
				duplicatedPoint = previousPoint.clone();
				duplicatedPoint.SetDate(previousDay);
				reversedMissedDays.add(duplicatedPoint);
				numMissedDays--;
			}
		}
		
		
			// The 2 days in the same month
		if(currentDay.GetMonth() == previousDay.GetMonth()){
			numMissedDays = currentDay.GetDate() - previousDay.GetDate() -1;
			while(numMissedDays > 0){
				StockPoint duplicatedPoint = previousPoint.clone();
				previousDay.SetDate(previousDay.GetDate() + 1);
				duplicatedPoint.SetDate(previousDay);
				reversedMissedDays.add(duplicatedPoint);
				numMissedDays--;
			}
		}
		
		for(int i=reversedMissedDays.size()-1; i>=0; i--){
			dowJonesStock.AddPoint(reversedMissedDays.get(i));
		}
		
	}
}
