package com.example.mma.tespis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 34;
    private static final String TAG = "TesPIs";

    /**
     * GoogleApiClient is a service connection to Google Play services and provides access
     * to the user's OAuth2 and API availability state for the APIs and scopes requested. Before
     * making Google Play services API calls ensure
     * {@code com.google.android.gms.common.api.GoogleApiClient.isConnected()} returns true.
     */

    GoogleApiClient mGoogleApiClient;

    TextView textView;
    GoogleSignInAccount acct;
    private static final String SHARED_PREFS = "GoogleAccountSamplePrefs";

    /**
     * Preference that tracks whether the user is currently signed into the app.
     * Specifically, if a user signs into the app via a Google Account and then comes back to it
     * later this indicates they were last signed in. This preference is used to determine
     * whether to initiate the GoogleApiClient connection immediately upon opening the activity.
     * This logic prevents the user's first experience with your app from being an OAuth2 consent
     * dialog.
     */
    private static final String PREFS_IS_SIGNED_IN = "IS_SIGNED_IN";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (isSignedIn()) {
//            rebuildGoogleApiClient();
//            // TODO: This next IF statement temporarily deals with an issue where autoManage doesn't
//            // call the onConnected callback after a Builder.build() when re-connecting after a
//            // rotation change. Will remove when fixed.
//            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//                onConnected(null);

            // Go to home activity directly.
            Intent goToHomeIntent = new Intent(this, HomeActivity.class);
            startActivity(goToHomeIntent);
            }


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(this);

        textView = (TextView) findViewById(R.id.textview);





    }

    @Override
    public void onClick(View v) {

        //textView.setText("Logged in successfully!");
        signIn();

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = result.getStatus().getStatusCode();
            Log.d(TAG, "Status code:" + statusCode);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();

//            textView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean b) {
        if (b){

            Log.d(TAG, "b is true");
            textView.setText("Welcome " + acct.getDisplayName() + "!");

            // Update shared preferences
            storeSignInState(true);

            // Go to home activity
            Intent goToHomeIntent = new Intent(this, HomeActivity.class);
            startActivity(goToHomeIntent);

        } else {
            Log.d(TAG, "b is false");
            textView.setText("Unauthenticated!..");

            // Update shared preferences
            storeSignInState(false);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(TAG, "On connection failed...");
        textView.setText(connectionResult.toString());
    }

    /**
     * Returns whether the user is signed into the app.
     */
    private boolean isSignedIn() {
        Context context = getApplicationContext();
        SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFS,
                MODE_PRIVATE);
        return sharedPrefs.getBoolean(PREFS_IS_SIGNED_IN, false);
    }

    /**
     * Changes the user's app sign in state.
     *
     * @param signedIn Whether the user is signed in.
     */
    private void storeSignInState(boolean signedIn) {
        Context context = getApplicationContext();
        SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(PREFS_IS_SIGNED_IN, signedIn);
        editor.apply();
    }
}
