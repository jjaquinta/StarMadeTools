package jo.sm.plugins.planet.select;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import jo.sm.data.SparseMatrix;
import jo.sm.data.StarMade;
import jo.sm.logic.GridLogic;
import jo.sm.mods.IBlocksPlugin;
import jo.sm.mods.IPluginCallback;
import jo.sm.ship.data.Block;
import jo.vecmath.Point3i;

public class SelectCutPlugin implements IBlocksPlugin, ClipboardOwner
{
    public static final String NAME = "Cut";
    public static final String DESC = "Copy selection to clipboard and delete selection";
    public static final String AUTH = "Jo Jaquinta";
    public static final int[][] CLASSIFICATIONS = 
        {
        { TYPE_SHIP, SUBTYPE_EDIT, 12 },
        { TYPE_STATION, SUBTYPE_EDIT, 12 },
        { TYPE_SHOP, SUBTYPE_EDIT, 12 },
        { TYPE_FLOATINGROCK, SUBTYPE_EDIT, 12 },
        { TYPE_PLANET, SUBTYPE_EDIT, 12 },
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
        return null;
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
        Point3i lower = sm.getSelectedLower();
        Point3i upper = sm.getSelectedUpper();
        if ((lower != null) && (upper != null))
        {
            SparseMatrix<Block> clip = GridLogic.extract(original, lower, upper);
            String xml = GridLogic.toString(clip);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(xml), this);
            GridLogic.delete(original, lower, upper);
            return original;
        }
        return null;
    }

    @Override
    public void lostOwnership(Clipboard cb, Transferable t)
    {
    }
}
