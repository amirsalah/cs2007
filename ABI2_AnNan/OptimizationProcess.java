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
	private Random rn = new Random();
	private int numSR = 2;
	private int numLR = 16;
	private double times = 15;
	
	fileProcess stockData;
	LinearRegression LRPrediction;

	
	public OptimizationProcess(Vector<Double> raw){
		rawData = raw;
	}
	
	public void optimize()
	{
		int dataIndex = -1;
		double SR = 0.0;
		double LR = 0.0;
		double newAdjClose = 0;
		boolean STIncrease;
		double buyRate = 0.005;
		
		stockData = new fileProcess();
		LRPrediction = new LinearRegression(rawData);
		
		// Initialize the prediction model
		LRPrediction.InitLinearModel();
		
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
			
			// Predict adj close using linear regression model
			newAdjClose = LRPrediction.predictAdjClose(i);
			if((newAdjClose - rawData.get(i + 1)) > 0){
				STIncrease = true;
			}else{
				STIncrease = false;
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
			if( !STIncrease && LR >0 && SR > (double)times * LR){
				double sellShares = holdingShares.get(dataIndex);
				double getMoney = sellShares * rawData.get(i) * 0.997;
				sharesBuy.set(dataIndex, -sellShares);
				bankBalance.set(dataIndex, getMoney + bankBalance.get(dataIndex));
				holdingShares.set(dataIndex, (double)0);
//				System.out.println("sell");
			}
			
			if(!STIncrease && rn.nextDouble() < buyRate){
				double getShares = bankBalance.get(dataIndex) * 0.997 / rawData.get(i);
				double newShares = holdingShares.get(dataIndex) + getShares;
				holdingShares.set(dataIndex, newShares);
				bankBalance.set(dataIndex, (double)0);
				
				sharesBuy.set(dataIndex, getShares);
			}
			
			// Buy shares
			if( SR < 0 && LR < 0 && SR < LR *(double)times){
				double getShares = bankBalance.get(dataIndex) * 0.997 / rawData.get(i);
				double newShares = holdingShares.get(dataIndex) + getShares;
				holdingShares.set(dataIndex, newShares);
				bankBalance.set(dataIndex, (double)0);
				
				sharesBuy.set(dataIndex, getShares);
			}
			
			// Adapt prediction model every 20 days
			if(i%20 == 0 && i < 2520){
				LRPrediction.AdaptLRParameters(i);
				AdaptOptimizationSystem(i);
			}
		}
		
		if(holdingShares.get(holdingShares.size() - 1) > 0){
			double sellShares = holdingShares.get(holdingShares.size() - 1);
			double getMoney = sellShares * rawData.get(0) * 0.997;
			sharesBuy.set(holdingShares.size() - 1, -sellShares);
			bankBalance.set(holdingShares.size() - 1, getMoney + bankBalance.get(holdingShares.size() - 1));
			holdingShares.set(holdingShares.size() - 1, (double)0);
		}
		
		System.out.println("Cash out: $" + bankBalance.get(bankBalance.size() - 1));
	}
	
	// Using hill climing technique to adapt the optimization system
	private void AdaptOptimizationSystem(int index){
		for(int i=0; i<100; i++){
			int NewnumSR = numSR + rn.nextInt(1);
			int NewnumLR = numLR + rn.nextInt(1);
			double Newtimes = 15 + rn.nextInt(1);
			
			double initShares = holdingShares.get(holdingShares.size() - 21); // get data in 20 days ago
			double initBalance = bankBalance.get(holdingShares.size() - 21);
			double earnedMoney = 0;
			double bestearnedMoney = 0;
			
			if(NewnumSR > 5){
				NewnumSR = 2;
			}
			
			if(NewnumLR > 20){
				NewnumLR = 16;
			}
			
			if(Newtimes > 20){
				Newtimes = 15;
			}
			
			for(int j=0; j<20; j++){
				double SR = 0;
				double LR = 0;
				initBalance++;
				
				// Compute short term trend
				for(int isr=0; isr<NewnumSR; isr++){
					SR += rawData.get(index + 1 + isr) - rawData.get(index + 2 + isr);
				}
				
				SR = SR/(double)NewnumSR;
				
				// Compute long term trend
				for(int ilr=0; ilr<NewnumLR; ilr++){
					LR += rawData.get(index + 1 + ilr) - rawData.get(index + 2 + ilr);
				}
				
				LR = LR/(double)NewnumLR;
				
				// sell shares
				if( SR >0 && LR >0 && SR > (double)Newtimes * LR){
					initBalance = initBalance + initShares * rawData.get(index + j) * 0.997;
					initShares = 0;
				}
				
				
				// Buy shares
				if( SR < 0 && LR < 0 && SR < LR *(double)Newtimes){
					initShares = initShares + initBalance * 0.997 / rawData.get(index + j);
					initBalance = 0;
				}
				
				if(j == 19){
					earnedMoney = initBalance + initShares * rawData.get(index + j) * 0.997;
					if(earnedMoney > bestearnedMoney){
						bestearnedMoney = earnedMoney;
						numSR = NewnumSR;
						numLR = NewnumLR;
						times = Newtimes;
					}
					
				}
			}
			
		}
		
	}
	

	
}
