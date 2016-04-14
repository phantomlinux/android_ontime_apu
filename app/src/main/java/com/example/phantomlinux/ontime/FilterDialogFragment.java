package com.example.phantomlinux.ontime;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import com.example.phantomlinux.ontime.Util.Logi;

import java.util.ArrayList;

/**
 * Created by root on 11/04/16.
 */
public class FilterDialogFragment extends DialogFragment{

    private String[] filterList;
    private int page;

    public FilterDialogFragment() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        filterList = getArguments().getStringArray("filterList");
        page = getArguments().getInt("page");
        final ArrayList<String> mSelectedItems = new ArrayList<String>();  // Where we track the selected items
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Filter")
                .setMultiChoiceItems(filterList, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    if (which == 0){
                                        for (int x=1; x< filterList.length ; x++){
                                            mSelectedItems.add(filterList[x]);
                                        }
                                        ListView dialogListView = ((AlertDialog)dialog).getListView();
                                        for (int y=0; y<dialogListView.getChildCount();y++){
                                            ((CheckedTextView)dialogListView.getChildAt(y)).setChecked(true);
                                        }
                                        return;
                                    }
                                    if (!mSelectedItems.contains(filterList[which])) {
                                        mSelectedItems.add(filterList[which]);
                                    }
                                } else {
                                    if (which == 0){
                                        mSelectedItems.clear();
                                        ListView dialogListView = ((AlertDialog)dialog).getListView();
                                        for (int y=0; y<dialogListView.getChildCount();y++){
                                            ((CheckedTextView)dialogListView.getChildAt(y)).setChecked(false);
                                        }
                                        return;
                                    }
                                    mSelectedItems.remove(filterList[which]);
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mSelectedItems.size()==0){
                            dialog.dismiss();
                            return;
                        }
                        Logi.v("page"+page);
                        SectionsPagerAdapter sectionsPagerAdapter = MainActivity.mSectionsPagerAdapter;
                        sectionsPagerAdapter.placeholderFragmentArrayList.get(page).filterRecyclerView(mSelectedItems);
                        sectionsPagerAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

}
