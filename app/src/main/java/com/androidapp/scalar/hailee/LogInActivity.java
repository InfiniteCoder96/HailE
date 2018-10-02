package com.androidapp.scalar.hailee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {

    private TextView registerTxt;
    private Button mLoginBtn;
    private EditText mEmail,mPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private DatabaseReference mUserDatabase,mUserRolesDatabase;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        mAuth = FirebaseAuth.getInstance();


        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                try {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user != null) {

                        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user.getUid());

                        FirebaseDatabase.getInstance().getReference().child("roles").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                try {
                                    String role = dataSnapshot.getValue(String.class);


                                    if (role != null && role.equals("customer")) {
                                        Intent intent = new Intent(LogInActivity.this, CustomerMapActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    } else if (role != null && role.equals("driver")){
                                        Intent intent = new Intent(LogInActivity.this, DriverMapActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(LogInActivity.this, "Something went wrong..Please try again later",Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }

                        });
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(LogInActivity.this, "Something went wrong..Please try again later",Toast.LENGTH_SHORT).show();
                }


            }
        };

        registerTxt = (TextView) findViewById(R.id.registertxt);

        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        login();
    }

    protected void login(){

        mEmail = (EditText) findViewById(R.id.emailLogTxt);
        mPassword = (EditText) findViewById(R.id.passwordLogTxt);
        mLoginBtn = (Button) findViewById(R.id.loginBtn);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LogInActivity.this, "Please fill required fields",Toast.LENGTH_SHORT).show();
                }
                else if(!email.contains("@")){
                    Toast.makeText(LogInActivity.this, "Please enter valid email address",Toast.LENGTH_SHORT).show();
                }
                else if(password.length() < 6){
                    Toast.makeText(LogInActivity.this, "The password must be at least 6 characters.",Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LogInActivity.this, "Login error",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                userID = mAuth.getCurrentUser().getUid();

                                FirebaseDatabase.getInstance().getReference().child("roles").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String role = dataSnapshot.getValue(String.class);

                                        if (role != null && role.equals("customer")) {
                                            Intent intent = new Intent(LogInActivity.this, CustomerMapActivity.class);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        } else if (role != null && role.equals("driver")){
                                            Intent intent = new Intent(LogInActivity.this, DriverMapActivity.class);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }

                                });



                            }
                        }
                    });
                }
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
