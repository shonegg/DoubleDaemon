package com.miraclesnow.app;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DaemonHelper {
    private static final String DAEMON = "snowDameon";

    public static void initDaemon(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> sysAppList = getSystemAppList();
                for (int i = 0; i < sysAppList.size(); i++) {
                    if (sysAppList.get(i).contains(DAEMON)) {
                        return;
                    }
                }
                File fileDir = context.getFilesDir();
                if (!fileDir.exists())
                    fileDir.mkdirs();
                File file = new File(fileDir, DAEMON);
                if (!file.exists())
                    AssertUtils.copyAssertApkToFileDir(context, DAEMON);
                if (file.exists()) {
                    awakeDaemon(context, DAEMON);
                }
            }
        }).start();

    }

    public static void awakeDaemon(Context context, String fileName) {

        File daemonFile = context.getFileStreamPath(fileName);
        Process process = null;
        try {
            process = new ProcessBuilder("sh").start();
            OutputStream os = process.getOutputStream();
            os.write(("chmod 755 " + daemonFile.getAbsolutePath() + "\n")
                    .getBytes());
            os.flush();

            String sdkInt = android.os.Build.VERSION.SDK_INT + "";
            String cmd = daemonFile.getAbsoluteFile() + " " + sdkInt + "\n";
            os.write(cmd.getBytes());
            os.write("exit\n".getBytes());
            os.flush();
            process.waitFor();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null)
                process.destroy();
        }
    }

    public static List<String> getSystemAppList() {
        List<String> procName = new ArrayList<String>();
        Process p;
        try {
            p = Runtime.getRuntime().exec("sh");
            OutputStream os = p.getOutputStream();
            os.write("ps\n".getBytes());
            os.write("exit\n".getBytes());
            os.flush();
            p.waitFor();
            InputStream is = p.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int count = -1;
            while ((count = is.read(buf)) > 0) {
                baos.write(buf, 0, count);
            }
            baos.close();

            String pss = baos.toString();
            String[] procs = pss.split("\n");
            if (procs == null)
                return procName;

            for (int i = 0; i < procs.length; i++) {
                String[] infos = procs[i].split("\\s+");
                String proc = infos[infos.length - 1];
                procName.add(proc);
            }

        } catch (Exception e) {
           e.printStackTrace();
        }

        return procName;
    }
}
