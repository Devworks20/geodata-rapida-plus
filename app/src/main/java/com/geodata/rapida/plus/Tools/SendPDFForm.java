package com.geodata.rapida.plus.Tools;

import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import com.geodata.rapida.plus.Fragment.RESAFragment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SendPDFForm
{
    private static final String TAG = RESAFragment.class.getSimpleName();

    public String SendPDFForm(File f)
    {
        InputStream inputStream;

        String encodedFile = "", lastVal;

        try
        {
            inputStream = new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[8192];//specify the size to allow
            int bytesRead;

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();

            encodedFile = output.toString();

        }
        catch (IOException e)
        {
            Log.e(TAG, e.toString());

        }

        lastVal = encodedFile;

        return lastVal;
    }
}
