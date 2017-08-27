package com.example.phantomlinux.ontime;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.example.phantomlinux.ontime.Util.Logi;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by phantomlinux on 10/17/2015.
 */

class DownloadTimetable extends AsyncTask<Void, Void, Void> {
    private Context appContext;
    private Context mainContext;

    DownloadTimetable(Context appContext, Context mainContext) {
        this.appContext = appContext;
        this.mainContext = mainContext;
        MainActivity mainActivity = (MainActivity)mainContext;
        mainActivity.swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        MainActivity mainActivity = (MainActivity) mainContext;
        mainActivity.runParse();
        mainActivity.updateSection();
        mainActivity.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            //create directory
            File directory = new File(Tools.getDataDir(appContext)+"/TTFolder");
            if (!directory.exists()) {
                if (directory.mkdir()) {
                    Logi.v("TTFolder directory created.");
                }
            }

            //download timetable
            URL url = new URL("http://webspace.apiit.edu.my/intake-timetable/download_timetable/timetableXML.zip");
            URLConnection connection = url.openConnection();
            connection.connect();
            int lengthOfFile = connection.getContentLength();
            InputStream is = url.openStream();
            FileOutputStream fos = new FileOutputStream(directory + "/tt.zip");
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

            //unzip
            unpackZip(Tools.getDataDir(appContext)+"/TTFolder/", "tt.zip");


            //delete tt.zip
            File tt = new File(Tools.getDataDir(appContext)+"/TTFolder/tt.zip");
            if(tt.exists()) {
                tt.delete();
            }

            //clean dir
            cleanDir();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void cleanDir(){
        try {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
            File dir = new File(Tools.getDataDir(appContext) + "/TTFolder");
            File[] listOfFiles = dir.listFiles();
            Date latestDate = null;
            for (File file : listOfFiles) {
                if (file.isFile()) {

                    Date d= format.parse(file.getName().replaceFirst("[.][^.]+$", ""));
                    long diff = d.getTime() - new Date().getTime();
                    float days = (diff / (1000*60*60*24));

                    if (latestDate == null || d.compareTo(latestDate) > 0 ) {
                        latestDate = d;
                    }

                    if (days < -30) {
                        Logi.v("Delete:"+ file.getName());
                        File oldFile = new File(Tools.getDataDir(appContext)+"/TTFolder/"+file.getName());
                        if(oldFile.exists()) {
                            oldFile.delete();
                        }
                    }
                }
            }
            SharedPreferences.Editor editor = appContext.getSharedPreferences("sharedpref", MODE_PRIVATE).edit();
            editor.putString("week", format.format(latestDate));
            editor.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean unpackZip(String path, String zipname) {
        InputStream is;
        ZipInputStream zis;
        try {
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                //unzip dir
                if (ze.isDirectory()) {
                    File fmd = new File(path + ze.getName());
                    fmd.mkdirs();
                    continue;
                }

                //unzips files
                FileOutputStream fout = new FileOutputStream(path + ze.getName());
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
