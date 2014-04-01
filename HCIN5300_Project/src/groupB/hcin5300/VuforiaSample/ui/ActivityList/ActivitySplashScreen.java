/*==============================================================================
 Copyright (c) 2012-2013 Qualcomm Connected Experiences, Inc.
 All Rights Reserved.
 ==============================================================================*/

package groupB.hcin5300.VuforiaSample.ui.ActivityList;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import groupB.hcin5300.SampleApplication.utils.ImportedMesh;
import groupB.hcin5300.SampleApplication.utils.MeshObject;
import groupB.hcin5300.SampleApplication.utils.Texture;
import groupB.hcin5300.VuforiaSample.R;


public class ActivitySplashScreen extends Activity
{
    
    private static long SPLASH_MILLIS = 450;
    
    // The textures we will use for rendering:
    public static Vector<Texture> mTextures;
    
 // Ag objects
    //MeshObject AgLvl11;
    public static MeshObject AgLvl12;
    public static MeshObject AgLvl21;
    public static MeshObject Ag3_orbits;
    public static MeshObject Ag3_electrons;
    public static MeshObject Ag3_neutrons;
    public static MeshObject Ag3_protons;
    public static MeshObject AgLvl41;
    public static MeshObject AgLvl51;
    
    // Pb objects
    //public static MeshObject PbLvl11;
    public static MeshObject PbLvl12;
    public static MeshObject PbLvl21;
    public static MeshObject Pb3_orbits;
    public static MeshObject Pb3_electrons;
    public static MeshObject Pb3_neutrons;
    public static MeshObject Pb3_protons;
    public static MeshObject PbLvl41;
    public static MeshObject PbLvl51;
    
 // We want to load specific textures from the APK, which we will later use
    // for rendering.
    private void loadTextures()
    {
    	mTextures.add(Texture.loadTextureFromApk("Groups/Ag-Group.png",
                getAssets())); // 0
        
        mTextures.add(Texture.loadTextureFromApk("Groups/Pb-Group.png",
                getAssets())); // 1
        
        mTextures.add(Texture.loadTextureFromApk("VirtualButtons/button-selection.png",
                getAssets())); // 2       
        
        //level 1 textures
    	mTextures.add(Texture.loadTextureFromApk("Ag/TextureSphereRed.png",
                getAssets())); // 3
    	
        mTextures.add(Texture.loadTextureFromApk("Planes/ag-level1/ag-level1.png",
                getAssets())); // 4
        
        mTextures.add(Texture.loadTextureFromApk("Pb/TextureSphereBlue.png",
                getAssets())); // 5   
        
        mTextures.add(Texture.loadTextureFromApk("Planes/pb-level1/pb-level1.png",
                getAssets())); // 6
        
        //level 2 textures
        mTextures.add(Texture.loadTextureFromApk("Planes/ag-level2/ag-level2.png",
                getAssets())); // 7
        
        mTextures.add(Texture.loadTextureFromApk("Planes/pb-level2/pb-level2.png",
                getAssets())); // 8
    	
        //level 3 textures
    	mTextures.add(Texture.loadTextureFromApk("Ag/TextureSpherePurple.png",
                getAssets())); // 9
    	
    	mTextures.add(Texture.loadTextureFromApk("Pb/TextureSphereOrange.png",
                getAssets())); // 10
    	
    	//level 4 textures
    	mTextures.add(Texture.loadTextureFromApk("Planes/ag-level4/ag-level4.png",
                getAssets())); // 11
    	
    	mTextures.add(Texture.loadTextureFromApk("Planes/pb-level4/pb-level4.png",
                getAssets())); // 12
    	
    	//level 5 textures
    	mTextures.add(Texture.loadTextureFromApk("Planes/ag-level5/ag-level5.png",
                getAssets())); // 13
       
       mTextures.add(Texture.loadTextureFromApk("Planes/pb-level5/pb-level5.png",
               getAssets())); // 14
       
       // Bohr Model
       // Ag
       mTextures.add(Texture.loadTextureFromApk("Bohr/Ag/orbits.jpg",
               getAssets())); // 15
       mTextures.add(Texture.loadTextureFromApk("Bohr/Ag/electrons.jpg",
               getAssets())); // 16
       mTextures.add(Texture.loadTextureFromApk("Bohr/Ag/neutrons.jpg",
               getAssets())); // 17
       mTextures.add(Texture.loadTextureFromApk("Bohr/Ag/protons.jpg",
               getAssets())); // 18      
       // Pb
       mTextures.add(Texture.loadTextureFromApk("Bohr/Pb/orbits.jpg",
               getAssets())); // 19
       mTextures.add(Texture.loadTextureFromApk("Bohr/Pb/electrons.jpg",
               getAssets())); // 20
       mTextures.add(Texture.loadTextureFromApk("Bohr/Pb/neutrons.jpg",
               getAssets())); // 21
       mTextures.add(Texture.loadTextureFromApk("Bohr/Pb/protons.jpg",
               getAssets())); // 22
    }   
    
    public void loadElementSpecs()
    {
    	//AgLvl11 = new Cube(); // placeholder
    	AgLvl12 = new ImportedMesh("Planes/ag-level1", this);
    	AgLvl21 = new ImportedMesh("Planes/ag-level2", this);
    	Ag3_orbits = new ImportedMesh("Bohr/Ag/orbits", this);
    	Ag3_electrons = new ImportedMesh("Bohr/Ag/electrons", this);
    	Ag3_neutrons = new ImportedMesh("Bohr/Ag/neutrons", this);
    	Ag3_protons = new ImportedMesh("Bohr/Ag/protons", this);
    	AgLvl41 = new ImportedMesh("Planes/ag-level4", this);
    	AgLvl51 = new ImportedMesh("Planes/ag-level5", this);
    	
    	//PbLvl11 = new Sphere(); // placeholder
    	PbLvl12 = new ImportedMesh("Planes/pb-level1", this);
    	PbLvl21 = new ImportedMesh("Planes/pb-level2", this);
    	Pb3_orbits = new ImportedMesh("Bohr/Pb/orbits", this);
    	Pb3_electrons = new ImportedMesh("Bohr/Pb/electrons", this);
    	Pb3_neutrons = new ImportedMesh("Bohr/Pb/neutrons", this);
    	Pb3_protons = new ImportedMesh("Bohr/Pb/protons", this);
    	PbLvl41 = new ImportedMesh("Planes/pb-level4", this);
    	PbLvl51 = new ImportedMesh("Planes/pb-level5", this);
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
     // Load any sample specific textures:
        mTextures = new Vector<Texture>();
        loadTextures();
        loadElementSpecs();
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
            R.layout.splash_screen, null, false);
        
        addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            
            @Override
            public void run()
            {
                
                Intent intent = new Intent(ActivitySplashScreen.this,
                    ActivityLauncher.class);
                startActivity(intent);
                
            }
            
        }, SPLASH_MILLIS);
    }
    
}
