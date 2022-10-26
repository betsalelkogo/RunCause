package com.example.runcause.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.wineapp.MainActivity;
import com.example.wineapp.MyApplication;
import com.example.wineapp.R;
import com.example.wineapp.model.Constants;
import com.example.wineapp.model.Post;
import com.example.wineapp.model.adapter.PermissionCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class PostDetailsFragment extends Fragment{
    Post p;
    TextView subjectEt, details;
    ImageView photo;
    MapView map;
    ProgressBar progressBar;
    OnMapReadyCallback onMapReadyCallback;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_details, container, false);
        p= PostDetailsFragmentArgs.fromBundle(getArguments()).getPost();
        subjectEt=view.findViewById(R.id.post_details_cave_name_tv);
        details=view.findViewById(R.id.post_detail_tv);
        photo=view.findViewById(R.id.post_detail_wineryPicture);
        map=view.findViewById(R.id.post_details_map);
        progressBar=view.findViewById(R.id.post_details_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        if (p != null){
            updateDisplay();
        }
        setHasOptionsMenu(true);
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
                            MarkerOptions marker=new MarkerOptions().position(new LatLng(p.getLant(), p.getLang())).title(p.getSubject());
                            map.addMarker(marker);
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(p.getLant(), p.getLang()),7.5F));
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
    private void updateDisplay() {
        progressBar.setVisibility(View.GONE);
        subjectEt.setText(p.getSubject());
        details.setText(p.getDetails());
        photo.setImageResource(R.drawable.win);
        if(p.getImageUrl()!=null){
            Picasso.get().load(p.getImageUrl()).into(photo);
        }
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
}