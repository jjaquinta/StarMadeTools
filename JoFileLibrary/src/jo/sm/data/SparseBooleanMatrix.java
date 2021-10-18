package jo.sm.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jo.vecmath.Point3i;
import jo.vecmath.Point3s;

public class SparseBooleanMatrix
{
    private Map<Integer,Map<Integer,Map<Integer,Long>>> mMatrix;
    
    public SparseBooleanMatrix()
    {
        mMatrix = new HashMap<Integer, Map<Integer,Map<Integer,Long>>>();
    }
    
    public SparseBooleanMatrix(SparseBooleanMatrix original)
    {
        this();
        set(original);
    }
    
    public void addAll(SparseBooleanMatrix original)
    {
        for (Iterator<Point3i> i = original.iteratorNonNull(); i.hasNext(); )
        {
            Point3i p = i.next();
            set(p, original.get(p));
        }
    }
    
    public void set(SparseBooleanMatrix original)
    {
        mMatrix.clear();
        addAll(original);
    }
    
    public void set(int x, int y, int z, Boolean val)
    {
        Map<Integer,Map<Integer,Long>> xrow = mMatrix.get(x);
        if (xrow == null)
        {
            xrow = new HashMap<Integer, Map<Integer,Long>>();
            mMatrix.put(x, xrow);
        }
        Map<Integer,Long> yrow = xrow.get(y);
        if (yrow == null)
        {
            yrow = new HashMap<Integer, Long>();
            xrow.put(y,  yrow);
        }
        Long raster = yrow.get(z/64);
        if (raster == null)
            raster = 0L;
        long mask = 1<<(Math.abs(z%64));
        if (val == Boolean.TRUE)
            raster |= mask;
        else if (val == Boolean.FALSE)
            raster &= ~mask;
        if (raster == 0L)
            yrow.remove(z/64);
        else
            yrow.put(z/64,  raster);
    }
    
    public Boolean get(int x, int y, int z)
    {
        Map<Integer,Map<Integer,Long>> xrow = mMatrix.get(x);
        if (xrow == null)
            return null;
        Map<Integer,Long> yrow = xrow.get(y);
        if (yrow == null)
            return null;
        Long raster = yrow.get(z/64);
        if (raster == null)
            return null;
        long mask = 1<<(Math.abs(z%64));
        if ((raster&mask) != 0)
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }
    
    public boolean contains(int x, int y, int z)
    {
        return get(x, y, z) != null;
    }
    
    public void set(Point3i v, Boolean val)
    {
        set(v.x, v.y, v.z, val);
    }
    
    public Boolean get(Point3i v)
    {
        return get(v.x, v.y, v.z);
    }
    
    public Boolean get(Point3s v)
    {
        return get(v.x, v.y, v.z);
    }
    
    public boolean contains(Point3i v)
    {
        return get(v.x, v.y, v.z) != null;
    }
    
    public void getBounds(Point3i lower, Point3i upper)
    {
        boolean first = true;
        for (Integer x : mMatrix.keySet())
        {
            Map<Integer,Map<Integer,Long>> xrow = mMatrix.get(x);
            for (Integer y : xrow.keySet())
            {
                Map<Integer,Long> yrow = xrow.get(y);
                for (Integer z : yrow.keySet())
                    if (contains(x, y, z))
                    {
                        if (first)
                        {
                            lower.x = x;
                            upper.x = x;
                            lower.y = y;
                            upper.y = y;
                            lower.z = z*64;
                            upper.z = z*64 + 63;
                            first = false;
                        }
                        else
                        {
                            lower.x = Math.min(lower.x, x);
                            upper.x = Math.max(upper.x, x);
                            lower.y = Math.min(lower.y, y);
                            upper.y = Math.max(upper.y, y);
                            lower.z = Math.min(lower.z, z*64);
                            upper.z = Math.max(upper.z, z*64 + 63);
                        }
                    }
            }
        }
    }
    
    public Iterator<Point3i> iterator()
    {
        Point3i lower = new Point3i();
        Point3i upper = new Point3i();
        getBounds(lower, upper);
        return new CubeIterator(lower, upper);
    }
    
    public Iterator<Point3i> iteratorNonNull()
    {
        return new NonNullIterator();
    }
    
    public int size()
    {
        int size = 0;
        for (Iterator<Point3i> i = iteratorNonNull(); i.hasNext(); i.next())
            size++;
        return size;
    }
    
    public int sizeLongs()
    {
        int size = 0;
        for (Map<Integer,Map<Integer,Long>> xrows : mMatrix.values())
            for (Map<Integer,Long> yrow : xrows.values())
                size += yrow.size();
        return size;
    }
    
    class NonNullIterator implements Iterator<Point3i>
    {
        private Iterator<Point3i> mRootIterator;
        private Point3i mNext;
        
        public NonNullIterator()
        {
            mRootIterator = iterator();
            advance();
        }
        
        private void advance()
        {
            while (mRootIterator.hasNext())
            {
                Point3i n = mRootIterator.next();
                if (contains(n))
                {
                    mNext = n;
                    return;
                }
            }
            mNext = null;
        }

        @Override
        public boolean hasNext()
        {
            return mNext != null;
        }

        @Override
        public Point3i next()
        {
            Point3i next = mNext;
            advance();
            return next;
        }

        @Override
        public void remove()
        {
        }
        
    }
}
