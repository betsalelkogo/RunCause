package com.example.runcause;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.runcause.model.adapter.PermissionCallback;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationProviderClient;
    NavController navCtrl;
    public static PermissionCallback permissionCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        NavHostFragment nav_host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host);
        navCtrl = nav_host.getNavController();
        //NavigationUI.setupActionBarWithNavController(this, navCtrl);
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (!super.onOptionsItemSelected(item)) {
//            switch (item.getItemId()) {
//                case android.R.id.home:
//                    navCtrl.navigateUp();
//                    return true;
//                default:
//                    return NavigationUI.onNavDestinationSelected(item, navCtrl);
//            }
//        }
//        return true;
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // Location
            case 123: {
                boolean result = (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (permissionCallback != null)
                    permissionCallback.onResult(result);
                break;
            }
        }
    }
}