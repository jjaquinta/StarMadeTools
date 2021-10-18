package jo.sm.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.Manifest;

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
   
    private static void discoverPlugins(String baseDir,
            List<String> blocksPlugins, List<String> pluginFactories)
    {
        List<URL> urls = new ArrayList<URL>();
        File pluginDir = new File(baseDir, "jo_plugins");
        if (pluginDir.exists())
            for (File jar : pluginDir.listFiles())
                if (jar.getName().endsWith(".jar"))
                {
                    try
                    {
                        URL url = jar.toURI().toURL();
                        urls.add(url);
                        URL jarURL = new URL("jar:"+url.toString()+"!/");
                        JarURLConnection jarConnection = (JarURLConnection)jarURL.openConnection();
                        Manifest manifest = jarConnection.getManifest();
                        String plugins = manifest.getMainAttributes().getValue("BlocksPlugins");
                        if (plugins != null)
                            for (StringTokenizer st = new StringTokenizer(plugins, ","); st.hasMoreTokens(); )
                            {
                                String plugin = st.nextToken();
                                blocksPlugins.add(plugin);
                                //System.out.println("BLock Plugin = "+plugin);
                            }
                        String factories = manifest.getMainAttributes().getValue("PluginFactories");
                        if (factories != null)
                            for (StringTokenizer st = new StringTokenizer(factories, ","); st.hasMoreTokens(); )
                            {
                                String factory = st.nextToken();
                                pluginFactories.add(factory);
                                //System.out.println("Plugin Factory = "+factory);
                            }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
        // instantiate plugins
        URLClassLoader modLoader = new URLClassLoader(urls.toArray(new URL[0]), StarMadeLogic.class.getClassLoader());
        getInstance().setModLoader(modLoader);
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
        File smJar = new File(d, "StarMade.jar");
        return smJar.exists();
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
