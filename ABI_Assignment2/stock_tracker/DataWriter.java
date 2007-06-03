/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 27th, May 2007
=========================================================*/
package stock_tracker;

import stock_tracker.models.*;
import stock_tracker.data.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class DataWriter {
	private PrintWriter fileWriter;
	
	public DataWriter(String fileName){
		try{
			fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
		}catch(IOException ioe){
			System.out.println("Could not find the file: " + fileName);
		}
	}
	
	/**
	 * Write records, involving: Date, Adj_close, Prediction, ABS Error and LMS Error
	 * to a file. The average errors are also computed and saved in the bottom of the file
	 * The data is sorted by its dates
	 * @param predictionModel the model that contains predicted values (after invoking Pridict() method)
	 */
	public void WriteRecordToFile(StockPointsSet givenDataSet){
		StockPointsSet dataSet = givenDataSet;;
		
		String dateStr;
		String adjCloseStr;
		String predictedValueStr;
		String absErrorStr;
		String lmsErrorStr;
		
		String tempStr;
		
		String sharesBuyStr;
		String balanceStr;
		String sharesStr;
		
		double absError = 0.0;
		double lmsError = 0.0;
		
		double sumAbsErrors = 0.0;
		double sumLmsErrors = 0.0;
		
		// Write title for each column to the file
		fileWriter.println("Date,Adj.Close*,Buy/Sell,Bank,Shares");
		
		int numDays = dataSet.Length() - 29;
		for(int i=0; i<numDays; i++){
			//Transfer all the data into String type
			dateStr = dataSet.GetPoint(i).GetCalendar().toString();
			adjCloseStr = String.valueOf(dataSet.GetAdjClose(i));
//			predictedValueStr = String.valueOf(dataSet.GetPoint(i).GetPredictedValue());
			
//			absError =dataSet.GetPoint(i).GetAbsError() ;
//			absErrorStr = String.valueOf(absError);

//			lmsError =  dataSet.GetPoint(i).GetLmsError();
//			lmsErrorStr = String.valueOf(lmsError);

			// Sum all the errors
//			sumAbsErrors += absError;
//			sumLmsErrors += lmsError;

			/***
			 * Generate new .cvs file.
			 */
/*			String openStr = String.valueOf(dataSet.GetStockPrices(i).get(0));
			String highStr = String.valueOf(dataSet.GetStockPrices(i).get(1));
			String lowStr = String.valueOf(dataSet.GetStockPrices(i).get(2));
			String closeStr = String.valueOf(dataSet.GetStockPrices(i).get(3));
			String volumeStr = String.valueOf(dataSet.GetStockPrices(i).get(4));
			
			tempStr = dateStr + "," 
			+ openStr + ","
			+ highStr + ","
			+ lowStr + ","
			+ closeStr + ","
			+ volumeStr + ","
			+ adjCloseStr;
*/			
			sharesBuyStr = String.valueOf(dataSet.GetPoint(i).GetSharesBuy());
			balanceStr = String.valueOf(dataSet.GetPoint(i).GetBalance());
			sharesStr = String.valueOf(dataSet.GetPoint(i).GetShares());
			
			// Add "," as token to be parsed by Microsoft Excel
			tempStr = dateStr + "," 
						+ adjCloseStr + ","
						+ sharesBuyStr + ","
						+ balanceStr + ","
						+ sharesStr;
				
			fileWriter.println(tempStr);
		}

//		fileWriter.println();
//		fileWriter.println("Average ABS error: " + (double)sumAbsErrors/(dataSet.Length() - 29) + ",");
//		fileWriter.println("Average LMS error: " + (double)sumLmsErrors/(dataSet.Length() - 29) + ",");
		fileWriter.close();
	}
		
}
