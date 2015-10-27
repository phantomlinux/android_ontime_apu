package com.example.phantomlinux.ontime;

/**
 * Created by phantomlinux on 10/17/2015.
 */
public class Timetable {

    public String date;
    public String time;
    public String location;
    public String classroom;
    public String module;
    public String lecturer;

    public Timetable(String date, String time, String location, String classroom, String module, String lecturer) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.classroom = classroom;
        this.module = module;
        this.lecturer = lecturer;
    }

}
