package stockpredictor;

import stockpredictor.models.*;
import stockpredictor.data.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
		HashMap<String, ArrayList<Double>> predictedValues = predictionModel.GetPredictionPoints();
		StockDate endDate = predictionModel.GetEndDate();;
		StockPointsSet dataSet = predictionModel.GetActualPoints();;
		
		String dateStr;
		String adjCloseStr;
		String predictedValueStr;
		String absErrorStr;
		String lmsErrorStr;
		
		String tempStr;
		
		// The index of generated values in the arraylists
		int indexPredictedValue = 0;
		int indexAdjError = 1;
		int indexLmsError = 2;
		
		double absError = 0.0;
		double lmsError = 0.0;
		
		double sumAbsErrors = 0.0;
		double sumLmsErrors = 0.0;
		
		// Write title for each column to the file
		fileWriter.println("Date,Adj.Close*,Prediction,ABS Error,LMS Error");
		try{
			for(int i=0; i<predictedValues.size(); i++){
				//Transfer all the data into String type
				dateStr = endDate.toString();
				adjCloseStr = String.valueOf(dataSet.GetAdjClose(endDate));
				predictedValueStr = String.valueOf(predictedValues.get(endDate.toString()).get(indexPredictedValue));
				
				absError = predictedValues.get(endDate.toString()).get(indexAdjError);
				absErrorStr = String.valueOf(absError);
				
				lmsError =  predictedValues.get(endDate.toString()).get(indexLmsError);
				lmsErrorStr = String.valueOf(lmsError);

				// sum all the errors
				sumAbsErrors += absError;
				sumLmsErrors += lmsError;

				// Add "," as token to be parsed by Microsoft Excel
				tempStr = dateStr + "," + adjCloseStr + ","
							+ predictedValueStr + ","
							+ absErrorStr + ","
							+ lmsErrorStr;
				
				fileWriter.println(tempStr);
				
				endDate = endDate.PreviousValidDate(dataSet);
			}
		}
		catch(InvalidDateException ide){
			return;
		}
			fileWriter.println();
			fileWriter.println("Average ABS error: " + (double)sumAbsErrors/predictedValues.size() + ",");
			fileWriter.println("Average LMS error: " + (double)sumLmsErrors/predictedValues.size() + ",");
			fileWriter.close();
	}
		
}
