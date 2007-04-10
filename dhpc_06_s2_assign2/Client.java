import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.*;
import java.net.ConnectException;

public class Client{
    public static void main(String[] args) throws Exception{
    	  BufferedReader input = null;
    	  double mean = 0;
    	  double[] numberProb = new double[10];
    	  int [] k;
    	  datamining.ws.DataminingService service;
    	  
    	  
    	  try {
           input = new BufferedReader(new FileReader(args[1]));
        }
        catch (java.io.FileNotFoundException e) {
            System.out.println("Input file '" + args[1] + "' could not be found");
            System.out.println("Usage: java Standalone inputfile");
            System.exit(-1);
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e){
        	  System.out.println("Insufficient argument");
            System.out.println("Usage: java Standalone inputfile");
            System.exit(-1);
        }
        
        /* Make a service and handle the connectless exception */
        try{
    	  	  service = new datamining.ws.DataminingServiceLocator();

       	    /* Now use the service to get a stub which implements the SDI. */
       	    datamining.ws.Datamining dm = service.getdatamining();
    	  
       	    /* Make the actual call */ 
       	    /* Print out the mean */
    	      System.out.println("mean: " + dm.mean(args[1]));
    	  
    	      numberProb = dm.histogram(args[1]);
       	    /* Print out the histogram */
          	System.out.println("Histogram: ");
       	    for(int i=0; i<10; i++){
       		      System.out.println(i + ": " + numberProb[i]);
    	      }
    	  
    	      /* Initialize k values */
            k = new int[4];
            k[0] = 1;
            k[1] = 2;
            k[2] = 10;
            k[3] = 1000;
          
    	      /* Calculate and print out the corelations */
    	      for(int i =0; i<4; i++){
    	    	    System.out.println("corelations(" + k[i] + ") = " + dm.correlations(args[1], k[i]));
    	      }
    	  
    	  }
    	  /* Catch the connection problem */
    	  catch(Exception e){
    	      System.out.println("Services unavailable.");
    	      System.out.println("Please check your tomcat server status and the services availability");
           System.exit(-1);
    	  }
    	  
    }
    
    	
}