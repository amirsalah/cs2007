/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 7th, August 2007
=========================================================*/

public class EnhancedReactionController implements Controller{
	private Gui gui;
	private Random rnGenerator;
	
	private int NUM_GAMES = 3;
	
//	private int numCoins = 0;
	private int currentTime = 0; //The timing infomation for the reaction system (millisecond)
	private int delay = 0;
	private int[] reactionTimes = new int[NUM_GAMES];
	private int averageTime = 0;
	private int NthGame = 0;
	private int finalResultDelay = 5000;
	
	// Different stages of the system
	private enum Stages{
		DemandCoin, CoinInserted, DelayPeriod, WaitingUser, DisplayResult, GameOver
	}
	private Stages currentStage;
	
	public EnhancedReactionController(){
		currentStage = Stages.DemandCoin;
	}
	
    //Connect controller to gui
    //(This method will be called before any other methods)
    public void connect(Gui gui, Random rng){
    	this.gui = gui;
    	rnGenerator = rng;
    }

    //Called to initialise the controller
    public void init(){
    	gui.setDisplay("insert coin");
    	currentTime = 0;
    	delay = 0;
    	for(int i=0; i<3; i++){
    		reactionTimes[i] = 0;
    	}
    	finalResultDelay = 5000;
    }

    //Called whenever a coin is inserted into the machine
    public void coinInserted(){
//    	numCoins++;
		gui.setDisplay("Press GO!");
		currentStage = Stages.CoinInserted;
		NthGame = 0;
		delay = 10000;
		currentTime = 0;
    }

    //Called to deliver a TICK to the controller
    public void tick(){
    	switch(currentStage){
    	case CoinInserted:
    		// abort the game if the user didn't press go/stop button
    		currentTime += 10;
    		if(currentTime == delay){
    			currentStage = Stages.DemandCoin;
    			gui.setDisplay("Insert coin");
    		}
    		break;
    		
    	case DelayPeriod:
    		currentTime += 10;
    		// delay expires, stage transformed to wait for the user reaction
    		if(currentTime == delay){
    			currentStage = Stages.WaitingUser;
    			currentTime = 0; // Initilized the timer for the next stage
    			gui.setDisplay("0.00"); // Display the starting time value "0.00"
    		}
    		break;
    		
    	case WaitingUser:
    		currentTime += 10;
    		// The user has maximum 2 second to react, or the system will press go/stop automatically
    		if(currentTime == 2000){
    			goStopPressed();
    		}else{
    			gui.setDisplay(TimeToString(currentTime));
    		}
    		break;
    		
    	case DisplayResult:
    		currentTime += 10;
    		// Display result for a single game for 3 seconds
    		if(currentTime == delay){
    			if(NthGame < 3){
    				// Start a new game (automatically insert a coin)
    				currentStage = Stages.CoinInserted;
    				goStopPressed();
    			}
    			if(NthGame == 3){
    				currentStage = Stages.GameOver;
    				currentTime = 0;
    				gui.setDisplay("Average time= " + TimeToString(averageTime)); // Immediately display the average time
    			}
    		}else{
    			gui.setDisplay(TimeToString(reactionTimes[NthGame - 1]));
    		}
    		break;
    		
    	case GameOver:
    		currentTime += 10;
    		// Display the average time for 5 seconds
    		if(currentTime == finalResultDelay){
				gui.setDisplay("Insert coin");
				currentStage = Stages.DemandCoin;
    		}else{
    			gui.setDisplay("Average time= " + TimeToString(averageTime));
    		}
    		break;
    	}
    }

    
	public void goStopPressed() {
		// State transformed if the button is pressed.
		switch(currentStage){
			case CoinInserted:
				delay = rnGenerator.getRandom(100, 250); // millisecond
				delay = delay*10; // delay in milliseconds [1000, 2500),
				currentTime = 0;
				currentStage = Stages.DelayPeriod;
				gui.setDisplay("Wait...");
//				NthGame = 0; // 3 games for 1 single coin
				break;
				
				// User pressed the button before the delay expire
			case DelayPeriod:
				currentStage = Stages.DemandCoin;
				gui.setDisplay("insert coin");
				break;
				
				// User reacted, show the result for 3 seconds
			case WaitingUser:
				reactionTimes[NthGame] = currentTime;
				NthGame++;
				currentTime = 0;
				delay = 3000;
				// stage transformed
				currentStage = Stages.DisplayResult;
				
				// Calculate the average time after 3 games
				if(NthGame == 3){
					averageTime = 0;
					for(int i=0; i<3; i++){
						averageTime += reactionTimes[i];
					}
					averageTime = averageTime/3;
				}
				break;
				
			case DisplayResult:
    			if(NthGame < 3){
    				currentStage = Stages.CoinInserted;
    				goStopPressed();
    			}
    			if(NthGame == 3){
    				currentStage = Stages.GameOver;
    			}
				break;
				
				// User pressed the go/stop button while displaying the reaction time value
			case GameOver:
				currentStage = Stages.DemandCoin;
				gui.setDisplay("insert coin");
				NthGame = 0;
				break;
		}
	}
	
	/* Transform the time from int (millisecond) to String form "t.tt" */
	private String TimeToString(int time){
		double timeInSecond = time/(double)1000.0;
		String timeString = null;
		
		timeString = String.valueOf(timeInSecond);
		
		if(timeString.length() == 3){
			timeString += "0";
		}
		
		if(timeString.length() >=5){
			timeString = timeString.substring(0, 4);
//			System.out.println("possible Error result");
		}
		
		return timeString;
	}
	
}
