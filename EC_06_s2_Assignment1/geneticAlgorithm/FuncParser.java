/* Author: Bo CHEN 		Student ID: 1139520
 * 
 */
package geneticAlgorithm;

import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

public class FuncParser {

	/*
	 * Transfer the function from the form of String to the Stack with tokens of String and Double
	 */
	public static Stack evalFuncToStack(String function, ArrayList<String> funcVar, Stack<String> dataIndicator) {
		Stack resultStack = new Stack();
		Stack tempStack = new Stack();
		StringTokenizer anaFunc = new StringTokenizer(function, " ");
		Stack<String> tempIndicator = new Stack<String>();		
		int stackSize;
		
		while( anaFunc.hasMoreElements() ){
			String temp = anaFunc.nextToken();
						
			if (temp.equals("+") || temp.equals("-") || temp.equals("*") || temp.equals("/") || temp.equals("^") || temp.equals("(") || temp.equals(")") ){
				tempStack.push(temp);
				if (temp.equals("(") || temp.equals(")"))
					tempIndicator.push("Par"); // indicating the element is a parenthese
				else
					tempIndicator.push("Opr");
			}else if (funcVar.indexOf(temp) >= 0){
					tempStack.push(temp);
					tempIndicator.push("Var"); // indicating the element is a variable
					continue;
				}
			else{
				try{
					// If the number cannot be recognized as a double, exception will be dealt with
					tempStack.push(Double.valueOf(temp)); 
				}
				catch (NumberFormatException e){
					System.out.println("Incorrect variables. Please check the function!");
				}
				tempIndicator.push("Dig"); // indicating the element is a digit
			}
		}
		
		// reverse the order of tokens in the temp stack
		stackSize = tempIndicator.size();
		for(int i = 0; i < stackSize; i++ ){
			resultStack.push(tempStack.pop());
			dataIndicator.push(tempIndicator.pop());
//			System.out.println(dataIndicator.peek());
		}
		return resultStack;	
	}
	
	
	/* Transfer a stack containing infix notion to a stack of RPN notion */
	public static Stack infixToPostfix(Stack infixStack, Stack<String> dataIndicator){
		Stack reOutPut = new Stack();
		Stack outPut = new Stack();
		Stack<String> tempStack = new Stack<String>();
		Stack<String> tempIndicator = new Stack<String>();	
		String dig = "Dig" , var = "Var", opr = "Opr", par = "Par", dataType;

		while ( !dataIndicator.empty() ){
			dataType = dataIndicator.pop();
			
			// token is a number, push into the reOutPut stack
			if( dataType.equals(dig) ){
				reOutPut.push(infixStack.pop());
				tempIndicator.push("Dig");
				continue;
			} 

			if( dataType.equals(var)){
				reOutPut.push(infixStack.pop());
				tempIndicator.push("Var");
				continue;
			}
			
			// token is a operator && tempStack is empty
			if ( dataType.equals(opr) && tempStack.empty() ){
				tempStack.push((String) infixStack.pop());
				continue;
			}
			
			//token is a operator && top of tempStack have a operator
			if ( dataType.equals(opr) && !tempStack.empty() && !tempStack.peek().equals("(") ){
				while ( !tempStack.empty() && rank( infixStack.peek().toString().charAt(0)) <= rank(tempStack.peek().charAt(0)) ){
					reOutPut.push(tempStack.pop());	
					tempIndicator.push("Opr");
				}	
				tempStack.push((String)infixStack.pop());
				continue;
			}
			
			if ( dataType.equals(opr) && tempStack.peek().equals("(") ){
				tempStack.push((String)infixStack.pop());
				continue;
			}
			
			// Deal with the case of containing parentheses
			if ( infixStack.peek().toString().equals("(") ){
				tempStack.push((String)infixStack.pop());
				continue;
			}
			
			if ( infixStack.peek().toString().equals(")") ){
				while ( !tempStack.peek().equals("(") ){
					reOutPut.push(tempStack.pop());
					tempIndicator.push("Opr");
				}
				
				if( tempStack.empty()){
					System.out.println("Error while parsing function, please check the func!");
					System.exit(0);
				} else if (tempStack.peek().equals("(")){
					tempStack.pop();
					infixStack.pop();
				}
				continue;
			}
			
		} 
		
		while ( !tempStack.empty() ){
			reOutPut.push(tempStack.pop());
			tempIndicator.push("Opr");
		}
		
		// Reverse the order of elements in the reOutPut stack and indicator stack.
		int size_reOutPut = reOutPut.size();
		for( int i = 0; i < size_reOutPut; i++){
			outPut.push(reOutPut.pop());
			dataIndicator.push(tempIndicator.pop());
//			System.out.println(dataIndicator.peek());
//			System.out.println(outPut.peek());
		}
		
		return outPut;
		
	}

	/* Rank the precedence of operators. 
	 * Higer number indicates higher precedence.
	 */
	private static int rank(char operator)
	{
	int rank = 0;
	switch (operator)
	{
	case '+':case'-': rank = 1; 
					  break;
	case '*':case'/': rank = 2; 
					  break;
	case '^': 		  rank = 3;
					  break;
	}
	return rank;
	}
	
}
