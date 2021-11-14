package myalarm.example.locolam;

import androidx.appcompat.app.AppCompatActivity;
import android.app.NotificationManager;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;

import android.widget.TextView;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.widget.TextClock;
import android.widget.TimePicker;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;


    TimePicker alarmTime;//time picker inte object
    TextClock currentTime;// text clock inte object
    private GpsTracker gpsTracker;
    private TextView tvLatitude,tvLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // id defining
        alarmTime = findViewById(R.id.timePicker);
        currentTime = findViewById(R.id.textClock);
        tvLatitude = (TextView)findViewById(R.id.latitude);
        tvLongitude = (TextView)findViewById(R.id.longitude);

        final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));//default ring tone
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Timer t = new Timer();  // timer object t

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (currentTime.getText().toString().equals(AlarmTime()))// both sting types compare cheiyum ie current time and alarm set time
                {

                    r.play();   // ringtone run
                    addNotification();
                    long[] pattern = { 0, 100, 1000 }; // vibrator run
                    vibrator.vibrate(pattern, 0);
                }else
                {
                    r.stop();// ringtonne stop
                }
            }


        },0, 1000);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }



    public void getLocation(View view){
        gpsTracker = new GpsTracker(MainActivity.this);
        if(gpsTracker.canGetLocation())
        {
            double latitude = gpsTracker.getLatitude(); // latitude is stored in variable
            double longitude = gpsTracker.getLongitude(); // longitude is taken and given to variable
            tvLatitude.setText(String.valueOf(latitude)); // for layout
            tvLongitude.setText(String.valueOf(longitude));// same"
        }
        else
        {
            gpsTracker.showSettingsAlert();
        }
    }



    public String AlarmTime(){

        Integer alarmHours = alarmTime.getCurrentHour();
        Integer alarmMinutes = alarmTime.getCurrentMinute();
        String stringAlarmMinutes;

        if (alarmMinutes<10)
        {
            stringAlarmMinutes = "0";
            stringAlarmMinutes = stringAlarmMinutes.concat(alarmMinutes.toString());
        }
        else
        {
            stringAlarmMinutes = alarmMinutes.toString();
        }

        String stringAlarmTime;

        if(alarmHours>12)  //for checking  alarm time in 24 hr format
        {
            alarmHours = alarmHours - 12;    // 24 hour to 12 hr
            stringAlarmTime = alarmHours.toString().concat(":").concat(stringAlarmMinutes).concat(" PM"); // min conversion to string
        }
        else
        {
            stringAlarmTime = alarmHours.toString().concat(":").concat(stringAlarmMinutes).concat(" AM");  // min conversion to string
        }
        return stringAlarmTime;
    }
    //notification methord 
    private void addNotification() {
        // Builds your notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("TIME IS UP WAKE UP")
                .setContentText("Ring...ring...ring...");


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);


        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }


}

