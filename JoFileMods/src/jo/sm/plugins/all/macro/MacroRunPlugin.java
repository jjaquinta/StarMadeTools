package jo.sm.plugins.all.macro;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jo.sm.data.SparseMatrix;
import jo.sm.data.StarMade;
import jo.sm.logic.macro.MacroLogic;
import jo.sm.mods.IBlocksPlugin;
import jo.sm.mods.IPluginCallback;
import jo.sm.ship.data.Block;

public class MacroRunPlugin implements IBlocksPlugin
{
    public static final String NAME = "Macro/Run...";
    public static final String DESC = "Run a macro from file";
    public static final String AUTH = "Jo Jaquinta";
    public static final int[][] CLASSIFICATIONS = 
        {
        { TYPE_ALL, SUBTYPE_EDIT, 90 },
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
        return new MacroRunParameters();
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
    	MacroRunParameters params = (MacroRunParameters)p;        
        return run(original, params, sm, cb);
    }

    @SuppressWarnings("unchecked")
	public static SparseMatrix<Block> run(SparseMatrix<Block> original,
			MacroRunParameters params, StarMade sm, IPluginCallback cb)
	{
		try
        {
        	Map<String,Object> props = new HashMap<String, Object>();
        	props.put("grid",  original);
        	props.put("sm",  sm);
        	props.put("cb",  cb);
        	Object ret = MacroLogic.eval(new File(params.getFile()), props);
        	if (ret instanceof SparseMatrix)
        		return (SparseMatrix<Block>)ret;
        	return null;
        }
        catch (Exception e)
        {
            cb.setError(e);
            return null;
        }
	}
}
