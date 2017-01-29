package com.example.phantomlinux.ontime;

import android.content.Context;
import android.os.AsyncTask;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by phantomlinux on 10/17/2015.
 */
public class DownloadTimetable extends AsyncTask<Void, Void, Void> {
    public Context appContext;
    public Context mainContext;

    public DownloadTimetable (Context appContext, Context mainContext) {
        this.appContext = appContext;
        this.mainContext = mainContext;
        MainActivity mainActivity = (MainActivity)mainContext;
        mainActivity.swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            URL url = new URL("http://webspace.apiit.edu.my/intake-timetable/download_timetable/timetableXML.zip");
            URLConnection connection = url.openConnection();
            connection.connect();
            int lengthOfFile = connection.getContentLength();
            InputStream is = url.openStream();

            File testDirectory = null;
            try {
                testDirectory = new File(getDataDir(appContext)+"/TTFolder");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!testDirectory.exists()) {
                testDirectory.mkdir();
            }
            FileOutputStream fos = new FileOutputStream(testDirectory + "/tt.zip");
            byte data[] = new byte[1024];
            int count = 0;
            long total = 0;
            int progress = 0;
            while ((count = is.read(data)) != -1) {
                total += count;
                int progress_temp = (int) total * 100 / lengthOfFile;
                if (progress_temp % 10 == 0 && progress != progress_temp) {
                    progress = progress_temp;
                }
                fos.write(data, 0, count);
            }
            is.close();
            fos.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            unpackZip(getDataDir(appContext)+"/TTFolder/", "tt.zip");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        MainActivity mainActivity = (MainActivity) mainContext;
        mainActivity.runParse();
        mainActivity.updateSectionAdapter();
        mainActivity.swipeRefreshLayout.setRefreshing(false);
    }

    public String getDataDir(final Context context) throws Exception {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.dataDir;
    }

    private boolean unpackZip(String path, String zipname) {
        InputStream is;
        ZipInputStream zis;
        try {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                filename = "timetable.xml";
                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }
                FileOutputStream fout = new FileOutputStream(path + filename);
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }
                fout.close();
                zis.closeEntry();
            }
            zis.close();
        }
        catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
