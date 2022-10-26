package com.example.runcause.UI;

import static android.app.Activity.RESULT_OK;
import static com.example.wineapp.UI.UserPageFragment.getPickImageIntent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.wineapp.MainActivity;
import com.example.wineapp.MyApplication;
import com.example.wineapp.R;
import com.example.wineapp.model.Constants;
import com.example.wineapp.model.Model;
import com.example.wineapp.model.Post;
import com.example.wineapp.model.User;
import com.example.wineapp.model.adapter.PermissionCallback;
import com.example.wineapp.model.intefaces.UploadImageListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;


public class UserAddPostFragment extends Fragment{
    View view;
    EditText postEt, subjectEt;
    Button cancelBtn, sendBtn;
    ImageButton editPhoto;
    ImageView postPhoto;
    ProgressBar progressBar;
    User user;
    MapView map;
    LatLng lastKnownLocation = null;
    Post p = new Post();
    OnMapReadyCallback onMapReadyCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_add_post, container, false);
        user = UserAddPostFragmentArgs.fromBundle(getArguments()).getUser();
        sendBtn = view.findViewById(R.id.user_add_new_post_send_btn);
        cancelBtn = view.findViewById(R.id.user_add_new_post_cancel_btn);
        postEt = view.findViewById(R.id.user_add_new_post_et);
        subjectEt = view.findViewById(R.id.user_add_subject_post_et);
        progressBar = view.findViewById(R.id.user_add_new_post_progressbar);
        map = view.findViewById(R.id.user_add_new_post_map);
        editPhoto = view.findViewById(R.id.user_add_post_edit_image_btn);
        postPhoto = view.findViewById(R.id.user_add_new_post_photo_upload);
        progressBar.setVisibility(View.GONE);
        editPhoto.setOnClickListener(v -> editPhoto());
        sendBtn.setOnClickListener(v -> {
            if (!validate()) {
                Toast.makeText(getActivity(), "Please check your input", Toast.LENGTH_SHORT).show();
                return;
            }
            save();
        });
        cancelBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            UserAddPostFragmentDirections.ActionUserAddPostFragmentToUserPageFragment action1 = UserAddPostFragmentDirections.actionUserAddPostFragmentToUserPageFragment(user);
            Navigation.findNavController(view).navigate(action1);
        });
        InitialGoogleMap(savedInstanceState);
        setHasOptionsMenu(true);
        return view;
    }


    private void InitialGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        }
        onMapReadyCallback = map -> {
            MainActivity.permissionCallback = new PermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(boolean isGranted) {
                    if (isGranted) {
                    }
                }
            };
            if (ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
            }
            map.setMyLocationEnabled(true);
            map.setOnMapClickListener(latLng -> {
                lastKnownLocation = new LatLng(latLng.latitude, latLng.longitude);
                map.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title(p.getSubject()));

            });
            if (lastKnownLocation == null)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.789080,34.654600), 7.5F));
        };
        map.onCreate(mapViewBundle);
        map.getMapAsync(onMapReadyCallback);
    }

    private void editPhoto() {
        Intent intent = getPickImageIntent(getActivity());
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                if (data.getAction() != null && data.getAction().equals("inline-data")){
                    // take picture from camera
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    postPhoto.setImageBitmap(imageBitmap);
                }
                else{
                    // pick from gallery
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                    Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                    postPhoto.setImageBitmap(imageBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void save() {
        progressBar.setVisibility(View.VISIBLE);
        sendBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        p.setName(user.getName());
        p.setSubject(subjectEt.getText().toString());
        p.setDetails(postEt.getText().toString());
        p.setLang(lastKnownLocation.longitude);
        p.setLant(lastKnownLocation.latitude);
        BitmapDrawable bitmapDrawable=(BitmapDrawable)postPhoto.getDrawable();
        Bitmap bitmap=bitmapDrawable.getBitmap();
        Model.instance.uploadImage(bitmap, p.getId_key(), new UploadImageListener() {
            @Override
            public void onComplete(String url) {
                if (url == null) {

                } else {
                    p.setImageUrl(url);
                    Model.instance.addPost(p, () -> Navigation.findNavController(sendBtn).navigateUp());
                }
            }});
    }
    private boolean validate() {
        return (subjectEt.getText().length() > 2 && postEt.getText().length() > 2&&lastKnownLocation!=null);
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