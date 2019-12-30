package np.com.nayabus.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import np.com.nayabus.utils.DialogUtils;
import np.com.nayabus.R;
import np.com.nayabus.utils.SharedUtils;
import np.com.nayabus.utils.SharedValue;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        btnLogin.setOnClickListener(view -> loginUser());

        SharedUtils.setBoolean(this, SharedValue.isTripStarted, false);
    }

    private void loginUser() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (auth.getCurrentUser().isEmailVerified()) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    DialogUtils.showFailedDialog(LoginActivity.this, "Failed!!!",
                            "Please verify your account before login.");
                }
                dialog.dismiss();
            } else {
                DialogUtils.showFailedDialog(LoginActivity.this, "Failed!!!", task.getException().getMessage());
                dialog.dismiss();
            }
        });

    }

    public void openRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
