package com.example.harvestfresh;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import es.dmoral.toasty.Toasty;

public class SignupActivity extends AppCompatActivity {

    private Button btnFinalSignup;
    private EditText etSignupUsername;
    private EditText etSignupPassword;
    private EditText etConfirmPassword;

    private static final String TAG = "SignupActivity";
    private static final String SIGN_ERROR = "Error with signup";
    public static final String PASSWORD_MISMATCH = String.valueOf(R.string.password_mismatch);
    public static final String BLANK_FIELDS = String.valueOf(R.string.blank_field);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnFinalSignup = findViewById(R.id.btnFinalSignup);
        etSignupUsername = findViewById(R.id.etSignupUsername);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);


        btnFinalSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = etSignupUsername.getText().toString();
                String newPassword = etSignupPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (newUsername.isEmpty()
                        || newPassword.isEmpty()
                        || confirmPassword.isEmpty()) {
                    Toasty.error(SignupActivity.this, BLANK_FIELDS, Toast.LENGTH_SHORT, true).show();
                } else if (newPassword == confirmPassword) {
                    signupUser(newUsername, newPassword);
                } else {
                    Toasty.error(SignupActivity.this, PASSWORD_MISMATCH, Toast.LENGTH_SHORT, true).show();
                }
            }
        });
    }

    private void signupUser(String newUsername, String newPassword) {
        ParseUser user = new ParseUser();

        user.setUsername(newUsername);
        user.setPassword(newPassword);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    goMainActivity();
                } else {
                    Log.e(TAG, SIGN_ERROR);
                }
            }
        });
    }

    private void goMainActivity() {
    }
}
