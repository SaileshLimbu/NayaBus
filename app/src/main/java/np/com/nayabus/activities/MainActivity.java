package np.com.nayabus.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import net.glxn.qrgen.android.QRCode;

import np.com.nayabus.R;
import np.com.nayabus.databinding.ActivityMainBinding;
import np.com.nayabus.listeners.MyValueEventListener;
import np.com.nayabus.models.User;

import static np.com.nayabus.utils.FirebaseUtils.auth;
import static np.com.nayabus.utils.FirebaseUtils.database;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private ActivityMainBinding b;
    private GoogleMap map;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        b.toolbar.setTitle("Home");
        b.toolbar.imgMenu.setOnClickListener(v->b.drawerLayout.openDrawer(GravityCompat.START));

        initNavClick();
        loadBalance();
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mnuHome:
                startActivityForResult(new Intent(this, QRScannerActivity.class), 1);
                break;
            case R.id.mnuMyProfile:
                startActivity(new Intent(this, MyProfileActivity.class));
                break;
            case R.id.mnuLogout:
                confirmLogout();
                break;
        }
        b.drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void confirmLogout() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_confirm);
        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnYes.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            dialog.dismiss();
        });
        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.width = 600;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lWindowParams);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            String result = data.getStringExtra("result");
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
    }
}