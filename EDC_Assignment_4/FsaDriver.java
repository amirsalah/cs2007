import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class FsaDriver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName = "fsa.txt";
		String writerFileName = "fsaWriter.txt";
		
		Fsa fsa = new FsaImpl();
		FsaReaderWriter frw = new FsaReaderWriter();
		FileReader r = null;
		FileWriter w = null;
		
		
		try{
			r = new FileReader(fileName);
			w = new FileWriter(writerFileName);
		}
		catch(FileNotFoundException fnfe){
			System.out.println("file not found");
		}
		catch(IOException ioe){
			System.out.println("io exception in writer");
		}
		
		try{
			frw.read(r, fsa);
			frw.write(w, fsa);
		}
		catch(FsaFormatException ffe){
			System.out.println("fsa exception");
		}
		catch(IOException ioe){
			System.out.println("io exception");
		}
		
		
		
		System.out.println("fsa driver");
	}

}
