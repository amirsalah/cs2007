/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 7th, August 2007
=========================================================*/

public class SimpleReactionController implements Controller{
	private Gui gui;
	private Random rnGenerator;
	
//	private int numCoins = 0;
	private int currentTime = 0; //The timing infomation for the reaction system, milliseconds
	private int delay = 0;
	private String strReactionTime = null; 
	
	// Different stages of the system
	private enum Stages{
		DemandCoin, coinInserted, DelayPeriod, WaitingUser, GameOver
	}
	private Stages currentStage;

	
	public SimpleReactionController(){
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
    	currentTime = 0;
    	delay = 0;
    	gui.setDisplay("insert coin");
    }

    //Called whenever a coin is inserted into the machine
    public void coinInserted(){
    	// numCoins++;
		gui.setDisplay("Press GO!");
		currentStage = Stages.coinInserted;
    }

    //Called to deliver a TICK to the controller
    public void tick(){
    	switch(currentStage){
    	case DemandCoin:
    		gui.setDisplay("Insert coin");
    		break;
    		
    	case DelayPeriod:
    		// Note: timer should be initilized first, since the tick() is executed after sleep(10)
			currentTime += 10;
    		// delay expires, stage transformed to wait for the user reaction
    		if(currentTime == delay){
    			currentStage = Stages.WaitingUser;
    			currentTime = 0; // Initilized the timer for the next stage
    			gui.setDisplay("0.00"); //Display the starting time value "0.00"
    		}
//    		System.out.println(strCurrentTime);
    		break;
    		
    	case WaitingUser:
			currentTime += 10;
    		// The user has maximum 2 second to react, or the system will press go/stop automatically
    		if(currentTime == 2000){
    			goStopPressed();
    		}else{
    			gui.setDisplay(TimeToString(currentTime));
    		}
//			System.out.println(currentTime/1000.0);
//			System.out.println(strCurrentTime);
    		break;
    		
    	case GameOver:
			currentTime += 10;
    		// Display the result for 3 seconds
    		if(currentTime == 3000){
				gui.setDisplay("Insert coin");
				currentStage = Stages.DemandCoin;
    		}else{
    			gui.setDisplay(strReactionTime);
    		}
    		break;
    	}
    }
    
	public void goStopPressed() {
		// State transformed if the button is pressed.
		switch(currentStage){
			case coinInserted:
				delay = rnGenerator.getRandom(100, 250); // millisecond
				delay = delay*10; // delay in milliseconds [1000, 2500),
				currentTime = 0;
				currentStage = Stages.DelayPeriod;
				gui.setDisplay("Wait...");
				break;
				
				// User pressed the button before the delay expire
			case DelayPeriod: 
				currentStage = Stages.DemandCoin;
				gui.setDisplay("Insert coin");
				break;
				
				// User reacted, show the result for 3 seconds
			case WaitingUser:
				currentStage = Stages.GameOver;
				strReactionTime = TimeToString(currentTime);
				currentTime = 0;
				break;
				
				// User pressed the button while displaying the reaction time value
			case GameOver:
				currentStage = Stages.DemandCoin;
				gui.setDisplay("Insert coin");
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
			System.out.println("possible Error result");
		}
		
		return timeString;
	}
}
