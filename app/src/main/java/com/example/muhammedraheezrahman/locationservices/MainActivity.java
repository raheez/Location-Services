package com.example.muhammedraheezrahman.locationservices;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    private boolean mAlreadyStartedService = false;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE =47;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String lattitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);

                if (lattitude!=null && longitude!=null){
                    tv.setText("Location fetching started" +"\n lattitude "+lattitude +"\n"+"Longitude "+longitude );
                }
            }
        },new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST));

    }

    @Override
    protected void onResume() {
        super.onResume();
        step1();
    }

    private void step1() {

        if (checkPlayServices()){

            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if (activeNetworkInfo != null || activeNetworkInfo.isConnected()) {

                if (checkpermision()){
                    startStep2();
                }
                if (!checkpermision()){
                    requestPermission();

                }

            }
        }
    }

    private void startStep2() {
        if (!mAlreadyStartedService && tv!=null){
            tv.setText("Location service started");
            Intent intent = new Intent(this,LocationMonitoringService.class);
            startService(intent);
            mAlreadyStartedService = true;
        }
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 21)
                        .show();
            } else {
                Log.i("Main Activity", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private boolean checkpermision(){
        if ((ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            return  true;
        }

        else
            return false;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE){

            if (grantResults.length<0){
                Toast.makeText(getApplicationContext(),"Permission discarded",Toast.LENGTH_SHORT).show();
            }
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startStep2();
            }
        }
    }


}