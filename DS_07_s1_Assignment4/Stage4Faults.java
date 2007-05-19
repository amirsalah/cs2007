public class Stage4Faults {
	public final static	int FIRST = 1;
	public final static	int NONE = 0;
	public final static int JOIN = 1;	
	public final static int JOINKEY = 2;
	public final static int JOINPRIV = 3;
	public final static int JOINURL = 4;	
	public final static int JOINNOTFOUND = 5;
	public final static int JOINWRONGTYPE = 6;	
	public final static int LEAVE = 7;
	public final static int LEAVEKEY = 8;	
	public final static int LEAVEPRIV = 9;
	public final static int JOINNOTIDLE = 10;
	
	public final static int LIMIT4 = 11;
	
	public final static String[] FAULTS = new String[]{ 
		"ok","joinFault","joinKeyFault","joinPrivFault","joinUrlFault","joinNotFoundFault","joinWrongTypeFault",
		"leaveFault","leaveKeyFault","leavePrivFault","joinNotIdleFault"
	};
}	