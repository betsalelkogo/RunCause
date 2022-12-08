package com.example.runcause;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.runcause.model.Constants;
import com.example.runcause.model.Location;
import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.Run;
import com.example.runcause.model.User;
import com.example.runcause.model.adapter.PermissionCallback;
import com.example.runcause.model.intefaces.AddLocationListener;
import com.example.runcause.model.intefaces.AddProjectListener;
import com.example.runcause.model.intefaces.AddRunListener;
import com.example.runcause.service.RunService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class RunScreenFragment extends Fragment {
    MapView map;
    GoogleMap googleMap;
    TextView averageTime, distance, totalTime;
    View view;
    OnMapReadyCallback onMapReadyCallback;
    Button startRun, saveRun ;
    Timer timer;
    TimerTask timerTask;
    Project p;
    User user;
    Double time = 0.0;
    float distanceRun =0;
    boolean isTracking = false;
    static Handler handler;
    ArrayList<Location> locations;
    Run r;
    LatLng lastKnownLocation = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_run_screen, container, false);
        map = view.findViewById(R.id.mapView);
        p = RunScreenFragmentArgs.fromBundle(getArguments()).getProject();
        user = RunScreenFragmentArgs.fromBundle(getArguments()).getUser();
        averageTime = view.findViewById(R.id.tvSpeed);
        distance = view.findViewById(R.id.tvDistance);
        totalTime = view.findViewById(R.id.tvTime);
        startRun = view.findViewById(R.id.btn_close_run_detailes);
        saveRun = view.findViewById(R.id.btnSaveJourney);

        //todo: add the pause option for the timer


        timer = new Timer();
        startRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRun.setVisibility(View.VISIBLE);
                StartTimer();
                startGpsService();
            }
        });
        saveRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRunToFirestore();
            }
        });
        InitialGoogleMap(savedInstanceState);
        setLocationBroadcast();

        return view;
    }

    private void saveRunToFirestore() {
        p.setRunDistance(String.valueOf(Float.parseFloat(p.getRunDistance())+distanceRun));
        timerTask.cancel();
        r = new Run();
        r.setDate(new Date().toString());
        r.setDistance(""+distanceRun);
        r.setTime(time.toString());
        r.setProjectId(p.getId_key());
        r.setUser(user.getEmail());
        r.setId_key("run_" + System.currentTimeMillis());
        time = 0.0;
        isTracking = false;
        totalTime.setText(formatTime(0, 0, 0));

        Intent intent = new Intent(MyApplication.getContext(), RunService.class);
        intent.putExtra("stop", true);
        intent.putExtra("run_data",new Gson().toJson(r));
        ContextCompat.startForegroundService(MyApplication.getContext(), intent);
        Model.instance.addProject(p, new AddProjectListener() {
            @Override
            public void onComplete() {
                Toast.makeText(MyApplication.getContext(),"Project Updated completed",Toast.LENGTH_LONG).show();
            }
        });
        Model.instance.addRun(r, new AddRunListener() {
            @Override
            public void onComplete() {
                Model.instance.addLocation(r.getId_key(),locations, new AddLocationListener() {
                    @Override
                    public void onComplete() {
                        Location[] locations1 = new Location[locations.size()];
                        for(int i=0;i<locations1.length;i++){
                            locations1[i]=locations.get(i);
                        }
                        Toast.makeText(MyApplication.getContext(),"Save completed",Toast.LENGTH_LONG).show();
                        RunScreenFragmentDirections.ActionRunScreenFragmentToEndRunFragment action = RunScreenFragmentDirections.actionRunScreenFragmentToEndRunFragment(user, r, locations1);
                        Navigation.findNavController(view).navigate(action);

                    }
                });
            }
        });
    }

    private void startGpsService() {
        Intent intent = new Intent(MyApplication.getContext(), RunService.class);
        intent.putExtra("start", true);
        ContextCompat.startForegroundService(MyApplication.getContext(), intent);
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
                            googleMap = map;


                            map.setMyLocationEnabled(true);

                        }
                    }
                };
                map.setOnMapClickListener(latLng -> {
                    lastKnownLocation = new LatLng(latLng.latitude, latLng.longitude);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation,12F));

                });
                if (lastKnownLocation == null)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.789080,34.654600), 7.5F));
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
    private void drawLineOnMap(ArrayList<Location> locations) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (googleMap == null) return;
                List<LatLng> list = new ArrayList<>();
                for(Location l : locations){
                    list.add(new LatLng(l.getLat(),l.getLng()));
                }
                googleMap.addPolyline(new PolylineOptions()
                        .clickable(false).color(R.color.teal_200)
                        .addAll(list));
            }
        });
    }
    private void setLocationBroadcast(){
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("Locations")){
                    String data = intent.getStringExtra("Locations");
                    locations= new ArrayList<>(Arrays.asList(new Gson().fromJson(data, Location[].class)));
                    drawLineOnMap(locations);
                }
            }
        };
       MyApplication.getContext().registerReceiver(
                broadcastReceiver, new IntentFilter("GPSLocationUpdates"));
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

    private void StartTimer() {
        timerTask=new TimerTask() {

            @Override
            public void run() {
                if (handler == null){
                    handler = new Handler(Looper.getMainLooper());
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        totalTime.setText(getTimerText());
                        if(locations!=null){
                            for(int i =0;i<locations.size();i++){
                                averageTime.setText(new DecimalFormat("##.##").format(locations.get(i).getSpeed()));
                                if(i>1){
                                    int R = 6371; // km
                                    double x = (locations.get(i).getLng() - locations.get(i-1).getLng()) * Math.cos((locations.get(i-1).getLat() + locations.get(i).getLat()) / 2);
                                    double y = (locations.get(i).getLat()- locations.get(i-1).getLat());
                                    distanceRun += (Math.sqrt(x * x + y * y) * R)/10;
                                }
                                distance.setText(new DecimalFormat("##.##").format(distanceRun)+"KM");
                            }
                        }
                    }
                });

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

}