/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 27th, May 2007
=========================================================*/
package stock_tracker;

import stock_tracker.optimization.*;
import stock_tracker.optimization.hybrid_system.*;

/**
 * @author Bo CHEN
 * The driver routine for the stock predictior
 */
public class StockDriver {

	public static void main(String[] args) {
		DataReader dowJonesReader = new DataReader("DOWJONES_data.csv");
		
//		Optimization optimizor = new PermanentHolder(dowJonesReader.GetPointsSet());
//		Optimization optimizor = new RandomBuyer(dowJonesReader.GetPointsSet());
//		Optimization optimizor = new PermanentBuyer(dowJonesReader.GetPointsSet());
		Optimization optimizor = new FL_EC_Hybrid_System(dowJonesReader.GetPointsSet());
//		Optimization optimizor = new MACDBasedOptimization(dowJonesReader.GetPointsSet());
		System.out.println("Cash out at 2007/1/25: $" + optimizor.optimize());
		DataWriter writer = new DataWriter("DOWJONES_Stock_activities.csv");
		writer.WriteRecordToFile(dowJonesReader.GetPointsSet());
	}

}
