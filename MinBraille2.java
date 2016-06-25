import java.io.*;
import java.util.*;

public class MinBraille2
{
	public static void main(String[] args)
	{
		System.out.print("Reading ngrams.txt...");
		CSVReader reader = new CSVReader();
		reader.setTargetFile("ngrams.txt");
		reader.readFile();
		ArrayList<String[]> ngramdata_ar = reader.getFileData();
		System.out.println(" Complete");

		System.out.print("Reading common-words.csv...");
		reader.setTargetFile("common-words.csv");
		reader.readFile();
		ArrayList<String[]> wordlist_ar = reader.getFileData();
		System.out.println(" Complete");

		CodingSet codingSet = new CodingSet();
		WordSet wordSet = new WordSet();

		System.out.print("Building coding set...");
		for(int i=0; i<63 && i<ngramdata_ar.size(); i++)
			codingSet.add(new Glyph(ngramdata_ar.get(i)[0],"1"));
		System.out.println(" Complete");
		
		System.out.print("Building wordSet...");
		for(int i=0; i<200 && i<wordlist_ar.size(); i++)
			wordSet.add(new Word(wordlist_ar.get(i)[0],wordlist_ar.get(i)[1]));
		System.out.println(" Complete");
		
		System.out.print("Encoding...");
		wordSet.encodeAll(codingSet);
		System.out.println(" Complete");

		for(int i=0; i<wordSet.size(); i++)
			System.out.println(wordSet.get(i));
			
		System.out.println(wordSet.totalWeight());
		System.out.println(wordSet.areAllEncodedWordsValid());
		
		/*
		codingSet.add(new Glyph("A", "5"));
		codingSet.add(new Glyph("B", "5"));
		codingSet.add(new Glyph("AB", "7"));
		//codingSet.add(new Glyph("BA", "6"));
		codingSet.add(new Glyph("AA", "4"));
		codingSet.add(new Glyph("BBB", "2"));
		
		Word w1 = new Word("ABBABBAAABBABBA","1");
		Word w2 = new Word("CABBABBAAABBABBA","1");
		w1.encode(codingSet);
		w2.encode(codingSet);

		System.out.println(w1);
		System.out.println(w2);
		
		wordSet.add(w1);
		wordSet.add(w2);
		wordSet.encodeAll(codingSet);
		System.out.println(wordSet.totalWeight());
		*/
	}
}

class CodingSet
{
	private HashMap<String,Glyph> glyphs;
	
	public CodingSet()
	{
		glyphs=new HashMap<String,Glyph>(0);
	}
	
	public void add(Glyph c) { glyphs.put(c.getSequence(),c); }
	public void remove(Glyph c) { glyphs.remove(c.getSequence()); }
	public int size() { return glyphs.size(); }
	public Glyph findGlyph(Word s)
	{
		return glyphs.get(s.getSequence());
	}

	public CodedWord encode(Word w)
	{
		CodedWord bestSoFar = new CodedWord();
		for (int i = 1; i <= w.length(); i++)
		{
			Glyph g = findGlyph(w.substring(0, i));
			if (g != null)
			{
				CodedWord cw = new CodedWord(g);
				CodedWord rest = encode(w.substring(i));
				if (rest.isEmpty() == false || i==w.length())
				{
					cw.append(rest);
					if (cw.getWeight() < bestSoFar.getWeight() || bestSoFar.isEmpty())
						bestSoFar = cw;
				}
			}
		}
		return bestSoFar;
	}
}

class CodedWord
{
	private LinkedList<Glyph> glyphs;
	public CodedWord()
	{
		glyphs=new LinkedList<Glyph>();
		glyphs.clear();
	}
	public CodedWord(Glyph g)
	{
		this();
		glyphs.add(g);
	}
	public void append(CodedWord cw)
	{
		for (int i = 0; i < cw.size(); i++)
			glyphs.add(cw.get(i));
	}
	public int size() { return glyphs.size(); }
	public Glyph get(int i) { return glyphs.get(i); }

	public int getWeight()
	{
		int ret = 0;
		for (Glyph g: glyphs)
		    ret+=g.getWeight();
		return ret;
	}

	public boolean isEmpty()
	{
		return glyphs.isEmpty();
	}

	public String toString()
	{
		String ret = "";
		for(Glyph g: glyphs)
			ret += g+"+";
		
		if (ret.length()>0)
			ret = ret.substring(0,ret.length()-1);
		return ret;
	}
}

class Glyph
{
	private String sequence;
	private int weight;
	public Glyph(String l, String w)
	{
		setSequence(l);
		setWeight(Integer.parseInt(w));
	}
	public String getSequence() { return sequence; }
	public void setSequence(String s) { sequence=s; }
	public int getWeight() { return weight; }
	public void setWeight(int x) { weight=x; }

	public String toString()
	{
		return sequence;
	}
}

class WordSet
{
	private ArrayList<Word> words;
	
	public WordSet()
	{
		words = new ArrayList<Word>(0);
	}
	public void add(Word w) { words.add(w); }
	public void encodeAll(CodingSet c)
	{
		int size=words.size();
		int count=0;
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

class Word
{
	private String sequence;
	private CodedWord encodedWord;
	private double frequency;
	
	public Word(String x)
	{
		this(x,-1);
	}
	
	public Word(String x, String f)
	{
		this(x,Double.parseDouble(f));
	}

	public Word(String x, double f)
	{
		sequence = x;
		frequency = f;
		encodedWord=null;
	}
	public int length() { return sequence.length(); }
	public String getSequence() { return sequence; }
	public double getFrequency() { return frequency; }
	public double getEncodedWeight() { return encodedWord.getWeight(); }
	public Word substring(int i)
	{
		if (sequence.length() < i) return new Word("");
		return new Word(sequence.substring(i));
	}
	public Word substring(int i, int j) { return new Word(sequence.substring(i, j)); }

	public void encode(CodingSet cs)
	{
		encodedWord = cs.encode(this);
	}
	public boolean encodedValid()
	{
		return encodedWord!=null && encodedWord.size()>0;
	}

	public String toString()
	{
		return (sequence + " {" + frequency + "} = [" + encodedWord + "] ("+(encodedValid()?"valid":"INVALID")+") ("+encodedWord.getWeight()+")");
	}
}