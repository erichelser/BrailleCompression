import CBSE.*;

import java.util.*;
import java.text.*;

//Attempts to optimize substrings chosen by
//populating the solution with arbitrary
//data and swapping out potentially better
//combinations through simulated annealing


public class CBSE_SimAnneal
{
	private static final int SOLUTION_SIZE=30;
	private static Random random=new Random();

	public static void main(String[] args)
	{
		CSVReader reader = new CSVReader();

		reader.setTargetFile("ngrams-short.txt");
		reader.readFile();		
		ArrayList<String[]> ngramdata_ar = reader.getFileData();

		reader.setTargetFile("common-words.csv");
		reader.readFile();
		ArrayList<String[]> wordlist_ar = reader.getFileData();

		WordSet wordSet = new WordSet();
		for(int i=0; i<1000 && i<wordlist_ar.size(); i++)
			wordSet.add(new Word(wordlist_ar.get(i)[0],wordlist_ar.get(i)[1]));

		CodingSet codingSet = new CodingSet();
		for(char i='A'; i<='Z'; i++)
			codingSet.add(new Glyph(""+i,1));

		//Initialize codingSet with first rows in n-gram data set
		for(int i=0; i<ngramdata_ar.size() && codingSet.size()<SOLUTION_SIZE; i++)
			codingSet.add(new Glyph(ngramdata_ar.get(i)[0],1));
		
		double currentTemp=100;
		double minTemp=.000000001;
		double coolingFactor=0.99;

		wordSet.encodeAll(codingSet);
		double currentScore=wordSet.totalWeight();

		double testScore;
		int swapTargetA, swapTargetB;

		CodingSet testSet=new CodingSet();
		testSet.cloneFrom(codingSet);

		while(currentTemp>minTemp)
		{
			//Select a random glyph in the current set
			//that's not a single character
			do{
				swapTargetA=random.nextInt(codingSet.size());
			}while(codingSet.get(swapTargetA).length()==1);

			//Pick a random glyph outside of the current set
			do{
				swapTargetB=random.nextInt(ngramdata_ar.size());
			}while(codingSet.glyphExists(ngramdata_ar.get(swapTargetB)[0]));
			
			//Swap them and calculate the difference in totalWeight()
			testSet.swapGlyph(swapTargetA,
					new Glyph(ngramdata_ar.get(swapTargetB)[0],1));

			wordSet.encodeAll(testSet);
			testScore=wordSet.totalWeight();

			if(accepted(currentScore,testScore,currentTemp))
			{
				codingSet.cloneFrom(testSet);
				currentScore=testScore;
				System.out.println("currentScore: "+formatNumber(currentScore));
			}
			else
			{
				testSet.cloneFrom(codingSet);
				currentTemp*=coolingFactor;
			}
		}
		wordSet.encodeAll(codingSet);
		System.out.println("Final results:");
		System.out.println(codingSet.getStatusReport());
		System.out.println("currentScore: "+formatNumber(currentScore));
	}

	//Acceptance function for Simulated Annealing
	private static boolean accepted(double currentScore, double testScore, double currentTemp)
	{
		//The +1 ensures that diff>0 and P<1. Otherwise diff is frequently 0.
		double diff=(testScore-currentScore)+1;

		//Always accept a better state.		
		if(diff<0)
			return true;

		//Probability of acceptance of a worse state.
		//as currentTemp decreases, -diff/currentTemp will be distributed over a larger
		//range of X-values, resulting in smaller values for P.
		//Checking that P exceeds a random value between (0-1) ensures that as the
		//currentTemp decreases, fewer worse states will be accepted.
		double P=Math.exp(-diff/currentTemp);
		return (random.nextDouble()<P);
	}

	public static String formatNumber(double x)
	{
		return NumberFormat.getInstance().format(x);
	}
}

