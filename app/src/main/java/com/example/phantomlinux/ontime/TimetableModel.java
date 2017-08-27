package com.example.phantomlinux.ontime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phantomlinux on 01/06/2017.
 */

class TimetableModel {

    public ArrayList<Event> monTable = new ArrayList<>();
    public ArrayList<Event> tueTable = new ArrayList<>();
    public ArrayList<Event> wedTable = new ArrayList<>();
    public ArrayList<Event> thuTable = new ArrayList<>();
    public ArrayList<Event> friTable = new ArrayList<>();
    public ArrayList<String> filterList = new ArrayList<>();

    public TimetableModel() {
        super();
    }

    public TimetableModel(List timetable) {
        super();
        for(int x=0; x<timetable.size();x++){
            Event data = (Event)timetable.get(x);

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

            String last = data.module.substring(data.module.lastIndexOf("-")+1);
            if (!filterList.contains(last)){
                filterList.add(last);
            }
        }

        filterList.add(0,"Select All");

    }

    public String[] getFilterlist() {
        return filterList.toArray(new String[filterList.size()]);
    }


    public boolean isEmpty() {
        return monTable.isEmpty() &&
                tueTable.isEmpty() &&
                wedTable.isEmpty() &&
                thuTable.isEmpty() &&
                friTable.isEmpty();
    }

    static class Event {
        String date;
        String time;
        String location;
        String classroom;
        String module;
        String lecturer;

        Event(String date, String time, String location, String classroom, String module, String lecturer) {
            this.date = date;
            this.time = time;
            this.location = location;
            this.classroom = classroom;
            this.module = module;
            this.lecturer = lecturer;
        }
    }
}
