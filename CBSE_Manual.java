import CBSE.*;

import java.util.*;
import java.text.*;

//Calculates compression based on
//user-provided glyphs

//Input is specified as pairs of cmdline arguments:
//Argument 1: Character sequence to recognize as a glyph (all caps, no quotes)
//Argument 2: Weight of glyph

public class CBSE_Manual
{
	public static void main(String[] args)
	{
		CSVReader reader = new CSVReader();

		reader.setTargetFile("common-words.csv");
		reader.readFile();
		ArrayList<String[]> wordlist_ar = reader.getFileData();

		WordSet wordSet = new WordSet();
		for(int i=0; i<1000 && i<wordlist_ar.size(); i++)
			wordSet.add(new Word(wordlist_ar.get(i)[0],wordlist_ar.get(i)[1]));

		CodingSet codingSet = new CodingSet();
		for(char i='A'; i<='Z'; i++)
			codingSet.add(new Glyph(""+i,1));

		codingSet.resetGlyphFreq();

		for(int i=0; i<args.length; i+=2)
		{
			//System.out.println(codingSet.encode(new Word(args[i],0)).getWeight());
			codingSet.add(new Glyph(args[i],args[i+1]));
		}

		wordSet.encodeAll(codingSet);
		codingSet.sortGlyphsByFreq();
		System.out.println(codingSet.getStatusReport());
		System.out.println(wordSet.getStatus());
	}

	public static String formatNumber(double x)
	{
		return NumberFormat.getInstance().format(x);
	}
}

