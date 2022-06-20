package com.example.appn2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

public class EventEditActivity extends AppCompatActivity
{
    private EditText eventTitle;
    private TextView eventDateTV;
    private TextView eventTimeTV;
    private ImageView imageView;
    private Button imagePickerButton;
    private Button timePickerButton;
    int mHour, mMin;
    private LocalTime time;
    private DB db = new DB(this);
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    private Calendar calendar;
    Bitmap thumb;
    private static final int SELECT_PHOTO = 1;
    private static final int CAPTURE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        time = LocalTime.now();
        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(time));
        CalendarUtils.selectedTime = time;
        if (ContextCompat.checkSelfPermission(EventEditActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            imageView.setEnabled(false);
            ActivityCompat.requestPermissions(EventEditActivity.this, new String[]{ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },0);
        } else {
            imageView.setEnabled(true);
        }
    }

    private void initWidgets()
    {
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeTV = findViewById(R.id.eventTimeTV);
        timePickerButton = findViewById(R.id.timePicker);
        eventTitle = findViewById(R.id.eventTitle);
        imageView = findViewById(R.id.imageView);
        imagePickerButton = findViewById(R.id.imagePickerButton);
        eventTitle = findViewById(R.id.eventTitle);

        imagePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                new MaterialAlertDialogBuilder(EventEditActivity.this)
                        .setTitle("Set your image")
                        .setItems(R.array.uploadImages, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                        photoPickerIntent.setType("image/*");
                                        startActivityForResult(photoPickerIntent,SELECT_PHOTO);
                                        break;
                                    case 1:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(intent, CAPTURE_PHOTO);
                                        break;
                                    case 2:
                                        imageView.setImageResource(R.drawable.image_icon);
                                        break;
                                }
                            }

                        })
                        .show();
            }
        });

        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                TimePickerDialog timePickerDialog = new TimePickerDialog(EventEditActivity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                        time = LocalTime.of(hourOfDay, minute, 0);
                        eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(time));
                        CalendarUtils.selectedTime = time;
                    }
                }, mHour, mMin, true);
                timePickerDialog.show();
            }
        });

    }

    public void onRequestPermissionResult(int requestCode, String[] permission, int[] grantResults){
        if(requestCode == 0) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                imageView.setEnabled(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PHOTO){
            if(resultCode == RESULT_OK){
                try{
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CAPTURE_PHOTO){
            if(resultCode == RESULT_OK){
                onCaptureImageResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data){
        thumb = (Bitmap) data.getExtras().get("data");
        imageView.setMaxWidth(200);
        imageView.setImageBitmap(thumb);
    }

    private void scheduleNotification (Notification notification, Integer id) {
        Intent notificationIntent = new Intent( this, MyNotificationPublisher.class );
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID , 1 );
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION , notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast( this, id , notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT );
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE );
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() , pendingIntent);
    }

    private Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id );
        builder.setContentTitle("Aviso!");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setAutoCancel( true );
        builder.setChannelId( NOTIFICATION_CHANNEL_ID );
        return builder.build();
    }

    public void saveEventAction(View view) throws SQLException {

        String title = eventTitle.getText().toString();
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        EventEntity event = db.addOne(EventEntity.builder()
                .date(CalendarUtils.selectedDate)
                .time(CalendarUtils.selectedTime)
                .title(title)
                .image(BitmapFactory.decodeByteArray(data, 0, data.length))
                .build());

        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, event.getDate().getDayOfMonth());
        calendar.set(Calendar.MONTH, (event.getDate().getMonth().getValue() - 1));
        calendar.set(Calendar.YEAR, event.getDate().getYear());
        calendar.set(Calendar.HOUR_OF_DAY, event.getTime().getHour());
        calendar.set(Calendar.MINUTE, event.getTime().getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        LocalDateTime eventDateTime = event.getDate().atTime(event.getTime());
        if(eventDateTime.isAfter(LocalDate.now().atTime(LocalTime.now())))
        {
            scheduleNotification(getNotification(title), event.getId());
        }
        finish();
    }

    public void monthlyAction(View view)
    {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void weeklyAction(View view)
    {
        startActivity(new Intent(this, WeekViewActivity.class));
    }

}
