import java.lang.Math;

public class MyMath {
	
	/* Min */
	public static double Min(double a1, double a2){
		return Math.min(a1, a2);
	}
	public static double Min(double a1, double a2, double a3){
		return Math.min(Math.min(a1, a2), a3);
	}
	public static double Min(double a1, double a2, double a3, double a4){
		return Math.min(Math.min(Math.min(a1, a2), a3), a4);
	}
	public static double Min(double a1, double a2, double a3, double a4, double a5){
		return Math.min( Math.min(Math.min(Math.min(a1, a2), a3), a4), a5);
	}
	
	/* Max */
	public static double Max(double a1, double a2, double a3){
		return Math.max( Math.max(a1, a2), a3);
	}
}
