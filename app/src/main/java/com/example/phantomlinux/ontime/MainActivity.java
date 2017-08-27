package com.example.phantomlinux.ontime;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.R.attr.format;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, Serializable {

    private ViewPager mViewPager;
    public SectionsPagerAdapter mSectionsPagerAdapter;
    public static String intakeCode;
    public Toolbar toolbar;
    public FloatingActionButton fab;
    public SwipeRefreshLayout swipeRefreshLayout;
    public TimetableModel timetableModel;
    public FragmentManager fragmentManager;
    public FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timetableModel = new TimetableModel();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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
                if (intakeCode == null || Objects.equals(intakeCode, "")) {
                    Snackbar.make(view, "Please enter your intake code.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (Tools.haveNetworkConnection(getApplicationContext())) {
                    onRefresh();
                } else {
                    Snackbar.make(view, "No internet connection.", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        if (intakeCode!=null){
            runParse();
            updateSection();
            //mViewPager.setAdapter(mSectionsPagerAdapter);
            //mSectionsPagerAdapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public void updateSection(){
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;

            case R.id.action_about:
                Intent intentA = new Intent(this, About.class);
                startActivity(intentA);
                return true;

            case R.id.filter:
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                FilterDialogFragment filterDialogFragment = new FilterDialogFragment();
                String[] abc = timetableModel.getFilterlist();
                Bundle bundle = new Bundle();
                bundle.putStringArray("filterList",abc);
                bundle.putInt("page", mViewPager.getCurrentItem());
                filterDialogFragment.setArguments(bundle);
                fragmentTransaction.add(filterDialogFragment, "filter");
                fragmentTransaction.commit();
                return true;

            case R.id.week:
                fragmentManager =getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                WeekDialogFragment weekDialogFragment = new WeekDialogFragment();
                ArrayList<String> dates = new ArrayList<>();

                try {
                    File dir = new File(Tools.getDataDir(getApplicationContext()) + "/TTFolder");
                    File[] listOfFiles = dir.listFiles();
                    for (File file : listOfFiles) {
                        if (file.isFile()) {
                            dates.add(file.getName().replaceFirst("[.][^.]+$", ""));
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                Bundle weekBundle = new Bundle();
                weekBundle.putStringArray("weekList", dates.toArray(new String[0]));
                weekDialogFragment.setArguments(weekBundle);
                fragmentTransaction.add(weekDialogFragment, "week");
                fragmentTransaction.commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void runParse(String date){
        if (date == null){
            runParse();
        }
        else {
            try {
                InputStream inputStream = new FileInputStream(new File(Tools.getDataDir(getApplicationContext())+"/TTFolder/"+date+".xml"));
                XMLParser xmlParser = new XMLParser();
                List timetable = xmlParser.parse(new InputStreamReader(inputStream));
                timetableModel = new TimetableModel(timetable);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void runParse() {

        SharedPreferences prefs = getSharedPreferences("sharedpref", MODE_PRIVATE);
        String week = prefs.getString("week", null);
        if (week == null) {
            try {
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                File dir = new File(Tools.getDataDir(getApplicationContext())+"/TTFolder");
                Date latestDate = null;
                for (int g= 0; g<dir.listFiles().length; g++){
                    try {
                        Date d= format.parse(dir.listFiles()[g].getName().replaceFirst("[.][^.]+$", ""));
                        if (latestDate == null || d.compareTo(latestDate) > 0 ) {
                            latestDate = d;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                InputStream inputStream = new FileInputStream(new File(Tools.getDataDir(getApplicationContext())+"/TTFolder/"+format.format(latestDate)+".xml"));
                XMLParser xmlParser = new XMLParser();
                List timetable = xmlParser.parse(new InputStreamReader(inputStream));
                timetableModel = new TimetableModel(timetable);

                if (timetableModel.isEmpty()) {
                    Snackbar.make(fab, "Your intake code might be wrong.", Snackbar.LENGTH_LONG).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            runParse(week);
        }
    }

    @Override
    public void onRefresh() {
        if (MainActivity.intakeCode!= null) {
            new DownloadTimetable(getApplicationContext(), this).execute();
        }
    }

}
