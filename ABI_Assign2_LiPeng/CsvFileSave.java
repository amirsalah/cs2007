import java.io.*;

public class CsvFileSave 
{
	CsvFileImport dowJones = Driver.dowJones;
	
	public void Save(Optimization optima)
	{
		try{
			PrintWriter Writer = new PrintWriter(new FileWriter("DOWJONES_Optimization_data.csv"));
			Writer.println("Date" + "," + "Adj.close" + "," + "Buy/Sell" + "," + "Bank" + "," + "Shares");
			for(int i = 0; i < dowJones.close.size()-29; i++)
			{
				Writer.println(dowJones.dates.get(dowJones.dates.size() - 30 - i) + "," + 
						dowJones.close.get(dowJones.close.size() - 30 -i) + "," + 
						optima.gotShares.get(i) + "," + optima.balance.get(i) + 
						"," + optima.existingShares.get(i));
			}
			Writer.close();
		}catch(IOException ioe){
			System.out.println("Saving data failed");
		}
	}
	
}
