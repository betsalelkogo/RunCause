package com.example.runcause;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.runcause.model.Constants;
import com.example.runcause.model.Project;
import com.example.runcause.model.Run;
import com.example.runcause.model.User;
import com.example.runcause.model.adapter.PermissionCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class RunScreenFragment extends Fragment {
    MapView map;
    TextView averageTime, distance,totalTime;
    View view;
    OnMapReadyCallback onMapReadyCallback;
    Button startRun,stopRun;
    Timer timer;
    TimerTask timerTask;
    Project p;
    User user;
    Double time =0.0;
    boolean timerStarted=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_run_screen, container, false);
        map=view.findViewById(R.id.mapView_run_track);
        p=RunScreenFragmentArgs.fromBundle(getArguments()).getProject();
        user=RunScreenFragmentArgs.fromBundle(getArguments()).getUser();

        averageTime=view.findViewById(R.id.average_run_tv);
        distance=view.findViewById(R.id.run_distance_tv);
        totalTime=view.findViewById(R.id.time_run_details_tv);
        startRun=view.findViewById(R.id.run_start_btn);
        stopRun=view.findViewById(R.id.run_stop_btn);
        timer=new Timer();
        startRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopTapped();
                StartTimer();
            }
        });
        stopRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTapped();
            }
        });
        InitialGoogleMap(savedInstanceState);

        return view;
    }

    private void InitialGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        }
        onMapReadyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                MainActivity.permissionCallback = new PermissionCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onResult(boolean isGranted) {
                        if (isGranted) {


                            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(p.getLant(), p.getLang()),7.5F));
                            map.setMyLocationEnabled(true);
                        }
                    }
                };
                if (ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
                }
                else
                    MainActivity.permissionCallback.onResult(true);
            }
        };

        map.onCreate(mapViewBundle);
        map.getMapAsync(onMapReadyCallback);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(Constants.MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        map.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        map.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        map.onStop();
    }


    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }
    private void startStopTapped(){
        if(timerStarted==false){
            timerStarted=true;
            startRun.setText("pause");
            startRun.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            StartTimer();
        }else
        {
            timerStarted=false;
            startRun.setText("Start");
            startRun.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
            timerTask.cancel();
        }
    }

    private void StartTimer() {
        timerTask=new TimerTask() {

            @Override
            public void run() {
                    time++;
                    totalTime.setText(getTimerText());

                }
            };
        timer.scheduleAtFixedRate(timerTask,0,1000);
        }
    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }


    public void resetTapped()
    {
        AlertDialog.Builder resetAlert = new AlertDialog.Builder(getContext());
        resetAlert.setTitle("End Activity");
        resetAlert.setMessage("Are you sure you want to Stop your activity?");
        resetAlert.setPositiveButton("Stop", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(timerTask != null)
                {
                    timerTask.cancel();
                    Run r=new Run();
                    r.setDate(new Date().toString());
                    r.setDistance(distance.toString());
                    r.setTime(time.toString());
                    r.setProjectId(p.getId_key());
                    setButtonUI("start", R.color.green);
                    time = 0.0;
                    timerStarted = false;
                    totalTime.setText(formatTime(0,0,0));
                    RunScreenFragmentDirections.ActionRunScreenFragmentToEndRunFragment action=RunScreenFragmentDirections.actionRunScreenFragmentToEndRunFragment(user,r);
                    Navigation.findNavController(view).navigate(action);
                }
            }
        });

        resetAlert.setNeutralButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //do nothing
            }
        });

        resetAlert.show();

    }
    private void setButtonUI(String start, int color)
    {
        startRun.setText(start);
        startRun.setTextColor(ContextCompat.getColor(getContext(), color));
    }
}