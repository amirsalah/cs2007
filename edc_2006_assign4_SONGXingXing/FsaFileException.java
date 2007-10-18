public class FsaFileException extends Exception
{
    private String msg;
    private int lineNr;

    public FsaFileException(String msg, int lineNr)
    {
	this.msg= msg;
	this.lineNr= lineNr;
    }


    public String toString()
    {
	return lineNr+":0:"+msg;
    }
}
