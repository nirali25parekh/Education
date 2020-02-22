package com.example.mind_mover;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends BaseActivity {

    private FirebaseAuth mAuth;

    private Button mGoogleButton;

    private LoginButton mFacebookButton;

    private CallbackManager mCallbackManager;

    private GoogleApiClient mGoogleApiClient;

    public static final String TAG = Login.class.getSimpleName();

    private static final int RC_SIGN_IN = 9001;

    private static ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initGoogleApi();

        initFacebookApi();

        initFirebaseApi();


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
    }

    private void initFirebaseApi() {
        mAuth = FirebaseAuth.getInstance();

    }

    private void initFacebookApi() {
        mFacebookButton = (LoginButton) findViewById(R.id.facebookSignin);

        mCallbackManager = CallbackManager.Factory.create();

        mFacebookButton.setReadPermissions("email", "public_profile");

        mFacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookLogin(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

                updateUIStatus(null);
            }

            @Override
            public void onError(FacebookException error) {
                updateUIStatus(null);
            }


        });


    }

    private void updateUIStatus(FirebaseUser user) {

        if (user != null) {

            Intent i = new Intent(this, Home.class);
            startActivity(i);
            this.finish();

            Redirector.from(this).goToHome();

        } else {

            Log.d(TAG, "updateUIStatus - user null");
            mGoogleButton.animate().alpha(1).withStartAction(new Runnable() {
                @Override
                public void run() {
                    mGoogleButton.setVisibility(View.VISIBLE);
                }
            });
            mFacebookButton.animate().alpha(1).withStartAction(new Runnable() {
                @Override
                public void run() {
                    mFacebookButton.setVisibility(View.VISIBLE);
                }
            });

        }

    }

    private void handleFacebookLogin(AccessToken accessToken) {

        progressDialog.show();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        this.doLoginWithCred(credential);
    }

    private void handleGoogleLogin(GoogleSignInAccount account) {

        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        this.doLoginWithCred(credential);

    }

    private void initGoogleApi() {
        mGoogleButton = (Button) findViewById(R.id.googleSignin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "connection failed - user null");
                        updateUIStatus(null);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();

                handleGoogleLogin(account);

            } else {
                Log.d(TAG, "result unsuccess - user null");
                updateUIStatus(null);
            }
        } else if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }
    private void doLoginWithCred (AuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    updateUIStatus(user);
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(Login.this, "Invalid Authentication Credential", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthUserCollisionException e) {

                        Toast.makeText(Login.this, "Your email is already registered with another Social Media Account. Kindly use that account to login", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {

                        Toast.makeText(Login.this, "Cannot Login due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    LoginManager.getInstance().logOut();
                    Log.d(TAG, "login unsuccess - user null");
                    updateUIStatus(null);
                }
                progressDialog.hide();

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        updateUIStatus(user);
    }

    @Override
    protected void checkAuthorisation() {

    }


}
