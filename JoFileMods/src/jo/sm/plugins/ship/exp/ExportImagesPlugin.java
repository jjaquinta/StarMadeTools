package jo.sm.plugins.ship.exp;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import jo.sm.data.SparseMatrix;
import jo.sm.data.StarMade;
import jo.sm.logic.DraftImageLogic;
import jo.sm.mods.IBlocksPlugin;
import jo.sm.mods.IPluginCallback;
import jo.sm.ship.data.Block;

public class ExportImagesPlugin implements IBlocksPlugin
{
    public static final String NAME = "Export/Images";
    public static final String DESC = "Export Images of object";
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
        return new ExportImagesParameters();
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
        ExportImagesParameters params = (ExportImagesParameters)p;        
        try
        {
        	File directory = new File(params.getDirectory());
            DraftImageLogic.saveDrafImages(directory, 
            		params.getName(), 
            		new Dimension(params.getWidth(), params.getHeight()), original, cb);
        }
        catch (IOException e)
        {
            cb.setError(e);
        }
        return null;
    }
}
