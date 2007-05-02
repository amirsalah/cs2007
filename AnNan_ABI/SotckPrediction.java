import java.util.*;

public class SotckPrediction {
	public static void main(String args[])
	{
		fileProcess Import = new fileProcess();
		
		Vector<Double> rawData = Import.LoadClose();
		PredictProcess newData = new PredictProcess(rawData);
		
		Vector<Double> ColumeOfABS = null;
		Vector<Double> ColumeOfLMS = null;
		Vector<Double> predicted = null;
		ColumeOfABS = newData.Get_ABS();
		ColumeOfLMS = newData.Get_LMS();
		predicted = newData.prediction();
		
		// Save the prediction data into a file
		Import.SaveToFile(predicted,ColumeOfABS,ColumeOfLMS,newData.Get_Average_ABS_Error(),newData.Get_Average_LMS_Error());
	}
} 