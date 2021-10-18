package jo.sm.plugins.planet.select;

import jo.sm.data.SparseMatrix;
import jo.sm.data.StarMade;
import jo.sm.mods.IBlocksPlugin;
import jo.sm.mods.IPluginCallback;
import jo.sm.ship.data.Block;
import jo.vecmath.Point3i;

public class SelectSpecificPlugin implements IBlocksPlugin
{
    public static final String NAME = "Select Specific";
    public static final String DESC = "Select Specific Region";
    public static final String AUTH = "Jo Jaquinta";
    public static final int[][] CLASSIFICATIONS = 
        {
        { TYPE_SHIP, SUBTYPE_EDIT, 24 },
        { TYPE_STATION, SUBTYPE_EDIT, 24 },
        { TYPE_SHOP, SUBTYPE_EDIT, 24 },
        { TYPE_FLOATINGROCK, SUBTYPE_EDIT, 24 },
        { TYPE_PLANET, SUBTYPE_EDIT, 24 },
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
        return new SelectSpecificParameters();
    }
	@Override
	public void initParameterBean(SparseMatrix<Block> original, Object params,
			StarMade sm, IPluginCallback cb)
	{
	    if ((sm.getSelectedLower() != null) && (sm.getSelectedUpper() != null))
	    {
	        Point3i lower = sm.getSelectedLower();
	        Point3i upper = sm.getSelectedUpper();
	        SelectSpecificParameters p = (SelectSpecificParameters)params;
	        p.setLowX(lower.x);
            p.setLowY(lower.y);
            p.setLowZ(lower.z);
            p.setHighX(upper.x);
            p.setHighY(upper.y);
            p.setHighZ(upper.z);
	    }
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
    	SelectSpecificParameters param = (SelectSpecificParameters)p;
        Point3i lower = new Point3i(Math.min(param.getLowX(), param.getHighX()), Math.min(param.getLowY(), param.getHighY()), Math.min(param.getLowZ(), param.getHighZ()));
        Point3i upper = new Point3i(Math.max(param.getLowX(), param.getHighX()), Math.max(param.getLowY(), param.getHighY()), Math.max(param.getLowZ(), param.getHighZ()));
        sm.setSelectedLower(lower);
        sm.setSelectedUpper(upper);
        return null;
    }
}
