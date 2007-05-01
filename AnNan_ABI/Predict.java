import java.util.*;

public class Predict {
	
	Vector<Double> S = new Vector<Double>();
	Vector<Double> y = null;
	LinkedList<Double> elements = new LinkedList<Double>();
	double predictedValue = 0.0;
	double sum = 0.0;
	double alpha = 0.8;
	
	public Predict(Vector<Double> raw){
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
	
	public Vector<Double> method()
	{
		ArrayList<Double> tempPrediction = new ArrayList<Double>(20);
		double previousY = 0.0;
		
		for(int i=0; i<y.size()-20; i++){
			tempPrediction.clear();
			init20Elements(i);
			
			tempPrediction.add(elements.get(0));
			
			for(int j=1; j<21; j++){
				previousY = y.get(y.size() - i - j);
				tempPrediction.add(alpha*previousY + (1-alpha)*tempPrediction.get(j - 1));
			}
			S.add(tempPrediction.get(20));
			sum += Math.abs(S.get(S.size()-1) - y.get(y.size() - 21 - i));
		}
 		return S;
	}
	
	public double GetABS_Error(){
		return sum/(double)(y.size()-20);
	}
}
