package stock_tracker.models;

import stock_tracker.data.*;

public abstract class TimeSeriesModel extends Model{

	public TimeSeriesModel(StockPointsSet dataSet){
		super(dataSet);
	}
	
	public abstract void Predict();
	

}



