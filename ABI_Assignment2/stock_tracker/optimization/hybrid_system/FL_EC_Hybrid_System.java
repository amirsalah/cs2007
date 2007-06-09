package stock_tracker.optimization.hybrid_system;

import stock_tracker.DataReader;
import stock_tracker.data.*;
import stock_tracker.optimization.*;

public class FL_EC_Hybrid_System extends Optimization{
	private StockPointsSet simulationData;
	private boolean simulationStage = true;
	
	public FL_EC_Hybrid_System(StockPointsSet dataSet){
		super(dataSet);
		DataReader simulationDataReader = new DataReader("DOWJONES_Simulation_data.csv");
		simulationData = simulationDataReader.GetSimulationData();
	}
	
	public double optimize(){
		if(simulationStage){
			// Initialize optimization model with historical data
			startDate = new StockDate(1995, 4, 7);
			startIndex = dataSet.GetIndex(startDate);
			simulationStage = false;
			
			
		}
		
		// Start real optimization
		startDate = new StockDate(2000, 2, 24);
		startIndex = dataSet.GetIndex(startDate);
		
		
		return 0;
	}
	
}
