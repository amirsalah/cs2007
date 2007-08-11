/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 7th, August 2007
=========================================================*/

public class EnhancedReactionController implements Controller{
	private Gui gui;
	private Random rnGenerator;
	
//	private int numCoins = 0;
	private int currentTime = 0; //The timing infomation for the reaction system
	private int delay = 0;
	private double[] reactionTimes = new double[3];
	private double averageTime = 0;
	private int timesCounter = 0;
	private int finalResultDelay = 5000;
	
	// Different stages of the system
	private enum Stages{
		NoCoin, CoinInserted, DelayPeriod, WaitingUser, DisplayResult, GameOver
	}
	private Stages currentStage;

	
	public EnhancedReactionController(){
		currentStage = Stages.NoCoin;
	}
	
    //Connect controller to gui
    //(This method will be called before any other methods)
    public void connect(Gui gui, Random rng){
    	this.gui = gui;
    	rnGenerator = rng;
    }

    //Called to initialise the controller
    public void init(){
    	currentTime = 0;
    	delay = 0;
    	for(int i=0; i<3; i++){
    		reactionTimes[i] = 0;
    	}
    	timesCounter = 0;
    	finalResultDelay = 5000;
    }

    //Called whenever a coin is inserted into the machine
    public void coinInserted(){
//    	numCoins++;
		gui.setDisplay("Press GO!");
		currentStage = Stages.CoinInserted;
		timesCounter = 0;
		delay = 10000;
    }


    //Called to deliver a TICK to the controller
    public void tick(){
    	switch(currentStage){
    	case CoinInserted:
    		// abort the game if the user didn't press go/stop button
    		if(delay <= 0){
    			currentStage = Stages.NoCoin;
    			gui.setDisplay("insert coin");
    		}else{
    			delay -= 10;
    		}
    		break;
    		
    	case DelayPeriod:
    		// delay expires, stage transformed to wait for the user reaction
    		if(delay <= 0){
    			currentStage = Stages.WaitingUser;
    		}else{
    			delay -= 10;
    		}
    		break;
    		
    	case WaitingUser:
    		// The user has maximum 2 second to react, or the system will press go/stop automatically
    		if(currentTime >= 2000){
    			goStopPressed();
    		}else{
    			currentTime +=10;
    			gui.setDisplay(String.valueOf(currentTime));
    		}
    		break;
    		
    	case DisplayResult:
    		// Display result for a single game for 3 seconds
    		if(delay <=0){
    			if(timesCounter < 3){
    				currentStage = Stages.CoinInserted;
    				goStopPressed();
    			}
    			if(timesCounter == 3){
    				currentStage = Stages.GameOver;
    			}
    		}else{
    			delay -= 10;
    			gui.setDisplay(String.valueOf(reactionTimes[timesCounter - 1]));
    		}
    		break;
    		
    	case GameOver:
    		currentTime = 0;
    		// Display the average time for 5 seconds
    		if(finalResultDelay <=0){
				gui.setDisplay("insert coin");
				finalResultDelay = 0;
    		}else{
    			finalResultDelay -= 10;
    			gui.setDisplay(String.valueOf(averageTime));
    		}
    		break;
    	}
    }

    
	public void goStopPressed() {
		// State transformed if the button is pressed.
		switch(currentStage){
			case CoinInserted:
				delay = rnGenerator.getRandom(1000, 2500); // millisecond
				
				// The system display "waiting" in the delay period
				currentStage = Stages.DelayPeriod;
				gui.setDisplay("Wait...");
				break;
				
				// User pressed the button before the delay expire
			case DelayPeriod:
				currentStage = Stages.NoCoin;
				gui.setDisplay("insert coin");
				break;
				
				// User reacted, show the result for 3 seconds
			case WaitingUser:
				delay = 0;
				reactionTimes[timesCounter] = (double)currentTime/(double)1000;
				timesCounter++;  
				currentTime = 0;
				delay = 3000;
				// stage transformed
				currentStage = Stages.DisplayResult;
				
				// Perform the game 3 times
				if(timesCounter == 3){
					for(int i=0; i<3; i++){
						averageTime += reactionTimes[i];
					}
					averageTime = averageTime/(double)3.0;
				}
				break;
				
			case DisplayResult:
    			if(timesCounter < 3){
    				currentStage = Stages.CoinInserted;
    				goStopPressed();
    			}
    			if(timesCounter == 3){
    				currentStage = Stages.GameOver;
    			}
				break;
				
				// User pressed the go/stop button while displaying the reaction time value
			case GameOver:
				currentStage = Stages.NoCoin;
				gui.setDisplay("insert coin");
				timesCounter = 0;
				break;
		}
	}
}
