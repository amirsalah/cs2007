import java.util.*;

public class SotckPrediction {
	public static void main(String args[])
	{
		fileProcess fileOperator = new fileProcess();
		
		// Generate adj. close values
		Vector<Double> rawData = fileOperator.LoadClose();
		// Use the generated data to make a new prediction
		PredictProcess predictionResults = new PredictProcess(rawData);
		
		Vector<Double> allABSError = null;
		Vector<Double> allLMSError = null;
		Vector<Double> predictedValues = null;
		allABSError = predictionResults.Get_ABS();
		allLMSError = predictionResults.Get_LMS();
		// Get prediction values
		predictedValues = predictionResults.prediction();
		
		// Save the prediction data into a file
		fileOperator.SaveToFile(predictedValues, allABSError, allLMSError,
				predictionResults.Get_Average_ABS_Error(),predictionResults.Get_Average_LMS_Error());
	}
}