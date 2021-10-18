package jo.util.lwjgl.win;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jo.sm.logic.utils.IntegerUtils;

public class JGLTextureCache
{
    private static final Map<Integer, JGLTextureSpec> mSpecCache = new HashMap<Integer, JGLTextureSpec>();
    private static final Map<Integer, Integer> mLoadedCache = new HashMap<Integer, Integer>();
    private static Set<Integer> mMRULoaded = new HashSet<Integer>();
    
    public static void register(int id, JGLTextureSpec spec)
    {
        mSpecCache.put(id, spec);
    }
    
    public static void register(int id, String fileName)
    {
        JGLTextureSpec spec = new JGLTextureSpec();
        spec.setFileName(fileName);
        register(id, spec);
    }

    public static void register(int id, BufferedImage img)
    {
        JGLTextureSpec spec = new JGLTextureSpec();
        spec.setImage(img);
        register(id, spec);
    }
    
    public static void register(int id, String fileName, int left, int top, int width, int height)
    {
        JGLTextureSpec spec = new JGLTextureSpec();
        spec.setFileName(fileName);;
        spec.setLeft(left);;
        spec.setTop(top);;
        spec.setWidth(width);;
        spec.setHeight(height);;
        register(id, spec);
    }
    
    public static boolean isRegistered(int id)
    {
        return mSpecCache.containsKey(id);
    }

    public static void markTextures()
    {
        synchronized (mMRULoaded)
        {
            mMRULoaded.clear();
        }
    }
    
    public static void useTexture(int textureID)
    {
        synchronized (mMRULoaded)
        {
            if (textureID == 0)
                return;
            mMRULoaded.add(textureID);
        }
    }

    public static int[] getUsedTextures()
    {
        synchronized (mMRULoaded)
        {
            return IntegerUtils.toArray(mMRULoaded.toArray());
        }
    }
    
    public static int[] getUnUsedTextures()
    {
        Set<Integer> unused = new HashSet<Integer>();
        synchronized (mMRULoaded)
        {
            unused.addAll(mLoadedCache.keySet());
            unused.removeAll(mMRULoaded);
        }
        return IntegerUtils.toArray(unused.toArray());
    }

    public static class JGLTextureSpec
    {
        private String  mFileName;
        private int     mLeft;
        private int     mTop;
        private int     mWidth;
        private int     mHeight;
        private BufferedImage   mImage;
        
        public String getFileName()
        {
            return mFileName;
        }
        public void setFileName(String fileName)
        {
            mFileName = fileName;
        }
        public int getLeft()
        {
            return mLeft;
        }
        public void setLeft(int left)
        {
            mLeft = left;
        }
        public int getTop()
        {
            return mTop;
        }
        public void setTop(int top)
        {
            mTop = top;
        }
        public int getWidth()
        {
            return mWidth;
        }
        public void setWidth(int width)
        {
            mWidth = width;
        }
        public int getHeight()
        {
            return mHeight;
        }
        public void setHeight(int height)
        {
            mHeight = height;
        }
        public BufferedImage getImage()
        {
            return mImage;
        }
        public void setImage(BufferedImage image)
        {
            mImage = image;
        }
    }

    public static class Texture {
        private ByteBuffer pixels;
        private int width;
        private int height;

        public Texture(ByteBuffer pixels, int width, int height) {
            this.height = height;
            this.pixels = pixels;
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public ByteBuffer getPixels() {
            return pixels;
        }

        public int getWidth() {
            return width;
        }
    }}

