/* For all non linear test cases */
import java.io.IOException;
//import java.util.Random;
//import java.util.Stack;

abstract class NLTestCase extends TestCase{
	int NLConstraints;
	int NLEConstraints;
	int bestIteration;
	
	public NLTestCase() throws IOException{
		super();      //Construct the super class
		NLP = true;
	}
	
	/* Test the Non-linear constraints that is violated by the input chromosome */
	public abstract double[] ViolationCalculate(double[] chromosome);
	
	/* Enable a non-linear constraint */
	public abstract void ActivateConstraint(int constraintNum);
	
	/* Disable a non-linear constraint */
	public abstract void DisableConstraint(int constraintNum);
	
	/* The evaluated value for the function only, exclude the penalty part */
	public abstract double EvalFunc(double[] chromosome);
}
