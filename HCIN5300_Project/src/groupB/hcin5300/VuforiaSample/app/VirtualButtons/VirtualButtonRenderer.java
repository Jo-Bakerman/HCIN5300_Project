/*==============================================================================
 Copyright (c) 2012-2013 Qualcomm Connected Experiences, Inc.
 All Rights Reserved.
 ==============================================================================*/

package groupB.hcin5300.VuforiaSample.app.VirtualButtons;

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
import groupB.hcin5300.SampleApplication.utils.LineShaders;
import groupB.hcin5300.SampleApplication.utils.MeshObject;
import groupB.hcin5300.SampleApplication.utils.RectCoords;
import groupB.hcin5300.SampleApplication.utils.SampleUtils;
import groupB.hcin5300.SampleApplication.utils.Teapot;
import groupB.hcin5300.SampleApplication.utils.Sphere;
import groupB.hcin5300.SampleApplication.utils.Texture;
import groupB.hcin5300.SampleApplication.utils.Vector3D;


public class VirtualButtonRenderer implements GLSurfaceView.Renderer
{
    private static final String LOGTAG = "VirtualButtonRenderer";
    
    private SampleApplicationSession vuforiaAppSession;
    
    public boolean mIsActive = false;
    
    private VirtualButtons mActivity;
    
    private Vector<Texture> mTextures;
    
