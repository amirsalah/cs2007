package stockpredictor.models;

import java.util.HashMap;
import java.util.ArrayList;

import stockpredictor.data.StockDate;
import stockpredictor.data.StockPointsSet;

/**
 * The abstract model for different prediction techniques.
 * All the prediction model should extend from this model, and implements predict()
 */
public abstract class Model {
	protected int numResults = 3;
	protected HashMap<String, ArrayList<Double>> predictedValues = new HashMap<String, ArrayList<Double>>(numResults);
	protected StockDate startDate;
	protected StockDate endDate;
	protected StockPointsSet dataSet;
	protected int numPredictionDays;
	protected int startIndex;

	
	public Model(StockPointsSet dataSet){
		startDate = new StockDate(2000, 2, 24);
		endDate = new StockDate(2007, 1, 25);
		this.dataSet = dataSet;
		numPredictionDays = dataSet.Length() - 29; // There are 29 days before 2000/2/24
		startIndex = dataSet.GetIndex(startDate);
	}
	
	/**
	 * Predict the stock prices based on the previous data
	 */
	public abstract void Predict();
	
	/**
	 * Return least mean square error(LMS Error)
	 * @param index the index of predicting date in the data Set (dowJonesStock)
	 * @param predictedValue
	 * @return the LMS error value
	 */
	public double LMSError(int index, double predictedValue){
		double actualValue = dataSet.GetAdjClose(index);
		return Math.pow(actualValue - predictedValue, 2)/(double)2;
	}

	public double AbsError(int index, double predictedValue){
		double actualValue = dataSet.GetAdjClose(index);
		return Math.abs(actualValue - predictedValue);
	}
	
	/**
	 *  Record the predicted value into a arraylist, with date as key
	 * @param predictingDate the day to predict
	 * @param predictedValue the predicted value in the day
	 */
	public void StorePrediction(int index, double predictedValue){
		double absError;
		double lmsError;
		
		absError = AbsError(index, predictedValue);
		lmsError = LMSError(index, predictedValue);
		dataSet.GetPoint(index).SetPredictionValue(predictedValue, absError, lmsError);
	}
	
	public StockPointsSet GetActualPoints(){
		return dataSet;
	}
	
	public StockDate GetStartDate(){
		return startDate;
	}
	
	public StockDate GetEndDate(){
		return endDate;
	}
	
}
