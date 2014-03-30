/*==============================================================================
 Copyright (c) 2012-2013 Qualcomm Connected Experiences, Inc.
 All Rights Reserved.
 ==============================================================================*/

package groupB.hcin5300.VuforiaSample.app.VirtualButtons;

import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

//import java.io.File;
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
import java.io.FileWriter;
import android.os.Environment;
//import android.widget.TextView;
//import android.widget.EditText;

import com.qualcomm.vuforia.Area;
import com.qualcomm.vuforia.ImageTargetResult;
import com.qualcomm.vuforia.Rectangle;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.qualcomm.vuforia.VirtualButton;
import com.qualcomm.vuforia.VirtualButtonResult;
import com.qualcomm.vuforia.Vuforia;

import groupB.hcin5300.SampleApplication.SampleApplicationSession;
import groupB.hcin5300.SampleApplication.utils.CubeShaders;
import groupB.hcin5300.SampleApplication.utils.ImportedMesh;
import groupB.hcin5300.SampleApplication.utils.LineShaders;
import groupB.hcin5300.SampleApplication.utils.MeshObject;
import groupB.hcin5300.SampleApplication.utils.Plane;
import groupB.hcin5300.SampleApplication.utils.RectCoords;
import groupB.hcin5300.SampleApplication.utils.SampleUtils;
import groupB.hcin5300.SampleApplication.utils.Teapot;
import groupB.hcin5300.SampleApplication.utils.Sphere;
import groupB.hcin5300.SampleApplication.utils.Cube;
import groupB.hcin5300.SampleApplication.utils.Texture;
import groupB.hcin5300.SampleApplication.utils.Vector3D;
import groupB.hcin5300.VuforiaSample.ui.ActivityList.AboutScreen;


public class VirtualButtonRenderer implements GLSurfaceView.Renderer
{
    private static final String LOGTAG = "VirtualButtonRenderer";
    
    private SampleApplicationSession vuforiaAppSession;
    
    public boolean mIsActive = false;
    
    private VirtualButtons mActivity;
    
    private Vector<Texture> mTextures;
    
    // virtual button coordinates
    public static final int RECTCOUNT = 7;
    Rectangle vbRectangle[] = new Rectangle[RECTCOUNT];
    public RectCoords agC = new RectCoords(21.2f, 33f, 35.2f, 19f);
    public RectCoords pbC = new RectCoords(63.7f, 18.5f, 77.7f, 3.5f);
    
    public RectCoords l1C = new RectCoords(-119f, -49f, -91f, -59f);
    public RectCoords l2C = new RectCoords(-86f, -49f, -58f, -59f);
    public RectCoords l3C = new RectCoords(-53f, -49f, -25f, -59f);
    public RectCoords l4C = new RectCoords(57f, -50f, 85f, -60f);
    public RectCoords l5C = new RectCoords(91f, -50f, 119f, -60f); 
    
    final int AGG = 0;
	final int PBG = 1;
	final int BUTTONSELECTION = 2;
	final int AG11 = 3;
	final int AG12 = 4;
	final int PB11 = 5;
	final int PB12 = 6;
	final int AG2 = 7;
	final int PB2 = 8;
	final int AG4 = 11;
	final int PB4 = 12;
	final int AG5 = 13;	
	final int PB5 = 14;

	 final int ORBITS1 = 15;
     final int AGE = 16;
     final int AGN = 17;
     final int AGP = 18;
     final int ORBITS2 = 19;
     final int PBE = 20;
     final int PBN = 21;
     final int PBP = 22;
    
    //private Teapot mTeapot = new Teapot();
    //private Sphere mTeapot = new Sphere(); 
    
    public boolean elementSelected = false;
    int elementIndex = -1;
    int currLevel = 1;
    
    // Ag objects
    MeshObject AgLvl11;
    MeshObject AgLvl12;
    MeshObject AgLvl21;
    MeshObject Ag3_orbits;
    MeshObject Ag3_electrons;
    MeshObject Ag3_neutrons;
    MeshObject Ag3_protons;
    MeshObject AgLvl41;
    MeshObject AgLvl51;
    
