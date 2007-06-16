import java.io.*;
import java.util.*;

public class CsvFileImport
{
	private static String line = null;
	BufferedReader in = null;
	Vector<Double> adj_close = new Vector<Double>(); 
	Vector<String> dates = new Vector<String>();
	Vector<Double> open = new Vector<Double>();
	Vector<Double> high = new Vector<Double>();
	Vector<Double> low = new Vector<Double>();
	Vector<Double> close = new Vector<Double>();
	Vector<Double> volume= new Vector<Double>();
	
	public Vector Load(){
		try{
			String fileName = "DOWJONES_revised_data.csv"; 
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			line = in.readLine();
			while((line = in.readLine()) != null){
				StringTokenizer st=new StringTokenizer(line,",");
				int tokenCount=0;
				while(st.hasMoreTokens()){
					tokenCount++;
					String token=st.nextToken();
					if(tokenCount == 1)			// the column of date
						dates.add(token);
					
					if(tokenCount == 2)	
					{
						try{
							double value2=Double.parseDouble(token);
							open.add(value2);
						}catch(NumberFormatException e){}
					}
					
					if(tokenCount == 3)	
					{
						try{
							double value3=Double.parseDouble(token);
							high.add(value3);
						}catch(NumberFormatException e){}
					}
					
					if(tokenCount == 4)	
					{
						try{
							double value4=Double.parseDouble(token);
							low.add(value4);
						}catch(NumberFormatException e){}
					}
					
					if(tokenCount == 5)	
					{
						try{
							double value5=Double.parseDouble(token);
							close.add(value5);
						}catch(NumberFormatException e){}
					}
					
					if(tokenCount == 6)	
					{
						try{
							double value6=Double.parseDouble(token);
							volume.add(value6);
						}catch(NumberFormatException e){}
					}
					
					if(tokenCount == 7) 		//the column of AdjClose
					{
						try{
						double value=Double.parseDouble(token);
						adj_close.add(value);
						}catch(NumberFormatException e){}
					}
				}
			}
		}catch(IOException e){}
		return adj_close;	
	}
}

