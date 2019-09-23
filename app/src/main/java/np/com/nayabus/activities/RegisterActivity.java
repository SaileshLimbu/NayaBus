package np.com.nayabus.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import np.com.nayabus.R;
import np.com.nayabus.models.User;
import np.com.nayabus.utils.DialogUtils;


public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword, etPhoneNumber;
    private AppCompatCheckBox chkTerms;
    private Button btnRegister;
    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etFullName = findViewById(R.id.etFullName);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        chkTerms = findViewById(R.id.chkTerms);
        btnRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("users");

        btnRegister.setOnClickListener(view -> register());
    }


    public void register() {
        if (!validate()) return;
        String fullName = etFullName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.show();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        database.child(auth.getCurrentUser().getUid()).setValue(new User(email, fullName, phoneNumber, 0)).addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                DialogUtils.showSuccessDialog(RegisterActivity.this, "Success!!!", "Please check your email to verify your account.");
                            } else {
                                DialogUtils.showFailedDialog(RegisterActivity.this, "Failed!!!", task2.getException().getMessage());
                            }
                        });
                    } else {
                        DialogUtils.showFailedDialog(RegisterActivity.this, "Failed!!!", task1.getException().getMessage());
                    }
                    dialog.dismiss();
                });
            } else {
                DialogUtils.showFailedDialog(RegisterActivity.this, "Failed!!!", task.getException().getMessage());
                dialog.dismiss();
            }
        });
    }

    public void openLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public boolean validate() {
        if (TextUtils.isEmpty(etFullName.getText().toString())) {
            etFullName.setError("Please enter full name.");
            etFullName.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etEmail.getText().toString())) {
            etEmail.setError("Please enter email.");
            etEmail.requestFocus();
            return false;
        } else if (!etEmail.getText().toString().contains("@")) {
            etEmail.setError("Please enter valid email.");
            etEmail.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("Please enter password.");
            etPassword.requestFocus();
            return false;
        } else if (!etConfirmPassword.getText().toString().trim().equals(etPassword.getText().toString().trim())) {
            etConfirmPassword.setError("Confirm password does not match.");
            etConfirmPassword.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
            etPhoneNumber.setError("Please enter phone number.");
            etPhoneNumber.requestFocus();
            return false;
        } else if (!chkTerms.isChecked()) {
            DialogUtils.showFailedDialog(this, "Alert!!!", "Please accept our terms and conditions.");
            return false;
        }
        return true;
    }
}