    // Pb objects
    MeshObject PbLvl11;
    MeshObject PbLvl12;
    MeshObject PbLvl21;
    MeshObject Pb3_orbits;
    MeshObject Pb3_electrons;
    MeshObject Pb3_neutrons;
    MeshObject Pb3_protons;
    MeshObject PbLvl41;
    MeshObject PbLvl51;
    
    // OpenGL ES 2.0 specific (3D model):
    private int shaderProgramID = 0;
    private int vertexHandle = 0;
    private int normalHandle = 0;
    private int textureCoordHandle = 0;
    private int mvpMatrixHandle = 0;
    private int texSampler2DHandle = 0;
    
    private int lineOpacityHandle = 0;
    private int lineColorHandle = 0;
    private int mvpMatrixButtonsHandle = 0;
    
    // OpenGL ES 2.0 specific (Virtual Buttons):
    private int vbShaderProgramID = 0;
    private int vbVertexHandle = 0;
    
    // Constants:
    //static private float kTeapotScale = 6.f; //1.f;
    
    // Log File Variables
    String participant = AboutScreen.message;
    String filename = participant.replace(" ", "");
	//Calendar cal = Calendar.getInstance();
    
    public VirtualButtonRenderer(VirtualButtons activity,
        SampleApplicationSession session)
    {
        mActivity = activity;
        vuforiaAppSession = session;             

        vbRectangle[0] = new Rectangle(agC.left, agC.top, agC.right, agC.bottom);
        vbRectangle[1] = new Rectangle(pbC.left, pbC.top, pbC.right, pbC.bottom);
        vbRectangle[2] = new Rectangle(l1C.left, l1C.top, l1C.right, l1C.bottom);  
        vbRectangle[3] = new Rectangle(l2C.left, l2C.top, l2C.right, l2C.bottom);
        vbRectangle[4] = new Rectangle(l3C.left, l3C.top, l3C.right, l3C.bottom);
        vbRectangle[5] = new Rectangle(l4C.left, l4C.top, l4C.right, l4C.bottom);  
        vbRectangle[6] = new Rectangle(l5C.left, l5C.top, l5C.right, l5C.bottom);       
           
        loadElementSpecs();            
    } 
    
    public void loadElementSpecs()
    {
    	AgLvl11 = new Cube(); // placeholder
    	AgLvl12 = new ImportedMesh("Planes/ag-level1", mActivity);
    	AgLvl21 = new ImportedMesh("Planes/ag-level2", mActivity);
    	Ag3_orbits = new ImportedMesh("Bohr/Ag/orbits", mActivity);
    	Ag3_electrons = new ImportedMesh("Bohr/Ag/electrons", mActivity);
    	Ag3_neutrons = new ImportedMesh("Bohr/Ag/neutrons", mActivity);
    	Ag3_protons = new ImportedMesh("Bohr/Ag/protons", mActivity);
    	AgLvl41 = new ImportedMesh("Planes/ag-level4", mActivity);
    	AgLvl51 = new ImportedMesh("Planes/ag-level5", mActivity);
    	
    	PbLvl11 = new Sphere(); // placeholder
    	PbLvl12 = new ImportedMesh("Planes/pb-level1", mActivity);
    	PbLvl21 = new ImportedMesh("Planes/pb-level2", mActivity);
    	Pb3_orbits = new ImportedMesh("Bohr/Pb/orbits", mActivity);
    	Pb3_electrons = new ImportedMesh("Bohr/Pb/electrons", mActivity);
    	Pb3_neutrons = new ImportedMesh("Bohr/Pb/neutrons", mActivity);
    	Pb3_protons = new ImportedMesh("Bohr/Pb/protons", mActivity);
    	PbLvl41 = new ImportedMesh("Planes/pb-level4", mActivity);
    	PbLvl51 = new ImportedMesh("Planes/pb-level5", mActivity);
    }
    
    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");
        
