public class Stage3Faults {
	public final static	int FIRST = 1;
	public final static	int NONE = 0;
	public final static int LOGIN = 1;	
	public final static int CONNECTKEY = 2;
	public final static int DISCONNECTKEY = 3;
	public final static int SENDKEY = 4;	
	public final static int GETTRANSKEY = 5;
	public final static int GETTRANSPRIV = 6;	
	public final static int CONNECTWITHTRANSKEY = 7;
	public final static int CONNECTWITHTRANSPRIV = 8;	
	public final static int SAVETRANSKEY = 9;
	public final static int SAVETRANSPRIV = 10;	
	public final static int SHUTDOWNKEY = 11;
	public final static int SHUTDOWNPRIV = 12;	
	public final static int SETPRIV = 13;	
	public final static int SETPRIVKEY = 14;
	public final static int SETPRIVPRIV = 15;	
	public final static int SETPASS = 16;	
	public final static int SETPASSKEY = 17;
	public final static int SETPASSPRIV = 18;	
	public final static int CREATEACC = 19;	
	public final static int CREATEACCKEY = 20;
	public final static int CREATEACCPRIV = 21;	
	public final static int LOGOUT = 22;	
	public final static int LOGOUTKEY = 23;
	public final static int ISPRIV = 24;	
	
	
	public final static int LIMIT3 = 25;
	
	public final static String[] FAULTS = new String[]{ 
		"ok", "loginFault", "connectKeyFault", "disconnectKeyFault", "sendKeyFault", "listKeyFault", "listPrivFault", "connectWithTransKeyFault", 
		"connectWithTransPrivFault", "saveKeyFault", "savePrivFault", "shutdownKeyFault", "shutdownPrivFault", "setPrivFault", "setPrivKeyFault", 
		"setPrivPrivFault","setPassFault", "setPassKeyFault",  "setPassPrivFault","createAccountFault", "createAccountKeyFault",  "createAccountPrivFault",
		"logoutFault", "logoutKeyFault", "isPrivFault"
	};
}	