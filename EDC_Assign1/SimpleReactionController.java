/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 7th, August 2007
=========================================================*/

public class SimpleReactionController implements Controller{
	private Gui gui;
	private Random rnGenerator;
	
//	private int numCoins = 0;
	private int currentTime = 0; //The timing infomation for the reaction system
	private int delay = 0;
	private double reactionTime = 0;
	
	// Different stages of the system
	private enum Stages{
		noCoin, coinInserted, DelayPeriod, WaitingUser, GameOver
	}
	private Stages currentStage;

	
	public SimpleReactionController(){
		currentStage = Stages.noCoin;
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
    	reactionTime = 0;
    }

    //Called whenever a coin is inserted into the machine
    public void coinInserted(){
//    	numCoins++;
		gui.setDisplay("press GO!");
		currentStage = Stages.coinInserted;
    }


    //Called to deliver a TICK to the controller
    public void tick(){
    	switch(currentStage){
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
    		
    	case GameOver:
    		currentTime = 0;
    		// Display the result for 3 seconds
    		if(delay <=0){
				gui.setDisplay("insert coin");
    		}else{
    			delay -= 10;
    			gui.setDisplay(String.valueOf(reactionTime));
    		}
    		break;
    	}
    }

    
	public void goStopPressed() {
		// State transformed if the button is pressed.
		switch(currentStage){
			case coinInserted:
				delay = rnGenerator.getRandom(1000, 2500); // millisecond
				
				currentStage = Stages.DelayPeriod;
				gui.setDisplay("Wait...");
				break;
				
				// User pressed the button before the delay expire
			case DelayPeriod: 
				currentStage = Stages.noCoin;
				gui.setDisplay("insert coin");
				break;
				
				// User reacted, show the result for 3 seconds
			case WaitingUser:
				delay = 0;
				currentStage = Stages.GameOver;
				reactionTime = (double)currentTime/(double)1000;
				delay = 3000;
				break;
				
				// User pressed the button while displaying the reaction time value
			case GameOver:
				currentStage = Stages.noCoin;
				gui.setDisplay("insert coin");
				break;
		}
		
	}
    
}
