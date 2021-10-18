package jo.sm.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jo.sm.data.SparseMatrix;
import jo.sm.data.StarMade;
import jo.sm.logic.utils.BooleanUtils;
import jo.sm.mods.IBlocksPlugin;
import jo.sm.mods.IStarMadePlugin;
import jo.sm.mods.IStarMadePluginFactory;
import jo.sm.ship.data.Block;

public class StarMadeLogic
{
    public static final String	INVERT_X_AXIS	= "InvertXAxis";
	public static final String	INVERT_Y_AXIS	= "InvertYAxis";
	
	private static StarMade mStarMade;
    
    public static synchronized StarMade getInstance()
    {
        if (mStarMade == null)
        {
            mStarMade = new StarMade();
        }
        return mStarMade;
    }
    
    public static void setBaseDir(String baseDir)
    {
        File bd = new File(baseDir);
        if (!bd.exists())
            throw new IllegalArgumentException("Base directory '"+baseDir+"' does not exist");
        getInstance().setBaseDir(bd);
        
        File ntive = new File(bd, "native");
        File libs = null;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("windows") >= 0)
            libs = new File(ntive, "windows");
        else if (os.indexOf("mac") >= 0)
            libs = new File(ntive, "macosx");
        else if (os.indexOf("linux") >= 0)
            libs = new File(ntive, "linux");
        else if (os.indexOf("solaris") >= 0)
            libs = new File(ntive, "solaris");
        if (libs != null)
        {
            String path = System.getProperty("java.library.path");
            path += File.pathSeparator + libs.toString();
            System.setProperty("java.library.path", path);
            // trick from http://blog.cedarsoft.com/2010/11/setting-java-library-path-programmatically/
            try
            {
                Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
                fieldSysPath.setAccessible( true );
                fieldSysPath.set( null, null );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        List<String> blocksPlugins = new ArrayList<String>();
        List<String> pluginFactories = new ArrayList<String>();
        discoverPlugins(baseDir, blocksPlugins, pluginFactories);
        loadPlugins(blocksPlugins, pluginFactories);
    }
    
    private static void loadPlugins(List<String> blocksPlugins, List<String> pluginFactories)
    {
        for (String blocksPluginClassName : blocksPlugins)
            addBlocksPlugin(blocksPluginClassName);
        for (String pluginFactoryClassName : pluginFactories)
            addPluginFactory(pluginFactoryClassName);
    }

    public static boolean addBlocksPlugin(String blocksPluginClassName)
    {
        try
        {
            Class<?> blocksPluginClass = getInstance().getModLoader().loadClass(blocksPluginClassName);
            if (blocksPluginClass == null)
            {
                System.err.println("No such class: "+blocksPluginClassName);
                return false;
            }
            IBlocksPlugin plugin = (IBlocksPlugin)blocksPluginClass.newInstance();
            if (plugin == null)
            {
                System.err.println("Can't instantiate class: "+blocksPluginClassName);
                return false;
            }
            addBlocksPlugin(plugin);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        
    }

    public static boolean addPluginFactory(String pluginFactoryClassName)
    {
        try
        {
            Class<?> pluginFactoryClass = getInstance().getModLoader().loadClass(pluginFactoryClassName);
            if (pluginFactoryClass == null)
                return false;
            IStarMadePluginFactory factory = (IStarMadePluginFactory)pluginFactoryClass.newInstance();
            if (factory == null)
                return false;
            getInstance().getPluginFactories().add(factory);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        
    }

    public static void addBlocksPlugin(IBlocksPlugin plugin)
    {
        getInstance().getBlocksPlugins().add(plugin);
    }
    
    private static final String[] BlocksPlugins = {
            "jo.sm.factories.ship.filter.SelectSelectionFilterPlugin",
            "jo.sm.plugins.all.props.PropsPlugin",
            "jo.sm.plugins.all.macro.MacroDeletePlugin",
            "jo.sm.plugins.all.macro.MacroRecordPlugin",
            "jo.sm.plugins.all.macro.MacroRunPlugin",
            "jo.sm.plugins.planet.hollow.HollowPlugin",
            "jo.sm.plugins.planet.gen.DomePlugin",
            "jo.sm.plugins.planet.gen.GiantsCausewayPlugin",
            "jo.sm.plugins.planet.gen.UndulatingPlugin",
            "jo.sm.plugins.planet.gen.VolcanoPlugin",
            "jo.sm.plugins.planet.info.ObjectReportPlugin",
            "jo.sm.plugins.planet.info.PluginReportPlugin",
            "jo.sm.plugins.planet.select.SelectCopyPlugin",
            "jo.sm.plugins.planet.select.SelectCopyToPlugin",
            "jo.sm.plugins.planet.select.SelectCutPlugin",
            "jo.sm.plugins.planet.select.SelectDeletePlugin",
            "jo.sm.plugins.planet.select.SelectAllPlugin",
            "jo.sm.plugins.planet.select.SelectNonePlugin",
            "jo.sm.plugins.planet.select.SelectPasteFromPlugin",
            "jo.sm.plugins.planet.select.SelectPastePlugin",
            "jo.sm.plugins.planet.select.SelectSpecificPlugin",
            "jo.sm.plugins.ship.edit.HardenPlugin",
            "jo.sm.plugins.ship.edit.SmoothPlugin",
            "jo.sm.plugins.ship.edit.SoftenPlugin",
            "jo.sm.plugins.ship.exp.ExportDAEPlugin",
            "jo.sm.plugins.ship.exp.ExportImagesPlugin",
            "jo.sm.plugins.ship.exp.ExportOBJPlugin",
            "jo.sm.plugins.ship.fill.DeckPlugin",
            "jo.sm.plugins.ship.fill.FillPlugin",
            "jo.sm.plugins.ship.fill.FillBlockPlugin",
            "jo.sm.plugins.ship.hull.HullPlugin",
            "jo.sm.plugins.ship.imp.ImportBinvoxPlugin",
            "jo.sm.plugins.ship.imp.ImportOBJPlugin",
            "jo.sm.plugins.ship.imp.ImportSchematicPlugin",
            "jo.sm.plugins.ship.imp.ImportVRMLPlugin",
            "jo.sm.plugins.ship.move.MovePlugin",
            "jo.sm.plugins.ship.replace.ReplaceBlocksPlugin",
            "jo.sm.plugins.ship.replace.ReplacePlugin",
            "jo.sm.plugins.ship.reflect.DuplicatePlugin",
            "jo.sm.plugins.ship.reflect.ReflectPlugin",
            "jo.sm.plugins.ship.rotate.RotatePlugin",
            "jo.sm.plugins.ship.scale.ScalePlugin",
            "jo.sm.plugins.ship.stripes.StripesPlugin",
            "jo.sm.plugins.ship.stripes.OmbrePlugin",
            "jo.sm.plugins.ship.text.TextPlugin",
            "jo.sm.plugins.ship.text.ImagePlugin"
    };
    private static final String[] PluginFactories = {
            "jo.sm.factories.all.macro.MacroFactory",
            "jo.sm.factories.planet.comp.MaterialFactory",
            "jo.sm.factories.planet.veg.VegetationFactory",
            "jo.sm.factories.ship.filter.ViewFilterFactory"
    };

   
    private static void discoverPlugins(String baseDir,
            List<String> blocksPlugins, List<String> pluginFactories)
    {
        for (String plugin : BlocksPlugins)
        {
            blocksPlugins.add(plugin);
            //System.out.println("Block Plugin = "+plugin);
        }
        for (String factory : PluginFactories)
        {
            pluginFactories.add(factory);
            //System.out.println("Plugin Factory = "+factory);
        }
        // instantiate plugins
        getInstance().setModLoader(StarMadeLogic.class.getClassLoader());
    }
    
    public static List<IBlocksPlugin> getAllBlocksPlugins()
    {
    	List<IBlocksPlugin> plugins = new ArrayList<IBlocksPlugin>();
    	plugins.addAll(getInstance().getBlocksPlugins());
    	for (IStarMadePluginFactory factory : getInstance().getPluginFactories())
    	{
	    	IStarMadePlugin[] plugs = factory.getPlugins();
	        if (plugins == null)
	        	continue;
	        for (IStarMadePlugin plugin : plugs)
	        	if (plugin instanceof IBlocksPlugin)
	        		plugins.add((IBlocksPlugin)plugin);
    	}
    	return plugins;
    }
    
    public static List<IBlocksPlugin> getBlocksPlugins(int type, int subtype)
    {
        List<IBlocksPlugin> plugins = new ArrayList<IBlocksPlugin>();
        for (IBlocksPlugin plugin : getAllBlocksPlugins())
            if (isPlugin(plugin, type, subtype))
                plugins.add(plugin);
        return plugins;
    }
    
    private static boolean isPlugin(IStarMadePlugin plugin, int type, int subtype)
    {
        try
        {   // lets be protective against badly written plugins
            for (int[] classification : plugin.getClassifications())
                if ((type == IBlocksPlugin.TYPE_ALL) || (IBlocksPlugin.TYPE_ALL == classification[0]) || (type == classification[0]))
                    if ((subtype == -1) || (subtype == classification[1]))
                        return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isStarMadeDirectory(String dir)
    {
        File d = new File(dir);
        if (!d.exists())
            return false;
        /*
        File smJar = new File(d, "StarMade.jar");
        return smJar.exists();
        */
        return true;
    }

    public static Properties getProps()
    {
        if (getInstance().getProps() == null)
        {
            Properties p = new Properties();
            File home = new File(System.getProperty("user.home"));
            File props = new File(home, ".josm");
            if (props.exists())
            {
                try
                {
                    FileInputStream fis = new FileInputStream(props);
                    p.load(fis);
                    fis.close();
                }
                catch (Exception e)
                {
                    
                }
            }
            getInstance().setProps(p);
        }
        return getInstance().getProps();
    }

    public static void saveProps()
    {
        if (getInstance().getProps() == null)
            return;
        File home = new File(System.getProperty("user.home"));
        File props = new File(home, ".josm");
        try
        {
            FileWriter fos = new FileWriter(props);
            getInstance().getProps().store(fos, "StarMade Utils defaults");
            fos.close();
        }
        catch (Exception e)
        {
            
        }
    }

	public static String getProperty(String key)
	{
		return getProps().getProperty(key);
	}

	public static boolean isProperty(String key)
	{
		return BooleanUtils.parseBoolean(getProperty(key));
	}
	
	public static void setProperty(String key, String value)
	{
		getProps().setProperty(key, value);
		saveProps();
	}

	public static void setProperty(String key, boolean value)
	{
		setProperty(key, String.valueOf(value));
	}

	public static boolean isClassification(int class1, int class2)
	{
		return ((class1 == IBlocksPlugin.TYPE_ALL) || (class1 == class2));
	}

    public static SparseMatrix<Block> getModel()
    {
        return getInstance().getModel();
    }

    public static void setModel(SparseMatrix<Block> model)
    {
        getInstance().setModel(model);
    }
}
