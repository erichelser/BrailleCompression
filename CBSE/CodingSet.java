package CBSE;
import java.util.*;
import java.text.*;

public class CodingSet
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
