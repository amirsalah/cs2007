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

	
	public Model(StockPointsSet dataSet){
		startDate = new StockDate(2000, 2, 24);
		endDate = new StockDate(2007, 1, 25);
		this.dataSet = dataSet;
	}
	
	/**
	 * Predict the stock prices based on the previous data
	 */
	public abstract void Predict();
	
	/**
	 * Return least mean square error(LMS Error)
	 * @param actualValue
	 * @param predictedValue
	 * @return
	 */
	public double LMSError(StockDate date, double predictedValue){
		double actualValue = dataSet.GetAdjClose(date);
		return Math.pow(actualValue - predictedValue, 2)/(double)2;
	}

	public double AbsError(StockDate date, double predictedValue){
		double actualValue = dataSet.GetAdjClose(date);
		return Math.abs(actualValue - predictedValue);
	}
	
	/**
	 *  Record the predicted value into a arraylist, with date as key
	 * @param date the day to predict
	 * @param predictedValue the predicted value in the day
	 */
	public void RecordPrediction(StockDate date, double predictedValue){
		double absError;
		double lmsError;
		ArrayList<Double> results = new ArrayList<Double>(numResults);
		
		absError = AbsError(date, predictedValue);
		lmsError = LMSError(date, predictedValue);
		results.add(predictedValue);
		results.add(absError);
		results.add(lmsError);

		predictedValues.put(date.toString(), results);
	}
	
	public StockPointsSet GetActualPoints(){
		return dataSet;
	}
	
	public HashMap<String, ArrayList<Double>> GetPredictionPoints(){
		return predictedValues;
	}
	
	public StockDate GetStartDate(){
		return startDate;
	}
	
	public StockDate GetEndDate(){
		return endDate;
	}
	
}
