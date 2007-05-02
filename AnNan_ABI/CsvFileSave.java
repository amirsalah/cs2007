import java.io.*;
import java.util.*;

public class CsvFileSave{
	
	public void Save(Vector<Double> originalData,Vector<Double> predictedData,
			Vector<Double> ABSCol,Vector<Double> LMSCol,double absMean, double lmsMean){
		Vector<Double> dates = null;
		
//		dates = 
		try{
			PrintWriter Writer = new PrintWriter(new FileWriter("DOWJONES_data_output.csv"));
			Writer.println("Adj.close" + "," + "Prediction P" + "," + "ABS Error" + "," + "LMS Error");
			for(int i = 0; i < originalData.size()-20; i++)
			{
				Writer.println(originalData.get(originalData.size() - 21 -i) + "," + predictedData.get(i) + "," +ABSCol.get(i) + 
						"," + LMSCol.get(i));
				Writer.flush(); 
			}
			Writer.println();
			Writer.println();
			Writer.println("The mean value of the ABS error:" + absMean);
			Writer.println("The mean value of the LMS error:" + lmsMean);
			Writer.close();
		}catch(IOException e){}
	}
}
