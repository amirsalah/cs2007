public class Stage2Faults {
	public final static	int FIRST = 1;
	public final static	int NONE = 0;
	public final static int LIST = 1;
	public final static int SAVE = 2;
	public final static int CONNECT_WITH_TRANS = 3;
	public final static int LIMIT2 = 4;
	
	public final static String[] FAULTS = new String[]{ 
		"ok", "listFault", "saveFault", "connectWithTransFault"
	};
}	