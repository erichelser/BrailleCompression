package CBSE;
import java.util.*;

public class CodedWord
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