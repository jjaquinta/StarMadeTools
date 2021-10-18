/*
 * Created on Sep 25, 2005
 *
 */
package jo.sm.logic.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IntegerUtils
{

    public static String format(int v, int w)
    {
        if (w < 0)
            return FormatUtils.rightJustify(String.valueOf(v), -w);
        else
            return FormatUtils.leftJustify(String.valueOf(v), w);
    }
    
    public static int[] dup(int[] arr)
    {
        int[] ret = new int[arr.length];
        System.arraycopy(arr, 0, ret, 0, arr.length);
        return ret;
    }

    public static int parseInt(String str)
    {
        try
        {
            if (str == null)
                return 0;
            str = str.trim();
            if (str.startsWith("+"))
                str = str.substring(1);
            int o = str.indexOf('.');
            if (o >= 0)
                str = str.substring(0, o);
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    public static int[] toArray(Object[] objArray)
    {
        if (objArray == null)
            return null;
        int[] intArray = new int[objArray.length];
        for (int i = 0; i < objArray.length; i++)
            if (objArray[i] == null)
                intArray[i] = 0;
            else if (objArray[i] instanceof Number)
                intArray[i] = ((Number)objArray[i]).intValue();
            else
                intArray[i] = parseInt(objArray[i].toString());
        return intArray;
    }

    public static Object[] toArray(int[] intArray)
    {
        if (intArray == null)
            return null;
        Integer[] objArray = new Integer[intArray.length];
        for (int i = 0; i < intArray.length; i++)
            objArray[i] = new Integer(intArray[i]);
        return objArray;
    }
    
    public static int[] fromBytes(byte[] bytes)
    {
        try
        {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            int len = ois.readInt();
            int[] ret = new int[len];
            for (int i = 0; i < len; i++)
                ret[i] = ois.readInt();
            return ret;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] toBytes(int[] ints)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeInt(ints.length);
            for (int i = 0; i < ints.length; i++)
                oos.writeInt(ints[i]);
            oos.flush();
            return baos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
