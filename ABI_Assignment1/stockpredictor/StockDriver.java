package stockpredictor;

import stockpredictor.models.*;

/**
 * @author Bo CHEN
 * The driver routine for the stock predictior
 */
public class StockDriver {

	public static void main(String[] args) {
		DataReader dowJonesReader = new DataReader("DOWJONES_data.csv");
		
//		Model predictionModel = new MovingAverageModel(dowJonesReader.GetPointsSet());		
//		Model predictionModel = new SingleExponentialSmoothingModel(dowJonesReader.GetPointsSet(), 0.5);
		
		/* Double exponential method, the 2nd parameter specify the alpha, 3rd specify the gamma value */
		Model predictionModel = new DoubleExponentialSmoothingModel(dowJonesReader.GetPointsSet(), 0.90, 0.90);
		
		predictionModel.Predict();
		DataWriter writer = new DataWriter("DOWJONES_predicted_data.csv");
		writer.WriteRecordToFile(predictionModel);
	}

}
