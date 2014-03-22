package groupB.hcin5300.SampleApplication.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;

import android.app.Activity;

public class TextPlane extends MeshObject {
	
	private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;
    private Buffer mIndBuff;
    
    private int indicesNumber = 0;
    private int verticesNumber = 0;
    
    Activity mActivity;
    private String rootD = "Planes/";
    
    public TextPlane(String planeDirectory, Activity mAc)
    {
    	mActivity = mAc;
        setVerts(rootD+planeDirectory+"/verts.txt");
        setTexCoords(rootD+planeDirectory+"/tex.txt");
        setNorms(rootD+planeDirectory+"/norms.txt");
        setIndices(rootD+planeDirectory+"/indices.txt");
    }
    
    private void setVerts(String vertsFile){
    	double[] SPHERE_VERTS = doubleReader(vertsFile, 3);
    	mVertBuff = fillBuffer(SPHERE_VERTS);
        verticesNumber = SPHERE_VERTS.length / 3;
    }
    
    private void setTexCoords(String texFile){
    	double[] SPHERE_TEX_COORDS = doubleReader(texFile, 2);
    	mTexCoordBuff = fillBuffer(SPHERE_TEX_COORDS);
    }
    
    private void setNorms(String normFile){
    	double[] SPHERE_NORMS = doubleReader(normFile, 3);
    	mNormBuff = fillBuffer(SPHERE_NORMS);  	
    }
    
    private void setIndices(String indicesFile){
    	short[] SPHERE_INDICES = shortReader(indicesFile);
        zeroBased(SPHERE_INDICES);
        mIndBuff = fillBuffer(SPHERE_INDICES);
        indicesNumber = SPHERE_INDICES.length;
    }
    
    public int getNumObjectIndex()
    {
        return indicesNumber;
    }
    
    
    @Override
    public int getNumObjectVertex()
    {
        return verticesNumber;
    }
    
    
    @Override
    public Buffer getBuffer(BUFFER_TYPE bufferType)
    {
        Buffer result = null;
        switch (bufferType)
        {
            case BUFFER_TYPE_VERTEX:
                result = mVertBuff;
                break;
            case BUFFER_TYPE_TEXTURE_COORD:
                result = mTexCoordBuff;
                break;
            case BUFFER_TYPE_NORMALS:
                result = mNormBuff;
                break;
            case BUFFER_TYPE_INDICES:
                result = mIndBuff;
            default:
                break;
        
        }
        
        return result;
    }
    
    private double[] doubleReader(String fileName, int countPerLine)
    {
		BufferedReader br = null;
		InputStream is = null;
		ArrayList<Double> getValues = new ArrayList<Double>();
		double[] valuesArray = null;
 
		try {
 
			String sCurrentLine;
 
			is = mActivity.getAssets().open(fileName);
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
 
			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				String[] ss = sCurrentLine.split("\\s+");
				
				for(int a=1; a<=countPerLine;++a)
				{
					getValues.add(Double.parseDouble(ss[a]));
				}
			}
			
			valuesArray = new double[getValues.size()];
			for(int l=0;l<valuesArray.length;++l)
			{
				valuesArray[l] = getValues.get(l);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return valuesArray;
    }
    
    private short[] shortReader(String fileName)
    {
    	BufferedReader br = null;
    	InputStream is = null;
		ArrayList<Short> getValues = new ArrayList<Short>();
		short[] valuesArray = null;
 
		try {
 
			String sCurrentLine;
			
			is = mActivity.getAssets().open(fileName);
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
 
			while ((sCurrentLine = br.readLine()) != null) {
				String[] ss = sCurrentLine.split("\\s+");

				for(int m=1;m<ss.length;++m)
				{
					String[] cc = ss[m].split("/");
					getValues.add(Short.parseShort(cc[0]));
				}
			}

			valuesArray = new short[getValues.size()];			
			for(int l=0;l<valuesArray.length;++l)
			{
				valuesArray[l] = getValues.get(l);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
				if(is != null)is.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return valuesArray;
    }
    
}
