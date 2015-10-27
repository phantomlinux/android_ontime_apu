package com.example.phantomlinux.ontime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    public SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static String intakeCode;
    public static Context appContext;
    public Toolbar toolbar;
    public static FloatingActionButton fab;
    public SwipeRefreshLayout swipeRefreshLayout;
    public static List<Timetable> timetable = new ArrayList<Timetable>();
    public static List<Timetable> monTable = new ArrayList<Timetable>();
    public static List<Timetable> tueTable = new ArrayList<Timetable>();
    public static List<Timetable> wedTable = new ArrayList<Timetable>();
    public static List<Timetable> thuTable = new ArrayList<Timetable>();
    public static List<Timetable> friTable = new ArrayList<Timetable>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        appContext = getApplicationContext();
        //MainActivity.intakeCode = "AFCF1408";
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        swipeRefreshLayout.setRefreshing(true);
        SharedPreferences prefs = getSharedPreferences("sharedpref", MODE_PRIVATE);
        intakeCode = prefs.getString("intakecode", null);
        //Log.v("log", "Shared preference:" + intakeCode);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intakeCode == null || intakeCode == "") {
                    Snackbar.make(view, "Please enter your intake code.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (Tools.haveNetworkConnection(getApplicationContext())) {
                    onRefresh();
                    return;
                } else {
                    Snackbar.make(view, "No internet connection.", Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        });


        if (intakeCode!=null){
            runParse();
            updateSectionAdapter();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Intent intentA = new Intent(this, About.class);
            startActivity(intentA);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void runParse() {

        File dir = null;
        try {
            dir = new File(getDataDir(getApplicationContext())+"/TTFolder/timetable.xml");
            Log.v("log", "File path: " + dir.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream inputStream = new FileInputStream(dir);
            XMLParser xmlParser = new XMLParser();
            timetable = xmlParser.parse(new InputStreamReader(inputStream));
            separateTimetable(timetable);
            checkForEmpty();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void separateTimetable (List<Timetable> timetable) {
        monTable.clear();
        tueTable.clear();
        wedTable.clear();
        thuTable.clear();
        friTable.clear();
        for(int x=0; x<timetable.size();x++){
            Timetable data = timetable.get(x);
            if(data.date.startsWith("MON")){
                monTable.add(data);
            }
            if(data.date.startsWith("TUE")){
                tueTable.add(data);
            }
            if(data.date.startsWith("WED")){
                wedTable.add(data);
            }
            if(data.date.startsWith("THU")){
                thuTable.add(data);
            }
            if(data.date.startsWith("FRI")){
                friTable.add(data);
            }
        }
            /*
            Log.v(null, "Separate timetable");
            for(int y=0; y<monTable.size(); y++){
                Timetable dd = monTable.get(y);
                Log.v(null, "dd"+dd.classroom+dd.lecturer+dd.time+dd.module+dd.date);
            }
            for(int y=0; y<tueTable.size(); y++){
                Timetable dd = tueTable.get(y);
                Log.v(null, "dd"+dd.classroom+dd.lecturer+dd.time+dd.module+dd.date);
            }
            for(int y=0; y<wedTable.size(); y++){
                Timetable dd = wedTable.get(y);
                Log.v(null, "dd"+dd.classroom+dd.lecturer+dd.time+dd.module+dd.date);
            }
            for(int y=0; y<thuTable.size(); y++){
                Timetable dd = thuTable.get(y);
                Log.v(null, "dd"+dd.classroom+dd.lecturer+dd.time+dd.module+dd.date);
            }
            for(int y=0; y<friTable.size(); y++){
                Timetable dd = friTable.get(y);
                Log.v(null, "dd"+dd.classroom+dd.lecturer+dd.time+dd.module+dd.date);
            }
            */
    }

    public void printTimetable () {
        for (int i = 0 ; i < timetable.size(); i++) {
            Timetable temp = timetable.get(i);
            Log.v("log", "Date :" + temp.date+ "Time" + temp.time+ "Location" + temp.location + "Classroom" + temp.classroom + "Module" + temp.module + "Lect" + temp.lecturer );
        }
    }

    /*
    public void printMonday() {
        for (int t =0; t < monTable.size(); t++){
            Timetable temp2 = monTable.get(t);
            Log.v(null, "Date"+temp2.date+" Class"+temp2.module);
        }
    }
    */

    public String getDataDir(final Context context) throws Exception {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.dataDir;
    }

    @Override
    public void onRefresh() {
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (MainActivity.intakeCode!= null) {
            new DownloadTimetable(MainActivity.appContext, this).execute();
        }
    }

    public void updateSectionAdapter() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    public void checkForEmpty() {
        if (monTable.isEmpty()
                && tueTable.isEmpty()
                && wedTable.isEmpty()
                && thuTable.isEmpty()
                && friTable.isEmpty()){
                Snackbar.make(fab, "Your intake code might be wrong.", Snackbar.LENGTH_LONG).show();
            }
    }
    class MyFilter implements FilenameFilter {
        @Override
        //return true if find a file named "a",change this name according to your file name
        public boolean accept(final File dir, final String name) {
            return ((name.endsWith(".xml")) | (name.startsWith("a") && name.endsWith(".txt")) | (name.startsWith("a") && name.endsWith(".mp3") | (name.startsWith("a") && name.endsWith(".mp4"))));
        }
    }
}
