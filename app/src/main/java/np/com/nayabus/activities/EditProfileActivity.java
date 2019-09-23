package np.com.nayabus.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.HashMap;

import np.com.nayabus.R;
import np.com.nayabus.databinding.ActivityEditProfileBinding;
import np.com.nayabus.models.User;
import np.com.nayabus.utils.DialogUtils;

import static np.com.nayabus.utils.FirebaseUtils.auth;
import static np.com.nayabus.utils.FirebaseUtils.database;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);

        User user = (User) getIntent().getSerializableExtra("user");

        b.etFullName.setText(user.getFullName());
        b.etPhoneNumber.setText(user.getPhoneNumber());

        b.include.setTitle("Edit Profile");
        b.include.imgBack.setOnClickListener(v -> super.onBackPressed());

    }

    public void updateProfile(View view) {
        if (!validate()) return;
        String fullName = b.etFullName.getText().toString().trim();
        String phoneNumber = b.etPhoneNumber.getText().toString().trim();

        HashMap<String, Object> data = new HashMap<>();
        data.put("fullName", fullName);
        data.put("phoneNumber", phoneNumber);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Profile...");
        dialog.show();

        database.child("users").child(auth.getCurrentUser().getUid()).updateChildren(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DialogUtils.showSuccessDialog(this, "Success!!!", "Profile Updated Successfully.");
            } else {
                DialogUtils.showFailedDialog(this, "Failed!!!", task.getException().getMessage());
            }
            dialog.dismiss();
        });
    }

    private boolean validate() {
        if (TextUtils.isEmpty(b.etFullName.getText().toString().trim())) {
            b.etFullName.setError("Full Name is required!!!");
            b.etFullName.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(b.etPhoneNumber.getText().toString().trim())) {
            b.etPhoneNumber.setError("Phone Number is required!!!");
            b.etPhoneNumber.requestFocus();
            return false;
        } else if (b.etPhoneNumber.getText().toString().trim().length() < 10) {
            b.etPhoneNumber.setError("Phone number must be 10 digit long!!!");
            b.etPhoneNumber.requestFocus();
            return false;
        }
        return true;
    }
}
