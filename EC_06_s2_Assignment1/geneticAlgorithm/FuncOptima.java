/* Author: Bo CHEN 		Student ID: 1139520
 * Evolutionary computation, Assignment 1
 */
package geneticAlgorithm;

import java.io.*;
import java.util.*;
import java.io.FileNotFoundException;

public class FuncOptima {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String evalFunc;
		String tempMaxMin = "Max";
		boolean evalMax = true;
		int numVar = 0;
		ArrayList<Double> varLowBound = new ArrayList<Double>();
		ArrayList<Double> varUpBound = new ArrayList<Double>();
		ArrayList<String> funcVar = new ArrayList<String>();
		int pop_size = 20;
		int num_generations = 2000;
		double prob_crossover = 0.30;
		double prob_mutation = 0.01;
		int dec_precision = 1;
		
		Stack oriFunc = new Stack();
		Stack rpnFunc = new Stack();
		BinaryOptima instanceOfBi;


/*
 * Analysis of the config file
 */
		if(args.length != 2){
			System.out.println("Incorrect number of the arguments!");
			System.out.println("Please input binary configbin.txt or real configreal.txt");
			System.exit(0);
		}		

			FileInputStream fin = new FileInputStream(args[1]);
			DataInputStream din	= new DataInputStream(fin);
			BufferedReader bufRead = new BufferedReader( new InputStreamReader(din));
			
			/* Obtaining function to be optimized -> evalFunc */
			searchCommand(bufRead);		
			evalFunc = bufRead.readLine();
//			System.out.print("#Function: " + evalFunc);
			
			/* Determing max or min */
			searchCommand(bufRead);
			tempMaxMin = bufRead.readLine();
			if( tempMaxMin.contains("Max") ){
				evalMax = true;
				System.out.println("#Maximize Function:  " + evalFunc);
			}
			else if ( tempMaxMin.contains("Min")){
				evalMax = false;
				System.out.println("#Minimize Function:  " + evalFunc);
			}
			else
				System.out.println("!Incorrect parameter. Please input Max or Min");
			
			/* Get the number of variables */
			searchCommand(bufRead);
			numVar = Integer.parseInt(bufRead.readLine());
//			System.out.println("Number of variables: " + numVar);

			/* Get the bound of variables and the name of variables */
			searchCommand(bufRead);
			for (int i = 0; i < numVar; i++){
				funcVar.add(i, bufRead.readLine());
				if (funcVar.get(i).length() <= 5){
					i--;
					funcVar.remove(i);
				}				
				else{
					String lowerBound, upperBound;
					lowerBound = funcVar.get(i).substring(funcVar.get(i).indexOf('[') + 1 , funcVar.get(i).indexOf(',') );
					upperBound = funcVar.get(i).substring(funcVar.get(i).indexOf(',') + 1 , funcVar.get(i).indexOf(']') );
//					System.out.println(lowerBound + " " + upperBound);
					varLowBound.add(i, Double.parseDouble(lowerBound)); 
					varUpBound.add(i, Double.parseDouble(upperBound));
//					System.out.println(varLowBound.get(i));
//					System.out.println(varUpBound.get(i));
					
					funcVar.set(i, funcVar.get(i).substring(0, funcVar.get(i).indexOf('=')));
				}
				
//				System.out.println(funcVar.get(i));
			}
			
			/* Set the pop_size, number of generations,
			 *  probability of crossover and mutation, decimal precision */
			searchCommand(bufRead);
			pop_size = Integer.parseInt(bufRead.readLine());
			
			searchCommand(bufRead);
			num_generations = Integer.parseInt(bufRead.readLine());
			
			searchCommand(bufRead);
			prob_crossover = Double.parseDouble(bufRead.readLine());
			
			searchCommand(bufRead);
			prob_mutation = Double.parseDouble(bufRead.readLine());
			
			if ( args[0].equals("binary") ){
				searchCommand(bufRead);
				dec_precision = Integer.parseInt(bufRead.readLine());				
			}


			
			/* Transfer the string evaluation function into Stack.
			 * N.B. variables originally was char become Object 
			 */
			oriFunc = FuncParser.evalFuncToStack(evalFunc, funcVar, dataIndicator);
			rpnFunc = FuncParser.infixToPostfix(oriFunc, dataIndicator);
			if ( args[0].equals("binary")){
				instanceOfBi = new BinaryOptima(rpnFunc, dataIndicator, varLowBound, varUpBound, funcVar, numVar, pop_size, num_generations, prob_crossover, prob_mutation, dec_precision, evalMax );
				System.out.println("Optima: " + instanceOfBi.getOptima());
				System.out.println("Where: " + "at the generation " + instanceOfBi.getBestGeneration());
				System.out.println("Who: " + instanceOfBi.getBestChromosome() );
			}else if ( args[0].equals("real")){
				DecimalOptima instanceOfReal = new DecimalOptima(rpnFunc, dataIndicator, varLowBound, varUpBound, funcVar, numVar, pop_size, num_generations, prob_crossover, prob_mutation, evalMax );
				System.out.println("Optima: " + instanceOfReal.getOptima());
				System.out.println("Where: " + "at the generation " + instanceOfReal.getBestGeneration());
				System.out.print("Who: ");
				for ( int i = 0; i < numVar; i++){
					System.out.print(funcVar.get(i) + " = " + instanceOfReal.getBestChromosome()[i] + "  ");
				}
		
			}
			
			
	}
	
	/*
	 * Locating the next valid command line.
	 */
	private static void searchCommand(BufferedReader bufRead) throws IOException{
		while(true){
			String temp = bufRead.readLine();
			if (temp.length() == 0)
				continue;
			else if (temp.charAt(0) == '#')
				break;
		}
		return ;
	}
	
	private static Stack<String> dataIndicator = new Stack<String>();

}


