package np.com.nayabus.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import np.com.nayabus.R;
import np.com.nayabus.databinding.ActivityMainBinding;
import np.com.nayabus.listeners.MyChildEventListener;
import np.com.nayabus.listeners.MyLocationListener;
import np.com.nayabus.listeners.MyValueEventListener;
import np.com.nayabus.utils.ImageUtils;
import np.com.nayabus.utils.ShakeDetector;
import np.com.nayabus.utils.SharedUtils;
import np.com.nayabus.utils.SharedValue;
import np.com.nayabus.utils.TimeUtils;

import static np.com.nayabus.utils.FirebaseUtils.auth;
import static np.com.nayabus.utils.FirebaseUtils.database;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private ActivityMainBinding b;
    private GoogleMap map;
    private Geocoder geocoder;
    private SensorManager manager;
    private Marker myMarker;
    private Sensor sensor;
    private ShakeDetector mShakeDetector;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        b.toolbar.setTitle("Home");
        b.toolbar.imgMenu.setOnClickListener(v -> b.drawerLayout.openDrawer(GravityCompat.START));

        geocoder = new Geocoder(this, Locale.getDefault());

        if (!isGPSEnable())
            Toast.makeText(this, "Please enable GPS to locate your location in real time.", Toast.LENGTH_SHORT).show();

        initNavClick();
        loadBalance();
        initSensor();
        getBusList();

        if (SharedUtils.getBoolean(this, SharedValue.isTripStarted)) {
            String name = SharedUtils.getString(this, SharedValue.startLocationName);
            showTripStartDialog(name);
        }
    }

    private void getBusList() {
        database.child("buses").addChildEventListener(new MyChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                super.onChildAdded(dataSnapshot, s);

            }
        });
    }

    private void initSensor() {
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(count -> {
            startActivityForResult(new Intent(this, QRScannerActivity.class), 1);
        });
    }

    private void loadBalance() {
        database.child("users/" + auth.getCurrentUser().getUid()).child("balance").addValueEventListener(new MyValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                String balance = String.valueOf(dataSnapshot.getValue());
                b.nav.etBalance.setText(balance);
            }
        });
    }

    private void initNavClick() {
        b.nav.mnuHome.setOnClickListener(this);
        b.nav.mnuMyProfile.setOnClickListener(this);
        b.nav.mnuFund.setOnClickListener(this);
        b.nav.mnuHistory.setOnClickListener(this);
        b.nav.mnuLogout.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new MyLocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                super.onLocationChanged(location);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (myMarker == null) {
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(ImageUtils.resizeMapIcons(MainActivity.this, R.drawable.ic_marker, 30, 40)));
                    myMarker = map.addMarker(markerOptions);
                } else {
                    myMarker.setPosition(latLng);
                }
            }
        });
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(ImageUtils.resizeMapIcons(MainActivity.this, R.drawable.ic_marker, 60, 65)));
            myMarker = map.addMarker(markerOptions);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mnuHome:
                break;
            case R.id.mnuMyProfile:
                startActivity(new Intent(this, MyProfileActivity.class));
                break;
            case R.id.mnuFund:
                startActivity(new Intent(this, LoadFundActivity.class));
                break;
            case R.id.mnuHistory:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            case R.id.mnuLogout:
                confirmLogout();
                break;
        }
        b.drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void confirmLogout() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null, false);
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(view);
        Button btnYes = view.findViewById(R.id.btnYes);
        Button btnNo = view.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnYes.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            dialog.dismiss();
            finish();
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            Double latitute = Double.parseDouble(result.split(":")[0]);
            Double longitude = Double.parseDouble(result.split(":")[1]);
            try {
                List<Address> list = geocoder.getFromLocation(latitute, longitude, 1);
                String name = "Unknown Road";
                if (list.size() != 0) {
                    name = list.get(0).getAddressLine(0);
                }
                if (SharedUtils.getBoolean(this, SharedValue.isTripStarted)) {
                    SharedUtils.setBoolean(this, SharedValue.isTripStarted, false);
                    SharedUtils.setString(this, SharedValue.endLocationName, name);
                    Intent intent = new Intent(MainActivity.this, PayNowActivity.class);
                    intent.putExtra("eta", TimeUtils.getRandomNumberInRange(30, 45) + "");
                    intent.putExtra("distance", TimeUtils.getRandomNumberInRange(5, 10) + "");
                    startActivity(intent);
                } else {
                    showTripStartDialog(name);
                    SharedUtils.setBoolean(this, SharedValue.isTripStarted, true);
                    SharedUtils.setString(this, SharedValue.startLocationName, name);
                    SharedUtils.setString(this, SharedValue.startLocationLatLng, result);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showTripStartDialog(String name) {
        manager.unregisterListener(mShakeDetector);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_start_trip, null, false);
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button btnEndTrip = view.findViewById(R.id.btnEndTrip);
        TextView tvStartTrip = view.findViewById(R.id.tvStartTrip);
        tvStartTrip.setText(Html.fromHtml("Your trip has been started from <b><font color='#D81B60'>" + name + "</font></b>"));
        btnEndTrip.setOnClickListener(v -> {
            dialog.dismiss();
            startActivityForResult(new Intent(this, QRScannerActivity.class), 1);
            manager.registerListener(mShakeDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        });
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.registerListener(mShakeDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.unregisterListener(mShakeDetector);
    }

    public boolean isGPSEnable() {
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}