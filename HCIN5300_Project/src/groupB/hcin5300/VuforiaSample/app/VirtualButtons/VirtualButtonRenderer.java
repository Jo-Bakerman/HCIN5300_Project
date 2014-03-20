/*==============================================================================
 Copyright (c) 2012-2013 Qualcomm Connected Experiences, Inc.
 All Rights Reserved.
 ==============================================================================*/

package groupB.hcin5300.VuforiaSample.app.VirtualButtons;

import java.io.FileNotFoundException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.qualcomm.vuforia.Area;
import com.qualcomm.vuforia.CameraCalibration;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.ImageTargetResult;
import com.qualcomm.vuforia.Rectangle;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.qualcomm.vuforia.Vec2F;
import com.qualcomm.vuforia.VirtualButton;
import com.qualcomm.vuforia.VirtualButtonResult;
import com.qualcomm.vuforia.Vuforia;
import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;


import groupB.hcin5300.SampleApplication.SampleApplicationSession;
import groupB.hcin5300.SampleApplication.utils.CubeShaders;
import groupB.hcin5300.SampleApplication.utils.LineShaders;
import groupB.hcin5300.SampleApplication.utils.MeshObject;
import groupB.hcin5300.SampleApplication.utils.Plane;
import groupB.hcin5300.SampleApplication.utils.RectCoords;
import groupB.hcin5300.SampleApplication.utils.SampleUtils;
import groupB.hcin5300.SampleApplication.utils.Teapot;
import groupB.hcin5300.SampleApplication.utils.Sphere;
import groupB.hcin5300.SampleApplication.utils.Texture;
import groupB.hcin5300.SampleApplication.utils.Vector3D;

