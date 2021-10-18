package jo.sm.plugins.ship.exp;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import jo.sm.data.SparseMatrix;
import jo.sm.data.StarMade;
import jo.sm.logic.utils.FileUtils;
import jo.sm.logic.utils.ResourceUtils;
import jo.sm.mods.IBlocksPlugin;
import jo.sm.mods.IPluginCallback;
import jo.sm.ship.data.Block;
import jo.sm.ui.BlockTypeColors;
import jo.sm.ui.lwjgl.LWJGLRenderLogic;
import jo.util.jgl.obj.JGLGroup;
import jo.util.jgl.obj.tri.JGLObj;
import jo.vecmath.Point3f;
import jo.vecmath.logic.Point3fLogic;

public class ExportDAEPlugin implements IBlocksPlugin
{
    public static final String NAME = "Export/DAE";
    public static final String DESC = "Export DAE file";
    public static final String AUTH = "Jo Jaquinta";
    public static final int[][] CLASSIFICATIONS = 
        {
        { TYPE_SHIP, SUBTYPE_FILE, 26 },
        { TYPE_STATION, SUBTYPE_FILE, 26 },
        { TYPE_SHOP, SUBTYPE_FILE, 26 },
        { TYPE_FLOATINGROCK, SUBTYPE_FILE, 26 },
        { TYPE_PLANET, SUBTYPE_FILE, 26 },
        };

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getDescription()
    {
        return DESC;
    }

    @Override
    public String getAuthor()
    {
        return AUTH;
    }

    @Override
    public Object newParameterBean()
    {
        return new ExportDAEParameters();
    }
	@Override
	public void initParameterBean(SparseMatrix<Block> original, Object params,
			StarMade sm, IPluginCallback cb)
	{
	}

    @Override
    public int[][] getClassifications()
    {
        return CLASSIFICATIONS;
    }

    @Override
    public SparseMatrix<Block> modify(SparseMatrix<Block> original,
            Object p, StarMade sm, IPluginCallback cb)
    {
        ExportDAEParameters params = (ExportDAEParameters)p;        
        try
        {
        	JGLGroup quads = new JGLGroup();
        	LWJGLRenderLogic.addBlocks(quads, original, false);
            writeFile(params.getFile(), quads);
            writeTexture(params.getFile());
        }
        catch (IOException e)
        {
            cb.setError(e);
        }
        return null;
    }
    
    private static final String TEXTUREMAPFILE = "TEXTUREMAPFILE";
    private static final String VERTEXPOSITIONS = "VERTEXPOSITIONS";
    private static final String VERTEXCOUNTBY3 = "VERTEXCOUNTBY3";
    private static final String VERTEXCOUNT = "VERTEXCOUNT";
    private static final String VERTEXNORMALS = "VERTEXNORMALS";
    private static final String VERTEXUVS = "VERTEXUVS";
    private static final String VERTEXCOUNTBY2 = "VERTEXCOUNTBY2";
    private static final String POLYLISTVCOUNT = "POLYLISTVCOUNT";
    private static final String POLYCOUNT = "POLYCOUNT";
    private static final String POLYLISTVERTS = "POLYLISTVERTS";
    
    private void writeTexture(String objFile) throws IOException
    {
    	String pngFile = objFile.substring(0, objFile.length() - 4) + ".png";
    	ImageIO.write(BlockTypeColors.mAllTextures, "PNG", new File(pngFile));
    }
    
    private void writeFile(String objFile, JGLGroup quads) throws IOException
    {
    	File pngFile = new File(objFile.substring(0, objFile.length() - 4) + ".png");
        String template = ResourceUtils.loadSystemResourceString("Template.dae", ExportDAEPlugin.class);

        JGLObj obj = (JGLObj)quads.getChildren().get(0);
		int facePoints = (obj.getMode() == JGLObj.TRIANGLES) ? 3 : 4;
        int vertexCount = obj.getVertices();
        int vertexCountBy2 = vertexCount*2;
        int vertexCountBy3 = vertexCount*3;
        int polyCount = vertexCount/facePoints;
		FloatBuffer verts = obj.getVertexBuffer();
		verts.rewind();
		StringBuffer vertexPositions = new StringBuffer();
		StringBuffer vertexNormals = new StringBuffer();
		StringBuffer polyListVerts = new StringBuffer();
		for (int i = 0; i < vertexCount; i += 4)
		{
			Point3f v1 = new Point3f(verts.get(), verts.get(), verts.get());
			Point3f v2 = new Point3f(verts.get(), verts.get(), verts.get());
			Point3f v3 = new Point3f(verts.get(), verts.get(), verts.get());
			Point3f v4 = new Point3f(verts.get(), verts.get(), verts.get());
			vertexPositions.append(" "+v1.x+" "+v1.y+" "+v1.z);
			vertexPositions.append(" "+v2.x+" "+v2.y+" "+v2.z);
			vertexPositions.append(" "+v3.x+" "+v3.y+" "+v3.z);
			if (facePoints == 4)
				vertexPositions.append(" "+v4.x+" "+v4.y+" "+v4.z);
			Point3f edge1 = new Point3f(v2);
			edge1.sub(v1);
			Point3f edge2 = new Point3f(v3);
			edge2.sub(v1);
			Point3f normal = Point3fLogic.cross(edge1, edge2);
			vertexNormals.append(" "+normal.x+" "+normal.y+" "+normal.z);
			vertexNormals.append(" "+normal.x+" "+normal.y+" "+normal.z);
			vertexNormals.append(" "+normal.x+" "+normal.y+" "+normal.z);
			if (facePoints == 4)
				vertexNormals.append(" "+normal.x+" "+normal.y+" "+normal.z);
			polyListVerts.append(" "+(i+0));
			polyListVerts.append(" "+(i+1));
			polyListVerts.append(" "+(i+2));
			if (facePoints == 4)
				polyListVerts.append(" "+(i+3));
		}
		FloatBuffer texts = obj.getTexturesBuffer();
		texts.rewind();
		StringBuffer vertexUVs = new StringBuffer();
		if (texts != null)
		{
    		for (int i = 0; i < vertexCount; i++)
    		{
    			float u = texts.get();
    			float v = texts.get();
    			vertexUVs.append(" "+u+" "+v);
    		}
		}
		int faceCount = obj.getIndices();
		StringBuffer polyListVCount = new StringBuffer();
		for (int i = 0; i < faceCount; i++)
		{
			polyListVCount.append(" "+facePoints);
		}

        template = template.replace(TEXTUREMAPFILE, pngFile.getName());
        template = template.replace(VERTEXPOSITIONS, vertexPositions);
        template = template.replace(VERTEXCOUNTBY3, String.valueOf(vertexCountBy3));
        template = template.replace(VERTEXCOUNTBY2, String.valueOf(vertexCountBy2));
        template = template.replace(VERTEXCOUNT, String.valueOf(vertexCount));
        template = template.replace(VERTEXNORMALS, vertexNormals);
        template = template.replace(VERTEXUVS, vertexUVs);
        template = template.replace(POLYLISTVCOUNT, polyListVCount);
        template = template.replace(POLYCOUNT, String.valueOf(polyCount));
        template = template.replace(POLYLISTVERTS, polyListVerts);

        FileUtils.writeFile(template, new File(objFile));
    }
}
