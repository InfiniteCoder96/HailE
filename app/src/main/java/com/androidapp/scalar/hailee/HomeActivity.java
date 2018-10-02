package com.androidapp.scalar.hailee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    LinearLayout l1,l2;
    Button nextBtn;
    Animation uptodown,downtoup;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private DatabaseReference mUserDatabase,mUserRolesDatabase;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        nextBtn = (Button) findViewById(R.id.nxtbtn);
        nextBtn.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();


        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                try {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user != null) {
                        nextBtn.setVisibility(View.GONE);

                        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user.getUid());

                        FirebaseDatabase.getInstance().getReference().child("roles").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                try {
                                    String role = dataSnapshot.getValue(String.class);

                                    if (role.equals("customer")) {

                                        Intent intent = new Intent(HomeActivity.this, CustomerMapActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    } else {
                                        Intent intent = new Intent(HomeActivity.this, DriverMapActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                }
                                catch (NullPointerException e){
                                    Toast.makeText(HomeActivity.this, "Something went wrong..Please try again later",Toast.LENGTH_SHORT).show();
                                }



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }

                        });
                    }
                }
                catch (NullPointerException e){
                    Toast.makeText(HomeActivity.this, "Something went wrong..Please try again later",Toast.LENGTH_SHORT).show();
                }


            }
        };

        animate();

    }

    public void animate(){
        l1 = (LinearLayout) findViewById(R.id.L1);
        l2 = (LinearLayout) findViewById(R.id.L2);



        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);

        l1.setAnimation(uptodown);
        l2.setAnimation(downtoup);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();
                return;

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListner);
    }
}
