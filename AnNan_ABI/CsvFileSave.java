import java.io.*;
import java.util.*;

public class CsvFileSave{
	CsvFileImport FI = new CsvFileImport();
	Vector<Double> r = null;
	
	public CsvFileSave(){
		r = FI.Load();
	}
	public void Save(Vector<Double> result){
		try{
			PrintWriter Writer = new PrintWriter(new FileWriter("DOWJONES_data_output.csv"));
			
			for(int i = 0; i < r.size(); i++)
			{
				Writer.println(r.get(i));
				Writer.flush(); 
			}
			Writer.close();
		}catch(IOException e){}
	}
}
