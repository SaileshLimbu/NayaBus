package np.com.nayabus.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import np.com.nayabus.R;
import np.com.nayabus.databinding.ActivityLoadFundBinding;
import np.com.nayabus.listeners.MyValueEventListener;
import np.com.nayabus.utils.FirebaseUtils;

public class LoadFundActivity extends AppCompatActivity {

    private ActivityLoadFundBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_load_fund);

        b.toolbar.setTitle("Load Fund");
        b.toolbar.imgBack.setOnClickListener(v -> super.onBackPressed());

        getBalance();
    }

    private void getBalance() {
        FirebaseUtils.database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance").addValueEventListener(new MyValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                int balance = dataSnapshot.getValue(Integer.class);
                b.etBalance.setText(balance + "");
            }
        });
    }
}
