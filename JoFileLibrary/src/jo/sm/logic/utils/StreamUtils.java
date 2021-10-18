/*
 * Created on Aug 14, 2005
 *
 */
package jo.sm.logic.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class StreamUtils
{
    public static void writeStream(byte[] data, OutputStream os) throws IOException
    {
        os.write(data);
        os.flush();
    }

    public static void writeStream(String data, OutputStream os) throws IOException
    {
        OutputStreamWriter fw = new OutputStreamWriter(os);
        fw.write(data);
        fw.flush();
    }

    public static byte[] readStream(InputStream is) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(is, baos);
        return baos.toByteArray();
    }

    public static byte[] readStream(InputStream is, int limit) throws IOException
    {
        byte[] inbuf = new byte[limit];
        is.read(inbuf);
        return inbuf;
    }

    public static String readStreamAsString(InputStream is) throws IOException
    {
        return new String(readStream(is));
    }
    
    public static void copy(InputStream is, OutputStream os) throws IOException
    {
        if (!(is instanceof BufferedInputStream))
            is = new BufferedInputStream(is);
        if (!(os instanceof BufferedOutputStream))
            os = new BufferedOutputStream(os);
        for (;;)
        {
            int ch = is.read();
            if (ch == -1)
                break;
            os.write(ch);
        }
        os.flush();
    }
    
}
