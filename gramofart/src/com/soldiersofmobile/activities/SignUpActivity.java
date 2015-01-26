package com.soldiersofmobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.soldiersofmobile.Constants;
import com.soldiersofmobile.R;


public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEmailEditText;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mCreateAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        bindFields();
    }

    private void bindFields() {
        mEmailEditText = (EditText) findViewById(R.id.sign_up_email_et);
        mUsernameEditText = (EditText) findViewById(R.id.sign_up_username_et);
        mPasswordEditText = (EditText) findViewById(R.id.sign_up_password_et);
        mCreateAccountButton = (Button) findViewById(R.id.sign_up_create_account_btn);
        mCreateAccountButton.setOnClickListener(this);
    }

    public static void startSignUpActivity(Context context) {
        Intent intent = new Intent(context, SignUpActivity.class);
        context.startActivity(intent);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_create_account_btn:
                signUpUser();
                break;
        }

    }

    private void signUpUser() {
        String email = mEmailEditText.getText().toString();
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();


        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);

        user.put(Constants.NICKNAME_PARSE_KEY, username);


        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    GramOfArtActivity.startGramOfArtActivity(SignUpActivity.this);
                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Toast.makeText(getApplicationContext(), "Sign up error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
