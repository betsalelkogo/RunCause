package com.example.runcause;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.runcause.model.Constants;
import com.example.runcause.model.Location;
import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.Run;
import com.example.runcause.model.User;
import com.example.runcause.model.adapter.PermissionCallback;
import com.example.runcause.model.intefaces.GetLocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class EndRunFragment extends Fragment {
    MapView map;
    GoogleMap googleMap;
    TextView averageTime, distance, totalTime;
    View view;
    Button closeBtn;
    Run r;
    User user;
    OnMapReadyCallback onMapReadyCallback;
    static Handler handler;
    ArrayList<Location> locations;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_end_run, container, false);
        user=EndRunFragmentArgs.fromBundle(getArguments()).getUser();
        r=EndRunFragmentArgs.fromBundle(getArguments()).getRun();
        closeBtn=view.findViewById(R.id.btn_close_run_detailes);
        averageTime = view.findViewById(R.id.tvSpeed);
        distance = view.findViewById(R.id.tvDistance);
        totalTime = view.findViewById(R.id.tvTime);
        map = view.findViewById(R.id.mapView);
        Model.instance.getLocationById(r.getId_key(), new GetLocationListener() {
            @Override
            public void onComplete(ArrayList<Location> arrLocation) {
                locations=arrLocation;
            }
        });
        InitialGoogleMap(savedInstanceState);
        drawRunOnMap((ArrayList<Location>) locations);
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
                            googleMap = map;

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.12354,35.546412),15F));

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
    private void drawRunOnMap(ArrayList<Location> locations) {
        if (handler == null){
            handler = new Handler(Looper.getMainLooper());
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (googleMap == null) return;
                List<LatLng> list = new ArrayList<>();
                for(Location l : locations){
                    list.add(new LatLng(l.getLat(),l.getLng()));
                }
                googleMap.addPolyline(new PolylineOptions()
                        .clickable(false).color(R.color.purple_500)
                        .addAll(list));
            }
        });
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
    private String getTimerText()
    {
        int rounded = (int) Math.round(Float.parseFloat(r.getTime()));

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