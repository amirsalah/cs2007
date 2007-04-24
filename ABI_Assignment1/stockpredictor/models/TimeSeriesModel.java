package stockpredictor.models;

import stockpredictor.data.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class TimeSeriesModel extends Model{

	public TimeSeriesModel(StockPointsSet dataSet){
		super(dataSet);
	}
	
	public abstract void Predict();
	

}



