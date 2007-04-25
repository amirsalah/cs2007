package stockpredictor;

import stockpredictor.models.*;

public class StockDriver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataReader dowJonesReader = new DataReader("DOWJONES_data.csv");
		System.out.println("Read completed");
		
		Model predictionModel = new DoubleExponentialSmoothingModel(dowJonesReader.GetPointsSet(), 0.85, 0.9);
		predictionModel.Predict();
		DataWriter writer = new DataWriter("DOWJONES_predicted_data.csv");
		writer.WriteRecordToFile(predictionModel);
	}

}
