public class Stage1Faults {
	public final static	int FIRST = 0;
	public final static	int NONE = 0;
	public final static int BIND = 1;
	public final static int LOOKUP = 2;
	public final static int NOT_BOUND = 3;
	public final static int WRONG_TYPE = 4;
	public final static int NAME = 5;
	public final static int CONNECT = 6;
	public final static int DISCONNECT = 7;
	public final static int SEND = 8;
	public final static int LIMIT1 = 9;
	
	public final static String[] FAULTS = new String[]{ 
		"ok", "bindFault", "lookupFault", "notboundFault", "wrongtypeFault", 
		"nameFault", "connectFault", "disconnectFault", "sendFault" 
	};
}	