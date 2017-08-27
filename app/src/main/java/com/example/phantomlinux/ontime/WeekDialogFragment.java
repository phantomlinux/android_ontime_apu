package com.example.phantomlinux.ontime;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ListView;
import com.example.phantomlinux.ontime.Util.Logi;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by phantomlinux on 26/08/2017.
 */

public class WeekDialogFragment extends DialogFragment {

    private String[] weekList;


    public WeekDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        weekList = getArguments().getStringArray("weekList");

        //Get saved week timetable
        SharedPreferences prefs = getActivity().getSharedPreferences("sharedpref", MODE_PRIVATE);
        String week = prefs.getString("week", null);
        int selectedWeek = 0;
        if (!Objects.equals(week, "")) {
            for (int x=0; x<weekList.length; x++){
                if (Objects.equals(week, weekList[x])) {
                    selectedWeek = x;
                }
            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Week of ...")
                .setSingleChoiceItems(weekList, selectedWeek, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setPositiveButton("Ok", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ListView dialogListView = ((AlertDialog)dialogInterface).getListView();
                Object checkedItem = dialogListView.getAdapter().getItem(dialogListView.getCheckedItemPosition());
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.runParse(checkedItem.toString());
                mainActivity.updateSection();
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("sharedpref", MODE_PRIVATE).edit();
                editor.putString("week", checkedItem.toString());
                editor.commit();
            }
        }).setNegativeButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;

    }
}