        // Call function to initialize rendering:
        initRendering();
        
        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();
    }
    
    
    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");
        
        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);
    }
    
    
    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;
        
        // Call our function to render content
        renderFrame();              
    }
    
    
    private void initRendering()
    {
        Log.d(LOGTAG, "VirtualButtonsRenderer.initRendering");
        
        // Define clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
            : 1.0f);
        
        // Now generate the OpenGL texture objects and add settings
        for (Texture t : mTextures)
        {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, t.mData);
        }
        
        shaderProgramID = SampleUtils.createProgramFromShaderSrc(
            CubeShaders.CUBE_MESH_VERTEX_SHADER,
            CubeShaders.CUBE_MESH_FRAGMENT_SHADER);
        
        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
            "vertexPosition");
        normalHandle = GLES20.glGetAttribLocation(shaderProgramID,
            "vertexNormal");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
            "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
            "modelViewProjectionMatrix");
        texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID,
            "texSampler2D");
        
        // OpenGL setup for Virtual Buttons
        vbShaderProgramID = SampleUtils.createProgramFromShaderSrc(
            LineShaders.LINE_VERTEX_SHADER, LineShaders.LINE_FRAGMENT_SHADER);
        
        mvpMatrixButtonsHandle = GLES20.glGetUniformLocation(vbShaderProgramID,
            "modelViewProjectionMatrix");
        vbVertexHandle = GLES20.glGetAttribLocation(vbShaderProgramID,
            "vertexPosition");
        lineOpacityHandle = GLES20.glGetUniformLocation(vbShaderProgramID,
            "opacity");
        lineColorHandle = GLES20.glGetUniformLocation(vbShaderProgramID,
            "color");
        
     // Write to File
        
        try
        {
	        // Write Header - Date/Time, Participant Name, Test Type
        	// File location : Public Downloads folder
        	//cal = cal.getInstance();
        	Calendar cal = Calendar.getInstance();
        	String dateTime = cal.getTime().toString();
        	//dateTime += ":" + Integer.toString(cal.get(Calendar.SECOND));
        	String fileHead = dateTime + "\n" + participant + "\n" + "AR Test";
        	
        	FileWriter fw = new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + filename + ".txt", true);
	        fw.append( fileHead + "\n");
	        fw.close();
	        //Log.d("FileWriter","File was Created");
        } catch (Exception e) {
            Log.e("FileWriter","Did Not Create File " + filename );
        }
    }
    
    private void renderFrame()
    {
        // Clear color and depth buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        // Get the state from Vuforia and mark the beginning of a rendering
        // section
        State state = Renderer.getInstance().begin();
        
        // Explicitly render the Video Background
        Renderer.getInstance().drawVideoBackground();
        
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        
        // We must detect if background reflection is active and adjust the
        // culling direction.
        // If the reflection is active, this means the post matrix has been
        // reflected as well,
        // therefore counter standard clockwise face culling will result in
        // "inside out" models.
        //GLES20.glEnable(GLES20.GL_CULL_FACE);
        //GLES20.glCullFace(GLES20.GL_BACK);
        
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        
        if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
            GLES20.glFrontFace(GLES20.GL_CW); // Front camera
        else
            GLES20.glFrontFace(GLES20.GL_CCW); // Back camera
            
        // Did we find any trackables this frame?
        if (state.getNumTrackableResults() > 0)
        {
            // Get the trackable:
            TrackableResult trackableResult = state.getTrackableResult(0);           
            
            // The image target specific result:
            assert (trackableResult.getType() == ImageTargetResult
                .getClassType());
                                
            
            // Set the texture used for the teapot model:
            //int textureIndex = 0;
            
            if(elementSelected)           
            	applyElementGroupHighlight(state);
            
            RenderVirtualButtons(trackableResult);
            RenderSelectionTexture(state);
            Render3DModel(trackableResult);          
        
            SampleUtils.checkGLError("VirtualButtons renderFrame");         
        }
        
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        
        Renderer.getInstance().end();       
    }
    
    private void Render3DModel(TrackableResult tr)
    {      
    	if(elementSelected)
    	{
    		// an array of mesh objects to be rendered in the current level
    		// and arrays of their translations and scales
    		Vector<MeshObject> meshObjects = new Vector<MeshObject>();
    		Vector<Texture> meshTextures = new Vector<Texture>();
    		Vector<Vector3D> meshTransls = new Vector<Vector3D>();
    		Vector<Vector3D> meshScales = new Vector<Vector3D>(); 
    		
    		// add objects and textures associated with the selected level
    		LoadLevelObjects(meshObjects, meshTextures, meshTransls, meshScales);
    		
    		// Assumptions:
            //assert (textureIndex < mTextures.size());
            //Texture thisTexture = mTextures.get(textureIndex);
    		
    		// render mesh objects in the current level 		
    		for(int i=0; i<meshObjects.size(); ++i){   			
    			RenderMeshObject(
    					tr, meshObjects.get(i), meshTextures.get(i), 
    					    meshTransls.get(i), meshScales.get(i));
    		}
    	}		 
    }
    
    private void LoadLevelObjects(Vector<MeshObject> meshObjects,
    				Vector<Texture> meshTextures,
    				Vector<Vector3D> meshTransls,
    				Vector<Vector3D> meshScales)
    {
		switch(currLevel)
		{
		case 1:
			if(elementIndex == 0) // Ag
			{
				// first Ag object in level 1
				meshObjects.add(AgLvl11);
				meshTextures.add(mTextures.get(AG11));
				meshTransls.add(new Vector3D(7.0f, 0.0f, 5.0f));
				meshScales.add(new Vector3D(10.0f, 10.0f, 10.0f));
				
				// second Ag object in level 1...
				meshObjects.add(AgLvl12);
				meshTextures.add(mTextures.get(AG12));
				meshTransls.add(new Vector3D(0.0f, 2.5f, 10.0f));
				meshScales.add(new Vector3D(6.0f, 30.0f, 1.0f));
			}
			else // Pb
			{
				// first Pb object in level 1
				meshObjects.add(PbLvl11);
				meshTextures.add(mTextures.get(PB11));
				meshTransls.add(new Vector3D(15.0f, 7.0f, 5.0f));
				meshScales.add(new Vector3D(6.0f, 6.0f, 6.0f));
				
				// second Pb object in level 1...
				meshObjects.add(PbLvl12);
				meshTextures.add(mTextures.get(PB12));
				meshTransls.add(new Vector3D(-4.0f, 2.5f, 10.0f));
				meshScales.add(new Vector3D(6.0f, 30.0f, 1.0f));
			}
			
			break;
		case 2:
			if(elementIndex == 0) // Ag
			{
				// first Ag object in level 2
				meshObjects.add(AgLvl21);
				meshTextures.add(mTextures.get(AG2));
				meshTransls.add(new Vector3D(0.0f, 2.0f, 10.0f));
				meshScales.add(new Vector3D(6.0f, 40.0f, 1.0f));			
			}
			else // Pb
			{
				// first Pb object in level 2
				meshObjects.add(PbLvl21);
				meshTextures.add(mTextures.get(PB2));
				meshTransls.add(new Vector3D(-9.0f, 2.0f, 10.0f));
				meshScales.add(new Vector3D(4.0f, 40.0f, 1.0f));
				
			}
			break;
		case 3:
			if(elementIndex == 0) // Ag
			{
				int meshCount = 4;
				meshObjects.add(Ag3_orbits);
				meshObjects.add(Ag3_electrons);
				meshObjects.add(Ag3_neutrons);
				meshObjects.add(Ag3_protons);
				
				for(int k=ORBITS1;k<=AGP;++k){
					meshTextures.add(mTextures.get(k)); }			
				for(int a=1;a<=meshCount;++a){
					meshTransls.add(new Vector3D(17.0f, 5.0f, 5.0f));
					meshScales.add(new Vector3D(5.0f, 5.0f, 5.0f)); }
			}
			else // Pb
			{
				int meshCount = 4;
				meshObjects.add(Pb3_orbits);
				meshObjects.add(Pb3_electrons);
				meshObjects.add(Pb3_neutrons);
				meshObjects.add(Pb3_protons);
				
				for(int k=ORBITS2;k<=PBP;++k){
					meshTextures.add(mTextures.get(k)); }			
				for(int a=1;a<=meshCount;++a){
					meshTransls.add(new Vector3D(0.0f, 5.0f, 5.0f));
					meshScales.add(new Vector3D(5.0f, 5.0f, 5.0f)); }
			}
			break;
		case 4:
			if(elementIndex == 0) // Ag
			{
				// first Ag object in level 4
				meshObjects.add(AgLvl41);
				meshTextures.add(mTextures.get(AG4));
				meshTransls.add(new Vector3D(0.0f, 2.0f, 10.0f));
				meshScales.add(new Vector3D(6.0f, 40.0f, 1.0f));				
			}
			else // Pb
			{
				meshObjects.add(PbLvl41);
				meshTextures.add(mTextures.get(PB4));
				meshTransls.add(new Vector3D(-9.0f, 2.2f, 10.0f));
				meshScales.add(new Vector3D(2.5f, 30.0f, 1.0f));
			}
			break;
		case 5:
			if(elementIndex == 0) // Ag
			{
				meshObjects.add(AgLvl51);
				meshTextures.add(mTextures.get(AG5));
				meshTransls.add(new Vector3D(0.0f, 2.0f, 10.0f));
				meshScales.add(new Vector3D(6.0f, 40.0f, 1.0f));
			}
			else // Pb
			{
				meshObjects.add(PbLvl51);
				meshTextures.add(mTextures.get(PB5));
				meshTransls.add(new Vector3D(-9.0f, 2.2f, 10.0f));
				meshScales.add(new Vector3D(2.5f, 30.0f, 1.0f));
			}
			break;
		}
    }
    
    private void RenderMeshObject(TrackableResult tr, MeshObject mO,
    		Texture mT,
			Vector3D mTr,
			Vector3D mS)
    {
    	float[] modelViewMatrix = Tool.convertPose2GLMatrix(
                tr.getPose()).getData();
    	float[] modelViewProjection = new float[16];
    	
    	Matrix.scaleM(modelViewMatrix, 0, 
        		mS.x, mS.y, mS.z); 
    	Matrix.translateM(modelViewMatrix, 0, 
    			mTr.x, mTr.y, mTr.z);                
        Matrix.multiplyMM(modelViewProjection, 0, vuforiaAppSession
            .getProjectionMatrix().getData(), 0, modelViewMatrix, 0);
        
        
        // Render 3D Model
        GLES20.glUseProgram(shaderProgramID);       
        
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
        	false, 0, mO.getVertices());
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
        	false, 0, mO.getNormals());
        
	    GLES20.glVertexAttribPointer(textureCoordHandle, 2,
	        	GLES20.GL_FLOAT, false, 0, mO.getTexCoords());
        
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);
        
        GLES20.glEnableVertexAttribArray(textureCoordHandle);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
            mT.mTextureID[0]);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
            modelViewProjection, 0);
        GLES20.glUniform1i(texSampler2DHandle, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,
        	mO.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
            mO.getIndices());
        
        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        
        if(currLevel != 3)
        	GLES20.glDisableVertexAttribArray(textureCoordHandle);   						
    }
    
    private Buffer fillBuffer(float[] array)
    {
        // Convert to floats because OpenGL doesnt work on doubles, and manually
        // casting each input value would take too much time.
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * array.length); // each
                                                                     // float
                                                                     // takes 4
                                                                     // bytes
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (float d : array)
            bb.putFloat(d);
        bb.rewind();
        
        return bb;      
    }   
    
    public void setTextures(Vector<Texture> textures)
    {
        mTextures = textures;       
    }
    
    public void applyElementGroupHighlight(State state)
    {
    	if(elementIndex > -1)
    	{
    		float tx = 0.0f;
        	float ty = 0.0f;
        	float tz = 0.0f;
        	
        	float sx = 1.0f;
        	float sy = 1.0f;
        	float sz = 1.0f;
        	
        	int groupTexture = 0;
        	
        	Plane grpPlane = new Plane();
        	TrackableResult trackableResult = state.getTrackableResult(0);
        	assert (trackableResult.getType() == ImageTargetResult
                    .getClassType());
        	float[] modelViewMatrix = Tool.convertPose2GLMatrix(
                    trackableResult.getPose()).getData();
        	float[] modelViewProjection = new float[16];
        	
        	switch(elementIndex)
        	{
        	case 0: // Ag group
        		groupTexture = AGG;
        		tx = -22.5f;
        		ty = 19f;
        		sx = 147.0f;
        		sy = 64.0f; 
        		break;
        	case 1: // Pb group
        		groupTexture = PBG;
        		tx = 78.0f;
        		ty = 33.5f;
        		sx = 58.0f;
        		sy = 62.0f; 
        		break;
        	}
        	
        	Matrix.translateM(modelViewMatrix, 0, 
    				tx, ty, tz);       
            Matrix.scaleM(modelViewMatrix, 0, 
            		sx, sy, sz);   
            Matrix.multiplyMM(modelViewProjection, 0, vuforiaAppSession
                .getProjectionMatrix().getData(), 0, modelViewMatrix, 0);
            
            GLES20.glUseProgram(shaderProgramID);
        	GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                	false, 0, grpPlane.getVertices());
            GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                	false, 0, grpPlane.getNormals());
            GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                	GLES20.GL_FLOAT, false, 0, grpPlane.getTexCoords());
            
            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(normalHandle);
            GLES20.glEnableVertexAttribArray(textureCoordHandle);
        	
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 
            		mTextures.get(groupTexture).mTextureID[0]);   	
        	
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                modelViewProjection, 0);
            GLES20.glUniform1i(texSampler2DHandle, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES,
            	6, GLES20.GL_UNSIGNED_SHORT,
                grpPlane.getIndices());
    	}
    }
    
    private void buttonSelection(int buttonIndex)
    {
    	switch(buttonIndex)
    	{
    	case 0: // Ag
    		currLevel = 1; // reset to level 1
    		if(elementIndex == -1){
    			mActivity.ElementIsSelected();
    			// Add Selected Element to File
    			try
    	        {
    	        	FileWriter fw = new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + filename + ".txt", true);
    		        fw.append("Ag\n");
    		        fw.append("-----\n");
    		        fw.close();
    		        //Log.d("FileWriter","File was Appended");
    	        } catch (Exception e) {
    	        	Log.e("FileWriter","Did Not Create File2");
    	        }
    		}
    		elementIndex = buttonIndex;
    		elementSelected = true;               		
    		break;
    	case 1: // Pb
    		currLevel = 1; // reset to level 1
    		if(elementIndex == -1){
    			mActivity.ElementIsSelected();
    			// Add Selected Element to File
    			try
    	        {
    	        	FileWriter fw = new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + filename + ".txt", true);
    		        fw.append("Pb\n");
    		        fw.append("-----\n");
    		        fw.close();
    		        //Log.d("FileWriter","File was Appended");
    	        } catch (Exception e) {
    	        	Log.e("FileWriter","Did Not Create File2");
    	        }
    		}
    		elementIndex = buttonIndex;
    		elementSelected = true;
    		break;
    	case 2: // level1
    		if(currLevel != 1)
    			addLogEntry();
    		currLevel = 1;
    		break;
    	case 3: // level2
    		if(currLevel != 2)
    			addLogEntry();
    		currLevel = 2;
    		break;
    	case 4: // level3
    		if(currLevel != 3)
    			addLogEntry();
    		currLevel = 3;
    		break;
    	case 5: // level4
    		if(currLevel != 4)
    			addLogEntry();
    		currLevel = 4;
    		break;
    	case 6: // level5
    		if(currLevel != 5)
    			addLogEntry();
    		currLevel = 5;
    		break;
    	}
    }
    
    private void addLogEntry()
    {
    	try
        {
			// **** Stores Previous Level & End Time of Prev Level
			Calendar cal = Calendar.getInstance();
			String dateTime = cal.getTime().toString();
			String newLine = dateTime + ", " + "AR" + ", " + participant + ", " + Integer.toString(currLevel) + "\n";
			
        	FileWriter fw = new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + filename + ".txt", true);
	        fw.append(newLine);
	        fw.close();
        } catch (Exception e) {
        	Log.e("FileWriter","Did Not Create File2");
        }
    }
    
    private void RenderVirtualButtons(TrackableResult trackableResult)
    {
        ImageTargetResult imageTargetResult = (ImageTargetResult) trackableResult; 
        
    	// Set transformations:
    	float[] modelViewMatrix = Tool.convertPose2GLMatrix(
              trackableResult.getPose()).getData();
    	float[] modelViewProjection = new float[16];
    	Matrix.multiplyMM(modelViewProjection, 0, vuforiaAppSession
          .getProjectionMatrix().getData(), 0, modelViewMatrix, 0);
      
    	float vbVertices[] = new float[imageTargetResult
    	                               .getNumVirtualButtons() * 24];
    	short vbCounter = 0;
    	                           
    	// Iterate through this targets virtual buttons:
        for (int i = 0; i < imageTargetResult.getNumVirtualButtons(); ++i)
        {
            VirtualButtonResult buttonResult = imageTargetResult
                .getVirtualButtonResult(i);
            VirtualButton button = buttonResult.getVirtualButton();
            
            int buttonIndex = 0;
            
            // Run through button name array to find button index
            for (int j = 0; j < VirtualButtons.NUM_BUTTONS; ++j)
            {
                if (button.getName().compareTo(
                    mActivity.virtualButtonInfo[j]) == 0)
                {
                    buttonIndex = j;
                    break;
                }
            }
            
            // If the button is pressed, than use this texture:
            if (buttonResult.isPressed())
            {
            	//textureIndex = buttonIndex;
            	buttonSelection(buttonIndex);
            }
            
            Area vbArea = button.getArea();
            assert (vbArea.getType() == Area.TYPE.RECTANGLE);
                           
            // We add the vertices to a common array in order to have one
            // single
            // draw call. This is more efficient than having multiple
            // glDrawArray calls
            if(buttonIndex == 0 || buttonIndex == 1)
            	vbCounter = fillVBvertices(vbCounter, buttonIndex, vbVertices);              
        }
        
        // We only render if there is something on the array
        if (vbCounter > 0)
        {
            // Render frame around button
            GLES20.glUseProgram(vbShaderProgramID);
            
            GLES20.glVertexAttribPointer(vbVertexHandle, 3,
                GLES20.GL_FLOAT, false, 0, fillBuffer(vbVertices));
            
            GLES20.glEnableVertexAttribArray(vbVertexHandle);
            
            GLES20.glUniform1f(lineOpacityHandle, 1.0f);
            GLES20.glUniform3f(lineColorHandle, 1.0f, 1.0f, 1.0f);
            
            GLES20.glUniformMatrix4fv(mvpMatrixButtonsHandle, 1, false,
                modelViewProjection, 0);
            
            // We multiply by 8 because that's the number of vertices per
            // button
            // The reason is that GL_LINES considers only pairs. So some
            // vertices
            // must be repeated.
            GLES20.glDrawArrays(GLES20.GL_LINES, 0,
                imageTargetResult.getNumVirtualButtons() * 8);
            
            SampleUtils.checkGLError("VirtualButtons drawButton");
            
            GLES20.glDisableVertexAttribArray(vbVertexHandle);
        }                 
    }
    
    private short fillVBvertices(short vbC, int buttonIndex, float[] vbVertices)
    {
    	short vbCounter = vbC;
    	vbVertices[vbCounter] = vbRectangle[buttonIndex].getLeftTopX();
        vbVertices[vbCounter + 1] = vbRectangle[buttonIndex]
            .getLeftTopY();
        vbVertices[vbCounter + 2] = 0.0f;
        vbVertices[vbCounter + 3] = vbRectangle[buttonIndex]
            .getRightBottomX();
        vbVertices[vbCounter + 4] = vbRectangle[buttonIndex]
            .getLeftTopY();
        vbVertices[vbCounter + 5] = 0.0f;
        vbVertices[vbCounter + 6] = vbRectangle[buttonIndex]
            .getRightBottomX();
        vbVertices[vbCounter + 7] = vbRectangle[buttonIndex]
            .getLeftTopY();
        vbVertices[vbCounter + 8] = 0.0f;
        vbVertices[vbCounter + 9] = vbRectangle[buttonIndex]
            .getRightBottomX();
        vbVertices[vbCounter + 10] = vbRectangle[buttonIndex]
            .getRightBottomY();
        vbVertices[vbCounter + 11] = 0.0f;
        vbVertices[vbCounter + 12] = vbRectangle[buttonIndex]
            .getRightBottomX();
        vbVertices[vbCounter + 13] = vbRectangle[buttonIndex]
            .getRightBottomY();
        vbVertices[vbCounter + 14] = 0.0f;
        vbVertices[vbCounter + 15] = vbRectangle[buttonIndex]
            .getLeftTopX();
        vbVertices[vbCounter + 16] = vbRectangle[buttonIndex]
            .getRightBottomY();
        vbVertices[vbCounter + 17] = 0.0f;
        vbVertices[vbCounter + 18] = vbRectangle[buttonIndex]
            .getLeftTopX();
        vbVertices[vbCounter + 19] = vbRectangle[buttonIndex]
            .getRightBottomY();
        vbVertices[vbCounter + 20] = 0.0f;
        vbVertices[vbCounter + 21] = vbRectangle[buttonIndex]
            .getLeftTopX();
        vbVertices[vbCounter + 22] = vbRectangle[buttonIndex]
            .getLeftTopY();
        vbVertices[vbCounter + 23] = 0.0f;
        vbCounter += 24;
        
        return vbCounter;
    }

    private void RenderSelectionTexture(State state)
    {
    	// highlight the pressed button
    	if(elementSelected == true)
    	{
    		float tx = 0.0f;
        	float ty = 0.0f;
        	float tz = 0.0f;
        	
        	float sx = 37.0f;
        	float sy = 20.0f;
        	float sz = 1.0f;
        	
        	int selectionTexture = BUTTONSELECTION;
        	
        	Plane selectionPlane = new Plane();
        	TrackableResult trackableResult = state.getTrackableResult(0);
        	assert (trackableResult.getType() == ImageTargetResult
                    .getClassType());
        	float[] modelViewMatrix = Tool.convertPose2GLMatrix(
                    trackableResult.getPose()).getData();
        	float[] modelViewProjection = new float[16];
        	
        	switch(currLevel)
        	{
        	case 1: 
        		tx = -105f;
        		ty = -54f;
        		break;
        	case 2:
        		tx = -72f;
        		ty = -54f;
        		break;
        	case 3:
        		tx = -39;
        		ty = -54f;
        		break;
        	case 4:
        		tx = 71;
        		ty = -55f;
        		break;
        	case 5:
        		tx = 105;
        		ty = -55f;
        		break;
        	}
        	
        	Matrix.translateM(modelViewMatrix, 0, 
    				tx, ty, tz);       
            Matrix.scaleM(modelViewMatrix, 0, 
            		sx, sy, sz);   
            Matrix.multiplyMM(modelViewProjection, 0, vuforiaAppSession
                .getProjectionMatrix().getData(), 0, modelViewMatrix, 0);
            
            GLES20.glUseProgram(shaderProgramID);
        	GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                	false, 0, selectionPlane.getVertices());
            GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                	false, 0, selectionPlane.getNormals());
            GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                	GLES20.GL_FLOAT, false, 0, selectionPlane.getTexCoords());
            
            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(normalHandle);
            GLES20.glEnableVertexAttribArray(textureCoordHandle);
        	
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 
            		mTextures.get(selectionTexture).mTextureID[0]);   	
        	
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                modelViewProjection, 0);
            GLES20.glUniform1i(texSampler2DHandle, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES,
            	6, GLES20.GL_UNSIGNED_SHORT,
                selectionPlane.getIndices());
    	}
    }
}

