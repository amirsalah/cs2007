import java.io.BufferedReader;
import java.io.FileReader;

public class Standalone {
	
	public static void main(String[] args){
		datamining.DataminingImpl dm = new datamining.DataminingImpl();
		BufferedReader input = null;
		double mean = 0;
		int [] k;
		double[] numberProb = new double[10];

        /* read parameters */
        //the file name is stored at args[0]
        try {
           input = new BufferedReader(new FileReader(args[0]));
        }
        catch (java.io.FileNotFoundException e) {
            System.out.println("Input file '" + args[0] + "' could not be found");
            System.out.println("Usage: java Standalone inputfile");
            System.exit(-1);
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e){
        	System.out.println("Insufficient argument");
            System.out.println("Usage: java Standalone inputfile");
            System.exit(-1);
        }
        
        mean = dm.mean(args[0]);
        
    	/* Print out the mean */
    	System.out.println("mean = " + mean);
    	
    	numberProb = dm.histogram(args[0]);
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
    		System.out.println("corelations(" + k[i] + ") = " + dm.correlations(args[0], k[i]));
    	}
        
	}
}
