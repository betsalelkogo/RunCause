package com.example.runcause.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.wineapp.MainActivity;
import com.example.wineapp.MyApplication;
import com.example.wineapp.R;
import com.example.wineapp.model.Model;
import com.example.wineapp.model.User;
import com.example.wineapp.model.adapter.PermissionCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {
    MarkerOptions[] marker;
    View view;
    User user;
    ListPostFragmentViewModel viewModel;
    MapFragmentDirections.ActionMapFragmentToUserPageFragment action1;
    MapFragmentDirections.ActionMapFragmentToListPostFragment action;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(ListPostFragmentViewModel.class);
    }
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            MainActivity.permissionCallback = new PermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(boolean isGranted) {
                    if (isGranted) {
                        return;
                    }
                }
            };
            if (ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
            }
            for(int i=0;i<viewModel.getData().getValue().size();i++){
                marker[i]=new MarkerOptions().position(new LatLng(viewModel.getData().getValue().get(i).getLant(), viewModel.getData().getValue().get(i).getLang())).title(viewModel.getData().getValue().get(i).getSubject());
                googleMap.addMarker(marker[i]);
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    marker.getTitle();
                    return false;
                }
            });
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker arg0) {
                    for(int i=0;i<viewModel.getData().getValue().size();i++){
                        if (arg0.getTitle().compareTo(viewModel.getData().getValue().get(i).getSubject())==0){
                            PostDetails(i);
                        }
                    }

                }
            });
            if(viewModel.getData().getValue().size()>0){
                LatLng latlang = new LatLng(viewModel.getData().getValue().get(0).getLant(),viewModel.getData().getValue().get(0).getLang());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlang,7.5F));
            }
        }
    };

    private void PostDetails(int i) {
        MapFragmentDirections.ActionMapFragmentToPostDetailsFragment action=MapFragmentDirections.actionMapFragmentToPostDetailsFragment(viewModel.getData().getValue().get(i));
        Navigation.findNavController(view).navigate(action);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_map, container, false);
        user= MapFragmentArgs.fromBundle(getArguments()).getUser();
        marker= new MarkerOptions[viewModel.getData().getValue().size()];
        setHasOptionsMenu(true);
        action1=MapFragmentDirections.actionMapFragmentToUserPageFragment(user);
        action=MapFragmentDirections.actionMapFragmentToListPostFragment(user);
        Model.instance.reloadPostsList();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view_gm);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_list_menu,menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean result = true;
        if (!super.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case R.id.userPage:

                    Navigation.findNavController(view).navigate(action1);
                    break;
                case R.id.post_list:

                    Navigation.findNavController(view).navigate(action);
                    break;
                default:
                    result = false;
                    break;
            }
        }
        return result;
    }
}