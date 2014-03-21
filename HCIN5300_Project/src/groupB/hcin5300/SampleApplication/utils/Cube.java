package groupB.hcin5300.SampleApplication.utils;

import java.nio.Buffer;

public class Cube extends MeshObject {
	
	private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;
    private Buffer mIndBuff;
    
    private int indicesNumber = 0;
    private int verticesNumber = 0;
	
	public Cube()
    {
        setVerts();
        setTexCoords();
        setNorms();
        setIndices();
    }
	
	private void setVerts()
    {
        double[] CUBE_VERTS = {-0.926983, -1.000000, 1.139350
        		, 1.073017, -1.000000, 1.139350
        		, -0.926983, 1.000000, 1.139350
        		, 1.073017, 1.000000, 1.139350
        		, -0.926983, 1.000000, -0.860650
        		, 1.073017, 1.000000, -0.860650
        		, -0.926983, -1.000000, -0.860650
        		, 1.073017, -1.000000, -0.860650};
        mVertBuff = fillBuffer(CUBE_VERTS);
        verticesNumber = CUBE_VERTS.length / 3;
    }
    
    
    private void setTexCoords()
    {
        double[] CUBE_TEX_COORDS = {0.375000, 0.000000
        		, 0.625000, 0.000000
        		, 0.375000, 0.250000
        		, 0.625000, 0.250000
        		, 0.375000, 0.500000
        		, 0.625000, 0.500000
        		, 0.375000, 0.750000
        		, 0.625000, 0.750000
        		, 0.375000, 1.000000
        		, 0.625000, 1.000000
        		, 0.875000, 0.000000
        		, 0.875000, 0.250000
        		, 0.125000, 0.000000
        		, 0.125000, 0.250000 };
        mTexCoordBuff = fillBuffer(CUBE_TEX_COORDS);
        
    }
    
    
    private void setNorms()
    {
        double[] CUBE_NORMS = { 0.000000, 0.000000, 1.000000
        		, 0.000000, 0.000000, 1.000000
        		, 0.000000, 0.000000, 1.000000
        		, 0.000000, 0.000000, 1.000000
        		, 0.000000, 1.000000, 0.000000
        		, 0.000000, 1.000000, 0.000000
        		, 0.000000, 1.000000, 0.000000
        		, 0.000000, 1.000000, 0.000000
        		, 0.000000, 0.000000, -1.000000
        		, 0.000000, 0.000000, -1.000000
        		, 0.000000, 0.000000, -1.000000
        		, 0.000000, 0.000000, -1.000000
        		, 0.000000, -1.000000, 0.000000
        		, 0.000000, -1.000000, 0.000000
        		, 0.000000, -1.000000, 0.000000
        		, 0.000000, -1.000000, 0.000000
        		, 1.000000, 0.000000, 0.000000
        		, 1.000000, 0.000000, 0.000000
        		, 1.000000, 0.000000, 0.000000
        		, 1.000000, 0.000000, 0.000000
        		, -1.000000, 0.000000, 0.000000
        		, -1.000000, 0.000000, 0.000000
        		, -1.000000, 0.000000, 0.000000
        		, -1.000000, 0.000000, 0.000000 };
        mNormBuff = fillBuffer(CUBE_NORMS);
    }
    
    
    private void setIndices()
    {
        short[] CUBE_INDICES = {1, 2, 3, 
        		3, 2, 4, 
        		3, 4, 5, 
        		5, 4, 6, 
        		5, 6, 7, 
        		7, 6, 8, 
        		7, 8, 1, 
        		1, 8, 2, 
        		2, 8, 4, 
        		4, 8, 6, 
        		7, 1, 5, 
        		5, 1, 3};
        zeroBased(CUBE_INDICES);
        mIndBuff = fillBuffer(CUBE_INDICES);
        indicesNumber = CUBE_INDICES.length;
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
    
}