    // virtual button coordinates
    public static final int RECTCOUNT = 7;
    public RectCoords agC = new RectCoords(21.45f, 12.35f, 35.15f, -2.25f);
    public RectCoords pbC = new RectCoords(64.25f, -3.15f, 77.55f, -16.95f);
    public RectCoords l1C = new RectCoords(-80f, -70f, -60f, -80f);
    public RectCoords l2C = new RectCoords(-50f, -70f, -30f, -80f);
    public RectCoords l3C = new RectCoords(-20f, -70f, 0f, -80f);
    public RectCoords l4C = new RectCoords(10f, -70f, 30f, -80f);
    public RectCoords l5C = new RectCoords(40f, -70f, 60f, -80f);
    
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
    
    
    public VirtualButtonRenderer(VirtualButtons activity,
        SampleApplicationSession session)
    {
        mActivity = activity;
        vuforiaAppSession = session;
         
        loadElementSpecs();
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
            float[] modelViewMatrix = Tool.convertPose2GLMatrix(
                trackableResult.getPose()).getData();
            
            // The image target specific result:
            assert (trackableResult.getType() == ImageTargetResult
                .getClassType());
            ImageTargetResult imageTargetResult = (ImageTargetResult) trackableResult;                     
            
            // Set transformations:
            float[] modelViewProjection = new float[16];
            Matrix.multiplyMM(modelViewProjection, 0, vuforiaAppSession
                .getProjectionMatrix().getData(), 0, modelViewMatrix, 0);
            
            // Set the texture used for the teapot model:
            //int textureIndex = 0;
            
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
                
                Area vbArea = button.getArea();
                assert (vbArea.getType() == Area.TYPE.RECTANGLE);
                Rectangle vbRectangle[] = new Rectangle[RECTCOUNT];

                vbRectangle[0] = new Rectangle(agC.left, agC.top, agC.right, agC.bottom);
                vbRectangle[1] = new Rectangle(pbC.left, pbC.top, pbC.right, pbC.bottom);
                vbRectangle[2] = new Rectangle(l1C.left, l1C.top, l1C.right, l1C.bottom);  
                vbRectangle[3] = new Rectangle(l2C.left, l2C.top, l2C.right, l2C.bottom);
                vbRectangle[4] = new Rectangle(l3C.left, l3C.top, l3C.right, l3C.bottom);
                vbRectangle[5] = new Rectangle(l4C.left, l4C.top, l4C.right, l4C.bottom);  
                vbRectangle[6] = new Rectangle(l5C.left, l5C.top, l5C.right, l5C.bottom);
                
                // We add the vertices to a common array in order to have one
                // single
                // draw call. This is more efficient than having multiple
                // glDrawArray calls
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
            
            Render3DModel(modelViewMatrix);
        
            SampleUtils.checkGLError("VirtualButtons renderFrame");         
        }
        
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        
        Renderer.getInstance().end();
        
    }
    
    private void Render3DModel(float[] modelViewMatrix)
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
    		
    		switch(currLevel)
    		{
    		case 1:
    			if(elementIndex == 0) // Ag
    			{
    				// first Ag object in level 1
    				meshObjects.add(AgLvl11);
    				meshTextures.add(mTextures.get(0));
    				meshTransls.add(new Vector3D(-20.0f, 0.0f, 0.0f));
    				meshScales.add(new Vector3D(3.0f, 3.0f, 3.0f));
    				
    				// second Ag object in level 1...
//    				meshObjects.add(AgLvl12);
//    				meshTextures.add(mTextures.get(2));
//    				meshTransls.add(new Vector3D(20.0f, 0.0f, 0.0f));
//    				meshScales.add(new Vector3D(6.0f, 6.0f, 6.0f));
    			}
    			else // Pb
    			{
    				// first Pb object in level 1
    				meshObjects.add(PbLvl11);
    				meshTextures.add(mTextures.get(3));
    				meshTransls.add(new Vector3D(0.0f, 0.0f, 10.0f));
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
    				meshTransls.add(new Vector3D(-10.0f, 0.0f, 0.0f));
    				meshScales.add(new Vector3D(6.0f, 6.0f, 6.0f));
    			}
    			else // Pb
    			{
    				// first Pb object in level 2
    				meshObjects.add(PbLvl21);
    				meshTextures.add(mTextures.get(4));
    				meshTransls.add(new Vector3D(0.0f, 0.0f, 5.0f));
    				meshScales.add(new Vector3D(3.0f, 3.0f, 3.0f));  	
    				
    				// second Pb object in level 2
//    				meshObjects.add(PbLvl22);
//    				meshTextures.add(mTextures.get(5));
//    				meshTransls.add(new Vector3D(0.0f, 0.0f, 0.0f));
//    				meshScales.add(new Vector3D(6.0f, 6.0f, 6.0f));
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
    		
    		// Assumptions:
            //assert (textureIndex < mTextures.size());
            //Texture thisTexture = mTextures.get(textureIndex);
    		
    		float[] modelViewScaled = modelViewMatrix;
    		
    		// render mesh objects in the current level
    		for(int i=0; i<meshObjects.size(); ++i)
    		{   			
    			Matrix.scaleM(modelViewScaled, 0, 
    					meshScales.get(i).x, meshScales.get(i).y, meshScales.get(i).z);   			
    			Matrix.translateM(modelViewMatrix, 0, 
    					meshTransls.get(i).x, meshTransls.get(i).y, meshTransls.get(i).z);
    			
    			float[] modelViewProjectionScaled = new float[16];
                Matrix.multiplyMM(modelViewProjectionScaled, 0, vuforiaAppSession
                    .getProjectionMatrix().getData(), 0, modelViewScaled, 0);
                
                // Render 3D Model
                GLES20.glUseProgram(shaderProgramID);       
                
                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                	false, 0, meshObjects.get(i).getVertices());
                GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                	false, 0, meshObjects.get(i).getNormals());
                GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                	GLES20.GL_FLOAT, false, 0, meshObjects.get(i).getTexCoords());
                
                GLES20.glEnableVertexAttribArray(vertexHandle);
                GLES20.glEnableVertexAttribArray(normalHandle);
                GLES20.glEnableVertexAttribArray(textureCoordHandle);
                
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                    meshTextures.get(i).mTextureID[0]);
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                    modelViewProjectionScaled, 0);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                	meshObjects.get(i).getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
                    meshObjects.get(i).getIndices());
//                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 
//                		meshObjects.get(i).getNumObjectVertex());
                
                GLES20.glDisableVertexAttribArray(vertexHandle);
                GLES20.glDisableVertexAttribArray(normalHandle);
                GLES20.glDisableVertexAttribArray(textureCoordHandle);   			
    			
    			Matrix.translateM(modelViewMatrix, 0, 
    					-meshTransls.get(i).x, -meshTransls.get(i).y, -meshTransls.get(i).z);
    			Matrix.scaleM(modelViewScaled, 0, 
    					-meshScales.get(i).x, -meshScales.get(i).y, -meshScales.get(i).z);
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
}
