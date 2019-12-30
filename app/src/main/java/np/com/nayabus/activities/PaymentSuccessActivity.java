package np.com.nayabus.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import np.com.nayabus.R;
import np.com.nayabus.databinding.ActivityPaymentSuccessBinding;

public class PaymentSuccessActivity extends AppCompatActivity {

    private ActivityPaymentSuccessBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_payment_success);

        b.btnContinue.setOnClickListener(v -> finish());
    }
}
