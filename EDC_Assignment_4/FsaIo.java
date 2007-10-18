import java.io.Reader;
import java.io.IOException;
import java.io.Writer;

public interface FsaIo 
{
    //This class handles reading and writing FSA representations as 
    //described in the practical specification

    //Read the description of a finite-state automaton from the 
    //InputStream, is, and transfers it to Fsa, f.
    public void read(Reader r, Fsa f) 
      throws IOException, FsaFormatException;
    
    
    //Write a representation of the Fsa, f, to the OutputStream, os.
    public void write(Writer w, Fsa f)
      throws IOException;
}
