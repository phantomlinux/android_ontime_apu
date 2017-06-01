package com.example.phantomlinux.ontime;

/**
 * Created by phantomlinux on 10/17/2015.
 */
class Event {

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
