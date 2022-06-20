package com.example.appn2;

import static com.example.appn2.CalendarUtils.daysInWeekArray;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventAdapter extends ArrayAdapter<EventEntity>
{
    private Context eventContext;
    private EventAdapter adapter;
    private CalendarAdapter.OnItemListener onItemListener;

    public EventAdapter(@NonNull Context context, List<EventEntity> events)
    {
        super(context, 0, events);
        eventContext = context;
        onItemListener = (CalendarAdapter.OnItemListener) context;
        adapter = this;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {

        EventEntity event = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_cell, parent, false);

        LinearLayout reviewLayout = convertView.findViewById(R.id.reviewLayout);
        TextView eventCellTV = convertView.findViewById(R.id.eventCellTV);
        ImageView eventCellIV = convertView.findViewById(R.id.eventCellIV);
        Button delete = convertView.findViewById(R.id.btnDelete);
        Button btnOneStar = convertView.findViewById(R.id.btnOneStar);
        Button btnTwoStar = convertView.findViewById(R.id.btnTwoStar);
        Button btnThreeStar = convertView.findViewById(R.id.btnThreeStar);
        Button btnFourStar = convertView.findViewById(R.id.btnFourStar);
        Button btnFiveStar = convertView.findViewById(R.id.btnFiveStar);
        Drawable gold = getContext().getResources().getDrawable( R.drawable.goldstar );
        eventCellIV.setImageBitmap(event.getImage());
        switch(event.getReview()){
            case 5:
                btnFiveStar.setCompoundDrawablesWithIntrinsicBounds(gold,null,null,null);
            case 4:
                btnFourStar.setCompoundDrawablesWithIntrinsicBounds(gold,null,null,null);
            case 3:
                btnThreeStar.setCompoundDrawablesWithIntrinsicBounds(gold,null,null,null);
            case 2:
                btnTwoStar.setCompoundDrawablesWithIntrinsicBounds(gold,null,null,null);
            case 1:
                btnOneStar.setCompoundDrawablesWithIntrinsicBounds(gold,null,null,null);
        }


        if(event.getDate().isAfter(LocalDate.now()))
        {
            reviewLayout.setVisibility(View.GONE);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        eventCellTV.setText(event.getTitle() + " - " + event.getTime().format(formatter));

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB db = new DB(eventContext);
                db.deleteOne(event.getId());
                adapter.remove(event);
                adapter.notifyDataSetChanged();
                ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);
                List<EventEntity> weekEvents = new ArrayList<>();

                days.forEach(day -> weekEvents.addAll(db.getAllByDate(day)));

                Intent notificationIntent = new Intent( eventContext, MyNotificationPublisher.class );

                PendingIntent pendingIntent = PendingIntent.getBroadcast( eventContext, event.getId() , notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT );
                AlarmManager alarmManager = (AlarmManager) eventContext.getSystemService(Context.ALARM_SERVICE);
                assert alarmManager != null;
                alarmManager.cancel(pendingIntent);
            }
        });

        btnOneStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB db = new DB(eventContext);
                db.updateReview(event.getId(), 1);
                onItemListener.refreshView();
            }
        });
        btnTwoStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB db = new DB(eventContext);
                db.updateReview(event.getId(), 2);
                onItemListener.refreshView();
            }
        });
        btnThreeStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB db = new DB(eventContext);
                db.updateReview(event.getId(), 3);
                onItemListener.refreshView();
            }
        });
        btnFourStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB db = new DB(eventContext);
                db.updateReview(event.getId(), 4);
                onItemListener.refreshView();
            }
        });
        btnFiveStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB db = new DB(eventContext);
                db.updateReview(event.getId(), 5);
                onItemListener.refreshView();
            }
        });
        return convertView;
    }

}
