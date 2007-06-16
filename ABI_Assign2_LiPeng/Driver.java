
public class Driver {
	static CsvFileImport dowJones;
	
	public static void main(String args[])
	{
		dowJones = new CsvFileImport();
		CsvFileSave Output = new CsvFileSave();
		
		dowJones.Load();
		
		Optimization optima = new Optimization(dowJones.close);
		optima.optimize();
		Output.Save(optima);
	}
}
