package np.com.nayabus.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

import np.com.nayabus.R;
import np.com.nayabus.databinding.ActivityPayNowBinding;
import np.com.nayabus.listeners.MyValueEventListener;
import np.com.nayabus.utils.DateUtils;
import np.com.nayabus.utils.FirebaseUtils;
import np.com.nayabus.utils.SharedUtils;
import np.com.nayabus.utils.SharedValue;

public class PayNowActivity extends AppCompatActivity {

    private ActivityPayNowBinding b;
    private String startLocation, endLocation, eta, distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_pay_now);

        b.toolbar.setTitle("Pay Now");
        b.toolbar.imgBack.setOnClickListener(v -> super.onBackPressed());

        startLocation = SharedUtils.getString(this, SharedValue.startLocationName);
        endLocation = SharedUtils.getString(this, SharedValue.endLocationName);
        eta = getIntent().getStringExtra("eta");
        distance = getIntent().getStringExtra("distance");

        b.tvPickupLocation.setText(startLocation);
        b.tvDropoffLocation.setText(endLocation);
        b.tvETA.setText(eta + " mins");
        b.tvDistance.setText(distance + " km");
        b.tvFair.setText(Integer.toString(Integer.parseInt(distance) * 20));

    }

    public void payNow(View view) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        int fair = Integer.parseInt(distance) * 20;
        FirebaseUtils.database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance").addListenerForSingleValueEvent(new MyValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                int balance = dataSnapshot.getValue(Integer.class);
                int newBalance = balance - fair;
                FirebaseUtils.database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance").setValue(newBalance);
                HashMap<String, String> history = new HashMap<>();
                history.put("date", DateUtils.getDateToday());
                history.put("distance", distance + " k/m");
                history.put("pickupLocation", startLocation);
                history.put("dropoffLocation", endLocation);
                FirebaseUtils.database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("history").push().setValue(history);
                dialog.dismiss();
                finish();
                startActivity(new Intent(PayNowActivity.this, PaymentSuccessActivity.class));
            }
        });
    }
}