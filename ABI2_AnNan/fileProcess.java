import java.io.*;
import java.util.*;

public class fileProcess {
	private  Vector<Double> adjclose = new Vector<Double>();
	public Vector<Double> open = new Vector<Double>();
	public Vector<Double> high = new Vector<Double>();
	public Vector<Double> low = new Vector<Double>();
	public Vector<Double> volume = new Vector<Double>();
	
	private  Vector<String> dates = new Vector<String>();

	public Vector<String> LoadDate(){
		BufferedReader Reader;
		String oneDay = null; // a string contains one day data
		String[] subDay = null; // strings contains data of ONE day
		String date = null;
		double adj_close = 0.0;
		double open_d = 0;
		double high_d = 0;
		double low_d = 0;
		double volume_d = 0;
		

		try{
			Reader = new BufferedReader(new FileReader("DOWJONES_full_data.csv"));

			oneDay = Reader.readLine();
			oneDay = Reader.readLine();
			while(oneDay != null){
				subDay = oneDay.split(",");
				
				// Get the dates
				date = subDay[0];
				dates.add(date);
				
				// Get the adj close value and save it
				adj_close = Double.valueOf(subDay[6]);
				adjclose.add(adj_close);
				
				// Get open value
				open_d = Double.valueOf(subDay[1]);
				open.add(open_d);
				
				high_d = Double.valueOf(subDay[2]);
				high.add(high_d);
				
				low_d = Double.valueOf(subDay[3]);
				low.add(low_d);
				
				volume_d = Double.valueOf(subDay[5]);
				volume.add(volume_d);
				
				// Next line is read
				oneDay = Reader.readLine();
			}
		}catch(IOException ioe){
			System.out.println("Error: file missing " + "DOWJONES_data.csv");
		}
		return dates;
	}
	
	public Vector<Double> LoadClose(){
		LoadDate();
		return adjclose;
	}
	
	/*
	 * Write the generated data into a file, named DOWJONES_Prediction_data.csv
	 * The data contains date, adj. close, prediction value, abs error and lms error
	 */
	public void SaveToFile(OptimizationProcess optimizor){
		try{
			PrintWriter Writer = new PrintWriter(new FileWriter("DOWJONES_Optimization_data.csv"));
			Writer.println("Date" + "," + "Adj.close" + "," + "Buy/Sell" + "," + "Bank" + "," + "Shares");
			for(int i = 0; i < adjclose.size()-29; i++)
			{
				Writer.println(dates.get(dates.size() - 30 - i) + "," + 
						adjclose.get(adjclose.size() - 30 -i) + "," + 
						optimizor.sharesBuy.get(i) + "," + optimizor.bankBalance.get(i) + 
						"," + optimizor.holdingShares.get(i));
			}
			Writer.println();
			Writer.println();
			Writer.close();
		}catch(IOException e)
		{
			System.out.println("IO exception!");
		}
	}
}
