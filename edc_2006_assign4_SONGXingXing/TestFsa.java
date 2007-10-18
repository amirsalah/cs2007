import java.io.FileInputStream;
import java.io.IOException;

/**
 * 
 */

/**
 * @author star
 *
 */
public class TestFsa {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Fsa fsa = new FsaImpl() ;
		try {										    	
	        FileInputStream in = null;				 
	        try {
	        	
	            in = new FileInputStream("input2.fsa" );
	            fsa.read(in) ;
	           
	        }
	        catch (IOException e) {
	            System.err.println("Caught IOException: " 
	                                + e.getMessage());
	           
	        }
	        catch (FsaFileException e) {
	            System.err.println("Caught FsaFileException: " 
	                                + e.toString());
	            		            
	        }
	        finally {
	        	if (in != null) {
	                in.close();
	            }	           
	        }					
			}
			catch(IOException e)
			{
				
			}
			System.out.println(fsa.toString());
			
			
	}

}
