package stockpredictor.models;

import stockpredictor.data.*;

public abstract class TimeSeriesModel extends Model{

	public TimeSeriesModel(StockPointsSet dataSet){
		super(dataSet);
	}
	
	public abstract void Predict();
	

}



