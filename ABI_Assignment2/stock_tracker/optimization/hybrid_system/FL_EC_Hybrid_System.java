 package stock_tracker.optimization.hybrid_system;

import stock_tracker.DataReader;
import stock_tracker.data.*;
import stock_tracker.models.DoubleExponentialSmoothingModel;
import stock_tracker.optimization.*;

/**
 * Hybrid optimization system, combining double exponential smoothing prediction,
 * fuzzy rules base system, and evolutionary algorithm.
 * 
 * The evolutionary algorithm is used to evlove the fuzzy trading rules sets, 
 * which are used to make trading decision.
 * 
 * Note: the system is initilized with simulation data: dow jones data from 1995-2000
 * and the best system is selected using EA algorithm.
 * @author Bo CHEN
 *
 */
public class FL_EC_Hybrid_System extends Optimization{
	private StockPointsSet simulationData;
	private boolean simulationStage = true;
	private StockPointsSet dataSet;
	
	public FL_EC_Hybrid_System(StockPointsSet dataSet){
		super(dataSet);
		this.dataSet = dataSet;
		DataReader simulationDataReader = new DataReader("DOWJONES_Simulation_data.csv");
		simulationData = simulationDataReader.GetSimulationData();
	}
	
	public double optimize(){
		FuzzyLogicRules bestFLRulesSet = null;
		if(simulationStage){
			// Initialize optimization model with historical data
			DoubleExponentialSmoothingModel predictionModel = new DoubleExponentialSmoothingModel(simulationData, 0.4, 0.4);
			predictionModel.SetStartDate(1995, 4, 7);
			predictionModel.SetSimulationStage(true);
			predictionModel.Predict();
			
			startDate = new StockDate(1995, 4, 7);
			numPredictionDays = simulationData.Length() - 30; // 30 days before 1995/4/7
			startIndex = simulationData.GetIndex(startDate);
			// Initialize the bank account for the starting date
			simulationData.GetPoint(startIndex).SetBalance(1);
			simulationData.GetPoint(startIndex).SetShares(0);
			
			simulationData.GetPoint(startIndex + 1).SetBalance(0);
			simulationData.GetPoint(startIndex + 1).SetShares(0);
			
			EA_System eaSystem = new EA_System(simulationData, dataSet);
			bestFLRulesSet = eaSystem.evolve();
			
			simulationStage = false;
		}
		
		// Initialize optimization model with historical data
		DoubleExponentialSmoothingModel predictionModel = new DoubleExponentialSmoothingModel(dataSet, 0.4, 0.4);
		predictionModel.SetStartDate(2000, 2, 24);
		predictionModel.SetSimulationStage(false);
		predictionModel.Predict();
		
		// Start real optimization
		startDate = new StockDate(2000, 2, 24);
		startIndex = dataSet.GetIndex(startDate);
		numPredictionDays = dataSet.Length() - 29; // There are 29 days before 2000/2/24
		startIndex = dataSet.GetIndex(startDate);
		
		for(int i=0; i<numPredictionDays; i++){
			// initialize the current account by yesterday's values.
			StockPoint yesterday = dataSet.GetPoint(startIndex - i + 1);
			StockPoint today = dataSet.GetPoint(startIndex - i);
			today.BankingInit(yesterday);			
//			double buyRate = rulesSet.BuyRecommend(today);
			
			if(bestFLRulesSet.BuyRecommend(today) >= 0.8){
				today.BuyMax();
				System.out.println("Buy:" + today.GetCalendar());
			}
			
			if(bestFLRulesSet.BuyRecommend(today) <= 0.2){
				today.SellMax();
				System.out.println("Sell:" + today.GetCalendar());
			}
			
		}
		
		StockPoint lastDay = dataSet.GetPoint(startIndex - numPredictionDays + 1);
		lastDay.SellMax();
		return lastDay.GetBalance();

	}
	
}
