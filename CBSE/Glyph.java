package CBSE;

public class Glyph
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
