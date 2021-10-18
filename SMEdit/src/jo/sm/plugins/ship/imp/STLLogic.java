package jo.sm.plugins.ship.imp;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import jo.sm.mods.IPluginCallback;
import jo.vecmath.Point3f;
import jo.vecmath.ext.Hull3f;
import jo.vecmath.ext.Triangle3f;

public class STLLogic
{

	public static Hull3f readFile(String stlFileName, IPluginCallback cb) throws IOException
	{
	    Hull3f stl = new Hull3f();
        // assume binary
        DataInputStream is = new DataInputStream(new FileInputStream(stlFileName));
        byte[] header = new byte[80];
        is.read(header); // skip header
        // TODO: check for ascii
        long numTri = Integer.reverseBytes(is.readInt());
        cb.startTask((int)(numTri/256));
        for (int i = 0; i < numTri; i++)
        {
            @SuppressWarnings("unused")
            float nx = readFloat(is);
            @SuppressWarnings("unused")
            float ny = readFloat(is);
            @SuppressWarnings("unused")
            float nz = readFloat(is);
            float p1x = readFloat(is);
            float p1y = readFloat(is);
            float p1z = readFloat(is);
            float p2x = readFloat(is);
            float p2y = readFloat(is);
            float p2z = readFloat(is);
            float p3x = readFloat(is);
            float p3y = readFloat(is);
            float p3z = readFloat(is);
            is.readUnsignedShort(); // attribute
            Triangle3f tri = new Triangle3f(new Point3f(p1x, p1y, p1z), new Point3f(p2x, p2y, p2z), new Point3f(p3x, p3y, p3z));
            stl.getTriangles().add(tri);
            if (i%256 == 255)
                cb.workTask(1);
        }
        is.close();
        cb.endTask();
        return stl;
    }
    
    private static float readFloat(DataInputStream is) throws IOException
    {
        return Float.intBitsToFloat(Integer.reverseBytes(is.readInt()));
    }

}
