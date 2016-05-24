package com.miraclesnow.app;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class AssertUtils {
    public static void copyAssertApkToFileDir(Context context, String fileName) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open(fileName);
            fos = context.openFileOutput(fileName, context.MODE_WORLD_READABLE);
            byte buf[] = new byte[2048];
            int count = -1;
            while ((count = is.read(buf)) > 0) {
                fos.write(buf, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }
}
