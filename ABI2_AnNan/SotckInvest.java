import java.util.*;

public class SotckInvest {
	public static void main(String args[])
	{
		fileProcess fileOperator = new fileProcess();
		
		// Generate adj. close values
		Vector<Double> rawData = fileOperator.LoadClose();
		// Use the generated data to make a new prediction
		OptimizationProcess optimizor = new OptimizationProcess(rawData);

		// Get prediction values
		optimizor.optimize();

		// Save the prediction data into a file
		fileOperator.SaveToFile(optimizor);
	}
}