//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.image.*;
import java.io.*;
//import javax.imageio.*;
import java.util.*;
//import java.lang.*;

class CSVReader
{
	protected String targetFile;
	protected ArrayList<String[]> fileData;
	protected int status;
	public CSVReader()
	{
		targetFile="";
		fileData=new ArrayList<String[]>(0);
		status=0;
	}
	public void setTargetFile(String f) { targetFile=f; }
	public String getTargetFile() { return targetFile; }
	public String getTargetFileBasename()
	{
		String[] x=targetFile.split("/");
		return x[x.length-1];
	}
	public void readFile()
	{
		try
		{
			BufferedReader reader=java.security.AccessController.doPrivileged(
				new java.security.PrivilegedAction<BufferedReader>() {
					public BufferedReader run() {
						try{
						return new BufferedReader(new FileReader(targetFile));
						}
						catch(FileNotFoundException e){
						System.out.println("FNFE");
						}
						return null;
					}
				}
				);
			fileData=new ArrayList<String[]>(0);
			String line="";
			//StringBuilder sb=new StringBuilder("");
			while((line=reader.readLine())!=null)
				fileData.add(line.split(","));
			reader.close();
			status=1;
		}
		catch(IOException e)
		{
			status=2;
		}
	}
	public void trimHeader()
	{
		fileData.remove(0);
	}
	public ArrayList<String[]> getFileData()
	{
		return fileData;
	}
	public int getStatus()
	{
		return status;
	}
}