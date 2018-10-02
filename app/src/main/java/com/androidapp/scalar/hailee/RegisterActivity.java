package com.androidapp.scalar.hailee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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


public class RegisterActivity extends AppCompatActivity {

    private TextView loginTxt;
    private Button registerBtn;
    private EditText mEmail,mPassword,mConfirmPw;
    private RadioGroup mRoleChooser;
    private RadioButton mRole;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private DatabaseReference mUserDatabase,mUserRolesDatabase;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

                                    if (role.equals("customer")) {
                                        Intent intent = new Intent(RegisterActivity.this, CustomerMapActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    } else {
                                        Intent intent = new Intent(RegisterActivity.this, DriverMapActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                }
                                catch (NullPointerException e){
                                    Toast.makeText(RegisterActivity.this, "Something went wrong..Please try again later",Toast.LENGTH_SHORT).show();
                                }



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }

                        });
                    }
                }
                catch (NullPointerException e){
                    Toast.makeText(RegisterActivity.this, "Something went wrong..Please try again later",Toast.LENGTH_SHORT).show();
                }


            }
        };

        loginTxt = (TextView) findViewById(R.id.logintxt);

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mEmail = (EditText) findViewById(R.id.emailTxt);
        mPassword = (EditText) findViewById(R.id.passwordTxt);
        mConfirmPw = (EditText) findViewById(R.id.confirmPwTxt);
        mRoleChooser = (RadioGroup) findViewById(R.id.roleChooser);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String confirmPw = mConfirmPw.getText().toString();
                int selectedId = mRoleChooser.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                mRole = (RadioButton) findViewById(selectedId);

                if(email.isEmpty() || password.isEmpty() || confirmPw.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please provide required fields", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(confirmPw)){
                    Toast.makeText(RegisterActivity.this, "Passwords didn't match", Toast.LENGTH_SHORT).show();
                }
                else if(!email.contains("@")){
                    Toast.makeText(RegisterActivity.this, "Please enter valid email address",Toast.LENGTH_SHORT).show();
                }
                else if(password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "The password must be at least 6 characters.",Toast.LENGTH_SHORT).show();
                }
                else if(mRole == null){
                    Toast.makeText(RegisterActivity.this, "Please select your role", Toast.LENGTH_SHORT).show();
                }
                else{

                    final String role = mRole.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Register error", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String user_id = mAuth.getCurrentUser().getUid();

                                if(role.equals("I'm a Customer")){
                                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);
                                    DatabaseReference myRef = database.getReference("roles").child(user_id);
                                    myRef.setValue("customer");
                                }
                                else if(role.equals("I'm a Driver")){
                                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id);
                                    DatabaseReference myRef = database.getReference("roles").child(user_id);
                                    myRef.setValue("driver");
                                }

                                mUserDatabase.setValue(true);

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
