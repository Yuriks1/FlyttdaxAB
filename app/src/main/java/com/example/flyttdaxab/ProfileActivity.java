package com.example.flyttdaxab;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flyttdaxab.databinding.ActivityProfilrBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    // view binding

    private ActivityProfilrBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfilrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        //handle click , logout

        binding.logoutBtn.setOnClickListener(v -> {
            firebaseAuth.signOut();
            checkUser();
        });

        binding.toCamera.setOnClickListener(v->{
            startCamera();
        });

    }

    private void startCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        this.startActivity ( intent );
    }

    private void checkUser() {

        // get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            //user not logged in
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else{
            //user logged in
            // get user info
            String email = firebaseUser.getEmail();
            // set email
            binding.emailTv.setText(email);
        }
    }
}