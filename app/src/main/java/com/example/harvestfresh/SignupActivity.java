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

public class SignupActivity extends AppCompatActivity {

    private Button btnFinalSignup;
    private EditText etSignupUsername;
    private EditText etSignupPassword;
    private EditText etConfirmPassword;
    private static final String TAG = "SIGNUPACTIVITY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

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

                if(newUsername.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()){
                    Toast.makeText(SignupActivity.this, "One or more of the fields are blank", Toast.LENGTH_SHORT).show();
                }
                else if (newPassword == confirmPassword){
                    signupUser(newUsername, newPassword);
                }
                else{
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
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
                    Log.e(TAG, "Error with signup");
                }
            }
        });
    }

    private void goMainActivity() {
    }
}
