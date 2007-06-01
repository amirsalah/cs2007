

import java.io.*;
import java.util.*;

public class Predictsystem
{
	private static String line = null;
	BufferedReader in = null;

	public Predictsystem(){
		super();
	}

public static void main(String args[]){
//get data from .csv file
    String[][] Wdata=new String[1800][7] ;
    int i=0;
    double[] fre=new double[1800];

    try
    {
    	String fileName = "DOWJONES_data.csv";
    	BufferedReader in = new BufferedReader(new FileReader(fileName));
    	line=in.readLine();
    	while((line = in.readLine()) != null){
    		StringTokenizer st=new StringTokenizer(line,",");
    		int tokenCount=0;
    		while(st.hasMoreTokens()){
    			tokenCount++;
    			String token=st.nextToken();
    			if(token.indexOf("\"")!=-1){
    				String next;
    				while((next=st.nextToken()).indexOf("\"")==-1)
    					token+=next;//ugly,might use a stringbuffer
    			}
    			Wdata[i][tokenCount-1]=token;
    		}
    		i=i+1;
    	}
    }
    catch (IOException e)
    {
    	System.err.println("wrong");
    }
//////////////////////////////////////////
//get data from .csvfile finish
////////////////////////////////////////////

    //operate data
   // Mnet trr=new Mnet(Wdata,i);
    //fre=trr.Trueop(i);
	Network network=new Network(Wdata,i);
	fre=network.Process();

//////////////////////////////////////////////////
//write data into .csvfile.
//////////////////////////////////////////////////

	try{
		File write=new File("fin_result.csv");
		BufferedWriter wr=new BufferedWriter(new FileWriter(write));
		wr.write("Date,Adj. Close,PredictPrice,ABS_ERROR,LMS_ERROR*"+"\r");
		for(int j=0;j<i-19;j++){
			double lms;
			double abs;
			abs=Math.abs(Double.parseDouble(Wdata[j][6])-fre[j]);
			lms=abs*abs/2;
			wr.write(Wdata[j][0]+","+Wdata[j][6]+","+fre[j]+","+abs+","+lms+"\r");
		}
		wr.close();
 	}catch(IOException e){
 		System.out.println("we have some trouble in write file");
 	}
 	System.out.println("mission complete");
	}
}
