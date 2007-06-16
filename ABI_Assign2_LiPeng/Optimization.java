import java.util.*;

public class Optimization {
	int shortDays = 2;
	int longDays = 16;
	double SOverL = 12;
	Random rn = new Random();
	CsvFileImport dowJones = Driver.dowJones;
	Prediction_LinearRegression predictonModel;
	
	Vector<Double> adj_close = new Vector<Double>();
	Vector<Double> balance = new Vector<Double>();
	Vector<Double> gotShares = new Vector<Double>();
	Vector<Double> existingShares = new Vector<Double>();
	
	public Optimization(Vector<Double> aclose){
		adj_close = aclose;
	}
	
	public void optimize()
	{
		int startingDay = 2527;
		int dataIndex = -1;
		boolean ifUp;
		double srdays = 0.0;
		double lrdays = 0.0;
		double newAdjClose = 0;
		predictonModel = new Prediction_LinearRegression(adj_close);
		predictonModel.initialPredictionModel();
		
		balance.add((double)1);
		existingShares.add((double)0);
		gotShares.add((double)0);
		
		
		for(int i=startingDay; i>=0; i--){
			srdays = 0;
			lrdays = 0;
			
			dataIndex++;
			if(i != startingDay){
				balance.add(balance.get(dataIndex - 1) + 1);
				existingShares.add(existingShares.get(dataIndex - 1));
				gotShares.add((double)0);
			}
			
			newAdjClose = predictonModel.predictAdjClose(i);
			ifUp = false;
			if((newAdjClose - adj_close.get(i + 1)) > 0){
				ifUp = true;
			}
			for(int j=0; j<shortDays; j++){
				srdays = srdays + adj_close.get(i + 1 + j) - adj_close.get(i + 2 + j);
			}
			srdays = srdays/(double)shortDays;
			for(int j=0; j<longDays; j++){
				lrdays = lrdays + adj_close.get(i + 1 + j) - adj_close.get(i + 2 + j);
			}
			lrdays = lrdays/(double)longDays;
			if( srdays < 0 ){
				if( lrdays < 0 ){
					if( srdays < lrdays *(double)SOverL){
						double getShares = balance.get(dataIndex) * 0.997 / adj_close.get(i);
						double newShares = existingShares.get(dataIndex) + getShares;
						existingShares.set(dataIndex, newShares);
						gotShares.set(dataIndex, getShares);
						balance.set(dataIndex, (double)0);
					}
				}
			}
			if( !ifUp ){
				if( lrdays >0 ){
					if( srdays > (double)SOverL * lrdays){
						double sellShares = existingShares.get(dataIndex);
						double getMoney = sellShares * adj_close.get(i) * 0.997;
						gotShares.set(dataIndex, -sellShares);
						balance.set(dataIndex, getMoney + balance.get(dataIndex));
						existingShares.set(dataIndex, (double)0);
					}
				}
			}
			if(i%30 == 0 && i < 2500){
				predictonModel.AdaptLRParameters(i);
				Adaption_EA adaption = new Adaption_EA(this);
				adaption.EvolveParameters(i);
			}
		}
		
		double sellShares = existingShares.get(existingShares.size() - 1);
		double getMoney = sellShares * adj_close.get(0) * 0.997;
		balance.set(existingShares.size() - 1, getMoney + balance.get(existingShares.size() - 1));
		existingShares.set(existingShares.size() - 1, (double)0);
		
		
		System.out.println("earn: $" + balance.get(balance.size() - 1));
	}
	
}
