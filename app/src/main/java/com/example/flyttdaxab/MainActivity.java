package com.example.flyttdaxab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flyttdaxab.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;

    private static final String TAG = "GOOGLE_SIGN_IN_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.flyttdaxab.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //configure google sign in
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();

        // google sign in button
        binding.GoogleSignInButton.setOnClickListener(v -> {
            // begin google sign in
            Log.d(TAG, "onClick: begin Google Signin");
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);

        });


    }

    private void checkUser() {
        // if user is already signed in , then go to profile acticity
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if( firebaseUser != null){
            Log.d(TAG, "checkUser:  User already logged in");
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: Google Signin intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                //google sign in success,now auth with firebase
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                FireBaseAuthWithGoogleAccount(account);


            } catch (Exception e) {
                // failed google sign
                Log.d(TAG, "onActivityResult: " +e.getMessage());

            }
        }
    }

    private void FireBaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "FireBaseAuthWithGoogleAccount: begin firebase auth with google account ");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    // login success
                    Log.d(TAG, "onSuccess: Logged In");

                    //get login in user
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    //get user info
                    String uid = firebaseAuth.getUid();
                    assert firebaseUser != null;
                    String email = firebaseUser.getEmail();

                    Log.d(TAG, "onSuccess: Email "+ email);
                    Log.d(TAG, "onSuccess: UID " +uid);

                    //check if user is new or existing

                    if(Objects.requireNonNull(authResult.getAdditionalUserInfo()).isNewUser()){
                        // if user is new - account created
                        Log.d(TAG, "onSuccess: Account Created ...\n" + email );
                        Toast.makeText(MainActivity.this, "Account Created ...\n" + email, Toast.LENGTH_SHORT).show();
                    }else {
                        //Existing user - logged in
                        Log.d(TAG, "onSuccess:  Existing user...\n "+ email);
                        Toast.makeText(MainActivity.this, "Existing user...\n "+ email, Toast.LENGTH_SHORT).show();
                    }

                    //start profile activity

                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    finish();





                })
                .addOnFailureListener(e -> {
                    // login failed
                    Log.d(TAG, "onFailure: Loggin failed " + e.getMessage());
                });

    }
}