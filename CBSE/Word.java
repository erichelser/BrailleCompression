package CBSE;

public class Word
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