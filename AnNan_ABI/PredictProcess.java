import java.util.*;

public class PredictProcess {
	
	Vector<Double> S = new Vector<Double>();
	Vector<Double> y = null;
	Vector<Double> absError = new Vector<Double>();
	Vector<Double> lmsError = new Vector<Double>();
	
	LinkedList<Double> elements = new LinkedList<Double>();
	double predictedValue = 0.0;
	double sum = 0.0;
	double alpha = 0.9;
	double sumABS;
	double sumLMS;
	
	public PredictProcess(Vector<Double> raw){
		y = raw;
	}
	
	private void init20Elements(int index){
		elements.clear();
		
		//Initial elements which contain 20 oldest data
		for(int i=0; i<20; i++){
			double j = y.get(y.size() - 1 - i - index);
			elements.addLast(j);
		}
	}
	
	public Vector<Double> prediction()
	{
		ArrayList<Double> tempPrediction = new ArrayList<Double>(20);
		double previousY = 0.0;
		double absErrorValue = 0.0;
		sumABS = 0.0;
		double lmsErrorValue = 0.0;
		sumLMS = 0.0;
		
		for(int i=0; i<y.size()-20; i++){
			tempPrediction.clear();
			init20Elements(i);
			
			tempPrediction.add(elements.get(0));
			
			for(int j=1; j<21; j++){
				previousY = y.get(y.size() - i - j);
				tempPrediction.add(alpha*previousY + (1-alpha)*tempPrediction.get(j - 1));
			}
			S.add(tempPrediction.get(20));
			// Compute abs Error and sum all the error values
			absErrorValue = Math.abs(tempPrediction.get(20) - y.get(y.size() - 21 - i));
			absError.add(absErrorValue); //Save it to the vector
			sumABS += absErrorValue;
			
			// Compute LMS error and sum all the error values
			lmsErrorValue = Math.pow(absErrorValue, 2)/(double)2.0;
			lmsError.add(lmsErrorValue);
			sumLMS += lmsErrorValue;
			
			sum += Math.abs(S.get(S.size()-1) - y.get(y.size() - 21 - i));
		}
 		return S;
	}
	
	
	public Vector<Double> Get_ABS(){
		return absError;
	}
	
	public Vector<Double> Get_LMS(){
		return lmsError;
	}
	
	public double Get_Average_ABS_Error(){
		return sumABS/(double)(y.size()-20);
	}
	
	public double Get_Average_LMS_Error(){
		return sumLMS/(double)(y.size()-20);
	}
}
