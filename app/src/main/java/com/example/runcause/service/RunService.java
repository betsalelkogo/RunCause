package com.example.runcause.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.runcause.MyApplication;
import com.example.runcause.R;
import com.example.runcause.RunScreenFragmentDirections;
import com.example.runcause.UI.ListProjectFragmentViewModel;
import com.example.runcause.UI.RunLocationViewModel;
import com.example.runcause.model.Model;
import com.example.runcause.model.Run;
import com.example.runcause.model.intefaces.AddLocationListener;
import com.example.runcause.model.intefaces.AddRunListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;

public class RunService extends Service {
    String NOTIFICATION_CHANNEL_ID = "Run cause Service";
    String channelName = "Run Cause channel";
    Notification notification;
    private LocationManager mLocationManager;
    private LocationListener locationListener;
    ArrayList<com.example.runcause.model.Location> arrLocations;
    RunLocationViewModel viewModel= new RunLocationViewModel();
    @Override
    public void onCreate() {
        super.onCreate();
        //Quick notification to prevent anr
        notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build();
        startForeground(555225, notification);
        startNotification();
        arrLocations = new ArrayList<>();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("stop")) {
            String json = intent.getStringExtra("run_data");
            saveLocationsToFirestore(json);
            stopSelf();
        }
        else if (intent.hasExtra("getLocations")){
            sendMessageToActivity();
        }
        else if(intent.hasExtra("start")){
            startGpsLocation();
        }
        return START_NOT_STICKY;
    }

    private void saveLocationsToFirestore(String json) {
        viewModel.setData(arrLocations);
        Run run = new Gson().fromJson(json,Run.class);
        Model.instance.addRun(run, new AddRunListener() {
            @Override
            public void onComplete() {

                Model.instance.addLocation(run.getId_key(),arrLocations, new AddLocationListener() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(getApplicationContext(),"Save completed",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
    private void sendMessageToActivity() {
        Intent intent = new Intent("GPSLocationUpdates");
        intent.putExtra("Locations",new Gson().toJson(arrLocations));
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }
    public void startNotification() {
        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(chan);

            Intent broadcastIntent = new Intent(this, RunService.class);
            broadcastIntent.putExtra("stop", true);
            PendingIntent actionIntent = PendingIntent.getForegroundService(this,
                    3, broadcastIntent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText("")
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .addAction(R.drawable.pause, getResources().getString(R.string.turnoff), actionIntent)
                    .build();
            //Android 11
            if (Build.VERSION.SDK_INT > 29)
                startForeground(555225, notification);
            else if (Build.VERSION.SDK_INT == 29)
                startForeground(555225, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
            else
                startForeground(555225, notification);
        } else
            startForeground(555225, new Notification());
    }

    private void startGpsLocation() {

        mLocationManager = (LocationManager) MyApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location.hasAccuracy() && location.getAccuracy() < 20){
                    com.example.runcause.model.Location location1 = new com.example.runcause.model.Location();
                    location1.setLat(location.getLatitude());
                    location1.setLng(location.getLongitude());
                    location1.setSpeed(location.getSpeed());
                    arrLocations.add(location1);
                    //send the location1
                    sendMessageToActivity();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                /*if (callback != null)
                    callback.onTimeout();*/
            }
        };
        if (ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 1, 1, locationListener);

    }
}
