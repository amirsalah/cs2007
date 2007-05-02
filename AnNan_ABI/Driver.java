import java.util.*;

public class Driver {
	public static void main(String args[])
	{
		CsvFileImport Import = new CsvFileImport();
		CsvFileSave Output = new CsvFileSave();
		
		Vector<Double> raw = Import.Load();
		Predict newData = new Predict(raw);
		
		Vector<Double> ColumeOfABS = null;
		Vector<Double> ColumeOfLMS = null;
		Vector<Double> predicted = null;
		ColumeOfABS = newData.Get_ABS();
		ColumeOfLMS = newData.Get_LMS();
		predicted = newData.prediction();

		Output.Save(raw,predicted,ColumeOfABS,ColumeOfLMS,newData.Get_Average_ABS_Error(),newData.Get_Average_LMS_Error());
	}
}