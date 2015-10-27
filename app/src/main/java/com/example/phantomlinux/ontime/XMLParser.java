package com.example.phantomlinux.ontime;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phantomlinux on 10/17/2015.
 */
public class XMLParser {

    private static final String ns = null;

    public List parse(InputStreamReader in) throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in);
            parser.nextTag();
            //Start testing
            /*
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                Log.v("log", "Parser name:" + parser.getName());
                parser.next();
            }
            */
            //Log.v("log", "Parser name:" + parser.getName());

            return readWeek(parser);
            //String test = parser.getText().toString();
            //Log.v("log", parser.toString());
        }
        finally {
            in.close();
        }
    }

    public List readWeek(XmlPullParser parser) throws XmlPullParserException, IOException {


        //Log.v("log", "Parser name:" + parser.getName());

        List week = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "weekof");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            String attrib = parser.getAttributeValue(null, "name");

            if (tag.equals("intake") && attrib.equals(MainActivity.intakeCode) ) {
                /*
                if (attrib.equals(HomeActivity.intakeCode)) {
                    Log.v("log", "if att equals intakecode");
                    Log.v("log", "Parser Name:" + parser.getName() + "Attribute" + parser.getAttributeValue(null, "name"));


                    week.add(readTimetable(parser));
                }
                else {
                    Log.v("log", "SKIP PARSER:"+parser.getName());
                    skip(parser);
                }
                */
                week = readIntake(parser);
            }
            else {
                skip(parser);
            }
        }
        return week;
    }

    public List readIntake(XmlPullParser parser) throws XmlPullParserException, IOException {
        List week = new ArrayList();
        parser.require(XmlPullParser.START_TAG, ns, "intake");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            if (tag.equals("timetable")) {

                week.add(readTimetable(parser));
            }
            else {
                //Log.v("log", "SKIP PARSER:"+parser.getName());
                skip(parser);
            }
        }
        return week;
    }

    private Timetable readTimetable(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "timetable");
        String date = null;
        String time = null;
        String location = null;
        String classroom = null;
        String module = null;
        String lecturer = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("date")) {
                date = readDate(parser);
            } else if (name.equals("time")) {
                time = readTime(parser);
            } else if (name.equals("location")) {
                location = readLocation(parser);
            } else if (name.equals("classroom")) {
                classroom = readClassroom(parser);
            } else if (name.equals("module")) {
                module = readModule(parser);
            } else if (name.equals("lecturer")) {
                lecturer = readLecturer(parser);
            }
            else {
                skip(parser);
            }
        }
        return new Timetable(date, time, location,classroom, module, lecturer);
    }


    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private String readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "date");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "date");
        //Log.v("log", "Title" + title);
        return title;
    }

    private String readTime(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "time");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "time");
        //Log.v("log", "Title" + title);
        return title;
    }

    private String readLocation(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "location");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "location");
        //Log.v("log", "Title" + title);
        return title;
    }

    private String readClassroom(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "classroom");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "classroom");
        //Log.v("log", "Title" + title);
        return title;
    }

    private String readModule(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "module");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "module");
        return title;
    }

    private String readLecturer(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lecturer");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lecturer");
        return title;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
