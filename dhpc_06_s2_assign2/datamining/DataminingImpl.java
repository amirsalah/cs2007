package datamining;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;


public class DataminingImpl implements Datamining{
	BufferedReader input = null;
	ArrayList<Integer> D = new ArrayList<Integer>();
	double summation = 0, N = 0;
	double mean = 0, tempSum = 0;;
	double[] corelations = new double[4];
	int[] numberCounter = new int[10], k;		
	int number, numbersIndex = 0;

	
	public double mean(String filename){
		BufferedReader input = null;
		double summation = 0, N = 0;
		String numberLine;
		char oneNumber;	          //One character in the input file.
		
        try {
            input = new BufferedReader(new FileReader(filename));
         }
         catch (java.io.FileNotFoundException e) {
             System.out.println("Input file '" + filename + "' could not be found");
             System.out.println("Usage: java Standalone inputfile");
         }
         catch(java.lang.ArrayIndexOutOfBoundsException e){
         	System.out.println("Insufficient argument");
             System.out.println("Usage: java Standalone inputfile");
         }
         
         try{
         	summation = 0;
         	
         	numberLine = input.readLine();
         	while(numberLine != null){
         		for(int i=0; i<numberLine.length(); i++){
         			oneNumber = numberLine.charAt(i);
         			
         			if(Character.isDigit(oneNumber)){
         				/* Number counter */
         				N++;
         				/* Calculate the total summation for N number */
         				number = Character.digit(oneNumber, 10);
         				summation += number;         				
        			}
        		}
        		/* Read a new line after processing */
        		numberLine = input.readLine();
        	}
         	/* Calculate the mean */
        	mean = summation/N;
         }
         catch(java.io.IOException e){
         	System.out.println("Error while reading input file");
         }
         
         return mean;
         
	}
	
	public double[] histogram(String filename){
		BufferedReader input = null;
		double N = 0;
		String numberLine;
		char oneNumber;	          //One character in the input file.
		double[] numberProb = new double[10]; //The proportion of the number from 0 - 9
		
        /* read parameters */
        //the file name is stored at args[0]
        try {
           input = new BufferedReader(new FileReader(filename));
        }
        catch (java.io.FileNotFoundException e) {
            System.out.println("Input file '" + filename + "' could not be found");
            System.out.println("Usage: java Standalone inputfile");
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e){
        	System.out.println("Insufficient argument");
            System.out.println("Usage: java Standalone inputfile");
        }
        
        /* Initialize the numbers counter */
        for(int i=0; i<10; i++){
        	numberCounter[i] = 0;
        }
        
        try{
        	summation = 0;
        	
        	numberLine = input.readLine();
        	while(numberLine != null){
        		for(int i=0; i<numberLine.length(); i++){
        			oneNumber = numberLine.charAt(i);
        			
        			if(Character.isDigit(oneNumber)){
        				/* Number counter */
        				N++;
        				/* Calculate the total summation for N number */
        				number = Character.digit(oneNumber, 10);

        				/* Refresh the numbers counter */
        				numberCounter[number] = 1 + numberCounter[number];
        			}
        		}
        	
        		/* Read a new line after processing */
        		numberLine = input.readLine();
        	}
        	/* Print out the histogram */
        	for(int i=0; i<10; i++){
        		numberProb[i] = numberCounter[i]/N;
        	}
        }
        catch(java.io.IOException e){
        	System.out.println("Error while reading input file");
        }
        
        return numberProb;
        				
	}
	
	
	public double correlations(String filename, int k){
		BufferedReader input = null;
		ArrayList<Integer> D = new ArrayList<Integer>();
		double N = 0;
		double mean = 0, tempSum = 0;;
		double corelations =0;
		int[] numberCounter = new int[10];		
		int number, numbersIndex = 0;
		String numberLine;
		char oneNumber;
		
        /* read parameters */
        //the file name is stored at args[0]
        try {
           input = new BufferedReader(new FileReader(filename));
        }
        catch (java.io.FileNotFoundException e) {
            System.out.println("Input file '" + filename + "' could not be found");
            System.out.println("Usage: java Standalone inputfile");
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e){
        	System.out.println("Insufficient argument");
            System.out.println("Usage: java Standalone inputfile");
        }
        
        /* Initialize the numbers counter */
        for(int i=0; i<10; i++){
        	numberCounter[i] = 0;
        }
        /* Add a meaningless value to D(0) */
        D.add(0);
        
        try{
        	numberLine = input.readLine();
        	while(numberLine != null){
        		for(int i=0; i<numberLine.length(); i++){
        			oneNumber = numberLine.charAt(i);
        			
        			if(Character.isDigit(oneNumber)){
        				/* Number counter */
        				N++;
        				
        				/* Transfer the character to integer */
        				number = Character.digit(oneNumber, 10);

        				/* Record the number, 
        				 * Note: the index is from 1, rathe than 0, to meet the formula */
        				numbersIndex++;
        				D.add(numbersIndex, number);
        				
        			}
        		}
        	
        		/* Read a new line after processing */
        		numberLine = input.readLine();
        	}
        	
        	mean = mean(filename);
        	for(int j=1; j<=N-k; j++){
        		tempSum += (D.get(j+k) - mean) * (D.get(j) - mean);
        	}
        	corelations = 1/(N-k) * tempSum;
        	
        }
        catch(java.io.IOException e){
        	System.out.println("Error while reading input file");
        }
        
        return corelations;	
	}
}
