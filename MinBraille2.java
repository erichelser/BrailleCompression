import java.util.*;
import java.text.*;
public class MinBraille2
{
	private static final int SOLUTION_SIZE=63;
	//private static Random random=new Random();

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
	/*
		//Simulated annealing
		double currentTemp=100;
		double minTemp=.000000001;
		double coolingFactor=0.99999;

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
		System.out.println(codingSet.getStatusReport());
	*/
	/*
		codingSet.resetGlyphFreq();
		codingSet.assignEqualWeights(0);

		for(int i=0; i<args.length; i+=2)
		{
			//System.out.println(codingSet.encode(new Word(args[i],0)).getWeight());
			codingSet.add(new Glyph(args[i],args[i+1]));
		}

		wordSet.encodeAll(codingSet);
		codingSet.sortGlyphsByFreq();
		System.out.println(codingSet.getStatusReport());
		System.out.println(wordSet.getStatus());
	*/
	}

	/*
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

	private static double testValue(WordSet wordSet, CodingSet codingSet1, String candidate)
	{
		codingSet1.resetGlyphFreq();
		codingSet1.assignEqualWeights(1);
		wordSet.encodeAll(codingSet1);
		double weight1=wordSet.totalWeight();

		CodingSet codingSet2=new CodingSet();
		codingSet2.cloneFrom(codingSet1);
		codingSet2.add(new Glyph(candidate,0));

		codingSet2.resetGlyphFreq();
		wordSet.encodeAll(codingSet2);
		
		codingSet2.sortGlyphsByFreq();
		codingSet2.truncateGlyphListSize(63);
		codingSet2.resetGlyphFreq();
		wordSet.encodeAll(codingSet2);
		
		double weight2=wordSet.totalWeight();
		return weight1-weight2;
		//returns a positive value incidating weight savings by including candidate
		//glyph in coding set. Does not alter original coding set other than modifying
		//weight/frequency data
	}
	*/
	public static String formatNumber(double x)
	{
		return NumberFormat.getInstance().format(x);
	}
}

class CodingSet
{
	private HashMap<String,Glyph> glyphs;
	private ArrayList<String> glyphList;
	
	public CodingSet()
	{
		glyphs=new HashMap<String,Glyph>(0);
		glyphList=new ArrayList<String>(0);
	}
	
	public void add(Glyph c)
	{
		if(!glyphExists(c))
		{
			glyphs.put(c.getSequence(),c);
			glyphList.add(c.getSequence());
		}
	}
	public void remove(Glyph c)
	{
		glyphs.remove(c.getSequence());
		glyphList.remove(c.getSequence());
	}
	public String truncateGlyphListSize(int x)
	{
		String ret="";
		if(this.size()>x)
		{
			Glyph g=findRightmostContraction();
			remove(g);
			ret=g.getSequence();
		}
		return ret;
	}
	private Glyph findRightmostContraction()
	{
		//Find Glyph with the highest index in glyphList array
		//that consists of more than one character
		int i=0;
		for(i=glyphList.size()-1; i>=0 && glyphList.get(i).length()==1; i--);
		return findGlyph(glyphList.get(i));
	}
	public String get(int i) { return glyphList.get(i); }
	public int size() { return glyphs.size(); }
	public HashMap<String,Glyph> getHashMap() {return glyphs;}
	public ArrayList<String> getArrayList() {return glyphList;}
	
	public boolean glyphExists(Glyph s)
	{
		return glyphExists(s.getSequence());
	}
	public boolean glyphExists(String s)
	{
		return glyphs.get(s)!=null;
	}

	public Glyph findGlyph(Word s)
	{
		return findGlyph(s.getSequence());
	}
	public Glyph findGlyph(String s)
	{
		return glyphs.get(s);
	}
	
	public void cloneFrom(CodingSet B)
	{
		glyphs.clear();
		glyphList.clear();
		for(String s: B.getArrayList())
			add(new Glyph(s,B.findGlyph(s).getWeight()));
	}
	
	public void swapGlyph(int index, Glyph addMe)
	{
		glyphs.remove(glyphList.remove(index));
		this.add(addMe);
	}

	public void sortGlyphsByFreq()
	{
		for(int i=0; i<glyphList.size()-1; i++)
		{
			int target=i;
			for(int j=i+1; j<glyphList.size(); j++)
				if(glyphs.get(glyphList.get(j)).getFreq() > glyphs.get(glyphList.get(target)).getFreq())
					target=j;
			if(i!=target)
				swapGlyphsByIndex(target,i);
		}
	}

	public void swapGlyphsByIndex(int A, int B)
	{
		String sA=glyphList.get(A);
		String sB=glyphList.get(B);
		glyphList.set(A,sB);
		glyphList.set(B,sA);
	}

	public void assignEqualWeights(int w)
	{
		for(String s:glyphList)
			glyphs.get(s).setWeight(w);
	}
	public void assignStandardWeights()
	{
		int count=1;
		for(String s:glyphList)
			glyphs.get(s).setWeight(calcWeightByIndex(count++));
	}
	public void resetGlyphFreq()
	{
		for(String s:glyphList)
			glyphs.get(s).resetFreq();
	}

	private int calcWeightByIndex(int x)
	// returns the numbers 0-6 based on Pascal's triangle:
	// 1 - 6 - 15 - 20 - 15 - 6 - 1
	// this corresponds to the number of dots on each configuration of glyph
	{
		if(x<1) return 0;
		else if(x<7) return 1;
		else if(x<22) return 2;
		else if(x<42) return 3;
		else if(x<57) return 4;
		else if(x<63) return 5;
		else if(x<64) return 6;
		return -1;
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
	public String getStatusReport()
	{
		String ret="";
		for(String s:glyphList)
			ret+=s+"\t"+
			     glyphs.get(s).getWeight()+"\t"+
			     this.prettyPrint(glyphs.get(s).getFreq())+"\n";
		return ret.trim();
	}
	private String prettyPrint(double n)
	{
		String ret="                                       ";
		ret+=(NumberFormat.getInstance().format(n));
		return ret.substring(ret.length()-20);
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

	public void incrGlyphsBy(double x)
	{
		for (Glyph g: glyphs)
			g.incrFreqBy(x);
	}

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
	private double freq;

	public Glyph(String l)
	{
		this(l,0);
	}
	public Glyph(String l, String w)
	{
		this(l,Integer.parseInt(w));
	}
	public Glyph(String l, int w)
	{
		resetFreq();
		setSequence(l);
		setWeight(w);
	}

	public String getSequence() { return sequence; }
	public void setSequence(String s) { sequence=s; }

	public int getWeight() { return weight; }
	public void setWeight(int x) { weight=x; }

	public void resetFreq() { freq=0; }
	public void incrFreqBy(double amt) { freq+=amt; }
	public double getFreq() { return freq; }

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
		encodedWord.incrGlyphsBy(frequency);
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