package com.example.phantomlinux.ontime;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import com.example.phantomlinux.ontime.Util.Logi;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
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
    public static SectionsPagerAdapter mSectionsPagerAdapter;

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
    public final static List<Timetable> monTable = new ArrayList<Timetable>();
    public final static List<Timetable> tueTable = new ArrayList<Timetable>();
    public final static List<Timetable> wedTable = new ArrayList<Timetable>();
    public final static List<Timetable> thuTable = new ArrayList<Timetable>();
    public final static List<Timetable> friTable = new ArrayList<Timetable>();
    public FragmentManager fragmentManager;
    public FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        appContext = getApplicationContext();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        swipeRefreshLayout.setRefreshing(true);
        SharedPreferences prefs = getSharedPreferences("sharedpref", MODE_PRIVATE);
        intakeCode = prefs.getString("intakecode", null);
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
        int id = item.getItemId();
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

        if (id  == R.id.filter){
            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            FilterDialogFragment filterDialogFragment = new FilterDialogFragment();
            String[] abc = getFilterList(getTimetableFromPosition(mViewPager.getCurrentItem()));
            Bundle bundle = new Bundle();
            bundle.putStringArray("filterList",abc);
            bundle.putInt("page", mViewPager.getCurrentItem());
            filterDialogFragment.setArguments(bundle);
            fragmentTransaction.add(filterDialogFragment, "filter");
            fragmentTransaction.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void runParse() {
        File dir = null;
        try {
            dir = new File(getDataDir(getApplicationContext())+"/TTFolder/timetable.xml");
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
    }

    public String getDataDir(final Context context) throws Exception {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.dataDir;
    }

    @Override
    public void onRefresh() {
        if (MainActivity.intakeCode!= null) {
            new DownloadTimetable(MainActivity.appContext, this).execute();
        }
    }

    public void updateSectionAdapter() {
        // mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager()); //dont run agn
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

    public String[] getFilterList(List<Timetable> timetable){
        ArrayList<String> tempList = new ArrayList<String>();
        String last;

        for (int x = 0; x < timetable.size(); x++) {
            last = timetable.get(x).module.substring(timetable.get(x).module.lastIndexOf("-")+1);
            if (!tempList.contains(last)){
                tempList.add(last);
            }
        }
        tempList.add(0,"Select All");
        return tempList.toArray(new String[tempList.size()]);
    }

    public List<Timetable> getTimetableFromPosition (int position){
        List<Timetable> timetableList = new ArrayList<Timetable>() {};
        switch (position) {
            case 0:
                timetableList.addAll(monTable);
                break;
            case 1:
                timetableList.addAll(tueTable);
                break;
            case 2:
                timetableList.addAll(wedTable);
                break;
            case 3:
                timetableList.addAll(thuTable);
                break;
            case 4:
                timetableList.addAll(friTable);
                break;
        }
        return timetableList;
    }

    class MyFilter implements FilenameFilter {
        @Override
        public boolean accept(final File dir, final String name) {
            return ((name.endsWith(".xml")) | (name.startsWith("a") && name.endsWith(".txt")) | (name.startsWith("a") && name.endsWith(".mp3") | (name.startsWith("a") && name.endsWith(".mp4"))));
        }
    }
}
