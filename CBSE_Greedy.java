import CBSE.*;

import java.util.*;
import java.text.*;

//Attempts to optimize substrings chosen by
//choosing the ones that offer the greatest
//"savings" on each pass.

public class CBSE_Greedy
{
	private static final int SOLUTION_SIZE=28;

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
		for(int i=0; i<ngramdata_ar.size() && codingSet.size()<SOLUTION_SIZE; i++)
			codingSet.add(new Glyph("ZZZZ"+i,1));
		
		CodingSet testSet=new CodingSet();

		wordSet.encodeAll(codingSet);
		double currentScore=wordSet.totalWeight();
		double testScore;

		for(int glyphIndex=0; glyphIndex<ngramdata_ar.size(); glyphIndex++)
		{
			if(glyphIndex%100==0)
				System.out.println("Testing index "+glyphIndex+"/"+ngramdata_ar.size()+"...");
			int target=-1;
			String subject=ngramdata_ar.get(glyphIndex)[0];
			if(subject.length()>1)
			{
				for(int i=0; i<codingSet.size(); i++)
				{
					if(codingSet.get(i).length()>1)
					{
						testSet.cloneFrom(codingSet);
						testSet.swapGlyph(i,new Glyph(subject,1));
						wordSet.encodeAll(testSet);
						testScore=wordSet.totalWeight();
						if(testScore<currentScore)
						{
							currentScore=testScore;
							target=i;
						}
					}
				}
				if(target!=-1)
				{
					codingSet.swapGlyph(target,new Glyph(ngramdata_ar.get(glyphIndex)[0],1));
					//System.out.println(codingSet.getStatusReport());
					//System.out.println("currentScore: "+formatNumber(currentScore));
				}
			}
		}
		wordSet.encodeAll(codingSet);		
		//codingSet.sortGlyphsByFreq();

		System.out.println("Final results:");
		System.out.println(codingSet.getStatusReport());
		System.out.println("currentScore: "+formatNumber(currentScore));
	}

	public static String formatNumber(double x)
	{
		return NumberFormat.getInstance().format(x);
	}
}

