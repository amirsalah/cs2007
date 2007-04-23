/*=======================================================
  File reader to read stock market data from a file
  
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 19th, April 2007
=========================================================*/
package stockpredictor;

import java.io.*;
import java.util.*;

import stockpredictor.data.*;


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
//				if(dowJonesStock.ContainsDate(new StockDate(2007, 1, 25	)))
//					System.out.println("got it");
				
				dayPoint = new StockPoint(stockDate, open, high, low, close, volume, adj_close);
				dowJonesStock.AddPoint(dayPoint);
				
				dayDataStr = fileReader.readLine();
			}
		}

		catch(IOException ioe){
			System.out.println("Error in reading file");
		}
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
	
}
