package com.example.phantomlinux.ontime;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by phantomlinux on 10/17/2015.
 */

public class TimetableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TimetableModel.Event> a = new ArrayList<>();
    public Context context;

    public TimetableAdapter(List<TimetableModel.Event> a, Context context) {
        super();
        this.a.addAll(a);
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0){
            View vFirstcard = LayoutInflater.from(parent.getContext()).inflate(R.layout.firstcard_view, parent, false);
            FirstCard vhFirstCard = new FirstCard(vFirstcard);
            return vhFirstCard;
        }
        else if (viewType == 2){
            View vMessageOnly = LayoutInflater.from(parent.getContext()).inflate(R.layout.messageonlycard_view, parent, false);
            MessageOnlyCard vhMessageOnly = new MessageOnlyCard(vMessageOnly);
            return vhMessageOnly;
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
            ViewHolder vh = new ViewHolder(v, this.context);
            return vh;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (a.size()>0){
            if (position == 0){
                return 0;
            }
            return 1;
        }
        else {
            return 2;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case 0:
                FirstCard firstCard= (FirstCard)holder;
                TimetableModel.Event firstCardData = a.get(0);
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date date = df.parse(firstCardData.date.substring(4));
                    DateFormat dfWithShortMonth = new SimpleDateFormat("dd MMMM");
                    firstCard.txtDate.setText(dfWithShortMonth.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(firstCardData.date.startsWith("MON")){
                    firstCard.txtDay.setText("MONDAY");
                }
                if(firstCardData.date.startsWith("TUE")){
                    firstCard.txtDay.setText("TUESDAY");
                }
                if(firstCardData.date.startsWith("WED")){
                    firstCard.txtDay.setText("WEDNESDAY");
                }
                if(firstCardData.date.startsWith("THU")){
                    firstCard.txtDay.setText("THURSDAY");
                }
                if(firstCardData.date.startsWith("FRI")){
                    firstCard.txtDay.setText("FRIDAY");
                }
                if (a.size() == 1){
                    firstCard.txtMessage.setText("You have "+a.size()+" class this day.");
                }
                else {
                    firstCard.txtMessage.setText("You have "+a.size()+" classes this day.");
                }
                break;
            case 1:
                ViewHolder viewHolder = (ViewHolder)holder;
                TimetableModel.Event data = a.get(position-1);
                viewHolder.txtLecturer.setText(data.lecturer);
                viewHolder.txtModule.setText(data.module);
                viewHolder.txtLocation.setText(data.location+" "+data.classroom);
                viewHolder.txtTime.setText(data.time);
                break;
            case 2:
                MessageOnlyCard messageOnlyCard = (MessageOnlyCard) holder;
                messageOnlyCard.txtMessage.setText("You have no classes this day.");
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (a.size()==0){
            return 1;
        }
        else {
            return a.size()+1;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView txtTime;
        public TextView txtLocation;
        public TextView txtModule;
        public TextView txtLecturer;
        public CardView cardView;
        public Context context;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtLocation = (TextView) itemView.findViewById(R.id.txtLocation);
            txtModule = (TextView) itemView.findViewById(R.id.txtModule);
            txtLecturer = (TextView) itemView.findViewById(R.id.txtLecturer);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "Time: " + txtTime.getText() +
                            "\nLocation: " + txtLocation.getText() +
                            "\nModule: " + txtModule.getText() +
                            "\nLect: " + txtLecturer.getText());
            sendIntent.setType("text/plain");
            context.startActivity(sendIntent);
            return false;
        }
    }

    public class FirstCard extends RecyclerView.ViewHolder {
        public TextView txtDate;
        public TextView txtDay;
        public TextView txtMessage;

        public FirstCard(View itemView) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.date);
            txtDay = (TextView) itemView.findViewById(R.id.day);
            txtMessage = (TextView) itemView.findViewById(R.id.firstcard_message);
        }
    }

    public class MessageOnlyCard extends RecyclerView.ViewHolder {
        public TextView txtMessage;

        public MessageOnlyCard(View itemView) {
            super(itemView);
            txtMessage = (TextView) itemView.findViewById(R.id.message);
        }
    }
}
