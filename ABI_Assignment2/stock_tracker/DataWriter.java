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
	public void WriteRecordToFile(Model predictionModel){
		StockPointsSet dataSet = predictionModel.GetActualPoints();;
		
		String dateStr;
		String adjCloseStr;
		String predictedValueStr;
		String absErrorStr;
		String lmsErrorStr;
		
		String tempStr;
		
		double absError = 0.0;
		double lmsError = 0.0;
		
		double sumAbsErrors = 0.0;
		double sumLmsErrors = 0.0;
		
		// Write title for each column to the file
		fileWriter.println("Date,Adj.Close*,Prediction,ABS Error,LMS Error");
		
		int numDays = dataSet.Length() - 29;
		for(int i=0; i<numDays; i++){
			//Transfer all the data into String type
			dateStr = dataSet.GetPoint(i).GetCalendar().toString();
			adjCloseStr = String.valueOf(dataSet.GetAdjClose(i));
			predictedValueStr = String.valueOf(dataSet.GetPoint(i).GetPredictedValue());
			
			absError =dataSet.GetPoint(i).GetAbsError() ;
			absErrorStr = String.valueOf(absError);

			lmsError =  dataSet.GetPoint(i).GetLmsError();
			lmsErrorStr = String.valueOf(lmsError);

			// Sum all the errors
			sumAbsErrors += absError;
			sumLmsErrors += lmsError;

			// Add "," as token to be parsed by Microsoft Excel
			tempStr = dateStr + "," + adjCloseStr + ","
						+ predictedValueStr + ","
						+ absErrorStr + ","
						+ lmsErrorStr;
				
			fileWriter.println(tempStr);
		}

		fileWriter.println();
		fileWriter.println("Average ABS error: " + (double)sumAbsErrors/(dataSet.Length() - 29) + ",");
		fileWriter.println("Average LMS error: " + (double)sumLmsErrors/(dataSet.Length() - 29) + ",");
		fileWriter.close();
	}
		
}
