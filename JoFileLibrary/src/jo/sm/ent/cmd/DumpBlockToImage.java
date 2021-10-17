package jo.sm.ent.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jo.sm.logic.StarMadeLogic;
import jo.sm.logic.utils.ResourceUtils;
import jo.sm.resources.LegacyResources;

public class DumpBlockToImage 
{
    private static final String TEXTURE_PREFIX = "textureId=\"";
    private static final String TYPE_PREFIX = "type=\"";
    
	private String[] mArgs;
	
	public DumpBlockToImage(String[] argv)
	{
		mArgs = argv;
	}
	
	public void run()
	{
		parseArgs();
		try
		{
            BufferedReader rdr = new BufferedReader(new InputStreamReader(ResourceUtils.loadSystemResourceStream("data/config/BlockConfig.xml", LegacyResources.class)));
		    for (;;)
		    {
		        String inbuf = rdr.readLine();
		        if (inbuf == null)
		            break;
		        int off = inbuf.indexOf(TEXTURE_PREFIX);
		        if (off < 0)
		            continue;
		        inbuf = inbuf.substring(off + TEXTURE_PREFIX.length());
		        off = inbuf.indexOf("\"");
		        if (off < 0)
		            continue;
		        String texture = inbuf.substring(0, off);
		        off = inbuf.indexOf(TYPE_PREFIX);
		        if (off < 0)
		            continue;
		        inbuf = inbuf.substring(off + TYPE_PREFIX.length());
		        off = inbuf.indexOf("\"");
                if (off < 0)
                    continue;
                String type = inbuf.substring(0, off);                
		        System.out.println("BLOCK_ICON.put(BlockTypes."+type+", "+texture+");");
		    }
		    rdr.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void parseArgs()
	{
		if (mArgs.length == 0)
		{
			System.err.println("Arg1 = starmade dir");
			System.exit(0);
		}
		StarMadeLogic.setBaseDir(mArgs[0]);
	}
	
	public static void main(String[] argv)
	{
		DumpBlockToImage app = new DumpBlockToImage(argv);
		app.run();
	}
}
