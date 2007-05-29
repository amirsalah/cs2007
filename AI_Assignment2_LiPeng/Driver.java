
public class Driver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte[] opponent = new byte[3];
		opponent[0] = 1;
		opponent[1] = 2;
		opponent[2] = 2;
		MyPlayer me = new MyPlayer();
		me.makeMove(opponent);
		opponent[0] = 0;
		opponent[1] = 0;
		opponent[2] = 2;
//		me.makeMove(opponent);
	}

}
