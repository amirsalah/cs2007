import java.lang.reflect.Constructor;

public class ReactionMachine
{
    public static void main(String[] args)
      throws Exception
    {
	if( args.length!=1 ){
	    System.err.println("Usage: ReactionMachine  CONTROLLERNAME");
	    System.exit(1);
	    return;
	}
	String controllerName= args[0];

	Class cl= Class.forName(controllerName,true,
          Thread.currentThread().getContextClassLoader());
	Constructor constructors[]= cl.getConstructors();
	if( constructors.length==0 ){
	    System.err.println("There is NO constructor in your controller class");
	    System.exit(1);
	    return;
	}

	if( constructors.length>1 ){
	    System.err.println( "There is more than one constructor in your class");
	    System.exit(1);
	}
	
	//Construct and initialise the components
	Controller controller=(Controller)constructors[0].newInstance(new Object[0]);
	Gui display= new Display();
	display.init();

	//Connect them to each other
        display.connect(controller);
	controller.connect(display,new Rng());

	controller.init();

	//And away we go...
	try{
	    for(;;){
		//Technically this is a BAD way to generate a 10ms
		//delay. because the time will gradually run slow
		//due to the execution time of the controller.tick()
		//method.  However, it is quite accurate enough for
		//our purposes.
		Thread.sleep(10); //milliseconds
		controller.tick();
	    }
	}catch(InterruptedException ie){
	    System.exit(1);
	}
    }


    ////////////////////////////////////
    //Methods for the "Random" interface
    ////////////////////////////////////
    private static class Rng
      implements Random
    {
	public int getRandom(int from, int to)
	{
	    return from + (int)((to-from)*Math.random());
	}
    }
}
