import java.io.*;
import java.util.*;

public class CsvFileImport
{
	private static String line = null;
	BufferedReader in = null;
	Vector<Double> RawData = new Vector<Double>();
	
	public Vector<Double> Load(){
		try{
			String fileName = "DOWJONES_data.csv"; 
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			while((line = in.readLine()) != null){
				StringTokenizer st=new StringTokenizer(line,",");
				int tokenCount=0;
				while(st.hasMoreTokens()){
					tokenCount++;
					String token=st.nextToken();
					if(tokenCount == 7) 
					{
						try{
						double value=Double.parseDouble(token);
						//token=String.valueOf(value);
						RawData.add(value);
						}catch(NumberFormatException e){}
					}
				}
			}
		}catch(IOException e){}
		return RawData;	
	}
}

