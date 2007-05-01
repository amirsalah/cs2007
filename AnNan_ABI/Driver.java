import java.util.*;

public class Driver {
	public static void main(String args[])
	{
		CsvFileImport Import = new CsvFileImport();
		CsvFileSave Output = new CsvFileSave();
		Vector<Double> raw = Import.Load();
		
		Predict newData = new Predict(raw);
	
		Vector<Double> result = newData.method();
		double abs = newData.GetABS_Error();
		System.out.println("ABS Error: " + abs);
		Output.Save(result);
	}
}
