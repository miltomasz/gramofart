package com.soldiersofmobile.activities;

import static com.soldiersofmobile.utils.LogUtils.LOGE;

import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.soldiersofmobile.Constants;
import com.soldiersofmobile.R;


public class LoginActivity extends BaseActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    
    public static String TAG = LoginActivity.class.getName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_fragment_layout);
        initActionBar();
        bindFields();
        setActionBarTitle(R.string.login_fragment);
    }

    private void bindFields() {
        mEmailEditText = (EditText) findViewById(R.id.login_et);
        mPasswordEditText = (EditText) findViewById(R.id.password_et);
    }

    public void clickHandler(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                logInUser();

                break;
            case R.id.sign_up_btn:
                SignUpActivity.startSignUpActivity(this);

                break;
                
            case R.id.connect_fb_btn:
            	loginWithFb();
            	break;
            default:
                break;
        }
    }

    public void loginWithFb(){
    	ParseFacebookUtils.logIn(this, new LogInCallback() {
    		  @Override
    		  public void done(final ParseUser user, ParseException err) {
    			  if (err != null) {
                      LOGE(TAG, "FBLogin:", err);

                  } else if (user == null) {
                      //Uh oh. The user cancelled the Facebook login."
                  } else if (user.isNew()) {
                      //User signed up and logged in through Facebook!
                      Request.executeMeRequestAsync(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {

						@Override
						public void onCompleted(GraphUser fbUser,Response response) {
							final ParseUser currentUser = ParseUser.getCurrentUser();
                            if (fbUser != null) {

                                String email = (String) fbUser.getProperty("email");
                                if (!TextUtils.isEmpty(email)) {
                                    currentUser.setEmail(email);
                                    currentUser.setUsername(email);
                                }
                                
                                String firstName = fbUser.getFirstName();
                                if (!TextUtils.isEmpty(firstName)) {
                                    currentUser.put(Constants.NICKNAME_PARSE_KEY, firstName);
                                }
                                //user.signUpInBackground();
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e != null && (e.getCode() == ParseException.CONNECTION_FAILED || e.getCode() == ParseException.INTERNAL_SERVER_ERROR)) {
                                            user.saveInBackground();
                                        }else{
                                        	 GramOfArtActivity.startGramOfArtActivity(LoginActivity.this);
                                        }
                                    }
                                });

                            }
							
						}});

                  } else {
                      //User signed up and logged in through Facebook!
                      Request.executeMeRequestAsync(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {

                          @Override
                          public void onCompleted(GraphUser fbUser,Response response) {
                              final ParseUser currentUser = ParseUser.getCurrentUser();
                              if (fbUser != null) {

                                  String email = (String) fbUser.getProperty("email");
                                  if (!TextUtils.isEmpty(email)) {
                                      currentUser.setEmail(email);
                                      currentUser.setUsername(email);
                                  }

                                  String firstName = fbUser.getFirstName();
                                  if (!TextUtils.isEmpty(firstName)) {
                                      currentUser.put(Constants.NICKNAME_PARSE_KEY, firstName);
                                  }
                                  //user.signUpInBackground();
                                  user.signUpInBackground(new SignUpCallback() {
                                      @Override
                                      public void done(ParseException e) {
                                          if (e != null && (e.getCode() == ParseException.CONNECTION_FAILED || e.getCode() == ParseException.INTERNAL_SERVER_ERROR)) {
                                              user.saveInBackground();
                                          }else{
                                              GramOfArtActivity.startGramOfArtActivity(LoginActivity.this);
                                          }
                                      }
                                  });
//                                  user.saveInBackground(new SaveCallback() {
//                                      @Override
//                                      public void done(ParseException e) {
//                                          if (e != null && (e.getCode() == ParseException.CONNECTION_FAILED || e.getCode() == ParseException.INTERNAL_SERVER_ERROR)) {
//                                              user.saveInBackground();
//                                          }else{
//                                              GramOfArtActivity.startGramOfArtActivity(LoginActivity.this);
//                                          }
//                                      }
//                                  });

                              }

                          }});

//                      GramOfArtActivity.startGramOfArtActivity(LoginActivity.this);
                  }
              }
    		});
    }
    
    public static void startLogInActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    private void logInUser() {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    GramOfArtActivity.startGramOfArtActivity(LoginActivity.this);

                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Toast.makeText(getApplicationContext(), "Login error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }
}
