package guiutils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import com.jme3.math.Vector3f;




class STLObject
{
	int numFaces = 0;
	ArrayList<Facet> f = new ArrayList<Facet>();
	
	
	public void addFacet(Facet c)
	{
		
	f.add(c);
	numFaces++;
	
	}
	
	public void removeFacet(Facet c)
	{
		
		f.remove(c);
		numFaces--;
		
	}
	
	
}

public class STLInterface 
{

	public ArrayList<Facet> facets;
	
	public STLInterface()
	{
		
		facets = new ArrayList<Facet>();
		
	}
	
	
	public void printFacets()
	{
		for (Facet f : facets)
			f.printFacet();
	}
	public int parseAscii(String filename)
	{
		int num = 0;

		
		boolean asciiSTL = false;				
		boolean normalParsed = false;

		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(filename));
			String line;
			Vector3f n = new Vector3f();
			Vector3f f[] = new Vector3f[3];
			f[0] = new Vector3f();
			f[1] = new Vector3f();
			f[2] = new Vector3f();
			
			
			while((line=r.readLine())!= null)
			{
				
				if (asciiSTL == false) 
				{
						if (line.indexOf("solid") != -1)
							asciiSTL = true;
						else
						{
							System.out.println("Not an ascii STL file!");
							r.close();
							return -1;
						}
				}
				
				if(line.indexOf("facet normal") != -1) 
				{
					if (normalParsed == false)
							{
							n = parseNormal(line);
							normalParsed = true;
							}
					else
					{
						System.out.println("More than one normal for facet!");
					}
				}	
				
				if (line.indexOf("vertex") != -1 && normalParsed)
				{
					if (num <= 3)
					{
						f[num] = parseVertex(line);
						num++;
					}
					else
					{
						System.out.println("More than 3 vertexes per face!");
						r.close();
						return -1;
					}
					
					
				}
				
				if (line.indexOf("endfacet") != -1 && normalParsed && num == 3)
				{
				
					
					Facet ff = new Facet(f,n);
					facets.add(ff);
					
					normalParsed = false;
					num = 0;
					n = new Vector3f();
					f = new Vector3f[3];
					f[0] = new Vector3f();
					f[1] = new Vector3f();
					f[2] = new Vector3f();
					
				}
				
			
			
			
			

			}
			r.close();
			
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return -1;

		}
		return 1;
		
	}
	
	
	private Vector3f parseVertex(String line) {
		
		
		Vector3f v = new Vector3f();
		String[] w = line.split(" ");
		w = trimSpaces(w);

		v.setX(parseNumber(w[1]));
		v.setY(parseNumber(w[2]));
		v.setZ(parseNumber(w[3]));
		
		
		return v;
	}


	private String[] trimSpaces(String[] w) 
	{
		int len = 0;
		for (String k : w){
			if (k.length() > 1)
				len++;}

		int i = 0;
		String[] ret = new String[len];
		for (String k : w)	
		{
			if (k.length() > 1)
			{
				ret[i] = k;
				i++;
			}
		}
			
			
			
		
		return ret;
	}


	private Vector3f parseNormal(String line) {
		Vector3f n = new Vector3f();
		String[] w = line.split(" ");
		w = trimSpaces(w);
		n.setX(parseNumber(w[2]));
		n.setY(parseNumber(w[3]));
		n.setZ(parseNumber(w[4]));
		return n;
	}


	private float parseNumber(String string) 
	{
		string = string.replace('e', 'E');
		boolean sciNotation = false;
		if(string.indexOf("E") != -1)
			sciNotation = true;
			
			
		
		
		if (sciNotation)
		{
			BigDecimal n = new BigDecimal(string);
			
			return n.floatValue();
			
			
		}
			
		else
			return Float.parseFloat(string);
	}


	public int parseBinary(String filename)
	{
		DataInputStream r = null;
	
		try {
		 r = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(filename)));
			
			
			int no_triangles = readBinaryHeader(r);
			for(int x=0;x<no_triangles;x++)
			{
				
				Facet t = readBinaryTriangle(r);
				facets.add(t);
			}	
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return -1;

		}
		return 1;
		
	}
	
	private int readBinaryHeader(DataInputStream r) {
		byte[] header = new byte[80];
		byte[] byte_triangle = new byte[4];
		int no_triangles = -1;
		try {
			r.read(header);
			r.read(byte_triangle);
			
			no_triangles = makeUInt32(byte_triangle);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return no_triangles;
	}


	private int makeUInt32(byte[] byte_triangle) 
	{
		double ret = 0;
		BitSet bs = BitSet.valueOf(byte_triangle);
		String a = "";
		for(int x = 0;x<bs.length();x++)
		{
			if (bs.get(x))
				a = a + "1";
			else
				a = a + "0";

			if (bs.get(x))
			{
				ret = ret + Math.pow((double) 2, (double) x);
			}
		}
		
		return (int) ret;
	}

	
	

	private Facet readBinaryTriangle(DataInputStream r) 
	{
		byte[] attr = new byte[2];
		Facet t = null;
		ByteBuffer dBuffer;
		Vector3f n = new Vector3f();
		Vector3f[] v = new Vector3f[3];
		byte[] facet_byte = new byte[50];
		try 
		{
			r.read(facet_byte);
			
			dBuffer = ByteBuffer.wrap(facet_byte);
			dBuffer.order(ByteOrder.nativeOrder());
			n = new Vector3f(dBuffer.getFloat(),dBuffer.getFloat(),dBuffer.getFloat());
			v[0] = new Vector3f(dBuffer.getFloat(),dBuffer.getFloat(),dBuffer.getFloat());
			v[1] = new Vector3f(dBuffer.getFloat(),dBuffer.getFloat(),dBuffer.getFloat());
			v[2] = new Vector3f(dBuffer.getFloat(),dBuffer.getFloat(),dBuffer.getFloat());
			
			
//		 n = new Vector3f(readLittleEndianFloat(r),readLittleEndianFloat(r),readLittleEndianFloat(r));
//		 v = new Vector3f[3];
//		v[0] = new Vector3f(readLittleEndianFloat(r),readLittleEndianFloat(r),readLittleEndianFloat(r));
//		v[1] = new Vector3f(readLittleEndianFloat(r),readLittleEndianFloat(r),readLittleEndianFloat(r));
//		v[2] = new Vector3f(readLittleEndianFloat(r),readLittleEndianFloat(r),readLittleEndianFloat(r));
//	

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	
		
		t = new Facet(v,n);
		
		return t;
	}


	private float readLittleEndianFloat(DataInputStream r) 
	{
		
		
		

		float ret = 0;
		try {
			ret = r.readFloat();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ret = EndianUtils.swap(ret);
	

		return ret;
	}


	public int loadSTLfile(String filename)
	{
		long startTime = System.currentTimeMillis();

		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(filename));
			String line = r.readLine();
			if(line.indexOf("solid") != -1)
			{  
				parseAscii(filename);

			
			}
			else
				parseBinary(filename);
			
			r.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
			return -1;

		}
		
		
		long endTime = System.currentTimeMillis();
	       System.out.println("Total STL loading time: " + (endTime-startTime) + "ms");
			
		
		
		
		return 1;
	}
	
	public void saveSTLfile(String filename)
	{
		
	}
	


}


