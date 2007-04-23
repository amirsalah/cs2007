package stockpredictor.models;

import stockpredictor.data.*;
import java.lang.Math;

public abstract class TimeSeriesModel {
	protected StockDate startDate;
	protected StockPointsSet dataSet;
	
	public TimeSeriesModel(StockPointsSet dataSet){
		startDate = new StockDate(2000, 2, 24);
		this.dataSet = dataSet;
	}
	
	public abstract void Predict();
	
	/**
	 * Return least mean square error(LMS Error)
	 * @param actualValue
	 * @param predictedValue
	 * @return
	 */
	public double LMSError(double actualValue, double predictedValue){
		return Math.pow(actualValue - predictedValue, 2)/(double)2;
	}
	
}
