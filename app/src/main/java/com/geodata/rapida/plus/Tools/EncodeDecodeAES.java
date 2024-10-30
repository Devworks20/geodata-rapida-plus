package com.geodata.rapida.plus.Tools;

import android.util.Log;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncodeDecodeAES
{
    private static final String TAG = EncodeDecodeAES.class.getSimpleName();

    private final IvParameterSpec ivSpec;
    private final SecretKeySpec keySpec;
    private Cipher cipher;

    public EncodeDecodeAES()
    {
        ivSpec  = new IvParameterSpec(Config.IV.getBytes());
        keySpec = new SecretKeySpec(Config.SecretKey.getBytes(), "AES");

        try
        {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        }
        catch (GeneralSecurityException e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    public static String bytesToHex(byte[] data)
    {
        if (data == null)
        {
            return null;
        }

        StringBuilder str = new StringBuilder();

        for (byte datum : data)
        {
            if ((datum & 0xFF) < 16)
                str.append("0").append(Integer.toHexString(datum & 0xFF));
            else
                str.append(Integer.toHexString(datum & 0xFF));
        }
        return str.toString();
    }

    public byte[] encrypt(String text) throws Exception
    {
        if (text == null || text.length() == 0)
            throw new Exception("Empty string");

        byte[] encrypted;

        try
        {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            encrypted = cipher.doFinal(padString(text).getBytes());
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());

            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }

    private static String padString(String source)
    {
        char paddingChar = ' ';
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        StringBuilder sourceBuilder = new StringBuilder(source);

        for (int i = 0; i < padLength; i++)
        {
            sourceBuilder.append(paddingChar);
        }
        source = sourceBuilder.toString();

        return source;
    }

    public byte[] decrypt(String code) throws Exception
    {
        if (code == null || code.length() == 0)
            throw new Exception("Empty string");

        byte[] decrypted;

        try
        {
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            decrypted = cipher.doFinal(hexToBytes(code));
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());

            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }

    public static byte[] hexToBytes(String str)
    {
        if (str == null)
        {
            return null;
        }
        else if (str.length() < 2)
        {
            return null;
        }
        else
        {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];

            for (int i = 0; i < len; i++)
            {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }
}