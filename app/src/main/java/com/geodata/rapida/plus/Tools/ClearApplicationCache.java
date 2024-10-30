package com.geodata.rapida.plus.Tools;

import java.io.File;

public class ClearApplicationCache
{
    public static boolean clearCache(File dir)
    {
        if (dir != null && dir.isDirectory())
        {
            String[] children = dir.list();

            for (String child : children)
            {
                boolean success = clearCache(new File(dir, child));

                if (!success)
                {
                    return false;
                }
            }
            return dir.delete();
        }
        else if(dir!= null && dir.isFile())
        {
            return dir.delete();
        }
        else
        {
            return false;
        }
    }
}
