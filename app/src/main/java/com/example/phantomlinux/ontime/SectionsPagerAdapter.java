package com.example.phantomlinux.ontime;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.phantomlinux.ontime.Util.Logi;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phantomlinux on 10/17/2015.
 */
class SectionsPagerAdapter extends FragmentPagerAdapter {

    ArrayList<PlaceholderFragment> placeholderFragmentArrayList= new ArrayList<>();

    SectionsPagerAdapter(FragmentManager fm, Context mainContext) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position){
        Logi.v("size getItem"+placeholderFragmentArrayList.size()+"pos"+position);
        PlaceholderFragment placeholderFragment = PlaceholderFragment.newInstance(position);
        placeholderFragmentArrayList.add(position, placeholderFragment);
        return placeholderFragment;
    }

    @Override
    public int getCount() {
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

    public static class PlaceholderFragment extends Fragment{

        private RecyclerView recyclerView;
        private RecyclerView.Adapter recyclerViewAdapter;
        private RecyclerView.LayoutManager recyclerViewLayoutManager;
        public List<Event> dayOnly;

        private static final String ARG_SECTION_NUMBER = "section_number";

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
            dayOnly = new ArrayList<Event>();
            Bundle args = getArguments();
            int sectionNumber = args.getInt(ARG_SECTION_NUMBER);
            switch (sectionNumber){
                case 0:
                    dayOnly.addAll(((MainActivity)this.getActivity()).monTable);
                    break;
                case 1:
                    dayOnly.addAll(((MainActivity)this.getActivity()).tueTable);
                    break;
                case 2:
                    dayOnly.addAll(((MainActivity)this.getActivity()).wedTable);
                    break;
                case 3:
                    dayOnly.addAll(((MainActivity)this.getActivity()).thuTable);
                    break;
                case 4:
                    dayOnly.addAll(((MainActivity)this.getActivity()).friTable);
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
                    if (newState==0 || newState==2){
                        ((FloatingActionButton)getActivity().findViewById(R.id.fab)).show();
//                        MainActivity.fab.show();
                    }
                    else if (newState == 1){
                        ((FloatingActionButton)getActivity().findViewById(R.id.fab)).hide();
//                        MainActivity.fab.hide();
                    }
                    else {
                        ((FloatingActionButton)getActivity().findViewById(R.id.fab)).show();
//                        MainActivity.fab.show();
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
            return rootView;
        }

        public void filterRecyclerView(ArrayList<String> cat) {
            List<Event> temp = new ArrayList<>();
            for (int x=0; x< dayOnly.size(); x++){
                boolean remove=false;
                for (int y= 0 ; y < cat.size(); y++){
                    if (dayOnly.get(x).module.substring(dayOnly.get(x).module.lastIndexOf("-")+1).equals(cat.get(y))){
                        remove=false;
                        break;
                    } else {
                        remove=true;
                    }
                }
                if (!remove){
                    temp.add(dayOnly.get(x));
                }
            }
            recyclerViewAdapter = new TimetableAdapter(temp, getContext());
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }
}