import java.util.*;

/*
 * The prediction is based on Single exponential smoothing method,
 * The windows is set to 20
 */
public class OptimizationProcess {
	Vector<Double> rawData = null; // the raw adj. close data
	Vector<Double> bankBalance = new Vector<Double>();
	Vector<Double> sharesBuy = new Vector<Double>();
	Vector<Double> holdingShares = new Vector<Double>();
	
	private int numSR = 9;
	private int numLR = 14;
	private double times = 19;
	
	public OptimizationProcess(Vector<Double> raw){
		rawData = raw;
	}
	
	public void optimize()
	{
		int dataIndex = -1;
		double SR = 0.0;
		double LR = 0.0;
		
		for(int i=2527; i>=0; i--){
			SR = 0;
			LR = 0;
			
			dataIndex++;
			if(i == 2527){
				bankBalance.add((double)1);
				holdingShares.add((double)0);
				sharesBuy.add((double)0);
			}else{
				// Initialize the banking data and shares
				bankBalance.add(bankBalance.get(dataIndex - 1) + 1);
				holdingShares.add(holdingShares.get(dataIndex - 1));
				sharesBuy.add((double)0);
			}
			
			// Compute short term trend
			for(int j=0; j<numSR; j++){
				SR += rawData.get(i + 1 + j) - rawData.get(i + 2 + j);
			}
			
			SR = SR/(double)numSR;
			
			// Compute long term trend
			for(int j=0; j<numLR; j++){
				LR += rawData.get(i + 1 + j) - rawData.get(i + 2 + j);
			}
			
			LR = LR/(double)numLR;
			
			// Sell shares
			if(SR > 0 && LR >0 && SR > (double)times * LR){
				double sellShares = holdingShares.get(dataIndex);
				double getMoney = sellShares * rawData.get(i) * 0.997;
				sharesBuy.set(dataIndex, -sellShares);
				bankBalance.set(dataIndex, getMoney + bankBalance.get(dataIndex));
				holdingShares.set(dataIndex, (double)0);
			}
			
			// Buy shares
			if(SR < 0 && LR < 0 && SR < LR *(double)times){
				double getShares = bankBalance.get(dataIndex) * 0.997 / rawData.get(i);
				double newShares = holdingShares.get(dataIndex) + getShares;
				holdingShares.set(dataIndex, newShares);
				bankBalance.set(dataIndex, (double)0);
				
				sharesBuy.set(dataIndex, getShares);
			}
		}
		
		if(holdingShares.get(holdingShares.size() - 1) > 0){
			double sellShares = -holdingShares.get(holdingShares.size() - 1);
			double getMoney = sellShares * rawData.get(0) * 0.997;
			sharesBuy.set(holdingShares.size() - 1, sellShares);
			bankBalance.set(holdingShares.size() - 1, getMoney + bankBalance.get(holdingShares.size() - 1));
			holdingShares.set(holdingShares.size() - 1, (double)0);
		}

		System.out.println(bankBalance.get(bankBalance.size() - 1));
	}
	

	
}
