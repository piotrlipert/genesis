package genesis;

import guiutils.DisplayUtils;
import guiutils.STLInterface;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.system.AppSettings;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
 
/** Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys. */
public class GenesisApp extends SimpleApplication {
	public  STLInterface s;
	public  DisplayUtils d;
	boolean isLoaded;
	boolean isRotating;
	boolean isTranslating;
	public boolean loading = false;
	
	GenesisApp()
	{
        this.initSettings();

	}
	
	

	private  void initSettings() 
	{
		s = new STLInterface();
		d = new DisplayUtils(this);
		
		AppSettings settings = new AppSettings(true);
        settings.setResolution(640,480);
        setSettings(settings);
        setShowSettings(false);
        
    	
	}
 
    private void initGlow() 
    {
    	FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
    	BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
    	fpp.addFilter(bloom);
    	viewPort.addProcessor(fpp);		
	}

	private void registerLocators() 
	{
		assetManager.registerLocator("assets/Textures/", FileLocator.class);
	}

	@Override
    public void simpleInitApp() {
        
		
		registerLocators();
        initGlow();

    	flyCam.setDragToRotate(true);
    	flyCam.setMoveSpeed(20f);
    	d.initialize();
    	d.loadFile("resources/3DNAME.stl");
         

    }



	@Override
	public void update() 
	{
		if (!loading )
		super.update();
	}

	

	
}