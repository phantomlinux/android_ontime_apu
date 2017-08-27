package com.example.phantomlinux.ontime;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.CheckedTextView;
import android.widget.ListView;
import com.example.phantomlinux.ontime.Util.Logi;
import java.util.ArrayList;

/**
 * Created by phantomlinux on 11/04/16.
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
        final ArrayList<String> mSelectedItems = new ArrayList<>();  // Where we track the selected items
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Filter")
                .setMultiChoiceItems(filterList, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                ListView dialogListView = ((AlertDialog)dialog).getListView();

                                if (which == 0 || dialogListView.isItemChecked(0) ) {
                                    boolean check = dialogListView.isItemChecked(0);
                                    Logi.v(""+check);
                                    for(int i = 1; i < dialogListView.getAdapter().getCount(); i++)
                                        dialogListView.setItemChecked(i, check);

                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ListView dialogListView = ((AlertDialog)dialog).getListView();

                        //if select all
                        if (dialogListView.getCheckedItemPositions().get(0)){
                            for(int j = 1; j < filterList.length; j++) {
                                mSelectedItems.add(filterList[j]);
                            }
                        }
                        else {
                            for(int i = 1; i < dialogListView.getAdapter().getCount(); i++){
                                if (dialogListView.getCheckedItemPositions().get(i)){
                                    mSelectedItems.add(filterList[i]);
                                }
                            }
                        }
                        MainActivity mainActivity = (MainActivity) getActivity();
                        SectionsPagerAdapter sectionsPagerAdapter = mainActivity.mSectionsPagerAdapter;
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
