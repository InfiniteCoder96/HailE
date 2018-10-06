package com.androidapp.scalar.hailee;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class CustomerProfileActivity extends AppCompatActivity {

    private CircularProgressButton mLogOutBtn;
    private CircularProgressButton mEditDetailsBtn;
    private TextView mNameField,mEmailField,mPhoneField,mCustomerNameView;
    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;

    private String userID,mName,mEmail,mPhone,mProfileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);


        viewUserInformation();
        editCustomerDetails();
        logoutCustomer();
    }

    protected void logoutCustomer(){
        mLogOutBtn = (CircularProgressButton) findViewById(R.id.customerLogoutBtn);

        mLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("StaticFieldLeak") final AsyncTask<String,String,String> logout = new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                       try {
                           Thread.sleep(2000);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }

                       return "done";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        AlertDialog.Builder logout_builder = new AlertDialog.Builder(CustomerProfileActivity.this);
                        logout_builder.setMessage("Do you want to Logout from HailE ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(CustomerProfileActivity.this, LogInActivity.class);
                                        startActivity(intent);
                                        return;
                                    }
                                })
                                .setNegativeButton("No,I'm just kidding", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        mLogOutBtn.revertAnimation();
                                    }
                                });

                        AlertDialog logout_alertDialog = logout_builder.create();
                        logout_alertDialog.setTitle("Logging Out");
                        logout_alertDialog.show();


                    }
                };

                mLogOutBtn.startAnimation();
                logout.execute();
            }
        });
    }

    protected void editCustomerDetails(){

        mEditDetailsBtn = (CircularProgressButton) findViewById(R.id.customerProfileEditBtn);

        mEditDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("StaticFieldLeak") final AsyncTask<String,String,String> logout = new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {

                        Intent intent = new Intent(CustomerProfileActivity.this, CustomerDetailsEditActivity.class);
                        startActivity(intent);


                        return "done";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        mEditDetailsBtn.revertAnimation();
                    }

                };

                mEditDetailsBtn.startAnimation();
                logout.execute();
            }
        });
    }

    private void viewUserInformation(){

        mNameField = (TextView) findViewById(R.id.customer_name_txt);
        mEmailField = (TextView) findViewById(R.id.customer_email_txt);
        mPhoneField = (TextView) findViewById(R.id.customer_mobnum_txt);
        mProfileImage = (ImageView) findViewById(R.id.userProfilePic);
        mCustomerNameView = (TextView) findViewById(R.id.customerNameView );

        mAuth = FirebaseAuth.getInstance();

        userID = mAuth.getCurrentUser().getUid();

        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);

        mCustomerDatabase.child("user details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>)dataSnapshot.getValue();

                    if(map.get("customerName") != null){
                        mName = map.get("customerName").toString();
                        mNameField.setText(mName);
                        mCustomerNameView.setText(mName);
                    }

                    if(map.get("customerEmail") != null){
                        mEmail = map.get("customerEmail").toString();
                        mEmailField.setText(mEmail);
                    }

                    if(map.get("customerMobile") != null){
                        mPhone = map.get("customerMobile").toString();
                        mPhoneField.setText(mPhone);
                    }

                    if(map.get("customerProfileImageUrl") != null){
                        mProfileImageUrl = map.get("customerProfileImageUrl").toString();

                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