import groupB.hcin5300.VuforiaSample.R;

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
    public RectCoords agC = new RectCoords(21f, 24.5f, 35f, 10f);
    public RectCoords pbC = new RectCoords(63.5f, 10f, 77.5f, -5f);
    
    public RectCoords l1C = new RectCoords(-128f, -74.5f, -92f, -87.5f);
    public RectCoords l2C = new RectCoords(-72.3f, -74.1f, -36.2f, -87f);
    public RectCoords l3C = new RectCoords(-17.5f, -73.5f, 18.5f, -86.3f);
    public RectCoords l4C = new RectCoords(38f, -73f, 74f, -86.1f);
    public RectCoords l5C = new RectCoords(93f, -73f, 129f, -86f);   
    
    //private Teapot mTeapot = new Teapot();
    //private Sphere mTeapot = new Sphere(); 
    
    public boolean elementSelected = false;
    int elementIndex = -1;
    int currLevel = 1;
    
    // Ag objects
    MeshObject AgLvl11;
    MeshObject AgLvl12;
    MeshObject AgLvl21;
    
    // Pb objects
    MeshObject PbLvl11;
    MeshObject PbLvl21;
    MeshObject PbLvl22;
    
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
    
    // test obj file
    private Object3D thing;
	private Light sun = null;
	private World world = null;
	private Camera cam = null;
	private FrameBuffer fb = null;
    
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
           
        loadJPCTparams();
        loadElementSpecs();
    } 
    
    public void loadJPCTparams()
    {
    	 try {
  			thing = loadModel("res/raw/rock_obj.obj", "res/raw/rock_mtl.mtl", 10);
  		} catch (FileNotFoundException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
    	 
    	world = new World();
    	world.setAmbientLight(20, 20, 20);

    	sun = new Light(world);
    	sun.setIntensity(250, 250, 250);
    	
    	thing.build();
		world.addObject(thing);
		cam = world.getCamera();
		
		SimpleVector sv = new SimpleVector();
		sv.set(thing.getTransformedCenter());
		sv.y -= 100;
		sv.z -= 100;
		sun.setPosition(sv);
		MemoryHelper.compact();
    }
    
    public void loadElementSpecs()
    {
    	AgLvl11 = new Sphere();
    	AgLvl12 = new Sphere();
    	AgLvl21 = new Sphere();
    	
    	PbLvl11 = new Sphere();
    	PbLvl21 = new Sphere();
    	PbLvl22 = new Sphere();   	
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
        
        if (fb != null) {
            fb.dispose();
       }
       fb = new FrameBuffer(width, height);
    }
    
    
    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;
        
        // Call our function to render content
        renderFrame();
        
        world.renderScene(fb);
        world.draw(fb);
        fb.display();
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
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
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
            //RenderObjModel(trackableResult);
            RenderSelectionTexture(state);
            Render3DModel(trackableResult);          
        
            SampleUtils.checkGLError("VirtualButtons renderFrame");         
        }
        
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        
        Renderer.getInstance().end();       
    }
    
    private void RenderObjModel(TrackableResult trackableResult)
    {
//    	const QCAR::CameraCalibration& cameraCalibration = QCAR::CameraDevice::getInstance().getCameraCalibration();
//    	QCAR::Vec2F size = cameraCalibration.getSize();
//    	QCAR::Vec2F focalLength = cameraCalibration.getFocalLength();
//    	float fovyRadians = 2 * atan(0.5f * size.data[1] / focalLength.data[1]);
//    	float fovRadians = 2 * atan(0.5f * size.data[0] / focalLength.data[0]);
    	
    	CameraCalibration cameraCali = CameraDevice.getInstance().getCameraCalibration();
    	Vec2F size = cameraCali.getSize();
    	Vec2F focalLength = cameraCali.getFocalLength();
    	float fovyRadians = (float)(2 * Math.atan(0.5f * size.getData()[1] / focalLength.getData()[1]));
    	float fovRadians = (float)(2 * Math.atan(0.5f * size.getData()[0] / focalLength.getData()[0]));
    	
    	// Set transformations:
    	float[] modelViewMatrix = Tool.convertPose2GLMatrix(
              trackableResult.getPose()).getData();
    	Matrix.rotateM(modelViewMatrix, 0, 180.0f, 1.0f, 0, 0);
    	com.threed.jpct.Matrix m = new com.threed.jpct.Matrix();
    	m.setDump(modelViewMatrix);
        cam.setBack(m);
//        cam.setFovAngle(fovRadians);
//        cam.setYFovAngle(fovyRadians);
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
    		
    		// an array of text objects to be rendered in the current level
    		// and arrays of their translations and scales
    		/*
    		Vector<textObject> textObjects = new Vector<textObject>();
    		Vector<Texture> textTextures = new Vector<Texture>();
    		Vector<Vector3D> textTransls = new Vector<Vector3D>();
    		Vector<Vector3D> textScales = new Vector<Vector3D>();
    		*/   		
    		
    		// add objects and textures associated with the selected level
    		LoadLevelObjects(meshObjects, meshTextures, meshTransls, meshScales);
    		
    		// Assumptions:
            //assert (textureIndex < mTextures.size());
            //Texture thisTexture = mTextures.get(textureIndex);
    		
    		// render mesh objects in the current level
    		for(int i=0; i<meshObjects.size(); ++i)
    		{   			
    			RenderMeshObject(
    					tr, meshObjects.get(i), meshTextures.get(i), 
    					    meshTransls.get(i), meshScales.get(i));
    		}

    		// render text objects in the current level
    		/*
    		for(int k=0; k<textObjects.size; ++k)
    		{
    			
    		}
    		*/ 		
    	}	       	
            
    		/*
            // Scale 3D model
            float[] modelViewScaled = modelViewMatrix;
            Matrix.scaleM(modelViewScaled, 0, kTeapotScale, kTeapotScale,
            kTeapotScale);
            
            // translate 3D model
            Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f, 10.0f);
            
            float[] modelViewProjectionScaled = new float[16];
            Matrix.multiplyMM(modelViewProjectionScaled, 0, vuforiaAppSession
                .getProjectionMatrix().getData(), 0, modelViewScaled, 0);
            
            
            // Render 3D model
            GLES20.glUseProgram(shaderProgramID);
            
            GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                false, 0, mTeapot.getVertices());
            GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                false, 0, mTeapot.getNormals());
            GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                GLES20.GL_FLOAT, false, 0, mTeapot.getTexCoords());
            
            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(normalHandle);
            GLES20.glEnableVertexAttribArray(textureCoordHandle);
            
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                thisTexture.mTextureID[0]);
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                modelViewProjectionScaled, 0);
            GLES20.glUniform1i(texSampler2DHandle, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                mTeapot.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
                mTeapot.getIndices());
            
            GLES20.glDisableVertexAttribArray(vertexHandle);
            GLES20.glDisableVertexAttribArray(normalHandle);
            GLES20.glDisableVertexAttribArray(textureCoordHandle); 
            */                          
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
				meshTextures.add(mTextures.get(0));
				meshTransls.add(new Vector3D(-50.0f, 10.0f, 0.0f));
				meshScales.add(new Vector3D(3.0f, 3.0f, 3.0f));
				
				// second Ag object in level 1...
				meshObjects.add(AgLvl12);
				meshTextures.add(mTextures.get(2));
				meshTransls.add(new Vector3D(50.0f, 0.0f, 0.0f));
				meshScales.add(new Vector3D(6.0f, 6.0f, 6.0f));
			}
			else // Pb
			{
				// first Pb object in level 1
				meshObjects.add(PbLvl11);
				meshTextures.add(mTextures.get(3));
				meshTransls.add(new Vector3D(0.0f, 10.0f, 10.0f));
				meshScales.add(new Vector3D(3.0f, 3.0f, 3.0f));
				
				// second Pb object in level 1...
			}
			break;
		case 2:
			if(elementIndex == 0) // Ag
			{
				// first Ag object in level 2
				meshObjects.add(AgLvl21);
				meshTextures.add(mTextures.get(1));
				meshTransls.add(new Vector3D(0.0f, 10.0f, 0.0f));
				meshScales.add(new Vector3D(6.0f, 6.0f, 6.0f));
			}
			else // Pb
			{
				// first Pb object in level 2
				meshObjects.add(PbLvl21);
				meshTextures.add(mTextures.get(4));
				meshTransls.add(new Vector3D(0.0f, 50.0f, 10.0f));
				meshScales.add(new Vector3D(3.0f, 3.0f, 3.0f));  	
				
				// second Pb object in level 2
				meshObjects.add(PbLvl22);
				meshTextures.add(mTextures.get(5));
				meshTransls.add(new Vector3D(-50.0f, 0.0f, 0.0f));
				meshScales.add(new Vector3D(6.0f, 6.0f, 6.0f));
			}
			break;
		case 3:
			if(elementIndex == 0) // Ag
			{
				
			}
			else // Pb
			{
				
			}
			break;
		case 4:
			if(elementIndex == 0) // Ag
			{
				
			}
			else // Pb
			{
				
			}
			break;
		case 5:
			if(elementIndex == 0) // Ag
			{
				
			}
			else // Pb
			{
				
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
    	
    	Matrix.translateM(modelViewMatrix, 0, 
    			mTr.x, mTr.y, mTr.z);       
        Matrix.scaleM(modelViewMatrix, 0, 
        		mS.x, mS.y, mS.z);   
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
        		groupTexture = 6;
        		tx = -22.5f;
        		ty = 10f;
        		sx = 147.0f;
        		sy = 64.0f; 
        		break;
        	case 1: // Pb group
        		groupTexture = 7;
        		tx = 78.0f;
        		ty = 25.0f;
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
    		if(elementIndex == -1)
    			mActivity.ElementIsSelected();
    		elementIndex = buttonIndex;
    		elementSelected = true;               		
    		break;
    	case 1: // Pb
    		currLevel = 1; // reset to level 1
    		if(elementIndex == -1)
    			mActivity.ElementIsSelected();
    		elementIndex = buttonIndex;
    		elementSelected = true;
    		break;
    	case 2: // level1
    		currLevel = 1;
    		break;
    	case 3: // level2
    		currLevel = 2;
    		break;
    	case 4: // level3
    		currLevel = 3;
    		break;
    	case 5: // level4
    		currLevel = 4;
    		break;
    	case 6: // level5
    		currLevel = 5;
    		break;
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

    private Object3D loadModel(String filename, String mtlname, float scale) 
    		throws FileNotFoundException {		
        Object3D[] model = Loader.loadOBJ
        		(mActivity.getResources().openRawResource(R.raw.rock_obj),
        		 mActivity.getResources().openRawResource(R.raw.rock_mtl),
        		 scale);
        Object3D o3d = new Object3D(0);
        Object3D temp = null;
        for (int i = 0; i < model.length; i++) {
            temp = model[i];
            temp.setCenter(SimpleVector.ORIGIN);
            temp.rotateX((float)( -.5*Math.PI));
            temp.rotateMesh();
            temp.setRotationMatrix(new com.threed.jpct.Matrix());
            o3d = Object3D.mergeObjects(o3d, temp);
            o3d.build();
        }
        return o3d;
    }
    
    private void RenderSelectionTexture(State state)
    {
    	// highlight the pressed button
    	if(elementSelected == true)
    	{
    		float tx = 0.0f;
        	float ty = 0.0f;
        	float tz = 0.0f;
        	
        	float sx = 50.0f;
        	float sy = 25.0f;
        	float sz = 1.0f;
        	
        	int selectionTexture = 8;
        	
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
        		tx = -110f;
        		ty = -81f;
        		break;
        	case 2:
        		tx = -54.25f;
        		ty = -80.55f;
        		break;
        	case 3:
        		tx = 0;
        		ty = -79.9f;
        		break;
        	case 4:
        		tx = 56;
        		ty = -79.55f;
        		break;
        	case 5:
        		tx = 111;
        		ty = -79.5f;
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

