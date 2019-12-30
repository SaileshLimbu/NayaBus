package np.com.nayabus.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import np.com.nayabus.R;
import np.com.nayabus.databinding.ActivityPaymentFailedBinding;
import np.com.nayabus.listeners.MyValueEventListener;
import np.com.nayabus.utils.FirebaseUtils;

public class PaymentFailedActivity extends AppCompatActivity {

    private ActivityPaymentFailedBinding b;
    private int fair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_payment_failed);

        fair = getIntent().getIntExtra("fair", 0);

        b.btnContinue.setOnClickListener(v -> makeOverDraft());
    }

    private void makeOverDraft() {
        FirebaseUtils.database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance").addValueEventListener(new MyValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                int balance = dataSnapshot.getValue(Integer.class);
                int newBalance = balance - fair;
                FirebaseUtils.database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance").setValue(newBalance);
                finish();
                startActivity(new Intent(PaymentFailedActivity.this, PaymentSuccessActivity.class));
            }
        });
    }
}
