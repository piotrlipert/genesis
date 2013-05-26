package utils;

import com.jme3.math.Vector3f;

public class Facet
{
	public Vector3f v[];
	public Vector3f n;
	Facet(Vector3f[] vert, Vector3f norm)
	{
	v = vert;
	n = norm;
	}

	public void rotate(float angle, int axis)
	{
		
	}
	
	public void printFacet()
	{
		System.out.println("FACET");
	
		
	}
	
	
}
