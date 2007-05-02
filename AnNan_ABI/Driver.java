import java.util.*;

public class Driver {
	public static void main(String args[])
	{
		fileProcess Import = new fileProcess();
		CsvFileSave Output = new CsvFileSave();
		
		Vector<Double> rawData = Import.LoadClose();
		Predict newData = new Predict(rawData);
		
		Vector<Double> ColumeOfABS = null;
		Vector<Double> ColumeOfLMS = null;
		Vector<Double> predicted = null;
		ColumeOfABS = newData.Get_ABS();
		ColumeOfLMS = newData.Get_LMS();
		predicted = newData.prediction();

		Output.Save(rawData,predicted,ColumeOfABS,ColumeOfLMS,newData.Get_Average_ABS_Error(),newData.Get_Average_LMS_Error());
	}
}