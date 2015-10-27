package com.example.phantomlinux.ontime;

/**
 * Created by phantomlinux on 10/17/2015.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return PlaceholderFragment.newInstance(position + 1);
        return PlaceholderFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        // return show how many pages
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "MON";
            case 1:
                return "TUE";
            case 2:
                return "WED";
            case 3:
                return "THU";
            case 4:
                return "FRI";
        }
        return null;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private static RecyclerView recyclerView;
        private static RecyclerView.Adapter recyclerViewAdapter;
        private static RecyclerView.LayoutManager recyclerViewLayoutManager;
        public static List<Timetable> dayOnly;

        //public static SwipeRefreshLayout refreshLayout;

        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment(){
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Bundle args = getArguments();
            int sectionNumber = args.getInt(ARG_SECTION_NUMBER);
            //Log.v(null, "Section number"+sectionNumber);
            switch (sectionNumber){
                case 0:
                    dayOnly = MainActivity.monTable;
                    break;
                case 1:
                    dayOnly = MainActivity.tueTable;
                    break;
                case 2:
                    dayOnly = MainActivity.wedTable;
                    break;
                case 3:
                    dayOnly = MainActivity.thuTable;
                    break;
                case 4:
                    dayOnly = MainActivity.friTable;
                    break;
                default:
                    break;
            }

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            //Recycler View
            recyclerView = (RecyclerView) rootView.findViewById(R.id.timetable_recycler);
            recyclerView.setHasFixedSize(true);

            //Layout Manager
            recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(recyclerViewLayoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    //Log.v(null, new Integer(newState).toString());
                    if (newState==0 || newState==2){
                        MainActivity.fab.show();
                    }
                    else if (newState == 1){
                        MainActivity.fab.hide();
                    }
                    else {
                        MainActivity.fab.show();
                    }

                }

//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    Log.v("scroll",new Integer(dy).toString());
//                    if (dy>0){
//                        MainActivity.fab.hide();
//                    }
//                    else{
//                        MainActivity.fab.show();
//                    }
//                }
            });
            // add recyclerview adapter here
            recyclerViewAdapter = new TimetableAdapter(dayOnly, getContext());
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();

            //refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
            //refreshLayout.setOnRefreshListener(this);

            return rootView;
        }

        /*
        @Override
        public void onRefresh() {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (MainActivity.intakeCode!= null) {
                new DownloadTimetable(MainActivity.appContext, getContext()).execute();
            }
            //Log.v(null, "getContext()=="+getContext());
            refreshLayout.setRefreshing(false);

        }
        */
        /*
        public static void updateRecyclerView() {
            recyclerViewAdapter = new TimetableAdapter(dayOnly, this.getContext());
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();
        }
        */
    }
}