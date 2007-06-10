package stock_tracker.optimization.hybrid_system;

import stock_tracker.data.*;
import java.util.Vector;

/**
 * The Fuzzy logic system, with trading rules,
 * rules 1&2: MACD (Moving Average Convergence Divergence)
 * rule 3: Support and Resistance
 * rule 4: On Balance Volume
 * @author Bo CHEN
 */
public class FuzzyLogicRules {
	private int numRules = 2; // The number of rules in the rule base system
	private StockPointsSet dataSet; 
	private StockPoint today;
	
	private int increasingSpeed;
	private int decreasingSpeed;
	
	private Vector<Boolean> rulesActivation = new Vector<Boolean>(numRules);
	private double buyStrength;
	private int previousNDays = 10;
	private enum buyLevel{
		L0 , L1, L2, L3, L4, L5, L6, L7, L8, L9, L10
	}
	
	public FuzzyLogicRules(StockPointsSet dataSet){
		this.dataSet = dataSet;
		// Activate all the rules
		for(int i=0; i<numRules; i++){
			rulesActivation.add(true);
		}
	}
	
	public double BuyRecommend(StockPoint today){
		
		double sumRatings = 0;
		this.today = today;
		
		for(int i=0; i<numRules; i++){
			sumRatings += EvaluateRule(i);
		}
		
		buyStrength = sumRatings/(double)numRules;
		return buyStrength;
	}
	
	private double EvaluateRule(int ruleID){
		double buyRate;
		
		switch(ruleID){
		case 0:
			buyRate = MovingAveragesBuySignal();
			break;
		case 1:
			buyRate = MovingAveragesSellSignal();
			break;
//		case 2:
//			buyRate = S_And_R();
//			break;
//		case 3:
//			buyRate = OBV();
//			break;
//		case 4:
//			buyRate = PriceMovement();
//			break;
		default:
			buyRate = 0.5;
		break;
		}
		
		return buyRate;
	}
	
	/**
	 * Rule 1: Moving average buying signal
	 * @return trading rate: ranging from 0.0 - 1.0
	 */ 
	private double MovingAveragesBuySignal(){
		double buyRate = -1;
		
		if(today.isDecreasing()){
			buyRate = 0.3;
			return buyRate;
		}
		
		if(today.isIncreasing()){
			// Calculate the increasing rate difference between short-term & long-term prediction
			increasingSpeed = (int)(today.ShortIncreasingRate()/today.LongIncreasingRate());
			switch(increasingSpeed){
			case 0: // Short-term increasing slower than long-term
				buyRate = 0.4;
				break;
			case 1: // Short-term roughly increasing with the same speed as long-term
				buyRate = 0.5;
				break;
			case 2:
				buyRate = 0.6;
				break;
			case 3:
			case 4:
			case 5:
				buyRate = 0.7;
				break;
			case 6:
			case 7:
			case 8:
				buyRate = 0.8;
				break;
			case 9:
			case 10:
			case 11:
				buyRate = 0.9;
				break;
			default:
				buyRate = 1.0;
			break;
			}
			return buyRate;
		}
		
		buyRate = 0.5; //suggest "Hold"
		return buyRate;
		
	}
	
	/**
	 * Rule 2: Moving average selling signal
	 * 
	 */ 
	private double MovingAveragesSellSignal(){
		double buyRate;
		
		if(today.isIncreasing()){
			buyRate = 0.7;
			return buyRate;
		}
		
		if(today.isDecreasing()){
			// Calculate the increasing rate difference between short-term & long-term prediction
			decreasingSpeed = (int)(today.ShortDecreasingRate()/today.LongDecreasingRate());
			switch(decreasingSpeed){
			case 0: // Short-term decreasing slower than long-term
				buyRate = 0.6;
				break;
			case 1: // Short-term roughly increasing with the same speed as long-term
				buyRate = 0.5;
				break;
			case 2:
				buyRate = 0.4;
				break;
			case 3:
			case 4:
			case 5:
				buyRate = 0.3;
				break;
			case 6:
			case 7:
			case 8:
				buyRate = 0.2;
				break;
			case 9:
			case 10:
			case 11:
				buyRate = 0.1;
				break;
			default:
				buyRate = 0;
			break;
			}
			
			return buyRate;
		}
		
		buyRate = 0.5; //suggest "Hold"
		return buyRate;

	}
	
	/** 
	 * Rule 3: Support and Resistance
	 * @return the trading rate
	 */
	private double S_And_R(){
		int todayIndex = dataSet.GetIndex(today.GetCalendar());
		double buyRate = 0.5;
		double maxPastDays = 0;
		double minPastDays = dataSet.GetAdjClose(todayIndex + 1);
		
		for(int i=0; i<previousNDays; i++){
			if(dataSet.GetAdjClose(todayIndex + i + 1) > maxPastDays){
				maxPastDays = dataSet.GetAdjClose(todayIndex + i + 1);
			}
			
			if(dataSet.GetAdjClose(todayIndex + i + 1) < minPastDays){
				minPastDays = dataSet.GetAdjClose(todayIndex + i + 1);
			}
		}
		
		if(today.GetAdjClose() > maxPastDays){
			buyRate = 0.9;
		}
		
		if(today.GetAdjClose() < minPastDays){
			buyRate = 0.1;
		}
		
		return buyRate;
	}
	
	/**
	 * Rule 4: On Balance Volume
	 * @return the trading rate
	 */
	private double OBV(){
		double buyRate = 0.5;
		
		return buyRate;
	}
	
	/**
	 * Rule 4: Price movement over past N days. (N=20)
	 * @return
	 */
	private double PriceMovement(){
		double buyRate = 0.5;
		double totalMovement = 0;
		double averageMovement = 0;
		int todayIndex = dataSet.GetIndex(today.GetCalendar());
		
		// Price changed over past 20 days.
		for(int i=0; i<20; i++){
			totalMovement += Math.abs(dataSet.GetAdjClose(todayIndex + i) - dataSet.GetAdjClose(todayIndex + i + 1));
		}
		
		averageMovement = totalMovement/20;
		
		if(averageMovement > 500){
			buyRate = 1;
			return buyRate;
		}
		
		if(averageMovement > 300){
			buyRate = 0.9;
			return buyRate;
		}
		
		if(averageMovement > 100){
			buyRate = 0.7;
			return buyRate;
		}
		
		if(averageMovement > 100){
			buyRate = 0.6;
			return buyRate;
		}
		
		return buyRate;
	}
}
