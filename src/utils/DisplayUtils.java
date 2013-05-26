package utils;

import genesis.Genesis;

import java.awt.Color;
import java.util.ArrayList;

import jme3tools.optimize.GeometryBatchFactory;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Triangle;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;

public class DisplayUtils {
	
public Mesh createGeometry(String string, STLInterface s) 
{		
		ArrayList<Triangle> tr = new ArrayList<Triangle>();
    	Vector3f[] normals = new Vector3f[3];
    	Vector3f[] verti = new Vector3f[3];
    	
    	
    	Vector2f[] texCoord = new Vector2f[3];
    	int[] index = new int[3];
        Mesh outMesh = new Mesh(); 

    	ArrayList<Geometry> geoms = new ArrayList<Geometry>();
    	long startTime = System.currentTimeMillis();
		s.loadSTLfile("resources/dragon.stl");

		Vector3f[] normals_batchfree = new Vector3f[s.facets.size()*3];
		Vector3f[] vertex_batchfree = new Vector3f[s.facets.size()*3];
		Vector3f[] texture_batchfree = new Vector3f[s.facets.size()*3];
		
		
		
    	int i = 0;
    	int k = 0;
    	int z = 0;
    	for(Facet x : s.facets)
    	{
    		Triangle t = new Triangle(x.v[0],x.v[1],x.v[2]);
    		t.calculateNormal();
    		
    		Vector3f area = new Vector3f();
    		area = (x.v[2].subtract(x.v[0])).cross(x.v[2].subtract(x.v[1]));
    		float ar = area.lengthSquared();
    		
    		
    		
    		
    		if (x.n.getX() == 0 && x.n.getY() == 0 && x.n.getZ() == 0)
    		{
    				
    			normals[0] =  t.getNormal();
    			normals[1] =  t.getNormal();
    			normals[2] =  t.getNormal();
    		}
    		else{
    				normals[0] = x.n;
    	    		normals[1] =  x.n;
    	    		normals[2] =  x.n;
    			}
    		
    		tr.add(t);
    		
    		index[0] =  0;
    		index[1] =  1;
    		index[2] =  2;
    		
    		
    		verti[0] = x.v[0];
    		verti[1] = x.v[1];
    		verti[2] = x.v[2];
    		
    		texCoord[0] = new Vector2f(0,1);
    		texCoord[1] = new Vector2f(1,0);
    		texCoord[2] = new Vector2f(1,1);
    		

        	Mesh mymesh = new Mesh();
    		mymesh.setBuffer(Type.Position, 3,BufferUtils.createFloatBuffer(verti));
        	mymesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));

    		mymesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
        	mymesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(index));
        	mymesh.updateBound();
        	mymesh.updateCounts();

        	Geometry geom = new Geometry("Triangle", mymesh);// create cube geometry from the shape

            geoms.add(geom);
            k = k + 1;
            
    	}

		long endTime = System.currentTimeMillis();
       System.out.println("Total geometry execution time: " + (endTime-startTime) + "ms");
    	
    	//mymesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));

       startTime = System.currentTimeMillis();
       
		GeometryBatchFactory.mergeGeometries(geoms, outMesh);
    	
        outMesh.updateBound();	
        outMesh.scaleTextureCoordinates(new Vector2f(2f, 2f));
		 endTime = System.currentTimeMillis();
	       System.out.println("Total batching execution time: " + (endTime-startTime) + "ms");
	    	
        return outMesh;
	}



public void initLight(Genesis app) {
	PointLight lamp_light = new PointLight();
    lamp_light.setColor(ColorRGBA.White);
    lamp_light.setRadius(40000000f);
    lamp_light.setPosition(new Vector3f(-10f,0f,-40f));
    app.getRootNode().addLight(lamp_light);
    
    
	 lamp_light = new PointLight();
    lamp_light.setColor(ColorRGBA.White);
    lamp_light.setRadius(40000000f);
    lamp_light.setPosition(new Vector3f(10f,0f,-40f));
    app.getRootNode().addLight(lamp_light);
    
	 lamp_light = new PointLight();
	    lamp_light.setColor(ColorRGBA.White);
	    lamp_light.setRadius(40000000f);
	    lamp_light.setPosition(new Vector3f(0f,-10f,-40f));
	    app.getRootNode().addLight(lamp_light);
	    

	    
		 lamp_light = new PointLight();
		    lamp_light.setColor(ColorRGBA.White);
		    lamp_light.setRadius(40000000f);
		    lamp_light.setPosition(new Vector3f(0f,10f,-40f));
		    app.getRootNode().addLight(lamp_light);
		    

    
//    DirectionalLight sun = new DirectionalLight();
//    sun.setDirection(new Vector3f(0,0.1f,1f).normalizeLocal());
//    sun.setColor(ColorRGBA.White);
//    app.getRootNode().addLight(sun);
//    
    
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(1.5f));
    app.getRootNode().addLight(al);
}

public Material initSTLMaterial(Genesis app) 
{
    Material mat_lit = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
    
    mat_lit.setTexture("DiffuseMap", app.getAssetManager().loadTexture("plastic.jpg"));
    mat_lit.setTexture("NormalMap", app.getAssetManager().loadTexture("plastic.jpg"));
    mat_lit.setTexture("SpecularMap", app.getAssetManager().loadTexture("plastic.jpg"));
    
    
    //mat_lit.setBoolean("UseMaterialColors",true);    
    
    //mat_lit.setColor("Ambient",ColorRGBA.White);
    
    //mat_lit.setColor("Specular",ColorRGBA.White);
    //mat_lit.setColor("Diffuse",ColorRGBA.White);
    mat_lit.setFloat("Shininess", 12f); // [1,128]    
		return mat_lit;
}



public void initTable(Genesis app) {

		  Geometry g = new Geometry("wireframe grid", new Grid(10000, 10000, 10f) );
		  Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		  mat.getAdditionalRenderState().setWireframe(true);
		  mat.setColor("Color",ColorRGBA.White);
		  g.setMaterial(mat);
		  g.center().move(0,0,0);
		  
		  app.getRootNode().attachChild(g);
		

	
	
}
}
