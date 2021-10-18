package jo.sm.ui.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.tree.DefaultMutableTreeNode;

import jo.sm.data.Entity;
import jo.sm.data.SparseMatrix;
import jo.sm.logic.BlueprintLogic;
import jo.sm.logic.EntityLogic;
import jo.sm.logic.StarMadeLogic;
import jo.sm.logic.utils.ResourceUtils;
import jo.sm.mods.IBlocksPlugin;
import jo.sm.mods.IPluginCallback;
import jo.sm.resources.LegacyResources;
import jo.sm.ship.data.Block;
import jo.sm.ship.data.Blueprint;
import jo.sm.ship.data.Data;
import jo.sm.ship.logic.DataLogic;
import jo.sm.ship.logic.ShipLogic;
import jo.vecmath.Point3i;

public class ShipTreeLogic
{
    public static DefaultMutableTreeNode getShipTree()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        addBlueprint(root, "Blueprints", false);
        addBlueprint(root, "Default Blueprints", true);
        addEntity(root, "Your Ships", "SHIP", "Player");
        addEntity(root, "Other Ships", "SHIP", "MOB_");
        addEntity(root, "Turrets", "SHIP", "AITURRET");
        addEntity(root, "Stations", "SPACESTATION", null);
        addEntity(root, "Shops", "SHOP", null);
        addEntity(root, "Planets", "PLANET", null);
        addEntity(root, "Rocks", "FLOATINGROCK", null);
        return root;
    }

    private static void addBlueprint(DefaultMutableTreeNode root,
            String title, boolean def)
    {
        DefaultMutableTreeNode group = new DefaultMutableTreeNode(title);
        String[] options;
        if (def)
            options = BlueprintLogic.getDefaultBlueprintNames().toArray(new String[0]);
        else
            options = BlueprintLogic.getBlueprintNames().toArray(new String[0]);
        if (options.length == 0)
            return;
        for (String name : options)
        {
            ShipSpec spec = getBlueprintSpec(name, def);
            DefaultMutableTreeNode option = new DefaultMutableTreeNode(spec);
            group.add(option);            
        }
        root.add(group);
    }

	public static ShipSpec getBlueprintSpec(String name, boolean def)
	{
		ShipSpec spec = new ShipSpec();
		spec.setType(def ? ShipSpec.DEFAULT_BLUEPRINT : ShipSpec.BLUEPRINT);
		spec.setClassification(IBlocksPlugin.TYPE_SHIP);
		spec.setName(name);
		spec.setClassification(IBlocksPlugin.TYPE_SHIP);
		File bpDir = new File(StarMadeLogic.getInstance().getBaseDir(), def ? "blueprints-default" : "blueprints");
		File baseDir = new File(bpDir, name);
		spec.setFile(baseDir);
		return spec;
	}

    private static void addEntity(DefaultMutableTreeNode root,
            String title, String typeFilter, String nameFilter)
    {
        DefaultMutableTreeNode group = new DefaultMutableTreeNode(title);
        boolean addedAny = false;
        try
        {
            for (Entity e : EntityLogic.getEntities())
            {
                if ((typeFilter != null) && !e.getType().equals(typeFilter))
                    continue;
                if (nameFilter != null)
                {
                    if ("Player".equals(nameFilter))
                    {
                        if (!e.toString().startsWith("Ship "))
                            continue;
                    }
                    else if (!e.getName().startsWith(nameFilter))
                        continue;
                }
                ShipSpec spec = new ShipSpec();
                spec.setType(ShipSpec.ENTITY);
                determineClassification(spec, e);
                spec.setName(e.toString());
                spec.setEntity(e);
                DefaultMutableTreeNode option = new DefaultMutableTreeNode(spec);
                group.add(option);
                addedAny = true;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (!addedAny)
            return;
        root.add(group);
    }

    private static void determineClassification(ShipSpec spec, Entity e)
    {
        String fname = e.getFile().getName();
        if (fname.indexOf("_SHIP_") >= 0)
            spec.setClassification(IBlocksPlugin.TYPE_SHIP);
        else if (fname.indexOf("_FLOATINGROCK_") >= 0)
            spec.setClassification(IBlocksPlugin.TYPE_FLOATINGROCK);
        else if (fname.indexOf("_SHOP_") >= 0)
            spec.setClassification(IBlocksPlugin.TYPE_SHOP);
        else if (fname.indexOf("_SPACESTATION_") >= 0)
            spec.setClassification(IBlocksPlugin.TYPE_STATION);
        else if (fname.indexOf("_PLANET_") >= 0)
            spec.setClassification(IBlocksPlugin.TYPE_PLANET);
    }

    public static SparseMatrix<Block> loadShip(ShipSpec spec, IPluginCallback cb)
    {
        try
        {
            if (spec.getType() == ShipSpec.BLUEPRINT)
            {
                Blueprint blueprint = BlueprintLogic.readBlueprint(spec.getName(), cb);
                SparseMatrix<Block> grid = ShipLogic.getBlocks(blueprint.getData());
                //System.out.println("Original:");
                //HeaderLogic.dump(blueprint.getHeader());
                //LogicLogic.dump(blueprint.getLogic(), grid);
                //System.out.println("Loopback:");
                //HeaderLogic.dump(HeaderLogic.make(grid));
                //LogicLogic.dump(LogicLogic.make(grid), grid);
                return grid;
            }
            else if (spec.getType() == ShipSpec.DEFAULT_BLUEPRINT)
            {
                Blueprint blueprint = BlueprintLogic.readDefaultBlueprint(spec.getName(), cb);
                SparseMatrix<Block> grid = ShipLogic.getBlocks(blueprint.getData());
                //System.out.println("Original:");
                //HeaderLogic.dump(blueprint.getHeader());
                //LogicLogic.dump(blueprint.getLogic(), grid);
                //System.out.println("Loopback:");
                //HeaderLogic.dump(HeaderLogic.make(grid));
                //LogicLogic.dump(LogicLogic.make(grid), grid);
                return grid;
            }
            else if (spec.getType() == ShipSpec.ENTITY)
            {
                Entity e = spec.getEntity();
                EntityLogic.readEntityData(e, cb);
                //ShipLogic.dumpChunks(e.getData());
                SparseMatrix<Block> grid = ShipLogic.getBlocks(e.getData());
                e.setData(null); // conserve memory
                return grid;
            }
            else if (spec.getType() == ShipSpec.FILE)
            {
                File smb2 = spec.getFile();
                String name = smb2.getName();
                Map<Point3i, Data> data = new HashMap<Point3i, Data>();
                InputStream is = new FileInputStream(smb2);
                if (smb2.getName().endsWith(".smd2"))
                {
                    Point3i p = new Point3i();
                    Data datum = DataLogic.readFile(is, true);
                    data.put(p, datum);
                    name = name.substring(0, name.length() - 5);
                }
                else if (smb2.getName().endsWith(".sment"))
                {
                    name = null;
                    ZipInputStream zis = new ZipInputStream(new FileInputStream(smb2));
                    for (;;)
                    {
                        ZipEntry entry = zis.getNextEntry();
                        if (entry == null)
                            break;
                        String ename = entry.getName();
                        if (name == null)
                        {
                            int o = ename.indexOf('/');
                            if (o > 0)
                                name = ename.substring(0, o);
                        }
                        if (name != null)
                            if (ename.startsWith(name+"/DATA/") && ename.endsWith(".smd2"))
                            {
                                String[] parts = entry.getName().split("\\.");
                                Point3i position = new Point3i(Integer.parseInt(parts[1]),
                                        Integer.parseInt(parts[2]),
                                        Integer.parseInt(parts[3]));
                                Data datum = DataLogic.readFile(zis, false);
                                data.put(position, datum);
                            }
                    }
                    zis.close();
                }
                else
                {
                    is.close();
                    throw new IllegalArgumentException("Unsupported file type '"+smb2+"'");
                }
                SparseMatrix<Block> grid = ShipLogic.getBlocks(data);
                spec.setName(name);
                spec.setClassification(IBlocksPlugin.TYPE_SHIP); // TODO: autodetect
                StarMadeLogic.getInstance().setCurrentModel(spec);
                StarMadeLogic.setModel(grid);
                return grid;
            }
            else if (spec.getType() == ShipSpec.RESOURCE)
            {
                String name = spec.getName();
                Map<Point3i, Data> data = new HashMap<Point3i, Data>();
                InputStream is = ResourceUtils.loadSystemResourceStream(spec.getName(), LegacyResources.class);
                Point3i p = new Point3i();
                Data datum = DataLogic.readFile(is, true);
                data.put(p, datum);
                name = name.substring(0, name.length() - 5);
                SparseMatrix<Block> grid = ShipLogic.getBlocks(data);
                spec.setName(name);
                spec.setClassification(IBlocksPlugin.TYPE_SHIP); // TODO: autodetect
                StarMadeLogic.getInstance().setCurrentModel(spec);
                StarMadeLogic.setModel(grid);
                return grid;
            }
            else
                throw new IllegalArgumentException("Unknown ship type "+spec.getType());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
