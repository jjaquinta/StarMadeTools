package jo.util.jgl.persist;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jo.util.jgl.obj.tri.JGLObj;

public class JGLMesh
{
    private int         mMeshID;
    private JGLObj      mData;

    public JGLMesh()
    {        
        mMeshID = 0;
        mData = new JGLObj();
    }
    
    public JGLMesh(InputStream is)
    {
        this();
        List<Float> coords = new ArrayList<Float>();
        List<Short> icoords = new ArrayList<Short>();
        List<Float> ncoords = new ArrayList<Float>();
        List<Float> tcoords = new ArrayList<Float>();
        try
        {
            BufferedReader rdr = new BufferedReader(new InputStreamReader(is, "utf-8"));
            for (;;)
            {
                String line = rdr.readLine();
                if (line == null)
                    break;
                StringTokenizer st = new StringTokenizer(line, " ()\t");
                if (st.countTokens() < 1)
                    continue;
                String cmd = st.nextToken();
                if ("v".equals(cmd))
                {
                    coords.add(Float.parseFloat(st.nextToken()));
                    coords.add(Float.parseFloat(st.nextToken()));
                    coords.add(Float.parseFloat(st.nextToken()));
                }
                else if ("n".equals(cmd))
                {
                    ncoords.add(Float.parseFloat(st.nextToken()));
                    ncoords.add(Float.parseFloat(st.nextToken()));
                    ncoords.add(Float.parseFloat(st.nextToken()));
                }
                else if ("u0".equals(cmd))
                {
                    tcoords.add(Float.parseFloat(st.nextToken()));
                    tcoords.add(Float.parseFloat(st.nextToken()));
                }
                else if ("i".equals(cmd))
                {
                    short p1 = Short.parseShort(st.nextToken());
                    if (p1 == 3)
                    {
                        short p2 = Short.parseShort(st.nextToken());
                        short p3 = Short.parseShort(st.nextToken());
                        short p4 = Short.parseShort(st.nextToken());
                        icoords.add(p2);
                        icoords.add(p3);
                        icoords.add(p4);
                    }
                    else
                        System.out.println("Unknown i command '"+line+"'");
                }
//                else
//                    System.out.println("Unknown command '"+line+"'");
            }
            is.close();
        }
        catch (Exception xppe)
        {
            xppe.printStackTrace();
        }
        mData.setVertices(coords);
        mData.setTextures(tcoords);
        mData.setNormals(ncoords);
        mData.setIndices(icoords);
    }
    
    /*
    public JGLMesh(Poly3D poly)
    {
        System.out.println("Making mesh from "+poly.getPolys().size()+" polygons");
        List<Float> coords = new ArrayList<Float>();
        List<Short> icoords = new ArrayList<Short>();
        List<Float> ncoords = new ArrayList<Float>();
        List<Float> tcoords = new ArrayList<Float>();
        Set<Point3D> ps = new HashSet<Point3D>();
        for (List<Point3D> p : poly.getPolys())
            ps.addAll(p);
        List<Point3D> points = new ArrayList<Point3D>();
        points.addAll(ps);
        for (Point3D p : points)
        {
            Point3D n = p.normal();
            coords.add((float)(p.x*10));
            coords.add((float)(p.y*10));
            coords.add((float)(p.z*10));
            ncoords.add((float)n.x);
            ncoords.add((float)n.y);
            ncoords.add((float)n.z);
        }
        for (List<Point3D> p : poly.getPolys())
        {
            for (int i = 0; i < p.size() - 2; i++)
            {
                Point3D p1 = p.get(i+0);
                Point3D p2 = p.get(i+1);
                Point3D p3 = p.get(i+2);
                int i1 = points.indexOf(p1);
                int i2 = points.indexOf(p2);
                int i3 = points.indexOf(p3);
                icoords.add((short)i1);
                icoords.add((short)i2);
                icoords.add((short)i3);
            }
        }
        mData.setVertices(coords);
        mData.setTextures(tcoords);
        mData.setNormals(ncoords);
        mData.setIndices(icoords);
    }
    */
    
    public void recycle()
    {
        mData.recycle();
    }

    public JGLObj getData()
    {
        return mData;
    }

    public void setData(JGLObj data)
    {
        mData = data;
    }

    public int getMeshID()
    {
        return mMeshID;
    }

    public void setMeshID(int meshID)
    {
        mMeshID = meshID;
    }
}