package stockpredictor;

import stockpredictor.models.*;

public class StockDriver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataReader dowJonesReader = new DataReader("DOWJONES_data.csv");
		System.out.println("Read completed");
		
		TimeSeriesModel predictionModel = new MovingAverageModel(dowJonesReader.GetPointsSet());
		predictionModel.Predict();
	
	}

}
