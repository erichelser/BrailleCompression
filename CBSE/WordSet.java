package CBSE;
import java.util.*;
import java.text.*;

public class WordSet
{
	private ArrayList<Word> words;
	
	public WordSet()
	{
		words = new ArrayList<Word>(0);
	}
	public void add(Word w) { words.add(w); }
	public void encodeAll(CodingSet c)
	{
		//int size=words.size();
		//int count=0;
		for(Word w:words)
		{
		    w.encode(c);
		}
	}
	public double totalWeight()
	{
		double total=0;
		for(Word w:words)
		    total+=w.getFrequency()*w.getEncodedWeight();
		return total;
	}
	public String getStatus()
	{
		return "TOTAL: "+(NumberFormat.getInstance().format(this.totalWeight())+" / "
		+(this.areAllEncodedWordsValid()?"valid":"invalid"));
	}
	public boolean areAllEncodedWordsValid()
	{
		boolean ret=true;
		for(Word w:words)
		{
			ret=ret&&w.encodedValid();
			if(!ret) break;
		}
		return ret;
	}
	public int size() { return words.size(); }
	public Word get(int i)
	{
		return words.get(i);
	}
}