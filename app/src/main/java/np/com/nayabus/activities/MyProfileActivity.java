package np.com.nayabus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;

import np.com.nayabus.R;
import np.com.nayabus.databinding.ActivityMyProfileBinding;
import np.com.nayabus.listeners.MyValueEventListener;
import np.com.nayabus.models.User;

import static np.com.nayabus.utils.FirebaseUtils.auth;
import static np.com.nayabus.utils.FirebaseUtils.database;

public class MyProfileActivity extends AppCompatActivity {

    private ActivityMyProfileBinding b;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_my_profile);

        b.toolbar.setTitle("My Profile");
        b.toolbar.imgBack.setOnClickListener(v->super.onBackPressed());
        b.btnEditProfile.setOnClickListener(v->updateProfile());

        loadProfile();
    }

    private void loadProfile() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        database.child("users/" + auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new MyValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                user = dataSnapshot.getValue(User.class);
                b.setUser(user);
                dialog.dismiss();
            }
        });
    }
    private void updateProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
